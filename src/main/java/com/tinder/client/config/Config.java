package com.tinder.client.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class Config {

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .rootUri("http://localhost:8080")
                .uriTemplateHandler(new DefaultUriBuilderFactory("http://localhost:8080"))
                .build();
    }
}
