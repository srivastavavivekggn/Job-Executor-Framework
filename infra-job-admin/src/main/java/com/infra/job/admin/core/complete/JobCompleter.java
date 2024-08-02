package com.infra.job.admin.core.complete;

import com.infra.job.admin.core.conf.JobAdminConfig;
import com.infra.job.admin.core.model.JobInfo;
import com.infra.job.admin.core.model.JobLog;
import com.infra.job.admin.core.thread.JobTriggerPoolHelper;
import com.infra.job.admin.core.trigger.TriggerTypeEnum;
import com.infra.job.admin.core.util.I18nUtil;
import com.infra.job.core.biz.model.ReturnT;
import com.infra.job.core.context.JobContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

public class JobCompleter {
    private static Logger logger = LoggerFactory.getLogger(JobCompleter.class);

    public static int updateHandleInfoAndFinish(JobLog JobLog) {

        finishJob(JobLog);

        if (JobLog.getHandleMsg().length() > 15000) {
            JobLog.setHandleMsg( JobLog.getHandleMsg().substring(0, 15000) );
        }

        return JobAdminConfig.getAdminConfig().getJobLogDao().updateHandleInfo(JobLog);
    }


    private static void finishJob(JobLog JobLog){

        String triggerChildMsg = null;
        if (JobContext.HANDLE_CODE_SUCCESS == JobLog.getHandleCode()) {
            JobInfo JobInfo = JobAdminConfig.getAdminConfig().getJobInfoDao().loadById(JobLog.getJobId());
            if (JobInfo!=null && JobInfo.getChildJobId()!=null && JobInfo.getChildJobId().trim().length()>0) {
                triggerChildMsg = "<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>"+ I18nUtil.getString("jobconf_trigger_child_run") +"<<<<<<<<<<< </span><br>";

                String[] childJobIds = JobInfo.getChildJobId().split(",");
                for (int i = 0; i < childJobIds.length; i++) {
                    int childJobId = (childJobIds[i]!=null && childJobIds[i].trim().length()>0 && isNumeric(childJobIds[i]))?Integer.valueOf(childJobIds[i]):-1;
                    if (childJobId > 0) {

                        JobTriggerPoolHelper.trigger(childJobId, TriggerTypeEnum.PARENT, -1, null, null, null);
                        ReturnT<String> triggerChildResult = ReturnT.SUCCESS;

                        triggerChildMsg += MessageFormat.format(I18nUtil.getString("jobconf_callback_child_msg1"),
                                (i+1),
                                childJobIds.length,
                                childJobIds[i],
                                (triggerChildResult.getCode()==ReturnT.SUCCESS_CODE?I18nUtil.getString("system_success"):I18nUtil.getString("system_fail")),
                                triggerChildResult.getMsg());
                    } else {
                        triggerChildMsg += MessageFormat.format(I18nUtil.getString("jobconf_callback_child_msg2"),
                                (i+1),
                                childJobIds.length,
                                childJobIds[i]);
                    }
                }

            }
        }

        if (triggerChildMsg != null) {
            JobLog.setHandleMsg( JobLog.getHandleMsg() + triggerChildMsg );
        }

    }

    private static boolean isNumeric(String str){
        try {
            int result = Integer.valueOf(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
