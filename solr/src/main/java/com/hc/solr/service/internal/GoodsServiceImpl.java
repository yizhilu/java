package com.hc.solr.service.internal;

import javax.transaction.Transactional;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hc.solr.entity.GoodsEntity;
import com.hc.solr.repository.primary.GoodsRepository;
import com.hc.solr.service.GoodsService;

@Service("goodsService")
public class GoodsServiceImpl implements GoodsService {
  @Autowired
  private GoodsRepository goodsRepository;

  @Override
  @Transactional
  public GoodsEntity add(GoodsEntity goods) {
    Validate.notNull(goods, "商品不能为空");
    return goodsRepository.save(goods);
  }

  @Override
  @Transactional
  public GoodsEntity updateStack(String goodsId, int stack) {
    Validate.notNull(goodsId, "商品不能为空");
    GoodsEntity oldGoods = goodsRepository.findOne(goodsId);
    Validate.notNull(oldGoods, "商品不能为空");
    oldGoods.setStack(stack);
    return goodsRepository.saveAndFlush(oldGoods);
  }

  @Override
  @Transactional
  public GoodsEntity subStack(String goodsId) {
    Validate.notNull(goodsId, "商品不能为空");
    GoodsEntity oldGoods = goodsRepository.findOne(goodsId);
    Validate.notNull(oldGoods, "商品不能为空");
    oldGoods.setStack(oldGoods.getStack() - 1);
    return goodsRepository.saveAndFlush(oldGoods);
  }
}
