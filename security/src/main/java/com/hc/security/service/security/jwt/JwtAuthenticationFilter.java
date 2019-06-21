package com.hc.security.service.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.hc.security.service.security.jwt.utils.JwtTokenUtil;

import io.jsonwebtoken.ExpiredJwtException;

// @Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final UserDetailsService userDetailsService;
  private final JwtTokenUtil jwtTokenUtil;
  private final String tokenHeader;

  public JwtAuthenticationFilter(
      @Qualifier("jwtUserDetailsService") UserDetailsService userDetailsService,
      JwtTokenUtil jwtTokenUtil, @Value("${jwt.header}") String tokenHeader) {
    this.userDetailsService = userDetailsService;
    this.jwtTokenUtil = jwtTokenUtil;
    this.tokenHeader = tokenHeader;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    // 1. 检查请求头上的 授权信息是否存在，
    // 1.2. 如果存在解析token获取username
    // 2. 检查SecurityContextHolder中是否有授权信息
    // 2.1 如果没有那么去数据库获取username的用户信息,验证token中的信息是否和数据库中的信息一致
    logger.info("processing authentication for '{}'", request.getRequestURL());
    final String requestHeader = request.getHeader(this.tokenHeader);
    String username = null;
    String authToken = null;
    if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
      authToken = requestHeader.substring(7);
      try {
        username = jwtTokenUtil.getUsernameFromToken(authToken);
      } catch (IllegalArgumentException e) {
        logger.error("an error occurred during getting username from token", e);
      } catch (ExpiredJwtException e) {
        logger.warn("the token is expired and not valid anymore", e);
      } catch (Exception e) {
        logger.error("token异常");
      }

    } else {
      logger.warn("couldn't find bearer string, will ignore the header");
    }

    logger.debug("checking authentication for user '{}'", username);

    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      logger.debug("security context was null, so authorizing user");
      UserDetails userDetails;
      try {
        userDetails = userDetailsService.loadUserByUsername(username);
      } catch (UsernameNotFoundException e) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        return;
      }
      if (jwtTokenUtil.validateToken(authToken, userDetails)) {
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        logger.info("authorized user '{}', setting security context", username);
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    }
    filterChain.doFilter(request, response);
  }

}
