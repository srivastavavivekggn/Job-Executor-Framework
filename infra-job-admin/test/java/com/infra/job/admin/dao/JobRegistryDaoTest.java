package com.infra.job.admin.dao;

import com.infra.job.admin.core.model.JobRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JobRegistryDaoTest {

    @Resource
    private JobRegistryDao JobRegistryDao;

    @Test
    public void test(){
        int ret = JobRegistryDao.registryUpdate("g1", "k1", "v1", new Date());
        if (ret < 1) {
            ret = JobRegistryDao.registrySave("g1", "k1", "v1", new Date());
        }

        List<JobRegistry> list = JobRegistryDao.findAll(1, new Date());

        int ret2 = JobRegistryDao.removeDead(Arrays.asList(1));
    }

}
