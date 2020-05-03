package com.tinder.client.config;

import com.tinder.client.service.SendRequestReceiveResponse;
import com.tinder.client.service.StatusOfClient;
import com.tinder.client.support.ShowResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Config {

    @Bean
    StatusOfClient statusOfClient() {
        return new StatusOfClient();
    }

    @Bean
    SendRequestReceiveResponse sendRequestReceiveResponse() {
        return new SendRequestReceiveResponse(showResult(), restTemplate());
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    ShowResult showResult() {
        return new ShowResult();
    }
}
