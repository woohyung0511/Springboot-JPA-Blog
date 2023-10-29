package com.cos.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.AuthorizeRequestsDsl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // IoC
public class SecurityConfig {
	
	@Bean
	BCryptPasswordEncoder encode() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	SecurityFilterChain conrigure(HttpSecurity http) throws Exception{
		
		
		return http.build();
	}

}