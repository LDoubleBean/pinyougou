package com.pinyougou.search.service.impl;

import com.alibaba.druid.sql.visitor.functions.Lpad;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.*;

/**
 * @author Administrator
 */
@Service(timeout=5000)
public class SearchServiceImpl implements SearchService {

	@Autowired
	private SolrTemplate solrTemplate;

	@Autowired
	private RedisTemplate redisTemplate;

	@Override
	public Map searchItem(Map map) {
		Map resultMap = new HashMap();

		//处理关键字
		String keywords = (String) map.get("keywords");
		map.put("keywords",keywords.replace(" ", ""));

		//获取符合搜索内容的结果
		resultMap.putAll(searchResultList(map));

		//获取商品分类
		List<String> categoryList = getCategoryList(map);
		resultMap.put("categoryList",categoryList);

		//获取商品品牌和规格
		if ("".equals(map.get("category"))) {
			if (categoryList.size() > 0) {
				resultMap.putAll(getBrandAndSpecList(categoryList.get(0)));
			}
		} else {
			resultMap.putAll(getBrandAndSpecList((String) map.get("category")));
		}

		return resultMap;
	}

	/**
	 * 获取搜索结果，并高亮显示搜索字段
	 * @param map
	 * @return
	 */
	public Map searchResultList(Map map) {

		Map resultMap = new HashMap();

		//设置查询条件
		Criteria criteria = new Criteria("item_keywords").is(map.get("keywords"));
		HighlightQuery query = new SimpleHighlightQuery();

		//分类过滤
		if (!"".equals(map.get("category"))) {
			Criteria filterCriteria = new Criteria("item_category").is(map.get("category"));
			FilterQuery filterQuery = new SimpleFacetQuery(filterCriteria);
			query.addFilterQuery(filterQuery);
		}

		//品牌过滤
		if (!"".equals(map.get("brand"))) {
			Criteria filterCriteria = new Criteria("item_brand").is(map.get("brand"));
			FilterQuery filterQuery = new SimpleFacetQuery(filterCriteria);
			query.addFilterQuery(filterQuery);
		}

		//规格过滤
		if (map.get("spec") != null) {
			Map<String,String> specMap = (Map) map.get("spec");
			for (Map.Entry<String, String> entry : specMap.entrySet()) {
				Criteria filterCriteria = new Criteria("item_spec_"+entry.getKey()).is(entry.getValue());
				FilterQuery filterQuery = new SimpleFacetQuery(filterCriteria);
				query.addFilterQuery(filterQuery);
			}
		}

		//价格过滤
		if (!"".equals(map.get("price"))) {
			String price = (String) map.get("price");
			String[] prices = price.split("-");

			//设置价格下限
			if (!"0".equals(prices[0])) {
				Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(prices[0]);
				FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
				query.addFilterQuery(filterQuery);
			}

			//设置价格上限
			if (!"*".equals(prices[1])) {
				Criteria filterCriteria = new Criteria("item_price").lessThanEqual(prices[1]);
				FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
				query.addFilterQuery(filterQuery);
			}
		}

		//获取排序规则
		String sortRule = (String) map.get("sortRule");
		//获取排序字段
		String sortName = (String) map.get("sortName");
		if (!"".equals(sortName) && !"".equals(sortRule)) {
			//判断排序规则
			if ("ASC".equals(sortRule)) {
				//创建排序对象(排序规则，排序字段)
				Sort sort = new Sort(Sort.Direction.ASC,"item_"+sortName);
				query.addSort(sort);
			} else if ("DESC".equals(sortRule)) {
				Sort sort = new Sort(Sort.Direction.DESC,"item_"+sortName);
				query.addSort(sort);
			}
		}

		//分页显示
		Integer pageNum = (Integer) map.get("pageNum");
		Integer pageSize = (Integer) map.get("pageSize");

		if ("".equals(map.get("pageNum"))) {
			pageNum = 1;
		}
		if ("".equals(map.get("pageSize"))) {
			pageSize = 40;
		}

		//设置分页起始页
		query.setOffset((pageNum - 1) * pageSize);
		query.setRows(pageSize);

		//设置高亮格式
		HighlightOptions highlightOptions = new HighlightOptions();
		highlightOptions.setSimplePrefix("<em style='color:red'>");
		//设置高亮域
		highlightOptions.addField("item_title");
		highlightOptions.setSimplePostfix("</em>");
		//添加查询条件和高亮设置
		query.addCriteria(criteria);
		query.setHighlightOptions(highlightOptions);
		//获取高亮页
		HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(query,TbItem.class);
		//获取高亮对象集合
		List<HighlightEntry<TbItem>> highlightEntries = highlightPage.getHighlighted();
		for (HighlightEntry<TbItem> highlightEntry : highlightEntries) {
			//根据高亮对象获取Item对象
			TbItem item = highlightEntry.getEntity();
			if (highlightEntry.getHighlights().size() > 0 && highlightEntry.getHighlights().get(0).getSnipplets().size() > 0) {
				//获取设置了高亮的标题
				String highlightTitle = highlightEntry.getHighlights().get(0).getSnipplets().get(0);
				//将高亮标题设置到Item中
				item.setTitle(highlightTitle);
			}
		}

		resultMap.put("pageTotal",highlightPage.getTotalPages());
		resultMap.put("total",highlightPage.getTotalElements());
		resultMap.put("resultList",highlightPage.getContent());
		return resultMap;
	}

	/**
	 * 根据查询条件获取商品分类
	 * @param map
	 * @return
	 */
	public List<String> getCategoryList(Map map) {
		List<String> list = new ArrayList<>();
		//设置查询条件
		Criteria criteria = new Criteria("item_keywords").is(map.get("keywords"));
		Query query = new SimpleQuery();
		query.addCriteria(criteria);
		//设置分组条件
		GroupOptions groupOptions = new GroupOptions();
		groupOptions.addGroupByField("item_category");
		query.setGroupOptions(groupOptions);
		//获取分组页
		GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(query, TbItem.class);
		//获取分组结果（根据分类获取）
		GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");
		//获取分组对象
		Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
		for (GroupEntry<TbItem> groupEntry : groupEntries) {
			//获取分类
			String groupValue = groupEntry.getGroupValue();
			list.add(groupValue);
		}
		return list;
	}

	/**
	 * 根据模板ID查询品牌与规格列表
	 * @param categoryName
	 * @return
	 */
	public Map getBrandAndSpecList(String categoryName) {

		Map resultMap = new HashMap();

		Long itemId = (Long) redisTemplate.boundHashOps("itemCat").get(categoryName);
		if (itemId != null) {
			List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(itemId);
			resultMap.put("brandList",brandList);
			List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(itemId);
			resultMap.put("specList",specList);
		}

		return resultMap;
	}

	/**
	 * 导入数据到solr
	 * @param list
	 */
	@Override
	public void ImportItem(List list) {
		solrTemplate.saveBeans(list);
		solrTemplate.commit();
	}

	@Override
	public void deleteItemByIds(Long[] ids) {
		Criteria criteria = new Criteria("item_goodsid").in(Arrays.asList(ids));
		Query query = new SimpleQuery();
		query.addCriteria(criteria);
		solrTemplate.delete(query);
		solrTemplate.commit();
	}

}
