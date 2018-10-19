package com.hecheng.wechat.openplatform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.hecheng.wechat.openplatform.entity.ValidCodeEntity;

/**
 * 验证码 数据层
 * 
 * @author hc
 *
 */
@Repository("validcodeRepostory")
public interface ValidCodeRepository
    extends
      JpaRepository<ValidCodeEntity, String>,
      JpaSpecificationExecutor<ValidCodeEntity> {

  /**
   * 按接收者账号按时间降序查找
   * 
   * @param receiveCode
   * @return
   */
  List<ValidCodeEntity> findByReceiveCodeOrderByCreateDateDesc(String receiveCode);

}
