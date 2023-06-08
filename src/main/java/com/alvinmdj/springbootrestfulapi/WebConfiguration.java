package com.alvinmdj.springbootrestfulapi;

import com.alvinmdj.springbootrestfulapi.resolver.UserArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
  @Autowired
  private UserArgumentResolver userArgumentResolver;

  // Register UserArgumentResolver.
  // Whenever a controller requires a User in the parameter,
  // UserArgumentResolver will executes first to get the User data
  // only if X-API-TOKEN exists in the header & valid, else throw an exception.
  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    WebMvcConfigurer.super.addArgumentResolvers(resolvers);
    resolvers.add(userArgumentResolver);
  }
}
