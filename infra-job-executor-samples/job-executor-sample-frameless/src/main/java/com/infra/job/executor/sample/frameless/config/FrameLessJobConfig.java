package com.infra.job.executor.sample.frameless.config;

import com.infra.job.executor.sample.frameless.jobhandler.SampleJob;
import com.infra.job.core.executor.impl.JobSimpleExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Properties;

public class FrameLessJobConfig {
    private static Logger logger = LoggerFactory.getLogger(FrameLessJobConfig.class);


    private static FrameLessJobConfig instance = new FrameLessJobConfig();
    public static FrameLessJobConfig getInstance() {
        return instance;
    }


    private JobSimpleExecutor JobExecutor = null;

    public void initJobExecutor() {

        Properties JobProp = loadProperties("job-executor.properties");

        JobExecutor = new JobSimpleExecutor();
        JobExecutor.setAdminAddresses(JobProp.getProperty("infra.job.admin.addresses"));
        JobExecutor.setAccessToken(JobProp.getProperty("infra.job.accessToken"));
        JobExecutor.setAppname(JobProp.getProperty("infra.job.executor.appname"));
        JobExecutor.setAddress(JobProp.getProperty("infra.job.executor.address"));
        JobExecutor.setIp(JobProp.getProperty("infra.job.executor.ip"));
        JobExecutor.setPort(Integer.valueOf(JobProp.getProperty("infra.job.executor.port")));
        JobExecutor.setLogPath(JobProp.getProperty("infra.job.executor.logpath"));
        JobExecutor.setLogRetentionDays(Integer.valueOf(JobProp.getProperty("infra.job.executor.logretentiondays")));

        JobExecutor.setJobBeanList(Arrays.asList(new SampleJob()));

        try {
            JobExecutor.start();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void destroyJobExecutor() {
        if (JobExecutor != null) {
            JobExecutor.destroy();
        }
    }


    public static Properties loadProperties(String propertyFileName) {
        InputStreamReader in = null;
        try {
            ClassLoader loder = Thread.currentThread().getContextClassLoader();

            in = new InputStreamReader(loder.getResourceAsStream(propertyFileName), "UTF-8");;
            if (in != null) {
                Properties prop = new Properties();
                prop.load(in);
                return prop;
            }
        } catch (IOException e) {
            logger.error("load {} error!", propertyFileName);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error("close {} error!", propertyFileName);
                }
            }
        }
        return null;
    }

}
