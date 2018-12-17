package com.pinyougou.manager.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 */

@RestController
@RequestMapping("/login")
public class loginController {

	@RequestMapping("/getLoginName")
	public Map<String,String> getLoginName () {
		String loginName = SecurityContextHolder.getContext().getAuthentication().getName();
		Map<String,String> map = new HashMap<>();
		map.put("loginName",loginName);
		return map;
	}

}
