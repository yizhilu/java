package com.hc.solr.service;

import java.util.Map;

import org.springframework.data.domain.Pageable;

import com.hc.solr.entity.GoodsEntity;

public interface SolrService {
  /**
   * 向索引库中添加索引
   * 
   * @param goods
   */
  public void addDocument(GoodsEntity goods);

  /**
   * 从索引库中删除索引
   * 
   * @param documentId
   */
  public void delDocumentById(String documentId);

  /**
   * 删除符合query的索引
   * 
   * @param query
   */
  public void delDocumentByQuery(String query);

  /**
   * 查询
   * 
   * @param keyWord 关键字
   * @param map 搜索条件
   * @param orderByFiled 排序字段
   * @param ascOrDesc 升序降序
   * @param pageable 分页
   * @return
   */
  public Map<String, Object> query(String keyWord, Map<String, Object> map, String orderByFiled,
      String ascOrDesc, Pageable pageable);
}
