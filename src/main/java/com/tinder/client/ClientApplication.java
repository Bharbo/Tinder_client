package com.tinder.client;

import com.tinder.client.service.SendRequestReceiveResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class ClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }

//    @Component
//    public class Initializing {
//
//        @Autowired
//        SendRequestReceiveResponse sendRequestReceiveResponse;
//
//        @PostConstruct
//        public void initial() {
//            System.out.println(sendRequestReceiveResponse.getNextProfile().toString());
//        }
//    }
}
