package com.infra.job.admin.controller;

import com.infra.job.admin.core.model.JobInfo;
import com.infra.job.admin.core.model.JobLogGlue;
import com.infra.job.admin.core.util.I18nUtil;
import com.infra.job.admin.dao.JobInfoDao;
import com.infra.job.admin.dao.JobLogGlueDao;
import com.infra.job.core.biz.model.ReturnT;
import com.infra.job.core.glue.GlueTypeEnum;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/jobcode")
public class JobCodeController {

	@Resource
	private JobInfoDao JobInfoDao;
	@Resource
	private JobLogGlueDao JobLogGlueDao;

	@RequestMapping
	public String index(HttpServletRequest request, Model model, int jobId) {
		JobInfo jobInfo = JobInfoDao.loadById(jobId);
		List<JobLogGlue> jobLogGlues = JobLogGlueDao.findByJobId(jobId);

		if (jobInfo == null) {
			throw new RuntimeException(I18nUtil.getString("jobinfo_glue_jobid_unvalid"));
		}
		if (GlueTypeEnum.BEAN == GlueTypeEnum.match(jobInfo.getGlueType())) {
			throw new RuntimeException(I18nUtil.getString("jobinfo_glue_gluetype_unvalid"));
		}

		JobInfoController.validPermission(request, jobInfo.getJobGroup());

		model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());

		model.addAttribute("jobInfo", jobInfo);
		model.addAttribute("jobLogGlues", jobLogGlues);
		return "jobcode/jobcode.index";
	}

	@RequestMapping("/save")
	@ResponseBody
	public ReturnT<String> save(Model model, int id, String glueSource, String glueRemark) {
		// valid
		if (glueRemark==null) {
			return new ReturnT<String>(500, (I18nUtil.getString("system_please_input") + I18nUtil.getString("jobinfo_glue_remark")) );
		}
		if (glueRemark.length()<4 || glueRemark.length()>100) {
			return new ReturnT<String>(500, I18nUtil.getString("jobinfo_glue_remark_limit"));
		}
		JobInfo exists_jobInfo = JobInfoDao.loadById(id);
		if (exists_jobInfo == null) {
			return new ReturnT<String>(500, I18nUtil.getString("jobinfo_glue_jobid_unvalid"));
		}

		// update new code
		exists_jobInfo.setGlueSource(glueSource);
		exists_jobInfo.setGlueRemark(glueRemark);
		exists_jobInfo.setGlueUpdatetime(new Date());

		exists_jobInfo.setUpdateTime(new Date());
		JobInfoDao.update(exists_jobInfo);

		// log old code
		JobLogGlue JobLogGlue = new JobLogGlue();
		JobLogGlue.setJobId(exists_jobInfo.getId());
		JobLogGlue.setGlueType(exists_jobInfo.getGlueType());
		JobLogGlue.setGlueSource(glueSource);
		JobLogGlue.setGlueRemark(glueRemark);

		JobLogGlue.setAddTime(new Date());
		JobLogGlue.setUpdateTime(new Date());
		JobLogGlueDao.save(JobLogGlue);

		// remove code backup more than 30
		JobLogGlueDao.removeOld(exists_jobInfo.getId(), 30);

		return ReturnT.SUCCESS;
	}

}
