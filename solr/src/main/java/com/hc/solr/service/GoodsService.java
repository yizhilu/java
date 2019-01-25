package com.hc.solr.service;

import com.hc.solr.entity.GoodsEntity;

public interface GoodsService {
  GoodsEntity add(GoodsEntity goods);

  /**
   * 
   * <p>
   * Title: update
   * </p>
   * Description:<pre> 修改</pre>
   * 
   * @author hecheng
   * @date 2018年12月27日
   * @param goods
   * @return
   */
  GoodsEntity updateStack(String goodsId, int stack);

  GoodsEntity subStack(String goodsId);
}
