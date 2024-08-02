package com.infra.job.executorbiz;

import com.infra.job.core.biz.ExecutorBiz;
import com.infra.job.core.biz.client.ExecutorBizClient;
import com.infra.job.core.biz.model.*;
import com.infra.job.core.enums.ExecutorBlockStrategyEnum;
import com.infra.job.core.glue.GlueTypeEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ExecutorBizTest {

    private static String addressUrl = "http://127.0.0.1:9999/";
    private static String accessToken = null;

    @Test
    public void beat() throws Exception {
        ExecutorBiz executorBiz = new ExecutorBizClient(addressUrl, accessToken);
        final ReturnT<String> retval = executorBiz.beat();

        Assertions.assertNotNull(retval);
        Assertions.assertNull(((ReturnT<String>) retval).getContent());
        Assertions.assertEquals(200, retval.getCode());
        Assertions.assertNull(retval.getMsg());
    }

    @Test
    public void idleBeat(){
        ExecutorBiz executorBiz = new ExecutorBizClient(addressUrl, accessToken);

        final int jobId = 0;

        final ReturnT<String> retval = executorBiz.idleBeat(new IdleBeatParam(jobId));

        Assertions.assertNotNull(retval);
        Assertions.assertNull(((ReturnT<String>) retval).getContent());
        Assertions.assertEquals(500, retval.getCode());
        Assertions.assertEquals("job thread is running or has trigger queue.", retval.getMsg());
    }

    @Test
    public void run(){
        ExecutorBiz executorBiz = new ExecutorBizClient(addressUrl, accessToken);

        final TriggerParam triggerParam = new TriggerParam();
        triggerParam.setJobId(1);
        triggerParam.setExecutorHandler("demoJobHandler");
        triggerParam.setExecutorParams(null);
        triggerParam.setExecutorBlockStrategy(ExecutorBlockStrategyEnum.COVER_EARLY.name());
        triggerParam.setGlueType(GlueTypeEnum.BEAN.name());
        triggerParam.setGlueSource(null);
        triggerParam.setGlueUpdatetime(System.currentTimeMillis());
        triggerParam.setLogId(1);
        triggerParam.setLogDateTime(System.currentTimeMillis());

        final ReturnT<String> retval = executorBiz.run(triggerParam);

        Assertions.assertNotNull(retval);
    }

    @Test
    public void kill(){
        ExecutorBiz executorBiz = new ExecutorBizClient(addressUrl, accessToken);

        final int jobId = 0;

        final ReturnT<String> retval = executorBiz.kill(new KillParam(jobId));

        Assertions.assertNotNull(retval);
        Assertions.assertNull(((ReturnT<String>) retval).getContent());
        Assertions.assertEquals(200, retval.getCode());
        Assertions.assertNull(retval.getMsg());
    }

    @Test
    public void log(){
        ExecutorBiz executorBiz = new ExecutorBizClient(addressUrl, accessToken);

        final long logDateTim = 0L;
        final long logId = 0;
        final int fromLineNum = 0;

        final ReturnT<LogResult> retval = executorBiz.log(new LogParam(logDateTim, logId, fromLineNum));

        Assertions.assertNotNull(retval);
    }

}
