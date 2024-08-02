package com.infra.job.admin.dao;

import com.infra.job.admin.core.model.JobInfo;
import com.infra.job.admin.core.scheduler.MisfireStrategyEnum;
import com.infra.job.admin.core.scheduler.ScheduleTypeEnum;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JobInfoDaoTest {
	private static Logger logger = LoggerFactory.getLogger(JobInfoDaoTest.class);

	@Resource
	private JobInfoDao JobInfoDao;

	@Test
	public void pageList(){
		List<JobInfo> list = JobInfoDao.pageList(0, 20, 0, -1, null, null, null);
		int list_count = JobInfoDao.pageListCount(0, 20, 0, -1, null, null, null);

		logger.info("", list);
		logger.info("", list_count);

		List<JobInfo> list2 = JobInfoDao.getJobsByGroup(1);
	}

	@Test
	public void save_load(){
		JobInfo info = new JobInfo();
		info.setJobGroup(1);
		info.setJobDesc("desc");
		info.setAuthor("setAuthor");
		info.setAlarmEmail("setAlarmEmail");
		info.setScheduleType(ScheduleTypeEnum.FIX_RATE.name());
		info.setScheduleConf(String.valueOf(33));
		info.setMisfireStrategy(MisfireStrategyEnum.DO_NOTHING.name());
		info.setExecutorRouteStrategy("setExecutorRouteStrategy");
		info.setExecutorHandler("setExecutorHandler");
		info.setExecutorParam("setExecutorParam");
		info.setExecutorBlockStrategy("setExecutorBlockStrategy");
		info.setGlueType("setGlueType");
		info.setGlueSource("setGlueSource");
		info.setGlueRemark("setGlueRemark");
		info.setChildJobId("1");

		info.setAddTime(new Date());
		info.setUpdateTime(new Date());
		info.setGlueUpdatetime(new Date());

		int count = JobInfoDao.save(info);

		JobInfo info2 = JobInfoDao.loadById(info.getId());
		info.setScheduleType(ScheduleTypeEnum.FIX_RATE.name());
		info.setScheduleConf(String.valueOf(44));
		info.setMisfireStrategy(MisfireStrategyEnum.FIRE_ONCE_NOW.name());
		info2.setJobDesc("desc2");
		info2.setAuthor("setAuthor2");
		info2.setAlarmEmail("setAlarmEmail2");
		info2.setExecutorRouteStrategy("setExecutorRouteStrategy2");
		info2.setExecutorHandler("setExecutorHandler2");
		info2.setExecutorParam("setExecutorParam2");
		info2.setExecutorBlockStrategy("setExecutorBlockStrategy2");
		info2.setGlueType("setGlueType2");
		info2.setGlueSource("setGlueSource2");
		info2.setGlueRemark("setGlueRemark2");
		info2.setGlueUpdatetime(new Date());
		info2.setChildJobId("1");

		info2.setUpdateTime(new Date());
		int item2 = JobInfoDao.update(info2);

		JobInfoDao.delete(info2.getId());

		List<JobInfo> list2 = JobInfoDao.getJobsByGroup(1);

		int ret3 = JobInfoDao.findAllCount();

	}

}
