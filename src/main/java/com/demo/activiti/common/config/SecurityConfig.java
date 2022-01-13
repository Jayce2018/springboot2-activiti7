package com.demo.activiti.common.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 配置Security
 *
 * @author sunjie
 * @date 2022/1/12 10:35
 **/
//@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //配置不需要登录验证,当前只验证activiti7。后续整合开放即可
        http.authorizeRequests().anyRequest().permitAll().and().logout().permitAll();
    }
}
