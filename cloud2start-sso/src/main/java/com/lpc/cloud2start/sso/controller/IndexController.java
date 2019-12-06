package com.lpc.cloud2start.sso.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

	@RequestMapping("/index")
	public String index(Map<String, String> map) {
//		SecurityContext context = SecurityContextHolder.getContext();
//		Authentication authentication = context.getAuthentication();
//		Object principal = authentication.getPrincipal();
//		System.out.println(principal.toString());
		map.put("userName", "lpc");
		return "index";
	}

	@RequestMapping("/login")
	public String login(Map<String, String> map) {
		map.put("msg", "消息");
		return "login";
	}

}
