/**
 * 
 */
package com.hc.security.repository.internal;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hc.security.entity.CompetenceEntity;



public interface CompetenceDao {

  /**
   * 条件分页查询.
   * 
   * @param params 条件
   * @param pageable 分页
   * @return Page<CompetenceEntity>
   */
  Page<CompetenceEntity> getByCondition(Map<String, Object> params, Pageable pageable);
}
