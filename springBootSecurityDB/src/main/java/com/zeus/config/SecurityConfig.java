package com.zeus.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.zeus.common.security.CustomAccessDeniedHandler;
import com.zeus.common.security.CustomLoginSuccessHandler;
import com.zeus.common.security.CustomNoOpPasswordEncoder;
import com.zeus.common.security.CustomUserDetailsService;

import jakarta.servlet.DispatcherType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled=true, securedEnabled=true)
public class SecurityConfig {
	
	@Autowired
	DataSource dataSource;

	@Bean
	SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		log.info("security config 셋팅의 시작");

		// 1.csrf토큰 비활성화
		httpSecurity.csrf((csrf) -> csrf.disable());

		// 2.접근제한 정책
		// URI 패턴으로 접근 제한을 설정
		httpSecurity.authorizeHttpRequests(auth -> auth
				.dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
				.requestMatchers("/accessError", "/login").permitAll()
				//.requestMatchers("/board/list").permitAll() // 게시판 목록: 누구나
				//.requestMatchers("/board/register").hasRole("MEMBER") // 게시판 등록: 회원만
				//.requestMatchers("/notice/list").permitAll() // 공지사항 목록: 누구나
				//.requestMatchers("/notice/register").hasRole("ADMIN") // 공지사항 등록: 관리자만
				.anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
		);
		
		// 접근 거부 처리자에 대한 페이지 이동 URI를 지정
		// 페이지 포워딩:서버내부에서 요청을 사용자가 지정한 /accessError경로로 포워딩(Forwarding)처리한다.
		//(브라우저 주소창은 바뀌지 않고 서버 내부에서 해당 페이지를 보여준다.
		//httpSecurity.exceptionHandling(exception -> exception.accessDeniedPage("/accessError"));
		
		// 등록한 CustomAccessDeniedHandler.java를 접근 거부 처리자로 지정한다.
		httpSecurity.exceptionHandling(exception -> exception.accessDeniedHandler(createAccessDeniedHandler()));

		// 3.기본폼 로그인 활성화
		//httpSecurity.formLogin(Customizer.withDefaults());
		httpSecurity.formLogin(form -> form
		        .loginPage("/login")            // 커스텀 로그인 페이지 URL
		        .loginProcessingUrl("/login")   // loginForm action URL
		        //.defaultSuccessUrl("/board/list") //성공시 기본 화면 설정
		        .successHandler(createAuthenticationSuccessHandler())
		        .permitAll()                        // 로그인 페이지는 누구나 접근 가능해야 함
		    );
		
		// 4. 로그아웃 처리를 위한 URI를 지정하고, 로그아웃 후에 세션을 무효화
		httpSecurity.logout(logout -> logout
		        .logoutUrl("/logout")                // 로그아웃을 처리할 URL (기본값: /logout)
		        .logoutSuccessUrl("/login")   // 로그아웃 성공 시 이동할 페이지
		        .invalidateHttpSession(true)         // HTTP 세션 무효화 (기본값: true)
		        .deleteCookies("JSESSIONID", "remember-me") // 로그아웃 시 관련 쿠키 삭제
		        .permitAll()                         // 로그아웃 요청은 누구나 접근 가능해야 함
		    );
		
		// 5. 자동 로그인 기능 설정
		httpSecurity.rememberMe(remember -> remember
				.key("zeus")
				.tokenRepository(createJDBCRepository())
				.tokenValiditySeconds(60*60*24)
				);

		return httpSecurity.build();
	}
	


	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(createUserDetailsService()).passwordEncoder(createPasswordEncoder());
	}
	
	// 스프링 시큐리티의 UserDetailsService를 구현한 클래스를 빈으로 등록한다.
	@Bean
	public UserDetailsService createUserDetailsService() {
	return new CustomUserDetailsService();
	}
	// 사용자가 정의한 비번 암호화 처리기를 빈으로 등록한다.
	@Bean
	public PasswordEncoder createPasswordEncoder() {
	return new CustomNoOpPasswordEncoder();
	}
	
	// 접근거부시 예외처리 설정을 클래스로 이동한다.
	@Bean
	public AccessDeniedHandler createAccessDeniedHandler() {
	return new CustomAccessDeniedHandler();
	}
	
	@Bean
	public AuthenticationSuccessHandler createAuthenticationSuccessHandler() {
		return new CustomLoginSuccessHandler();

	}
	
	private PersistentTokenRepository createJDBCRepository() {
		JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
		repo.setDataSource(dataSource);
		return repo;

	}
}