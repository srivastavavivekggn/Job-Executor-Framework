package com.infra.job.core.thread;

import com.infra.job.core.biz.model.HandleCallbackParam;
import com.infra.job.core.biz.model.ReturnT;
import com.infra.job.core.biz.model.TriggerParam;
import com.infra.job.core.context.JobContext;
import com.infra.job.core.context.JobHelper;
import com.infra.job.core.executor.JobExecutor;
import com.infra.job.core.handler.IJobHandler;
import com.infra.job.core.log.JobFileAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

public class JobThread extends Thread{
	private static Logger logger = LoggerFactory.getLogger(JobThread.class);

	private int jobId;
	private IJobHandler handler;
	private LinkedBlockingQueue<TriggerParam> triggerQueue;
	private Set<Long> triggerLogIdSet;		

	private volatile boolean toStop = false;
	private String stopReason;

    private boolean running = false;    
	private int idleTimes = 0;			


	public JobThread(int jobId, IJobHandler handler) {
		this.jobId = jobId;
		this.handler = handler;
		this.triggerQueue = new LinkedBlockingQueue<TriggerParam>();
		this.triggerLogIdSet = Collections.synchronizedSet(new HashSet<Long>());

		this.setName("job, JobThread-"+jobId+"-"+System.currentTimeMillis());
	}
	public IJobHandler getHandler() {
		return handler;
	}

	public ReturnT<String> pushTriggerQueue(TriggerParam triggerParam) {
		if (triggerLogIdSet.contains(triggerParam.getLogId())) {
			logger.info(">>>>>>>>>>> repeate trigger job, logId:{}", triggerParam.getLogId());
			return new ReturnT<String>(ReturnT.FAIL_CODE, "repeate trigger job, logId:" + triggerParam.getLogId());
		}

		triggerLogIdSet.add(triggerParam.getLogId());
		triggerQueue.add(triggerParam);
        return ReturnT.SUCCESS;
	}

	public void toStop(String stopReason) {
		this.toStop = true;
		this.stopReason = stopReason;
	}

    public boolean isRunningOrHasQueue() {
        return running || triggerQueue.size()>0;
    }

    @Override
	public void run() {

    	try {
			handler.init();
		} catch (Throwable e) {
    		logger.error(e.getMessage(), e);
		}

		while(!toStop){
			running = false;
			idleTimes++;

            TriggerParam triggerParam = null;
            try {
				triggerParam = triggerQueue.poll(3L, TimeUnit.SECONDS);
				if (triggerParam!=null) {
					running = true;
					idleTimes = 0;
					triggerLogIdSet.remove(triggerParam.getLogId());

					String logFileName = JobFileAppender.makeLogFileName(new Date(triggerParam.getLogDateTime()), triggerParam.getLogId());
					JobContext JobContext = new JobContext(
							triggerParam.getJobId(),
							triggerParam.getExecutorParams(),
							logFileName,
							triggerParam.getBroadcastIndex(),
							triggerParam.getBroadcastTotal());

					JobContext.setJobContext(JobContext);

					JobHelper.log("<br>----------- job job execute start -----------<br>----------- Param:" + JobContext.getJobParam());

					if (triggerParam.getExecutorTimeout() > 0) {
						Thread futureThread = null;
						try {
							FutureTask<Boolean> futureTask = new FutureTask<Boolean>(new Callable<Boolean>() {
								@Override
								public Boolean call() throws Exception {

									JobContext.setJobContext(JobContext);

									handler.execute();
									return true;
								}
							});
							futureThread = new Thread(futureTask);
							futureThread.start();

							Boolean tempResult = futureTask.get(triggerParam.getExecutorTimeout(), TimeUnit.SECONDS);
						} catch (TimeoutException e) {

							JobHelper.log("<br>----------- job job execute timeout");
							JobHelper.log(e);

							JobHelper.handleTimeout("job execute timeout ");
						} finally {
							futureThread.interrupt();
						}
					} else {
						handler.execute();
					}

					if (JobContext.getJobContext().getHandleCode() <= 0) {
						JobHelper.handleFail("job handle result lost.");
					} else {
						String tempHandleMsg = JobContext.getJobContext().getHandleMsg();
						tempHandleMsg = (tempHandleMsg!=null&&tempHandleMsg.length()>50000)
								?tempHandleMsg.substring(0, 50000).concat("...")
								:tempHandleMsg;
						JobContext.getJobContext().setHandleMsg(tempHandleMsg);
					}
					JobHelper.log("<br>----------- job job execute end(finish) -----------<br>----------- Result: handleCode="
							+ JobContext.getJobContext().getHandleCode()
							+ ", handleMsg = "
							+ JobContext.getJobContext().getHandleMsg()
					);

				} else {
					if (idleTimes > 30) {
						if(triggerQueue.size() == 0) {	
							JobExecutor.removeJobThread(jobId, "excutor idel times over limit.");
						}
					}
				}
			} catch (Throwable e) {
				if (toStop) {
					JobHelper.log("<br>----------- JobThread toStop, stopReason:" + stopReason);
				}

				StringWriter stringWriter = new StringWriter();
				e.printStackTrace(new PrintWriter(stringWriter));
				String errorMsg = stringWriter.toString();

				JobHelper.handleFail(errorMsg);

				JobHelper.log("<br>----------- JobThread Exception:" + errorMsg + "<br>----------- job job execute end(error) -----------");
			} finally {
                if(triggerParam != null) {
                    if (!toStop) {
                        TriggerCallbackThread.pushCallBack(new HandleCallbackParam(
                        		triggerParam.getLogId(),
								triggerParam.getLogDateTime(),
								JobContext.getJobContext().getHandleCode(),
								JobContext.getJobContext().getHandleMsg() )
						);
                    } else {
                        TriggerCallbackThread.pushCallBack(new HandleCallbackParam(
                        		triggerParam.getLogId(),
								triggerParam.getLogDateTime(),
								JobContext.HANDLE_CODE_FAIL,
								stopReason + " [job running, killed]" )
						);
                    }
                }
            }
        }

		while(triggerQueue !=null && triggerQueue.size()>0){
			TriggerParam triggerParam = triggerQueue.poll();
			if (triggerParam!=null) {
				TriggerCallbackThread.pushCallBack(new HandleCallbackParam(
						triggerParam.getLogId(),
						triggerParam.getLogDateTime(),
						JobContext.HANDLE_CODE_FAIL,
						stopReason + " [job not executed, in the job queue, killed.]")
				);
			}
		}

		try {
			handler.destroy();
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}

		logger.info(">>>>>>>>>>> job JobThread stoped, hashCode:{}", Thread.currentThread());
	}
}
