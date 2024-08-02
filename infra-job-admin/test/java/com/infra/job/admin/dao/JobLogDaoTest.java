package com.infra.job.admin.dao;

import com.infra.job.admin.core.model.JobLog;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JobLogDaoTest {

    @Resource
    private JobLogDao JobLogDao;

    @Test
    public void test(){
        List<JobLog> list = JobLogDao.pageList(0, 10, 1, 1, null, null, 1);
        int list_count = JobLogDao.pageListCount(0, 10, 1, 1, null, null, 1);

        JobLog log = new JobLog();
        log.setJobGroup(1);
        log.setJobId(1);

        long ret1 = JobLogDao.save(log);
        JobLog dto = JobLogDao.load(log.getId());

        log.setTriggerTime(new Date());
        log.setTriggerCode(1);
        log.setTriggerMsg("1");
        log.setExecutorAddress("1");
        log.setExecutorHandler("1");
        log.setExecutorParam("1");
        ret1 = JobLogDao.updateTriggerInfo(log);
        dto = JobLogDao.load(log.getId());


        log.setHandleTime(new Date());
        log.setHandleCode(2);
        log.setHandleMsg("2");
        ret1 = JobLogDao.updateHandleInfo(log);
        dto = JobLogDao.load(log.getId());


        List<Long> ret4 = JobLogDao.findClearLogIds(1, 1, new Date(), 100, 100);

        int ret2 = JobLogDao.delete(log.getJobId());

    }

}
