package com.lpc.cloud2start.sso.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Bean
	public MyUserdetailService myUserdetailService() {
		return new MyUserdetailService();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//		auth.inMemoryAuthentication().withUser("lpc")
//				.password("$2a$10$KoASwjxNyE.u8IgpbjiHyOe9o3zE1jotsxndJp4ZN8buKy2DtTpjK").roles("ADMIN").and()
//				.withUser("zhangsan").password("zhangs").roles("ADMIN").and()
//				.passwordEncoder(new BCryptPasswordEncoder());
		// BCryptPasswordEncoder,LdapShaPasswordEncoder,Md4PasswordEncoder,MessageDigestPasswordEncoder("MD5")
		// 默认提供了很多种加密方式
		auth.userDetailsService(myUserdetailService()).passwordEncoder(new BCryptPasswordEncoder());
	}

	@Bean
	@Override
	protected AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable(); // 关闭跨站检测
		http.authorizeRequests().anyRequest().fullyAuthenticated();
		http.formLogin().loginPage("/login").failureUrl("/login").defaultSuccessUrl("/index").permitAll();
		http.requestMatchers().anyRequest().and().authorizeRequests().antMatchers("/oauth/**").permitAll();
		http.logout().permitAll();
	}

	public static void main(String[] args) {
		BCryptPasswordEncoder encode = new BCryptPasswordEncoder();
		// $2a$10$KoASwjxNyE.u8IgpbjiHyOe9o3zE1jotsxndJp4ZN8buKy2DtTpjK
		String string = encode.encode("123456").toString();
		System.out.println(string);
	}

}
