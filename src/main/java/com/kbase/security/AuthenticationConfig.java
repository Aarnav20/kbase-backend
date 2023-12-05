package com.kbase.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.kbase.auth.JwtAuthenticationEntryPoint;
import com.kbase.auth.JwtAuthenticationFilter;

@Configuration
public class AuthenticationConfig {
	
	@Autowired
	private JwtAuthenticationEntryPoint point;
	
	@Autowired
	private JwtAuthenticationFilter filter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
			http.csrf(csrf -> csrf.disable())
			.authorizeRequests()
			.requestMatchers("/auth/login","/login/create","/auth/userinfo","/auth/refresh").permitAll()
			.anyRequest().permitAll()
			.and().exceptionHandling(ex -> ex.authenticationEntryPoint(point))
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
			http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
			return http.build();
		}
}
