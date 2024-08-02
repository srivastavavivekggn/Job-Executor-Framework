package com.infra.job.admin.service;


import com.infra.job.admin.core.model.JobInfo;
import com.infra.job.core.biz.model.ReturnT;

import java.util.Date;
import java.util.Map;

public interface JobService {


	public Map<String, Object> pageList(int start, int length, int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author);

	
	public ReturnT<String> add(JobInfo jobInfo);

	
	public ReturnT<String> update(JobInfo jobInfo);

	public ReturnT<String> remove(int id);


	public ReturnT<String> start(int id);


	public ReturnT<String> stop(int id);

	public Map<String,Object> dashboardInfo();


	public ReturnT<Map<String,Object>> chartInfo(Date startDate, Date endDate);

}
