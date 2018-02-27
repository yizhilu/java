package com.hc.solr.service.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hc.solr.repository.GoodsRepository;
import com.hc.solr.service.GoodsService;

@Service("goodsService")
public class GoodsServiceImpl implements GoodsService {
  @Autowired
  private GoodsRepository goodsRepository;
}
