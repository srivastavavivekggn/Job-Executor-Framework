package com.infra.job.admin.controller;

import com.infra.job.admin.controller.annotation.PermissionLimit;
import com.infra.job.admin.core.model.JobGroup;
import com.infra.job.admin.core.model.JobUser;
import com.infra.job.admin.core.util.I18nUtil;
import com.infra.job.admin.dao.JobGroupDao;
import com.infra.job.admin.dao.JobUserDao;
import com.infra.job.admin.service.LoginService;
import com.infra.job.core.biz.model.ReturnT;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    @Resource
    private JobUserDao JobUserDao;
    @Resource
    private JobGroupDao JobGroupDao;

    @RequestMapping
    @PermissionLimit(adminuser = true)
    public String index(Model model) {

        List<JobGroup> groupList = JobGroupDao.findAll();
        model.addAttribute("groupList", groupList);

        return "user/user.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    @PermissionLimit(adminuser = true)
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length,
                                        String username, int role) {

        List<JobUser> list = JobUserDao.pageList(start, length, username, role);
        int list_count = JobUserDao.pageListCount(start, length, username, role);

        if (list!=null && list.size()>0) {
            for (JobUser item: list) {
                item.setPassword(null);
            }
        }

        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", list_count);		
        maps.put("recordsFiltered", list_count);	
        maps.put("data", list);  					
        return maps;
    }

    @RequestMapping("/add")
    @ResponseBody
    @PermissionLimit(adminuser = true)
    public ReturnT<String> add(JobUser JobUser) {

        // valid username
        if (!StringUtils.hasText(JobUser.getUsername())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("system_please_input")+I18nUtil.getString("user_username") );
        }
        JobUser.setUsername(JobUser.getUsername().trim());
        if (!(JobUser.getUsername().length()>=4 && JobUser.getUsername().length()<=20)) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("system_lengh_limit")+"[4-20]" );
        }
        if (!StringUtils.hasText(JobUser.getPassword())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("system_please_input")+I18nUtil.getString("user_password") );
        }
        JobUser.setPassword(JobUser.getPassword().trim());
        if (!(JobUser.getPassword().length()>=4 && JobUser.getPassword().length()<=20)) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("system_lengh_limit")+"[4-20]" );
        }
        JobUser.setPassword(DigestUtils.md5DigestAsHex(JobUser.getPassword().getBytes()));

        JobUser existUser = JobUserDao.loadByUserName(JobUser.getUsername());
        if (existUser != null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("user_username_repeat") );
        }

        JobUserDao.save(JobUser);
        return ReturnT.SUCCESS;
    }

    @RequestMapping("/update")
    @ResponseBody
    @PermissionLimit(adminuser = true)
    public ReturnT<String> update(HttpServletRequest request, JobUser JobUser) {

        JobUser loginUser = (JobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);
        if (loginUser.getUsername().equals(JobUser.getUsername())) {
            return new ReturnT<String>(ReturnT.FAIL.getCode(), I18nUtil.getString("user_update_loginuser_limit"));
        }

        if (StringUtils.hasText(JobUser.getPassword())) {
            JobUser.setPassword(JobUser.getPassword().trim());
            if (!(JobUser.getPassword().length()>=4 && JobUser.getPassword().length()<=20)) {
                return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("system_lengh_limit")+"[4-20]" );
            }
            JobUser.setPassword(DigestUtils.md5DigestAsHex(JobUser.getPassword().getBytes()));
        } else {
            JobUser.setPassword(null);
        }

        JobUserDao.update(JobUser);
        return ReturnT.SUCCESS;
    }

    @RequestMapping("/remove")
    @ResponseBody
    @PermissionLimit(adminuser = true)
    public ReturnT<String> remove(HttpServletRequest request, int id) {

        JobUser loginUser = (JobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);
        if (loginUser.getId() == id) {
            return new ReturnT<String>(ReturnT.FAIL.getCode(), I18nUtil.getString("user_update_loginuser_limit"));
        }

        JobUserDao.delete(id);
        return ReturnT.SUCCESS;
    }

    @RequestMapping("/updatePwd")
    @ResponseBody
    public ReturnT<String> updatePwd(HttpServletRequest request, String password){

        if (password==null || password.trim().length()==0){
            return new ReturnT<String>(ReturnT.FAIL.getCode(), "密码不可为空");
        }
        password = password.trim();
        if (!(password.length()>=4 && password.length()<=20)) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("system_lengh_limit")+"[4-20]" );
        }

        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());

        JobUser loginUser = (JobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);

        JobUser existUser = JobUserDao.loadByUserName(loginUser.getUsername());
        existUser.setPassword(md5Password);
        JobUserDao.update(existUser);

        return ReturnT.SUCCESS;
    }

}
