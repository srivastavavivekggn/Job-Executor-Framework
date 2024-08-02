package com.infra.job.admin.dao;

import com.infra.job.admin.core.model.JobGroup;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JobGroupDaoTest {

    @Resource
    private JobGroupDao JobGroupDao;

    @Test
    public void test(){
        List<JobGroup> list = JobGroupDao.findAll();

        List<JobGroup> list2 = JobGroupDao.findByAddressType(0);

        JobGroup group = new JobGroup();
        group.setAppname("setAppName");
        group.setTitle("setTitle");
        group.setAddressType(0);
        group.setAddressList("setAddressList");
        group.setUpdateTime(new Date());

        int ret = JobGroupDao.save(group);

        JobGroup group2 = JobGroupDao.load(group.getId());
        group2.setAppname("setAppName2");
        group2.setTitle("setTitle2");
        group2.setAddressType(2);
        group2.setAddressList("setAddressList2");
        group2.setUpdateTime(new Date());

        int ret2 = JobGroupDao.update(group2);

        int ret3 = JobGroupDao.remove(group.getId());
    }

}
