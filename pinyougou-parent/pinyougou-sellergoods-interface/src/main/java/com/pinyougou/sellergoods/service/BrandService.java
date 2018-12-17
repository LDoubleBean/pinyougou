package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
public interface BrandService {

	public abstract List<TbBrand> findAll();

	public abstract PageResult findPage(int pageNum, int pageSize);

	public abstract void save(TbBrand brand);

	public abstract void updateById(TbBrand brand);

	public abstract TbBrand findById(Long id);

	public abstract void deleteByIds(Long[] ids);

	public abstract PageResult findByCondition(TbBrand brand, int pageNum, int pageSize);

	public abstract List<Map> selectOptionList();

}
