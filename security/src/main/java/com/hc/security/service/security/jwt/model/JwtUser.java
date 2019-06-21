package com.hc.security.service.security.jwt.model;

import java.util.Collection;
import java.util.Date;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class JwtUser implements UserDetails {

  /**
   * 
   */
  private static final long serialVersionUID = -3200542412158122675L;
  private final String id;
  private final String username;
  private final String password;
  private final Collection<? extends GrantedAuthority> authorities;
  private final boolean enabled;
  private final Date lastPasswordResetDate;

  public JwtUser(String id, String username, String password,
      Collection<? extends GrantedAuthority> authorities, boolean enabled,
      Date lastPasswordResetDate) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.authorities = authorities;
    this.enabled = enabled;
    this.lastPasswordResetDate = lastPasswordResetDate;
  }

  @JsonIgnore
  public String getId() {
    return id;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @JsonIgnore
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @JsonIgnore
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @JsonIgnore
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }


  @JsonIgnore
  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @JsonIgnore
  public Date getLastPasswordResetDate() {
    return lastPasswordResetDate;
  }

  @Override
  public String toString() {
    return "JwtUser [id=" + id + ", username=" + username + ", password=" + password
        + ", authorities=" + authorities + ", enabled=" + enabled + ", lastPasswordResetDate="
        + lastPasswordResetDate + "]";
  }

}
