package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;

import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
@Service
public class BrandServiceImpl implements BrandService {

	@Autowired
	private TbBrandMapper mapper;

	/**
	 * 查询所有
	 *
	 * @return
	 */
	@Override
	public List<TbBrand> findAll() {
		return mapper.selectByExample(null);
	}

	/**
	 * 查询所有并分页
	 *
	 * @param pageNum  当前页码
	 * @param pageSize 每页个数
	 * @return
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbBrand> page = (Page<TbBrand>) mapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 添加数据
	 *
	 * @param brand
	 */
	@Override
	public void save(TbBrand brand) {
		mapper.insertSelective(brand);
	}

	/**
	 * 修改数据
	 *
	 * @param brand
	 */
	@Override
	public void updateById(TbBrand brand) {
		mapper.updateByPrimaryKey(brand);
	}

	/**
	 * 根据id查询数据
	 *
	 * @param id
	 * @return
	 */
	@Override
	public TbBrand findById(Long id) {
		return mapper.selectByPrimaryKey(id);
	}

	/**
	 * 根据id删除数据
	 *
	 * @param ids
	 */
	@Override
	public void deleteByIds(Long[] ids) {
		for (Long id : ids) {
			mapper.deleteByPrimaryKey(id);
		}
	}

	/**
	 * 根据条件查询，并进行分页
	 *
	 * @param brand    条件对象
	 * @param pageNum  当前页码
	 * @param pageSize 每页条数
	 * @return
	 */
	@Override
	public PageResult findByCondition(TbBrand brand, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		TbBrandExample example = new TbBrandExample();
		if (brand != null) {
			TbBrandExample.Criteria criteria = example.createCriteria();
			if (brand.getName() != null && brand.getName().length() > 0) {
				criteria.andNameLike("%" + brand.getName() + "%");
			}
			if (brand.getFirstChar() != null && brand.getFirstChar().length() > 0) {
				criteria.andFirstCharLike("%" + brand.getFirstChar() + "%");
			}
		}
		Page<TbBrand> page = (Page<TbBrand>) mapper.selectByExample(example);
		return new PageResult(page.getTotal(),page.getResult());
	}

	@Override
	public List<Map> selectOptionList() {
		List<Map> maps = mapper.selectOption();
		return maps;
	}


}
