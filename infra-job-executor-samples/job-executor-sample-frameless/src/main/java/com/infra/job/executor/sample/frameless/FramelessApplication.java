package com.infra.job.executor.sample.frameless;

import com.infra.job.executor.sample.frameless.config.FrameLessJobConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class FramelessApplication {
    private static Logger logger = LoggerFactory.getLogger(FramelessApplication.class);

    public static void main(String[] args) {

        try {
            FrameLessJobConfig.getInstance().initJobExecutor();

            while (true) {
                try {
                    TimeUnit.HOURS.sleep(1);
                } catch (InterruptedException e) {
                    break;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            FrameLessJobConfig.getInstance().destroyJobExecutor();
        }

    }

}
