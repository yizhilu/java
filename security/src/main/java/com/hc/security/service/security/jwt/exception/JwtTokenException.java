package com.hc.security.service.security.jwt.exception;

import org.springframework.security.core.AuthenticationException;


public class JwtTokenException extends AuthenticationException {

  /**
   * 
   */
  private static final long serialVersionUID = -2177282436910547858L;

  public JwtTokenException(String msg) {
    super(msg);
  }
}
