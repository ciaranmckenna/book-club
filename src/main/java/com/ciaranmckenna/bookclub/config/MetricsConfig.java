package com.ciaranmckenna.bookclub.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

  @Bean
  public Counter bookCreationCounter(MeterRegistry meterRegistry) {
    return Counter.builder("bookclub.books.created")
        .description("Number of books created")
        .register(meterRegistry);
  }

  @Bean
  public Counter reviewCreationCounter(MeterRegistry meterRegistry) {
    return Counter.builder("bookclub.reviews.created")
        .description("Number of reviews created")
        .register(meterRegistry);
  }

  @Bean
  public Counter userRegistrationCounter(MeterRegistry meterRegistry) {
    return Counter.builder("bookclub.users.registered")
        .description("Number of users registered")
        .register(meterRegistry);
  }
}
