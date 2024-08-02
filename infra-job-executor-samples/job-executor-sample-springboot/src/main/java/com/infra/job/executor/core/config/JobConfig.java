package com.infra.job.executor.core.config;

import com.infra.job.core.executor.impl.JobSpringExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobConfig {
    private Logger logger = LoggerFactory.getLogger(JobConfig.class);

    @Value("${infra.job.admin.addresses}")
    private String adminAddresses;

    @Value("${infra.job.accessToken}")
    private String accessToken;

    @Value("${infra.job.executor.appname}")
    private String appname;

    @Value("${infra.job.executor.address}")
    private String address;

    @Value("${infra.job.executor.ip}")
    private String ip;

    @Value("${infra.job.executor.port}")
    private int port;

    @Value("${infra.job.executor.logpath}")
    private String logPath;

    @Value("${infra.job.executor.logretentiondays}")
    private int logRetentionDays;


    @Bean
    public JobSpringExecutor JobExecutor() {
        logger.info(">>>>>>>>>>> job config init.");
        JobSpringExecutor JobSpringExecutor = new JobSpringExecutor();
        JobSpringExecutor.setAdminAddresses(adminAddresses);
        JobSpringExecutor.setAppname(appname);
        JobSpringExecutor.setAddress(address);
        JobSpringExecutor.setIp(ip);
        JobSpringExecutor.setPort(port);
        JobSpringExecutor.setAccessToken(accessToken);
        JobSpringExecutor.setLogPath(logPath);
        JobSpringExecutor.setLogRetentionDays(logRetentionDays);

        return JobSpringExecutor;
    }

}