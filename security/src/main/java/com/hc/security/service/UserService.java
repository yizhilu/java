package com.hc.security.service;

import com.hc.security.entity.UserEntity;

public interface UserService {


  UserEntity findByUserName(String username);

}
