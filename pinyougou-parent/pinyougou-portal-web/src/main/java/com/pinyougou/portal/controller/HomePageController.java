package com.pinyougou.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.pojo.TbContent;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Administrator
 */
@RestController
@RequestMapping("/homePage")
public class HomePageController {

	@Reference
	private ContentService contentService;

	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	@RequestMapping("/findCarouselById")
	public List<TbContent> findCarouselById(Long id) {
		return contentService.findCarouselById(id);
	}

}
