package com.hecheng.wechat.openplatform.service.internal;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hecheng.wechat.openplatform.entity.UserContactEntity;
import com.hecheng.wechat.openplatform.entity.UserEntity;
import com.hecheng.wechat.openplatform.repository.UserContactRepository;
import com.hecheng.wechat.openplatform.service.UserContactService;
import com.hecheng.wechat.openplatform.service.UserService;

/**
 * 这是一个 UserContactService 的 实现类 ，用于修改用户或者绑定用户的联系电话
 * 
 * @author hq
 *
 */


@Service("userContactService")
public class UserContactServiceImpl implements UserContactService {

  @Autowired
  private UserContactRepository userContactRepository;

  @Autowired
  private UserService userService;
  /**
   * 手机绑定成功 微信通知 模板id
   */
  @Value("${weChat.bind_telephone_template}")
  private String BIND_TELEPHONE_TEMPLATE;

  @Value("${weChat.bind_updatePhone_template}")
  private String BIND_UPDATEPHONE_TEMPLATE;



  /**
   * 修改用户联系电话
   */
  @Transactional
  @Override
  public UserContactEntity updateUserContact(String userOnelyId, String phone) {
    Validate.notBlank(userOnelyId, "用户id不能为空");
    Validate.notBlank(phone, "用户绑定的联系电话不能为空");
    String phoneRegex = "^1[345789]\\d{9}$";
    Validate.isTrue(phone.matches(phoneRegex), "请输入正确的手机格式.");

    // 查找数据库中是否已经存在绑定的手机号码
    UserContactEntity userExit = userContactRepository.findByPhone(phone);
    // 如果通过手机号码查询为空，或者 用户输入的是自己的号码 都提示 不能再次绑定
    Validate.isTrue(userExit == null || userOnelyId.equals(userExit.getId()), "该手机号已绑定过，不能再次绑定");

    // 如果此手机号码不在数据库中，获取用户对像，更改电话,并且保存

    UserEntity user = userService.findByIdFetch(userOnelyId);
    Validate.notNull(user, "用户不存在");
    UserContactEntity olduser = userContactRepository.findByUserId(userOnelyId);
    String oldPhone = olduser.getPhone();
    olduser.setPhone(phone);

    /**
     * 尊敬的用户，您的手机号已成功更改。 更改时间：2014年10月6日 20:09 更改地址：民宿爱家 原手机号：13800138000 新手机号：13500135000
     * 如有疑问，请咨询XXXX
     */
    // 发送微信通知
    UserContactEntity userContactEntity = userContactRepository.saveAndFlush(olduser);
    //Date updateTime = new Date();
    // first0||更改时间1|| 更改地址2||原手机号3|| 新手机号4||remark5
    // StringBuffer msgContent = new StringBuffer();
    // msgContent.append("尊敬的用户，您的手机号已成功更改");
    // msgContent.append("||");
    // msgContent.append(DateUtils.format(updateTime));
    // msgContent.append("||");
    // msgContent.append("民宿爱家");
    // msgContent.append("||");
    // msgContent.append(oldPhone);
    // msgContent.append("||");
    // msgContent.append(phone);
    // msgContent.append("||");
    // msgContent.append("如有疑问、请咨询XXX");
    // WxTemplate wxupdateTemplate = new WxChangePhoneNotice(null, user, null,
    // msgContent.toString(),
    // updateTime, BIND_UPDATEPHONE_TEMPLATE);
    // MsgTools.getInstance().sendMessage(MessageType.MSGTYPE_WEIXIN, wxupdateTemplate);

    return userContactEntity;

  }

  @Transactional
  @Override
  public UserContactEntity bindUserContact(String userOnelyId, String phone) {
    Validate.notNull(userOnelyId, "用户id不能为空");
    Validate.notNull(phone, "用户绑定的联系电话不能为空");
    String phoneRegex = "^1[34578]\\d{9}$";
    Validate.isTrue(phone.matches(phoneRegex), "请输入正确的手机格式.");
    // 尋找指定ID的用戶對象
    UserEntity user = userService.findByIdFetch(userOnelyId);
    Validate.notNull(user, "用户不存在");
    UserContactEntity oldUserContact = userContactRepository.findByUserId(userOnelyId);
    UserContactEntity isUserContact = userContactRepository.findByPhone(phone);
    if (oldUserContact == null) {
      // 查找数据库中是否已经存在绑定的手机号码

      Validate.isTrue(isUserContact == null, "该手机号已绑定过，不能再次绑定");
      UserContactEntity newUserContact = new UserContactEntity();
      newUserContact.setPhone(phone);
      newUserContact.setUser(user);
      newUserContact = userContactRepository.save(newUserContact);
      // // 发送微信通知
      // Date bindTime = new Date();
      // StringBuffer msgContent = new StringBuffer();
      // msgContent.append("尊敬的用户，恭喜您!手机号已与微信成功绑定！");
      // msgContent.append("||");
      // msgContent.append(phone);
      // msgContent.append("||");
      // msgContent.append(DateUtils.format(bindTime));
      // msgContent.append("||");
      // msgContent.append("感谢您的支持，祝您生活愉快！");
      // WxTemplate wxTemplate = new WxBindPhoneNotice(null, user, null, msgContent.toString(),
      // bindTime, BIND_TELEPHONE_TEMPLATE);
      // MsgTools.getInstance().sendMessage(MessageType.MSGTYPE_WEIXIN, wxTemplate);
      return newUserContact;
    } else {

      Validate.isTrue(isUserContact == null, "该手机号已绑定过，不能再次绑定");
      oldUserContact.setPhone(phone);
      return userContactRepository.saveAndFlush(oldUserContact);
    }
  }

  @Override
  public UserContactEntity findByUserId(String userId) {
    Validate.notNull(userId, "用户的ID 不可以为空");

    return userContactRepository.findByUserId(userId);

  }

}
