package com.ciaranmckenna.bookclub.security;

import com.ciaranmckenna.bookclub.entity.User;
import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/** Custom implementation of UserDetails for authentication */
public class CustomUserDetails implements UserDetails {

  private final User user;

  public CustomUserDetails(User user) {
    this.user = user;
  }

  /**
   * Get the user entity
   *
   * @return User entity
   */
  public User getUser() {
    return user;
  }

  /**
   * Get the authorities (roles) of the user
   *
   * @return Collection of GrantedAuthority
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return user.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
  }

  /**
   * Get the password of the user
   *
   * @return Password
   */
  @Override
  public String getPassword() {
    return user.getPassword();
  }

  /**
   * Get the username of the user
   *
   * @return Username
   */
  @Override
  public String getUsername() {
    return user.getUsername();
  }

  /**
   * Check if the account is not expired
   *
   * @return true if the account is not expired
   */
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  /**
   * Check if the account is not locked
   *
   * @return true if the account is not locked
   */
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  /**
   * Check if the credentials are not expired
   *
   * @return true if the credentials are not expired
   */
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  /**
   * Check if the account is enabled
   *
   * @return true if the account is enabled
   */
  @Override
  public boolean isEnabled() {
    return user.isEnabled();
  }
}
