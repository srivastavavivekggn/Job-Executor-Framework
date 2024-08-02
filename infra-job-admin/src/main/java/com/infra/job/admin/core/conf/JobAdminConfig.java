package com.infra.job.admin.core.conf;

import com.infra.job.admin.core.alarm.JobAlarmer;
import com.infra.job.admin.core.scheduler.JobScheduler;
import com.infra.job.admin.dao.*;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Arrays;

@Component
public class JobAdminConfig implements InitializingBean, DisposableBean {

    private static JobAdminConfig adminConfig = null;
    public static JobAdminConfig getAdminConfig() {
        return adminConfig;
    }


    private JobScheduler JobScheduler;

    @Override
    public void afterPropertiesSet() throws Exception {
        adminConfig = this;

        JobScheduler = new JobScheduler();
        JobScheduler.init();
    }

    @Override
    public void destroy() throws Exception {
        JobScheduler.destroy();
    }


    @Value("${infra.job.i18n}")
    private String i18n;

    @Value("${infra.job.accessToken}")
    private String accessToken;

    @Value("${spring.mail.from}")
    private String emailFrom;

    @Value("${infra.job.triggerpool.fast.max}")
    private int triggerPoolFastMax;

    @Value("${infra.job.triggerpool.slow.max}")
    private int triggerPoolSlowMax;

    @Value("${infra.job.logretentiondays}")
    private int logretentiondays;

    @Resource
    private JobLogDao JobLogDao;
    @Resource
    private JobInfoDao JobInfoDao;
    @Resource
    private JobRegistryDao JobRegistryDao;
    @Resource
    private JobGroupDao JobGroupDao;
    @Resource
    private JobLogReportDao JobLogReportDao;
    @Resource
    private JavaMailSender mailSender;
    @Resource
    private DataSource dataSource;
    @Resource
    private JobAlarmer jobAlarmer;


    public String getI18n() {
        if (!Arrays.asList("zh_CN", "zh_TC", "en").contains(i18n)) {
            return "zh_CN";
        }
        return i18n;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getEmailFrom() {
        return emailFrom;
    }

    public int getTriggerPoolFastMax() {
        if (triggerPoolFastMax < 200) {
            return 200;
        }
        return triggerPoolFastMax;
    }

    public int getTriggerPoolSlowMax() {
        if (triggerPoolSlowMax < 100) {
            return 100;
        }
        return triggerPoolSlowMax;
    }

    public int getLogretentiondays() {
        if (logretentiondays < 7) {
            return -1;  // Limit greater than or equal to 7, otherwise close
        }
        return logretentiondays;
    }

    public JobLogDao getJobLogDao() {
        return JobLogDao;
    }

    public JobInfoDao getJobInfoDao() {
        return JobInfoDao;
    }

    public JobRegistryDao getJobRegistryDao() {
        return JobRegistryDao;
    }

    public JobGroupDao getJobGroupDao() {
        return JobGroupDao;
    }

    public JobLogReportDao getJobLogReportDao() {
        return JobLogReportDao;
    }

    public JavaMailSender getMailSender() {
        return mailSender;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public JobAlarmer getJobAlarmer() {
        return jobAlarmer;
    }

}
