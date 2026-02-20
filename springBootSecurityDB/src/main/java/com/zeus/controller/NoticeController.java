package com.zeus.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Slf4j
@Controller
@RequestMapping("/notice")
public class NoticeController {
	
	@GetMapping("/list")
	public String list() {
		log.info("notice list : 모두가 접근 가능");
		return "notice/list";
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("register")
	public void getMethodName() {
		log.info("notice register : 로그인한 회원만 접근 가능");
	}
	
	

}
