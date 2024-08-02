package com.infra.job.admin.core.alarm;

import com.infra.job.admin.core.model.JobInfo;
import com.infra.job.admin.core.model.JobLog;

public interface JobAlarm {

    public boolean doAlarm(JobInfo info, JobLog jobLog);

}
