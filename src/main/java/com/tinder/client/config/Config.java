package com.tinder.client.config;

import com.tinder.client.service.SendRequestReceiveResponse;
import com.tinder.client.service.StatusOfClient;
import com.tinder.client.shell.AuthorizationController;
import com.tinder.client.shell.MatchController;
import com.tinder.client.shell.StartController;
import com.tinder.client.support.ShowResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

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


//    @Value("${remote.server.link}")
//    private String serverLink;

//    @Bean
//    RestTemplate restTemplate() {
//        return new RestTemplateBuilder()
//                .rootUri(serverLink)
//                .uriTemplateHandler(new DefaultUriBuilderFactory(serverLink))
//                .build();
//    }