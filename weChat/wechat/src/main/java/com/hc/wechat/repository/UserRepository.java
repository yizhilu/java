package com.hc.wechat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hc.wechat.entity.UserEntity;


/**
 * @author hc
 */
@Repository("userRepository")
public interface UserRepository
    extends
      JpaRepository<UserEntity, String>,
      JpaSpecificationExecutor<UserEntity> {
  /**
   * 通过id查询用户并left join fetch查出第三方用户.
   * 
   * @param id 用户id
   * @return UserEntity
   */
  @Query(value = "from UserEntity u left join fetch u.thirdPartUsers where u.id=:id")
  UserEntity findByIdFetch(@Param("id") String id);

  /**
   * 通过手机号获取用户<br>
   * 该方法连带获取关联的三方用户.
   * 
   * @param telephone 手机号码
   * @return UserEntity
   */
  @Query("from UserEntity u left join fetch u.thirdPartUsers where u.telephone=:telephone")
  UserEntity findByTelephone(@Param("telephone") String telephone);
}
