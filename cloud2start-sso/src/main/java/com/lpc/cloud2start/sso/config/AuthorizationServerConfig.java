package com.lpc.cloud2start.sso.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import com.alibaba.fastjson.JSONObject;

// 配置授权中心信息
@Configuration
@EnableAuthorizationServer // 开启认证授权中心
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	// accessToken有效期
	private int accessTokenValiditySeconds = 60; // 两小时
	private int refreshTokenValiditySeconds = 60 * 60 * 12 * 7; // 一个星期

	@Bean
	public MyUserdetailService myUserdetailService() {
		return new MyUserdetailService();
	}

	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//		 withClient appid
		clients.inMemory().withClient("lpc_client").secret(passwordEncoder().encode("lpc_secret"))
				.authorizedGrantTypes("password", "authorization_code", "implicit", "client_credentials",
						"refresh_token")
				.scopes("all").accessTokenValiditySeconds(accessTokenValiditySeconds)
				.refreshTokenValiditySeconds(refreshTokenValiditySeconds);
		// clients.jdbc(dataSource);
	}

	@Bean
	public TokenStore tokenStore() {
		return new InMemoryTokenStore(); // 使用内存中的 token store
	}

	// 定义了授权和令牌端点和令牌服务
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
		endpoints.authenticationManager(authenticationManager()).allowedTokenEndpointRequestMethods(HttpMethod.GET,
				HttpMethod.POST);
		// 以下是在调用刷新令牌服务的时候需要的认证管理和用户信息来源
		endpoints.authenticationManager(authenticationManager());
		endpoints.userDetailsService(myUserdetailService());
		endpoints.accessTokenConverter(accessTokenJwtConverter());
		endpoints.tokenStore(tokenStore());
	}

	// 在令牌端点上定义了安全约束
	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
		// 允许表单认证
		oauthServer.allowFormAuthenticationForClients();
		// 允许check_token访问
		oauthServer.checkTokenAccess("permitAll()");
	}

	@Bean
	AuthenticationManager authenticationManager() {
		AuthenticationManager authenticationManager = new AuthenticationManager() {
			public Authentication authenticate(Authentication authentication) throws AuthenticationException {
				return daoAuhthenticationProvider().authenticate(authentication);
			}
		};
		return authenticationManager;
	}

	@Bean
	public AuthenticationProvider daoAuhthenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(myUserdetailService());
		daoAuthenticationProvider.setHideUserNotFoundExceptions(false);
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		return daoAuthenticationProvider;
	}

	// 设置添加用户信息,正常应该从数据库中读取
//	@Bean
//	UserDetailsService userDetailsService() {
//		InMemoryUserDetailsManager userDetailsService = new InMemoryUserDetailsManager();
//		userDetailsService.createUser(User.withUsername("user_1").password(passwordEncoder().encode("123456"))
//				.authorities("ROLE_USER").build());
//		userDetailsService.createUser(User.withUsername("user_2").password(passwordEncoder().encode("123456"))
//				.authorities("ROLE_USER").build());
//		return userDetailsService;
//	}

	@Bean
	PasswordEncoder passwordEncoder() {
		// 加密方式
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return passwordEncoder;
	}

	@Bean
	public JwtAccessTokenConverter accessTokenJwtConverter() {
		JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter() {
			@Override
			public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
				// 这里的UserName是OpenId
				String username = authentication.getUserAuthentication().getName();
				// 得到用户名，去处理数据库可以拿到当前用户的信息和角色信息（需要传递到服务中用到的信息）
				final Map<String, Object> addToJWTInformation = new HashMap<>();
				JSONObject json = new JSONObject();
				json.put("userId", 1);
				json.put("userName", username);

				addToJWTInformation.put("localUser", json.toJSONString());
				((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(addToJWTInformation);
				OAuth2AccessToken enhancedToken = super.enhance(accessToken, authentication);
				return enhancedToken;
			}
		};
		accessTokenConverter.setSigningKey("mjxy-sign-key");// 测试用,资源服务使用相同的字符达到一个对称加密的效果
		return accessTokenConverter;
	}

	public static void main(String[] args) {
		String encode = new BCryptPasswordEncoder().encode("mjxy_secret2");
		System.out.println(encode);
	}
}
