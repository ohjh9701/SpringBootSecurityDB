package com.zeus.controller;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@MapperScan(basePackages = "com.zeus.mapper")
public class HomeController {

	@GetMapping("/")
	public String home(Model model) {
		log.info("방문을 환영합니다.");
		model.addAttribute("serverTime", "2026-02-19");
		return "home";
	}

}
