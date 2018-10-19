package com.hecheng.wechat.openplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hecheng.wechat.openplatform.entity.UserContactEntity;


/**
 * 用户绑定联系电话 数据层
 * 
 * @author QuietBaby
 *
 */
@Repository("userContactRepository")
public interface UserContactRepository
    extends
      JpaRepository<UserContactEntity, String>,
      JpaSpecificationExecutor<UserContactEntity> {

  /**
   * 通过user 对象查询
   * 
   * @param user
   * @return
   */

  @Query(nativeQuery = true, value = "select *  from user_contact where user_contact.user_id=:userId")
  public UserContactEntity findByUserId(@Param("userId") String userId);

  /**
   * 通过电话号码查询 用户联系方式
   * 
   * @param phone
   * @return
   */
  @Query(nativeQuery = true, value = "select *  from user_contact where user_contact.phone=:phone")
  public UserContactEntity findByPhone(@Param("phone") String phone);

}
