package com.hc.wechat.repository.internal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hc.wechat.common.enums.PlatFormType;
import com.hc.wechat.entity.ThirdPartUserEntity;

/**
 * 
 * @author hc
 *
 */
public interface ThirdPartUserDao {
  /**
   * 按条件分页查询
   * 
   * @param nickName 昵称
   * @param platFormType 平台类型
   * @param pageable
   * @return
   */

  Page<ThirdPartUserEntity> getByCondition(String nickName, PlatFormType platFormType,
      Pageable pageable);
}
