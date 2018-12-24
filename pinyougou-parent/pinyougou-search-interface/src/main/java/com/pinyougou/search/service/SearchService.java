package com.pinyougou.search.service;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
public interface SearchService {

	public abstract Map searchItem(Map map);

	public abstract void ImportItem(List list);

	public abstract void deleteItemByIds(Long[] ids);

}
