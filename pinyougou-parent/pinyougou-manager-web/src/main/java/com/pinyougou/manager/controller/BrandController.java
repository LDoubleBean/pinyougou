package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;


import entity.Result;
import entity.PageResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
@RestController
@RequestMapping("/brand")
public class BrandController {

	@Reference
	private BrandService service;

	/**
	 * 查询所有数据
	 * @return
	 */
	@RequestMapping("/findAll.do")
	public List<TbBrand> findAll() {
		List<TbBrand> tbBrands = service.findAll();
		return tbBrands;
	}

	/**
	 * 查询所有数据并分页显示
	 * @param pageNum 当前页码
	 * @param pageSize 每页数据条数
	 * @return
	 */
	@RequestMapping("findPage.do")
	public PageResult findPage(int pageNum , int pageSize) {
		PageResult page = service.findPage(pageNum, pageSize);
		return page;
	}

	/**
	 * 添加数据
	 * @param brand
	 * @return
	 */
	@RequestMapping("/save.do")
	public Result save(@RequestBody TbBrand brand) {
		try {
			service.save(brand);
			return new Result(true,"增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"增加失败");
		}
	}

	@RequestMapping("/findById.do")
	public TbBrand findById(Long id) {
		return  service.findById(id);
	}

	/**
	 * 根据Id修改数据
	 * @param brand
	 * @return
	 */
	@RequestMapping("/update.do")
	public Result updateById(@RequestBody TbBrand brand) {
		try {
			service.updateById(brand);
			return new Result(true,"增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"增加失败");
		}
	}

	/**
	 * 根据id批量删除
	 * @param ids
	 */
	@RequestMapping("/deleteByIds.do")
	public Result deleteByIds(Long[] ids){
		try {
			service.deleteByIds(ids);
			return new Result(true,"删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"删除失败");
		}
	}

	/**
	 * 根据条件查询，并分页
	 * @param brand
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("/findByCondition.do")
	public PageResult findByCondition(@RequestBody TbBrand brand, int pageNum, int pageSize) {
		PageResult page = service.findByCondition(brand, pageNum, pageSize);
		return page;
	}

	/**
	 * 按要求封装查询的所有数据
	 * @return
	 */
	@RequestMapping("/selectOptionList")
	public List<Map> selectOptionList() {
		return service.selectOptionList();
	}


}


