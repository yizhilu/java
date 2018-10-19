package com.hecheng.wechat.openplatform.service.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hecheng.wechat.openplatform.common.enums.PlatFormType;
import com.hecheng.wechat.openplatform.common.enums.UseStatus;
import com.hecheng.wechat.openplatform.entity.ThirdPartUserEntity;
import com.hecheng.wechat.openplatform.entity.UserEntity;
import com.hecheng.wechat.openplatform.repository.ThirdPartUserRepository;
import com.hecheng.wechat.openplatform.repository.internal.ThirdPartUserDao;
import com.hecheng.wechat.openplatform.service.ThirdPartUserService;
import com.hecheng.wechat.openplatform.service.UserService;

/**
 * 
 * @author 作者 hc:
 */
@Service("thirdPartUserService")
public class ThirdPartUserServiceImpl implements ThirdPartUserService {
  private static final Logger LOG = LoggerFactory.getLogger(ThirdPartUserServiceImpl.class);
  @Autowired
  private ThirdPartUserRepository thirdPartUserRepository;
  @Autowired
  private ThirdPartUserDao thirdPartUserDao;
  @Autowired
  private UserService userService;

  @Override
  public ThirdPartUserEntity findByUnionIdAndPlatFormType(String unionId,
      PlatFormType platFormType) {
    Validate.notBlank("UnionId", "unionId 不能为空");
    Validate.notNull("platFormType", "platFormType 不能为空");
    return thirdPartUserRepository.findByUnionIdAndPlatFormType(unionId, platFormType);
  }

  @Override
  @Transactional
  public ThirdPartUserEntity weChatCreate(ThirdPartUserEntity thirdPartUser) {
    // 1.验证 三方信息是否合法
    Validate.notNull(thirdPartUser, "创建第三方用户时 ，第三方用户信息不能为空");
    String openId = thirdPartUser.getOpenId();
    Validate.notBlank(openId, "创建第三方用户时 ，openId 不能为空");
    String nickName = thirdPartUser.getNickName();
    Validate.notBlank(nickName, "创建第三方用户时 ，nickName 不能为空");
    PlatFormType platFormType = thirdPartUser.getPlatFormType();
    Validate.notNull(platFormType, "创建第三方用户时 ，第三方用户平台信息不能为空");
    ThirdPartUserEntity result = thirdPartUserRepository.save(thirdPartUser);
    // 2.创建 user 并和ThirdPartUserEntity 建立关系
    List<ThirdPartUserEntity> thirdPartUsers = new ArrayList<>();
    thirdPartUsers.add(result);
    UserEntity user = new UserEntity();
    user.setNickName(result.getNickName());
    user.setCity(result.getCity());
    user.setCover(result.getCover());
    user.setCreateDate(new Date());
    user.setSex(result.getSex());
    user.setStatus(UseStatus.STATUS_NORMAL);
    user.setThirdPartUsers(thirdPartUsers);
    result.setUser(userService.create(user));
    // TODO 初始化用户的配置信息
    return thirdPartUserRepository.saveAndFlush(result);
  }

  @Override
  @Transactional
  public void weChatModify(ThirdPartUserEntity thirdPartUser) {
    Validate.notNull(thirdPartUser, "更新第三方用户信息时 ，第三方用户信息不能为空");
    Validate.notBlank(thirdPartUser.getId(), "更新第三方用户信息时 ，第三方用户id不能为空");
    ThirdPartUserEntity old = thirdPartUserRepository.findOne(thirdPartUser.getId());
    String openId = thirdPartUser.getOpenId();
    Validate.notBlank(openId, "更新第三方用户信息时 ，openId 不能为空");
    old.setOpenId(openId);
    String nickName = thirdPartUser.getNickName();
    Validate.notBlank(nickName, "创建第三方用户时 ，nickName 不能为空");
    old.setNickName(nickName);
    // 因为存在 扫码绑定，扫码通知进入的用户 这类的用户可能没有关注公众号 没有unionid 所以 不能要求 unionid不能为空
    // String unionId = thirdPartUser.getUnionId();
    // Validate.notBlank(unionId, "更新第三方用户信息时，unionId 不能为空");
    PlatFormType platFormType = thirdPartUser.getPlatFormType();
    Validate.notNull(platFormType, "更新第三方用户信息时 ，第三方用户平台信息不能为空");
    old.setPlatFormType(platFormType);
    old.setSex(thirdPartUser.getSex());
    old.setCover(thirdPartUser.getCover());
    old.setCity(thirdPartUser.getCity());
    old.setLastLoginDate(new Date());
    old.setUnionId(thirdPartUser.getUnionId());
    thirdPartUserRepository.saveAndFlush(thirdPartUser);
  }

  @Override
  public ThirdPartUserEntity findByOpenIdAndPlatFormType(String openid, PlatFormType platFormType) {
    Validate.notBlank("openid", "openid 不能为空");
    Validate.notNull("platFormType", "platFormType 不能为空");
    LOG.info("openid=%s,platFormType=%s", openid, platFormType);
    return thirdPartUserRepository.findByOpenIdAndPlatFormType(openid, platFormType);
  }

  @Override
  public ThirdPartUserEntity findById(String id) {
    Validate.notBlank(id, "查询thirdPartUser用户时 id不能为空");
    return thirdPartUserRepository.findOne(id);
  }

  @Override
  public List<ThirdPartUserEntity> findByUser(UserEntity user) {
    Validate.notNull(user, "user不能为空");
    return thirdPartUserRepository.findByUser(user);
  }

  @Override
  public Page<ThirdPartUserEntity> getByConditions(String nickName, PlatFormType platFormType,
      Pageable pageable) {
    return thirdPartUserDao.getByCondition(nickName, platFormType, pageable);
  }
}
