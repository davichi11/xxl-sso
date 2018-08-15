package com.xxl.sso.core.filter;

import com.alibaba.fastjson.JSON;
import com.xxl.sso.core.conf.Conf;
import com.xxl.sso.core.entity.ReturnT;
import com.xxl.sso.core.user.User;
import com.xxl.sso.core.util.SsoLoginHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * app sso filter
 *
 * @author xuxueli 2018-04-08 21:30:54
 */
public class SsoTokenFilter extends HttpServlet implements Filter {
    private static Logger logger = LoggerFactory.getLogger(SsoTokenFilter.class);

    private String logoutPath;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        String ssoServer = filterConfig.getInitParameter(Conf.SSO_SERVER);
        if (ssoServer != null && ssoServer.trim().length() > 0) {
            logoutPath = filterConfig.getInitParameter(Conf.SSO_LOGOUT_PATH);
        }

        logger.info("SsoFilter init.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String servletPath = ((HttpServletRequest) request).getServletPath();
//        String link = req.getRequestURL().toString();

        String sessionid = SsoLoginHelper.cookieSessionIdGetByHeader(req);
        User user = SsoLoginHelper.loginCheck(sessionid);

        // logout filter
        if (StringUtils.isNotBlank(logoutPath) && logoutPath.equals(servletPath)) {

            if (user != null) {
                SsoLoginHelper.logout(sessionid);
            }

            // response
            res.setStatus(HttpServletResponse.SC_OK);
            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().println(JSON.toJSONString(new ReturnT(ReturnT.SUCCESS_CODE, null)));
            return;
        }

        // login filter
        if (user == null) {

            // response
            res.setStatus(HttpServletResponse.SC_OK);
            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().println(JSON.toJSONString(Conf.SSO_LOGIN_FAIL_RESULT));
            return;
        }

        // ser sso user
        request.setAttribute(Conf.SSO_USER, user);


        // already login, allow
        chain.doFilter(request, response);
    }


}
