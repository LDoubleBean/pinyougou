package com.pinyougou.solr;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * @author Administrator
 */
@Component
public class SolrUtil {

	@Autowired
	private TbItemMapper tbItemMapper;

	@Autowired
	private SolrTemplate solrTemplate;

	public void solrAdd() {
		TbItem item = new TbItem();
		item.setId(1L);
		item.setTitle("华为P20");
		item.setGoodsId(1L);
		item.setPrice(new BigDecimal(2999.00));
		item.setCategory("手机");
		item.setBrand("华为");
		item.setSeller("华为旗舰店");
		solrTemplate.saveBean(item);
	}

	public void saveDate() {
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo("1");
		List<TbItem> tbItems = tbItemMapper.selectByExample(example);
		System.out.println("----商品列表----");
		for (TbItem tbItem : tbItems) {
			System.out.println(tbItem.getTitle()+" "+tbItem.getBrand()+" "+tbItem.getPrice());
			String spec = tbItem.getSpec();
			Map<String,String> map = JSON.parseObject(spec, Map.class);
			tbItem.setSpecMap(map);
		}
		solrTemplate.saveBeans(tbItems);
		solrTemplate.commit();
		System.out.println("----结束----");

	}

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
		SolrUtil solrUtil = (SolrUtil) context.getBean("solrUtil");
		solrUtil.saveDate();
	}

}
