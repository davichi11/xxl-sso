package com.xxl.sso.server.controller;

import com.xxl.sso.core.user.User;
import com.xxl.sso.core.util.SsoLoginHelper;
import com.xxl.sso.server.core.model.UserInfo;
import com.xxl.sso.server.core.result.ReturnT;
import com.xxl.sso.server.dao.UserInfoDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;

/**
 * sso server (for app)
 *
 * @author xuxueli 2018-04-08 21:02:54
 */
@Controller
@RequestMapping("/app")
public class AppController {

    private final UserInfoDao userInfoDao;

    @Autowired
    public AppController(UserInfoDao userInfoDao) {
        this.userInfoDao = userInfoDao;
    }


    /**
     * Login
     *
     * @param username
     * @param password
     * @return
     */
    @RequestMapping("/login")
    @ResponseBody
    public ReturnT<String> login(String username, String password) {

        if (StringUtils.isBlank(username)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "Please input username.");
        }
        if (StringUtils.isBlank(password)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "Please input password.");
        }
        UserInfo existUser = userInfoDao.findByUsername(username);
        if (existUser == null) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "username is invalid.");
        }
        if (!existUser.getPassword().equals(password)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "password is invalid.");
        }

        // login success
        User user = new User();
        user.setUserid(existUser.getId());
        user.setUsername(existUser.getUsername());

        String sessionId = UUID.randomUUID().toString();

        SsoLoginHelper.login(sessionId, user);

        // result
        return new ReturnT<>(sessionId);
    }


    /**
     * Logout
     *
     * @param sessionId
     * @return
     */
    @RequestMapping("/logout")
    @ResponseBody
    public ReturnT<String> logout(String sessionId) {

        // logout
        SsoLoginHelper.logout(sessionId);
        return ReturnT.SUCCESS;
    }

    /**
     * logincheck
     *
     * @param sessionId
     * @return
     */
    @RequestMapping("/logincheck")
    @ResponseBody
    public ReturnT<User> logincheck(String sessionId) {

        // logout
        User user = SsoLoginHelper.loginCheck(sessionId);
        if (user == null) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "sso not login.");
        }
        return new ReturnT<>(user);
    }

}