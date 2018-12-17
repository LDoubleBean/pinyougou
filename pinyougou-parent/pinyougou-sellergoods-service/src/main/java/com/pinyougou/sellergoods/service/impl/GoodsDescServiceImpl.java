package com.pinyougou.sellergoods.service.impl;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsDescService;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsDescExample.Criteria;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class GoodsDescServiceImpl implements GoodsDescService {

	@Autowired
	private TbGoodsMapper goodsMapper;

	@Autowired
	private TbGoodsDescMapper goodsDescMapper;

	@Autowired
	private TbItemMapper itemMapper;

	@Autowired
	private TbItemCatMapper itemCatMapper;

	@Autowired
	private TbBrandMapper brandMapper;

	@Autowired
	private TbSellerMapper sellerMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoodsDesc> findAll() {
		return goodsDescMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoodsDesc> page=   (Page<TbGoodsDesc>) goodsDescMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {

		TbGoods tbGoods = goods.getTbGoods();
		tbGoods.setAuditStatus("0");
		goodsMapper.insert(tbGoods);

		TbGoodsDesc tbGoodsDesc = goods.getTbGoodsDesc();
		tbGoodsDesc.setGoodsId(tbGoods.getId());
		goodsDescMapper.insert(tbGoodsDesc);

		if ("1".equals(tbGoods.getIsEnableSpec())) {
			List<TbItem> itemList = goods.getItemList();
			for (TbItem tbItem : itemList) {
					String title = tbGoods.getGoodsName();
					Map<String,Object> map = JSON.parseObject(tbItem.getSpec(), Map.class);
					Set<String> keySet = map.keySet();
					for (String key : keySet) {
						title += " "+map.get(key);
					}
					tbItem.setTitle(title);
					setItemValue(tbGoods,tbGoodsDesc,tbItem);

			}
		} else {
			System.out.println(".....");
			TbItem tbItem = new TbItem();
			tbItem.setTitle(tbGoods.getGoodsName());
			setItemValue(tbGoods,tbGoodsDesc,tbItem);
		}
	}


	private void setItemValue(TbGoods tbGoods, TbGoodsDesc tbGoodsDesc, TbItem tbItem) {
		List<Map> maps = JSON.parseArray(tbGoodsDesc.getItemImages(), Map.class);
		tbItem.setImage((String) maps.get(0).get("url"));
		tbItem.setCategoryid(tbGoods.getCategory3Id());
		tbItem.setCreateTime(new Date());
		tbItem.setUpdateTime(new Date());
		tbItem.setGoodsId(tbGoods.getId());
		tbItem.setSellerId(tbGoods.getSellerId());
		//设置分类名
		TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
		tbItem.setCategory(tbItemCat.getName());
		//设置品牌
		TbBrand tbBrand = brandMapper.selectByPrimaryKey(tbGoods.getBrandId());
		tbItem.setBrand(tbBrand.getName());
		//设置店铺名
		TbSeller tbSeller = sellerMapper.selectByPrimaryKey(tbGoods.getSellerId());
		tbItem.setSeller(tbSeller.getNickName());
		itemMapper.insert(tbItem);
	}
	
	/**
	 * 修改
	 */
	@Override
	public void update(TbGoodsDesc goodsDesc){
		goodsDescMapper.updateByPrimaryKey(goodsDesc);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		Goods goods = new Goods();
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		goods.setTbGoods(tbGoods);
		goods.setTbGoodsDesc(tbGoodsDesc);
		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			goodsDescMapper.deleteByPrimaryKey(id);
		}
	}
	
	
		@Override
	public PageResult findPage(TbGoodsDesc goodsDesc, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsDescExample example=new TbGoodsDescExample();
		Criteria criteria = example.createCriteria();
		
		if(goodsDesc!=null){			
						if(goodsDesc.getIntroduction()!=null && goodsDesc.getIntroduction().length()>0){
				criteria.andIntroductionLike("%"+goodsDesc.getIntroduction()+"%");
			}
			if(goodsDesc.getSpecificationItems()!=null && goodsDesc.getSpecificationItems().length()>0){
				criteria.andSpecificationItemsLike("%"+goodsDesc.getSpecificationItems()+"%");
			}
			if(goodsDesc.getCustomAttributeItems()!=null && goodsDesc.getCustomAttributeItems().length()>0){
				criteria.andCustomAttributeItemsLike("%"+goodsDesc.getCustomAttributeItems()+"%");
			}
			if(goodsDesc.getItemImages()!=null && goodsDesc.getItemImages().length()>0){
				criteria.andItemImagesLike("%"+goodsDesc.getItemImages()+"%");
			}
			if(goodsDesc.getPackageList()!=null && goodsDesc.getPackageList().length()>0){
				criteria.andPackageListLike("%"+goodsDesc.getPackageList()+"%");
			}
			if(goodsDesc.getSaleService()!=null && goodsDesc.getSaleService().length()>0){
				criteria.andSaleServiceLike("%"+goodsDesc.getSaleService()+"%");
			}
	
		}
		
		Page<TbGoodsDesc> page= (Page<TbGoodsDesc>)goodsDescMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	
}
