package com.tinder.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
