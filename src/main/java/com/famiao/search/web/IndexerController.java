package com.famiao.search.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.famiao.search.database.sql.DynamicSqlResolver;
import com.famiao.search.database.sql.GSearchSqlExecution;
import com.famiao.search.database.sql.template.DynamicSqlTemplate;
import com.famiao.search.index.Indexer;

/**
 * 
 * 构建索引indexer
 * @author zengbin
 * @since 2019-06-30 18:39
 */
@RestController
@RequestMapping("/indexer")
public class IndexerController {

	@Autowired
	private Indexer indexer;
	@Autowired
	private DynamicSqlResolver dynamicSqlResolver;
	@Autowired
	private GSearchSqlExecution gSearchSqlExecution;

	private static final Logger logger = LoggerFactory.getLogger(IndexerController.class);

	@PostMapping("/create")
	public Map<String, Object> create(@RequestParam(value = "sqlId", required = false) Integer sqlId) throws Exception {
		List<DynamicSqlTemplate> tempList = dynamicSqlResolver.resolve();
		Map<String, Object> map = new HashMap<>();
		for (DynamicSqlTemplate temp : tempList) {
			logger.warn("the build index doc sql id={}", temp.getId());
			map.put("sqlId", temp.getSql());
			String executeSql = temp.getSql(); //String.format(temp.getSql(), temp.getModifyTime());
			if (null == sqlId) {
				this.gSearchSqlExecution.execute(executeSql, temp.getIndex()); //如果sqlId不传入就构建全文索引
			} else {
				if (sqlId.equals(temp.getId())) {
					this.gSearchSqlExecution.execute(executeSql, temp.getIndex()); //如果sqlId传入，就构建指定的sqlId索引
				}
			}
		}
		return map;
	}

	@PostMapping("/update")
	public String update(@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "productName", required = false) String productName,
			@RequestParam(value = "firstCategoryName", required = false) String firstCategoryName,
			@RequestParam(value = "secondCategoryName", required = false) String secondCategoryName,
			@RequestParam(value = "thirdCategoryName", required = false) String thirdCategoryName,
			@RequestParam(value = "brandName", required = false) String brandName) throws IOException {
		Map<String, Object> paramMap = new HashMap<>();
		if (StringUtils.isNotBlank(productName)) {
			paramMap.put("productName", productName);
		}
		if (StringUtils.isNotBlank(firstCategoryName)) {
			paramMap.put("firstCategoryName", firstCategoryName);
		}
		if (StringUtils.isNotBlank(secondCategoryName)) {
			paramMap.put("secondCategoryName", secondCategoryName);
		}
		if (StringUtils.isNotBlank(thirdCategoryName)) {
			paramMap.put("thirdCategoryName", thirdCategoryName);
		}
		if (StringUtils.isNotBlank(brandName)) {
			paramMap.put("brandName", brandName);
		}
		this.indexer.update(null, id, paramMap);
		return "success";
	}

	@DeleteMapping("/delete")
	public String deleteOrder(@RequestParam(value = "id", required = true) String id) throws IOException {
		this.indexer.delete(null, id);
		return "success";
	}
}