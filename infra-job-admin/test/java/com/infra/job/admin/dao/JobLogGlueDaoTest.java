package com.infra.job.admin.dao;

import com.infra.job.admin.core.model.JobLogGlue;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JobLogGlueDaoTest {

    @Resource
    private JobLogGlueDao JobLogGlueDao;

    @Test
    public void test(){
        JobLogGlue logGlue = new JobLogGlue();
        logGlue.setJobId(1);
        logGlue.setGlueType("1");
        logGlue.setGlueSource("1");
        logGlue.setGlueRemark("1");

        logGlue.setAddTime(new Date());
        logGlue.setUpdateTime(new Date());
        int ret = JobLogGlueDao.save(logGlue);

        List<JobLogGlue> list = JobLogGlueDao.findByJobId(1);

        int ret2 = JobLogGlueDao.removeOld(1, 1);

        int ret3 =JobLogGlueDao.deleteByJobId(1);
    }

}
