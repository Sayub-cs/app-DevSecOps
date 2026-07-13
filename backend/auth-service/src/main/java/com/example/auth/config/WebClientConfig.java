package com.example.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

  @Bean
  @LoadBalanced
  public WebClient.Builder loadBalancedWebClientBuilder() {
    return WebClient.builder();
  }

  @Bean
  public WebClient userServiceWebClient(
      WebClient.Builder loadBalancedWebClientBuilder,
      @Value("${USER_SERVICE_URL:lb://user-service}") String userServiceUrl
  ) {
    return loadBalancedWebClientBuilder
        .baseUrl(userServiceUrl)
        .build();
  }
}