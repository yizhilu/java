package com.hecheng.wechat.openplatform.service.internal;

import java.util.Calendar;
import java.util.Date;

import javax.transaction.Transactional;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hecheng.wechat.openplatform.common.enums.UseStatus;
import com.hecheng.wechat.openplatform.entity.UserEntity;
import com.hecheng.wechat.openplatform.repository.UserRepository;
import com.hecheng.wechat.openplatform.repository.internal.UserDao;
import com.hecheng.wechat.openplatform.service.UserService;

/**
 * 
 * @author 作者 hc:
 */
@Service("userService")
public class UserServiceImpl implements UserService {

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
  public UserEntity findByPhone(String phone) {
    Validate.notBlank(phone, "用户手机号不能为空");
    return userRepository.findByTelephone(phone);
  }

  @Override
  @Transactional
  public UserEntity create(UserEntity user) {
    Validate.notNull(user, "创建用户时，用户信息不能为空");
    Validate.notBlank(user.getNickName(), "昵称不能为空");
    user.setLastLoginDate(Calendar.getInstance().getTime());
    user.setCreateDate(Calendar.getInstance().getTime());
    user.setStatus(UseStatus.STATUS_NORMAL);
    user = userRepository.save(user);
    return user;
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
    oldUser.setCover(user.getCover());
    oldUser.setSex(user.getSex());
    oldUser.setLastLoginDate(new Date());
    return userRepository.saveAndFlush(oldUser);
  }

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
