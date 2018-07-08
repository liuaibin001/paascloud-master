/*
 * Copyright (c) 2018. paascloud.net All Rights Reserved.
 * 项目名称：paascloud快速搭建企业级分布式微服务平台
 *  类名称：PcResourceServerConfig.java
 *  创建人：刘兆明
 *  联系方式：paascloud.net@gmail.com
 *  开源地址: https://github.com/paascloud
 *  博客地址: http://blog.paascloud.net
 *  项目官网: http://paascloud.net
 */

package com.paascloud.service.config;

import com.paascloud.security.core.SecurityConstants;
import com.paascloud.service.security.code.ValidateCodeSecurityConfig;
import com.paascloud.service.security.mobile.SmsCodeAuthenticationSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * 资源服务器配置
 *
 * @author paascloud.net @gmail.com
 */
@Configuration
@EnableResourceServer
public class PcResourceServerConfig extends ResourceServerConfigurerAdapter {
	@Autowired
	private SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig;
	@Autowired
	private ValidateCodeSecurityConfig validateCodeSecurityConfig;
	@Autowired
	private FormAuthenticationConfig formAuthenticationConfig;

	@Resource
	private DataSource dataSource;

	/**
	 * 记住我功能的token存取器配置
	 *
	 * @return the persistent token repository
	 */
	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
		tokenRepository.setDataSource(dataSource);
//		tokenRepository.setCreateTableOnStartup(true); // 第一次启动创建
		return tokenRepository;
	}

	/**
	 * Configure.
	 *
	 * @param http the http
	 *
	 * @throws Exception the exception
	 */
	@Override
	public void configure(HttpSecurity http) throws Exception {
		formAuthenticationConfig.configure(http);

		http.apply(validateCodeSecurityConfig)
				.and()
				.apply(smsCodeAuthenticationSecurityConfig)
				.and()
				.authorizeRequests()
				.antMatchers(
						SecurityConstants.DEFAULT_UNAUTHENTICATION_URL,
						SecurityConstants.DEFAULT_VALIDATE_CODE_URL_PREFIX+"/*")
				.permitAll()
				.anyRequest()
				.authenticated()
				.and()
				.csrf().disable();
	}
}