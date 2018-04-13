package com.hc.wechat.service.internal;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hc.wechat.common.enums.UseStatus;
import com.hc.wechat.common.utils.StringValidateUtils;
import com.hc.wechat.entity.OperatorEntity;
import com.hc.wechat.repository.OperatorRepository;
import com.hc.wechat.repository.internal.OperatorDao;
import com.hc.wechat.service.OperatorService;

/**
 * 
 * @author hc
 *
 */

@Service("operatorService")
public class OperatorServiceImpl implements OperatorService {

  @Autowired
  private OperatorRepository operatorRepository;

  @Autowired
  private Md5PasswordEncoder passwordEncoder;

  @Autowired
  private OperatorDao operatorDao;



  @Override
  @Transactional
  public OperatorEntity create(OperatorEntity operator) {
    /*
     * 注册一个新的管理员，过程如下： 1、首先判断必须填写的信息是否都已经填写 2、检查数据库中是否存在重复信息 3、插入operator数据表
     * 4、返回添加后的结果信息，但是不包括管理员的任何关联信息
     */
    // 1、===================
    Validate.notNull(operator, "operator must not be null");
    this.validateBeforeCreate(operator);
    // 密码需要加密
    String encodePassword = passwordEncoder.encodePassword(operator.getPassword(), null);
    operator.setPassword(encodePassword);
    this.operatorRepository.saveAndFlush(operator);
    return operator;
  }

  @Override
  @Transactional
  public OperatorEntity modify(OperatorEntity operator) {
    OperatorEntity operatorEntity = findById(operator.getId());
    // 验证对象
    Validate.notNull(operatorEntity, "id为" + operator.getId() + "的后台用户对象不存在！");
    operatorEntity.setName(operator.getName());
    if (StringUtils.isNotBlank(operator.getPassword())) {
      operatorEntity.setPassword(operator.getPassword());
    }
    operatorEntity.setModifier(operator.getModifier());
    // 验证字段
    validBeforeModify(operatorEntity);
    // 密码加密存储
    if (StringUtils.isNotBlank(operator.getPassword())) {
      operatorEntity.setPassword(passwordEncoder.encodePassword(operator.getPassword(), null));
    }
    operatorEntity.setModifyDate(new Date());
    // 更新操作
    operatorRepository.saveAndFlush(operatorEntity);
    return operatorEntity;
  }

  @Override
  public OperatorEntity findByAccountAndStatus(String account, UseStatus status) {
    Validate.notBlank(account, "account must be input");
    Validate.notNull(status, "status must not be null");

    OperatorEntity result = this.operatorRepository.findByAccountAndStatus(account, status);
    if (result == null) {
      return null;
    }
    return result;
  }


  @Override
  public OperatorEntity findById(String id) {
    Validate.notBlank(id, "id不能为空！");
    OperatorEntity operatorEntity = operatorRepository.findOne(id);
    if (operatorEntity != null) {
      return operatorEntity;
    }
    return null;
  }


  @Override
  public Page<OperatorEntity> getByConditions(Map<String, Object> params, Pageable pageable) {
    Map<String, Object> map = new HashMap<String, Object>();
    // 查询状态-账号
    if (params.get("account") != null) {
      map.put("account", String.valueOf(params.get("account")));
    }
    // 查询状态-姓名
    if (params.get("name") != null) {
      map.put("name", String.valueOf(params.get("name")));
    }
    // 查询状态-状态
    if (params.get("status") != null) {
      map.put("status", params.get("status"));
    }
    // 查询状态-起始时间
    if (params.get("startCreateDate") != null) {
      map.put("startCreateDate", params.get("startCreateDate"));
    }
    // 查询状态-结束时间
    if (params.get("endCreateDate") != null) {
      map.put("endCreateDate", params.get("endCreateDate"));
    }

    Page<OperatorEntity> list = operatorDao.getByConditions(params, pageable);
    return list;
  }

  @Override
  @Transactional
  public OperatorEntity diableOrUndisable(String id, Boolean flag, OperatorEntity operator) {
    if (StringUtils.isEmpty(id) || flag == null) {
      throw new IllegalArgumentException("参数错误！");
    }
    OperatorEntity operatorEntity = findById(id);
    Validate.notNull(operatorEntity, "id为%s的对象不存在！", id);
    if (flag == true) {
      // 启用
      operatorEntity.setStatus(UseStatus.STATUS_NORMAL);
    } else if (flag == false) {
      // 禁用
      operatorEntity.setStatus(UseStatus.STATUS_DISABLE);
    }
    operatorEntity.setModifyDate(new Date());
    operatorEntity.setModifier(operator);
    operatorRepository.saveAndFlush(operatorEntity);
    return operatorEntity;
  }

  @Transactional
  @Override
  public void updateLoginTime(String account, Date loginTime) {
    /*
     * 只能更新登录时间，其它什么都不能更新
     */
    Validate.notBlank(account, "account must not be empty!!");
    Validate.notNull(loginTime, "login time not be empty!!");
    this.operatorRepository.updateLoginTime(account, loginTime);
  }

  @Override
  @Transactional
  public void updatePassword(String operatorId, String oldPass, String newPass,
      OperatorEntity modifyUser) {
    // 检查传入数据是否为空
    Validate.notNull(modifyUser, "修改人不能为空");
    Validate.notBlank(operatorId, "管理员id不能为空");
    Validate.notBlank(oldPass, "原密码不能为空");
    Validate.notBlank(newPass, "新密码不能为空");
    // 检查数据有效性
    OperatorEntity operator = findById(operatorId);
    Validate.notNull(operator, "无法获取该管理员");
    // 检查老密码是否与管理员密码匹配
    String oldPassword = passwordEncoder.encodePassword(oldPass, null);
    Validate.isTrue(operator.getPassword().equals(oldPassword), "密码不正确");
    // 检查新密码格式是否正确
    Validate.isTrue(StringValidateUtils.validStrByPattern(newPass, "[a-zA-Z\\d]{6,12}"),
        "新密码长度必须为6-12个字符，大小写英文字符或数字！");
    // 修改管理员的密码和修改人信息并保存
    operator.setPassword(passwordEncoder.encodePassword(newPass, null));
    operator.setModifier(modifyUser);
    operator.setModifyDate(new Date());
    operatorRepository.saveAndFlush(operator);
  }


  /**
   * 该私有方法在注册管理员之前，检查管理员的各个关键属性
   * 
   * @param operator
   */
  private void validateBeforeCreate(OperatorEntity operator) {
    if (!StringUtils.isEmpty(operator.getId())) {
      throw new IllegalArgumentException("注册账号时不应该有数据层id编号信息，请检查！");
    }
    Validate.notBlank(operator.getAccount(), "账号不能为空");
    Validate.isTrue(
        StringValidateUtils.validStrByPattern(operator.getPassword(), "[a-zA-Z\\d]{4,20}"),
        "账号长度必须为4-20个字符，大小写英文字符或数字");
    // 还要检查账号是否已被使用
    OperatorEntity oldOperator = this.operatorRepository.findByAccount(operator.getAccount());
    Validate.isTrue((oldOperator == null), "账号已被使用，请更换！");
    // 创建时间
    operator.setCreateDate(new Date());
    // 管理员名称，必须填写
    Validate.notBlank(operator.getName(), "姓名不能为空");
    // 登录密码
    Validate.notBlank(operator.getPassword(), "密码不能为空");
    Validate.isTrue(
        StringValidateUtils.validStrByPattern(operator.getPassword(), "[a-zA-Z\\d]{6,12}"),
        "密码长度必须为6-12个字符，大小写英文字符或数字！");
  }

  /**
   * 修改之前验证字段信息.
   * 
   * @param operator 要操作的信息
   */
  private void validBeforeModify(OperatorEntity operator) {
    // 验证对象是否存在
    Validate.notNull(operator, "对象不存在！");
    // 验证姓名
    String name = operator.getName();
    Validate.notBlank(name, "姓名不能为空！");
    // 验证密码
    String password = operator.getPassword();
    Validate.notBlank(password, "密码不能为空！");
    Validate.isTrue(StringValidateUtils.validStrByPattern(password, "[a-zA-Z\\d]{6,12}"),
        "密码长度必须为6-12个字符，大小写英文字符或数字！");
  }
}
