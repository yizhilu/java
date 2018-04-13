package com.hc.wechat.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hc.wechat.common.enums.PlatFormType;
import com.hc.wechat.entity.ThirdPartUserEntity;
import com.hc.wechat.entity.UserEntity;

/**
 * 
 * @author 作者 hc:
 * @version 创建时间：2017年10月23日 下午4:40:26 类说明
 */
public interface ThirdPartUserService {

  /**
   * 按unionId platFormType平台类型 查找 ThirdPartUserEntity
   * 
   * @param unionId
   * @param platFormType
   * @return
   */
  ThirdPartUserEntity findByUnionIdAndPlatFormType(String unionId, PlatFormType platFormType);

  /**
   * 按openid platFormType平台类型 查找 ThirdPartUserEntity
   * 
   * @param unionId
   * @param platFormType
   * @return
   */
  ThirdPartUserEntity findByOpenIdAndPlatFormType(String openid, PlatFormType platFormType);

  /**
   * 微信创建第三方账号 如果 该账号unionid对应的user 不存在 则创建 否则与user建立关系
   * 
   * @param thirdPartUser
   * @return
   */
  ThirdPartUserEntity weChatCreate(ThirdPartUserEntity thirdPartUser);

  /**
   * 修改第三方用户信息
   * 
   * @param thirdPartUser
   */
  void weChatModify(ThirdPartUserEntity thirdPartUser);

  /**
   * 按id查找
   * 
   * @param id
   * @return
   */
  ThirdPartUserEntity findById(String id);

  /**
   * 
   * @param user
   * @return
   */
  List<ThirdPartUserEntity> findByUser(UserEntity user);

  /**
   * 按条件分页查询
   * 
   * @param nickName 昵称
   * @param platFormType 平台类型
   * @param pageable
   * @return
   */

  Page<ThirdPartUserEntity> getByConditions(String nickName, PlatFormType platFormType,
      Pageable pageable);
}
