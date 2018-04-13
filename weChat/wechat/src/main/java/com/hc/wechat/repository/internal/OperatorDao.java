
package com.hc.wechat.repository.internal;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hc.wechat.entity.OperatorEntity;

/**
 * @author ly
 * @date 2017年8月16日 下午5:35:17
 * @version V1.0
 */
public interface OperatorDao {

  /**
   * 条件分页查询后台用户.
   * 
   * @param params 条件
   * @param pageable 分页
   * @return Page<OperatorEntity>
   */
  Page<OperatorEntity> getByConditions(Map<String, Object> params, Pageable pageable);
}
