package com.infra.job.core.executor.impl;

import com.infra.job.core.executor.JobExecutor;
import com.infra.job.core.handler.annotation.Job;
import com.infra.job.core.handler.impl.MethodJobHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class JobSimpleExecutor extends JobExecutor {
    private static final Logger logger = LoggerFactory.getLogger(JobSimpleExecutor.class);


    private List<Object> JobBeanList = new ArrayList<>();
    public List<Object> getJobBeanList() {
        return JobBeanList;
    }
    public void setJobBeanList(List<Object> JobBeanList) {
        this.JobBeanList = JobBeanList;
    }


    @Override
    public void start() {

        initJobHandlerMethodRepository(JobBeanList);

        try {
            super.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }


    private void initJobHandlerMethodRepository(List<Object> JobBeanList) {
        if (JobBeanList==null || JobBeanList.size()==0) {
            return;
        }

        for (Object bean: JobBeanList) {
            Method[] methods = bean.getClass().getDeclaredMethods();
            if (methods.length == 0) {
                continue;
            }
            for (Method executeMethod : methods) {
                Job Job = executeMethod.getAnnotation(Job.class);
                registJobHandler(Job, bean, executeMethod);
            }

        }

    }

}
