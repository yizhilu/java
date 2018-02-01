package com.hc.security.service.internal;

import java.util.Date;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hc.security.common.utils.StringValidateUtils;
import com.hc.security.entity.OperatorEntity;
import com.hc.security.entity.enums.StatusType;
import com.hc.security.repository.internal.OperatorDao;
import com.hc.security.service.OperatorService;


@Service("operatorService")
public class OperatorServiceImpl implements OperatorService {

  @Autowired
  private Md5PasswordEncoder passwordEncoder;

  @Autowired
  private OperatorDao operatorDao;



  @Override
  @Transactional
  @CacheEvict(value = "operator", allEntries = true)
  public OperatorEntity addOperator(OperatorEntity operator) {
    // 检查属性
    validateBeforeRegist(operator);
    // 加密密码
    String encodePassword = passwordEncoder.encodePassword(operator.getPassword(), null);
    operator.setPassword(encodePassword);
    return operatorDao.saveAndFlush(operator);
  }

  @Override
  @Transactional
  @CacheEvict(value = "operator", allEntries = true)
  public void deleteOperatorById(String operatorId, OperatorEntity operator) {
    // 检查数据有效性
    Validate.notBlank(operatorId, "管理员id不能为空");
    OperatorEntity nowOperator = getById(operatorId);
    Validate.notNull(nowOperator, "无法获取该管理员");
    nowOperator.setDel(true);
    nowOperator.setModifyUser(operator);
    // 由于逻辑删除字段是新增的，无法了解登录操作是用什么字段判断，故改变状态为禁用
    nowOperator.setStatus(StatusType.STATUS_DISABLE);
    nowOperator.setModifyDate(new Date());
    operatorDao.saveAndFlush(operator);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.cheshangma.operation.service.OperatorService#findByAccountAndStatus(java.lang.String,
   * com.cheshangma.operation.common.enums.StatusType)
   */
  @Override
  @Cacheable(value = "operator", key = "'findByAccountAndStatus.' + {#username} + '.' + {#statusType}")
  public OperatorEntity findByAccountAndStatus(String username, StatusType statusType) {
    return this.operatorDao.findByAccountAndStatus(username, statusType);
  }



  @Override
  @Cacheable(value = "operator", key = "'getById.' + {#operatorId}")
  public OperatorEntity getById(String operatorId) {
    Validate.notBlank(operatorId, "参数错误");
    OperatorEntity operator = operatorDao.getById(operatorId);
    return operator;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.cheshangma.operation.service.OperatorService#updateLoginTime(java.lang.String,
   * java.util.Date)
   */
  @Override
  @Transactional
  @CacheEvict(value = "operator", allEntries = true)
  public void updateLoginTime(String account, Date loginTime) {
    /*
     * 只能更新登录时间，其它什么都不能更新
     */
    Validate.notBlank(account, "account must not be empty!!");
    Validate.notNull(loginTime, "login time not be empty!!");
    this.operatorDao.updateLoginTime(account, loginTime);
  }

  @Override
  @Transactional
  @CacheEvict(value = "operator", allEntries = true)
  public void updatePassword(String operatorId, String oldPass, String newPass,
      OperatorEntity modifyUser) {
    // 检查传入数据是否为空
    Validate.notNull(modifyUser, "修改人不能为空");
    Validate.notBlank(operatorId, "管理员id不能为空");
    Validate.notBlank(oldPass, "原密码不能为空");
    Validate.notBlank(newPass, "新密码不能为空");
    // 检查数据有效性
    OperatorEntity operator = getById(operatorId);
    Validate.notNull(operator, "无法获取该管理员");
    // 检查老密码是否与管理员密码匹配
    String oldPassword = passwordEncoder.encodePassword(oldPass, null);
    Validate.isTrue(operator.getPassword().equals(oldPassword), "密码不正确");
    // 检查新密码格式是否正确
    Validate.isTrue(StringValidateUtils.validStrByPattern(newPass, "[a-zA-Z\\d]{6,12}"),
        "新密码长度必须为6-12个字符，大小写英文字符或数字！");
    // 修改管理员的密码和修改人信息并保存
    operator.setPassword(passwordEncoder.encodePassword(newPass, null));
    operator.setModifyUser(modifyUser);
    operator.setModifyDate(new Date());
    operatorDao.saveAndFlush(operator);
  }

  @Override
  @Transactional
  @CacheEvict(value = "operator", allEntries = true)
  public OperatorEntity modify(OperatorEntity operator) {
    OperatorEntity operatorEntity = getById(operator.getId());
    Validate.notNull(operatorEntity, "id为" + operator.getId() + "的管理员不存在!");
    // 验证姓名
    String name = operator.getName();
    Validate.notBlank(name, "姓名不能为空！");
    operatorEntity.setName(name);
    String password = operator.getPassword();
    if (StringUtils.isNotBlank(password)) {
      Validate.isTrue(StringValidateUtils.validStrByPattern(password, "[a-zA-Z\\d]{6,12}"),
          "密码长度必须为6-12个字符，大小写英文字符或数字！");
      // 密码加密存储
      operatorEntity.setPassword(passwordEncoder.encodePassword(password, null));
    }
    operatorEntity.setModifyUser(operator.getModifyUser());
    operatorEntity.setModifyDate(new Date());
    return operatorDao.saveAndFlush(operatorEntity);
  }

  @Override
  @Transactional
  @CacheEvict(value = "operator", allEntries = true)
  public void disableOrUsable(String operatorId, boolean flag, OperatorEntity modifyUser) {
    Validate.notBlank(operatorId, "管理员id不能为空");
    Validate.notNull(modifyUser, "修改者不能为空");
    OperatorEntity operator = getById(operatorId);
    Validate.notNull(operator, "无法获取该管理员");
    if (flag) {
      operator.setStatus(StatusType.STATUS_NORMAL);
    } else {
      operator.setStatus(StatusType.STATUS_DISABLE);
    }
    operator.setModifyUser(modifyUser);
    operator.setModifyDate(new Date());
    operatorDao.saveAndFlush(operator);
  }

  /**
   * 该私有方法在注册管理员之前，检查管理员的各个关键属性
   * 
   * @param operator 要验证的管理员信息
   */
  private void validateBeforeRegist(OperatorEntity operator) {
    Validate.notNull(operator, "管理员对象不能为空");
    if (!StringUtils.isEmpty(operator.getId())) {
      throw new IllegalArgumentException("注册账号时不应该有数据层id编号信息，请检查！");
    }
    Validate.notBlank(operator.getAccount(), "注册账号将用于登录，所以必须填写");
    // 还要检查账号是否已被使用
    OperatorEntity oldOperator =
        this.findByAccountAndStatus(operator.getAccount(), StatusType.STATUS_NORMAL);
    if (null == oldOperator) {
      oldOperator = this.findByAccountAndStatus(operator.getAccount(), StatusType.STATUS_DISABLE);
    }
    Validate.isTrue((null == oldOperator), "注册账号已被使用，请更换！");
    // 创建时间
    operator.setCreateDate(new Date());
    // 管理员名称，必须填写
    Validate.notBlank(operator.getName(), "管理员名称（全名）必须填写");
    // 登录密码
    Validate.notBlank(operator.getPassword(), "管理员登录密码必须填写");
    Validate.isTrue(
        StringValidateUtils.validStrByPattern(operator.getPassword(), "[a-zA-Z\\d]{6,12}"),
        "密码长度必须为6-12个字符，大小写英文字符或数字！");
  }

}
