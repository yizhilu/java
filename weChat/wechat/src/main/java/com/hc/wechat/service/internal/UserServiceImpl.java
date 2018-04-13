package com.hc.wechat.service.internal;

import java.util.Calendar;
import java.util.Date;

import javax.transaction.Transactional;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hc.wechat.common.enums.UseStatus;
import com.hc.wechat.entity.UserEntity;
import com.hc.wechat.repository.UserRepository;
import com.hc.wechat.repository.internal.UserDao;
import com.hc.wechat.service.UserService;

/**
 * 
 * @author 作者 hc:
 */
@Service("userService")
public class UserServiceImpl implements UserService {

  private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserDao userDao;

  @Override
  public UserEntity findById(String id) {
    Validate.notBlank(id, "查找user时，Id 不能为空");
    return userRepository.findOne(id);
  }

  @Override
  @Transactional
  public UserEntity create(UserEntity user) {
    Validate.notNull(user, "创建用户时，用户信息不能为空");
    Validate.notBlank(user.getNickName(), "昵称不能为空");
    user.setLastLoginDate(Calendar.getInstance().getTime());
    user.setCreateDate(Calendar.getInstance().getTime());
    user.setStatus(UseStatus.STATUS_NORMAL);
    return userRepository.save(user);
  }

  @Override
  @Transactional
  public UserEntity modify(UserEntity user) {
    Validate.notNull(user, "修改用户时，用户信息不能为空");
    Validate.notBlank(user.getId(), "修改用户时，用户信息id不能为空");
    Validate.notBlank(user.getNickName(), "昵称不能为空");
    UserEntity oldUser = findById(user.getId());
    oldUser.setNickName(user.getNickName());
    oldUser.setCity(user.getCity());
    oldUser.setNickName(user.getNickName());
    oldUser.setCover(user.getCover());
    oldUser.setSex(user.getSex());
    oldUser.setLastLoginDate(new Date());
    return userRepository.saveAndFlush(oldUser);
  }

  @Override
  @Transactional
  public void bindPhone(String userId, String telephone) {
    LOG.info("userId=%s,telephone=%s", userId, telephone);
    Validate.notBlank(userId, "用户id不能为空");
    Validate.notBlank(telephone, "绑定电话不能为空");
    UserEntity user = findById(userId);
    Validate.notNull(user, "用户未登录");
    Validate.isTrue(userRepository.findByTelephone(telephone) != null, "%s手机号已绑定过，不能再次绑定",
        telephone);
    user.setTelephone(telephone);
    user.setModifyDate(new Date());
    userRepository.saveAndFlush(user);
  }

  @Override
  public UserEntity findByIdFetch(String id) {
    Validate.notBlank(id, "用户id不能为空");
    return userRepository.findByIdFetch(id);
  }

  @Override
  public Page<UserEntity> getByConditions(String nickName, UseStatus status, String telephone,
      Pageable pageable) {

    return userDao.getByConditions(nickName, status, telephone, pageable);
  }
}
