package com.hc.wechat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.hc.wechat.common.enums.PlatFormType;
import com.hc.wechat.entity.ThirdPartUserEntity;
import com.hc.wechat.entity.UserEntity;


/**
 * @author hc
 */
@Repository("thirdPartUserRepository")
public interface ThirdPartUserRepository
    extends
      JpaRepository<ThirdPartUserEntity, String>,
      JpaSpecificationExecutor<ThirdPartUserEntity> {
  /**
   * 按unionId 和 platFormType平台类型 查找 ThirdPartUserEntity
   * 
   * @param unionId
   * @param platFormType
   * @return
   */
  ThirdPartUserEntity findByUnionIdAndPlatFormType(String unionId, PlatFormType platFormType);

  /**
   * 按openid 和 platFormType平台类型 查找 ThirdPartUserEntity
   * 
   * @param unionId
   * @param platFormType
   * @return
   */
  ThirdPartUserEntity findByOpenIdAndPlatFormType(String openid, PlatFormType platFormType);

  /**
   * 按unionid 查找第三方用户
   * 
   * @param unionId
   * @return
   */
  List<ThirdPartUserEntity> findByUnionId(String unionId);

  /**
   * 查询用户的第三方用户
   * 
   * @param user
   * @return
   */
  List<ThirdPartUserEntity> findByUser(UserEntity user);
}
