package com.pinyougou.manager.controller;
import java.util.ArrayList;
import java.util.List;

import com.pinyougou.page.service.PageService;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.SearchService;
import com.pinyougou.sellergoods.service.GoodsDescService;
import com.pinyougou.sellergoods.service.GoodsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;


import entity.PageResult;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;

	@Reference
	private GoodsDescService goodsDescService;

	@Reference
	private SearchService searchService;

	@Reference
	private PageService pageService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbGoods goods){
		try {
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param ids
	 * @param status
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(Long[] ids, String status){
		List<TbGoods> list = new ArrayList<>();
		try {
			for (Long id : ids) {
				TbGoods tbGoods = goodsService.findOne(id);
				tbGoods.setAuditStatus(status);
				goodsService.update(tbGoods);

			}
			//如果状态上架的数据，则添加到solr库中
			if ("1".equals(status)) {
				List<TbItem> tbItems = goodsDescService.findTbItemByGoodsIdAndStatus(ids, status);
				searchService.ImportItem(tbItems);
				for (Long id : ids) {
					pageService.createHtmlPage(id);
				}
			}



			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbGoods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			goodsService.delete(ids);
			searchService.deleteItemByIds(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param goods
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		return goodsService.findPage(goods, page, rows);		
	}

	/**
	 * 创建静态页面
	 * @param goodsId
	 */
	@RequestMapping("/createHtmlPage")
	public void createHtmlPage(Long goodsId) {
		pageService.createHtmlPage(goodsId);
	}
}
