package com.xxl.sso.sample.config;

import com.xxl.sso.core.conf.Conf;
import com.xxl.sso.core.filter.SsoFilter;
import com.xxl.sso.core.util.JedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SsoConfig {

    @Value("${xxl.sso.server}")
    private String xxlSsoServer;

    @Value("${xxl.sso.logout.path}")
    private String xxlSsoLogoutPath;

    @Value("${xxl.sso.redis.address}")
    private String xxlSsoRedisAddress;

    @Bean
    public FilterRegistrationBean xxlSsoFilterRegistration() {

        // redis init
        JedisUtil.init(xxlSsoRedisAddress);

        // filter
        FilterRegistrationBean registration = new FilterRegistrationBean();

        registration.setName("SsoFilter");
        registration.setOrder(1);
        registration.addUrlPatterns("/*");
        registration.setFilter(new SsoFilter());
        registration.addInitParameter(Conf.SSO_SERVER, xxlSsoServer);
        registration.addInitParameter(Conf.SSO_LOGOUT_PATH, xxlSsoLogoutPath);

        return registration;
    }

}
