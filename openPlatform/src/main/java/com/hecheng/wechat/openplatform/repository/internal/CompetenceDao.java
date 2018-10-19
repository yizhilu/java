
package com.hecheng.wechat.openplatform.repository.internal;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hecheng.wechat.openplatform.entity.CompetenceEntity;


/**
 * 权限dao接口.
 * 
 * @author ly
 * @date 2017年8月21日 下午6:03:50
 * @version V1.0
 */
public interface CompetenceDao {

  /**
   * 条件分页查询.
   * 
   * @param params 条件
   * @param pageable 分页
   * @return Page<CompetenceEntity>
   */
  Page<CompetenceEntity> getByConditions(Map<String, Object> params, Pageable pageable);
}
