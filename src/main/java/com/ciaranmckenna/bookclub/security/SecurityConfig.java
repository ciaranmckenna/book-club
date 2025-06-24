package com.ciaranmckenna.bookclub.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/** Security configuration for the application */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  /**
   * Configure the security filter chain
   *
   * @param http HttpSecurity
   * @return SecurityFilterChain
   * @throws Exception if an error occurs
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        "/",
                        "/register",
                        "/login",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/books/all",
                        "/books/view/**",
                        "/api/**")
                    .permitAll()
                    .requestMatchers("/admin/**")
                    .hasRole("ADMIN")
                    .requestMatchers("/user-management/**")
                    .hasRole("ADMIN")
                    .requestMatchers("/reading-lists/public/**")
                    .permitAll()
                    .requestMatchers("/reading-lists/**")
                    .hasRole("USER")
                    .anyRequest()
                    .authenticated())
        .formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/").permitAll())
        .logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll());

    return http.build();
  }

  /**
   * Password encoder bean
   *
   * @return BCryptPasswordEncoder
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Authentication manager bean
   *
   * @param authenticationConfiguration Authentication configuration
   * @return AuthenticationManager
   * @throws Exception if an error occurs
   */
  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }
}
