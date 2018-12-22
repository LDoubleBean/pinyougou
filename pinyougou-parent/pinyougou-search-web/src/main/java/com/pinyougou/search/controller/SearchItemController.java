package com.pinyougou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.search.service.SearchService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author Administrator
 */
@RestController
@RequestMapping("/search")
public class SearchItemController {

	@Reference
	private SearchService searchService;

	@RequestMapping("/searchItem")
	public Map searchItem(@RequestBody Map map ) {
		return searchService.searchItem(map);
	}

}
