package com.hc.wechat.repository.internal;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hc.wechat.entity.RoleEntity;


/**
 * 角色dao.
 * 
 * @author ly
 * @date 2017年8月21日 下午3:27:33
 * @version V1.0
 */
public interface RoleDao {

  /**
   * 条件分页查询.
   * 
   * @param params 条件
   * @param pageable 分页
   * @return Page<RoleEntity>
   */
  Page<RoleEntity> getByConditions(Map<String, Object> params, Pageable pageable);

  /**
   * 条件查询
   * 
   * @param params
   * @param pageable
   * @return
   */
  List<RoleEntity> findByConditions(Map<String, Object> params);
}
