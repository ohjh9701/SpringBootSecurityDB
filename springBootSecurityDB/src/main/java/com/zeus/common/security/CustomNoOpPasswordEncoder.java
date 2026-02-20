package com.zeus.common.security;

import org.jspecify.annotations.Nullable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomNoOpPasswordEncoder implements PasswordEncoder{
	
	private final PasswordEncoder passwordEncoder;
	 public CustomNoOpPasswordEncoder() {
	 this.passwordEncoder = new BCryptPasswordEncoder();
	 }

	@Override
	public @Nullable String encode(@Nullable CharSequence rawPassword) {
		log.info("before encode :" + rawPassword);
		return passwordEncoder.encode(rawPassword);
	}

	@Override
	public boolean matches(@Nullable CharSequence rawPassword, @Nullable String encodedPassword) {
		log.info("matches: " + rawPassword + ":" + encodedPassword);
		return passwordEncoder.matches(rawPassword, encodedPassword);
	}
}
