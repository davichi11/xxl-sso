package com.xxl.sso.core.filter;

import com.alibaba.fastjson.JSON;
import com.xxl.sso.core.conf.Conf;
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
 * web sso filter
 *
 * @author xuxueli 2018-04-03
 */
public class SsoFilter extends HttpServlet implements Filter {
    private static Logger logger = LoggerFactory.getLogger(SsoFilter.class);

    private String ssoServer;
    private String logoutPath;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        ssoServer = filterConfig.getInitParameter(Conf.SSO_SERVER);
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
        String link = req.getRequestURL().toString();

        // logout filter
        if (StringUtils.isNotBlank(logoutPath) && logoutPath.equals(servletPath)) {

            // remove cookie
            SsoLoginHelper.removeSessionIdInCookie(req, res);

            // redirect logout
            String logoutPageUrl = ssoServer.concat(Conf.SSO_LOGOUT);
            res.sendRedirect(logoutPageUrl);

            return;
        }

        // login filter
        User user;

        // valid cookie user
        String cookieSessionId = SsoLoginHelper.getSessionIdByCookie(req);
        user = SsoLoginHelper.loginCheck(cookieSessionId);

        // valid param user, client login
        if (user == null) {

            // remove old cookie
            SsoLoginHelper.removeSessionIdInCookie(req, res);

            // set new cookie
            String paramSessionId = request.getParameter(Conf.SSO_SESSIONID);
            if (paramSessionId != null) {
                user = SsoLoginHelper.loginCheck(paramSessionId);
                if (user != null) {
                    SsoLoginHelper.setSessionIdInCookie(res, paramSessionId);
                }
            }
        }

        // valid login fail
        if (user == null) {

            String header = req.getHeader("content-type");
            boolean isJson = header != null && header.contains("json");
            if (isJson) {

                // json msg
                res.setContentType("application/json;charset=utf-8");
                res.getWriter().println(JSON.toJSONString(Conf.SSO_LOGIN_FAIL_RESULT));
                return;
            } else {

                // redirect logout
                String loginPageUrl = ssoServer.concat(Conf.SSO_LOGIN) + "?" + Conf.REDIRECT_URL + "=" + link;

                res.sendRedirect(loginPageUrl);
                return;
            }

        }

        // ser sso user
        request.setAttribute(Conf.SSO_USER, user);


        // already login, allow
        chain.doFilter(request, response);
    }

}
