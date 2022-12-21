package com.amitco.matcher.security;

import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Value("${jwt.enabled}")
  private boolean JWT_ENABLED;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    if(JWT_ENABLED){
      configureHTTPSecurityJWT(http);
    }else {
      configureHTTPSecurityAllOpen(http);
    }

  }

  private void configureHTTPSecurityAllOpen(HttpSecurity http) throws Exception {

    http.sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()                // corsConfiguration.setExposedHeaders(Arrays.asList("Authorization"));

        .cors()
        .configurationSource(getCorsConfigurationSource())
        .and()
        .csrf()
        .disable()
        .authorizeRequests()
        .anyRequest()
        .permitAll();

  }

  private void configureHTTPSecurityJWT(HttpSecurity http) throws Exception{
    http.sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .cors()
        .configurationSource(getCorsConfigurationSource())
        .and()
        .csrf()
        .disable() // Don't need it with JWT enabled
        .authorizeRequests()
        .antMatchers(HttpMethod.POST, "/authenticate")
        .permitAll()
        .antMatchers("/", "/error", "/swagger-ui.html", "/swagger-ui/**")
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .oauth2ResourceServer()
        .jwt();

  }

  private CorsConfigurationSource getCorsConfigurationSource(){

      return httpServletRequest -> {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
        corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
        corsConfiguration.setAllowedOriginPatterns(Collections.singletonList("*"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setMaxAge(3600L);
        return corsConfiguration;
      };
  }


}
