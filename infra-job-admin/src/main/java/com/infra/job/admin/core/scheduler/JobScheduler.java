package com.infra.job.admin.core.scheduler;

import com.infra.job.admin.core.conf.JobAdminConfig;
import com.infra.job.admin.core.thread.*;
import com.infra.job.admin.core.util.I18nUtil;
import com.infra.job.core.biz.ExecutorBiz;
import com.infra.job.core.biz.client.ExecutorBizClient;
import com.infra.job.core.enums.ExecutorBlockStrategyEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class JobScheduler  {
    private static final Logger logger = LoggerFactory.getLogger(JobScheduler.class);


    public void init() throws Exception {
        initI18n();

        JobTriggerPoolHelper.toStart();

        JobRegistryHelper.getInstance().start();

        JobFailMonitorHelper.getInstance().start();

        JobCompleteHelper.getInstance().start();

        JobLogReportHelper.getInstance().start();

        JobScheduleHelper.getInstance().start();

        logger.info(">>>>>>>>> init job admin success.");
    }

    
    public void destroy() throws Exception {

        JobScheduleHelper.getInstance().toStop();

        JobLogReportHelper.getInstance().toStop();

        JobCompleteHelper.getInstance().toStop();

        JobFailMonitorHelper.getInstance().toStop();

        JobRegistryHelper.getInstance().toStop();

        JobTriggerPoolHelper.toStop();

    }

    private void initI18n(){
        for (ExecutorBlockStrategyEnum item:ExecutorBlockStrategyEnum.values()) {
            item.setTitle(I18nUtil.getString("jobconf_block_".concat(item.name())));
        }
    }

    private static ConcurrentMap<String, ExecutorBiz> executorBizRepository = new ConcurrentHashMap<String, ExecutorBiz>();
    public static ExecutorBiz getExecutorBiz(String address) throws Exception {
        if (address==null || address.trim().length()==0) {
            return null;
        }

        address = address.trim();
        ExecutorBiz executorBiz = executorBizRepository.get(address);
        if (executorBiz != null) {
            return executorBiz;
        }

        executorBiz = new ExecutorBizClient(address, JobAdminConfig.getAdminConfig().getAccessToken());

        executorBizRepository.put(address, executorBiz);
        return executorBiz;
    }

}
