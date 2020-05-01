package com.tinder.client.service;

import com.tinder.client.support.Response;
import com.tinder.client.support.URL;
import com.tinder.client.domain.User;
import com.tinder.client.support.ShowResult;
import lombok.var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;

public class SendRequestReceiveResponse {
    Integer num;
    RestTemplate restTemplate;
    ShowResult showResult;
    Logger logger = LoggerFactory.getLogger(SendRequestReceiveResponse.class);


    public SendRequestReceiveResponse(ShowResult showResult, RestTemplate restTemplate) {
        this.showResult = showResult;
        this.restTemplate = restTemplate;
        num = 0;
    }


    public Map<String, String> getCurrentUserAsMap() {

        URI uri = new URL().currentUser(1);// http://localhost:8080/login/currentuser?num=0
        var request = RequestEntity.get(uri).build();

        ResponseEntity<Map<String, String>> response = restTemplate.exchange
                (request, new ParameterizedTypeReference<Map<String, String>>() {
                });

        return response.getBody();
    }


    public Response getNextProfile() {
        Map<String, String> currentUser = getCurrentUserAsMap();
        Response response;
        if (currentUser == null) {
            response = getNextNoAuth(num);
            if (response.isStatus()) {
                num++;
                return response;
            }
        } else {
            response = getNextAuth(Long.parseLong(currentUser.get("id")));
        }
        return response;
    }

    public Response getNextNoAuth(int num) {

        URI uri = new URL().noAuthNext(num);
        var requestEntity = RequestEntity.get(uri).build();
//        try {
        ResponseEntity<Map<String, String>> response = restTemplate
                .exchange(requestEntity, new ParameterizedTypeReference<Map<String, String>>() {});
        return new Response(true, response.getBody());
//        } catch (RestClientException e) {
//            return new Response(false);
//        }
    }

    public Response getNextAuth(Long id) {
        URI uri = new URL().authNext();

        RequestEntity<Long> requestEntity = RequestEntity.post(uri).contentType(MediaType.APPLICATION_JSON).body(id);

        try {
            ResponseEntity<Map<String, String>> responseNext = restTemplate
                    .exchange(requestEntity, new ParameterizedTypeReference<Map<String, String>>() {});
//            System.out.println(responseNext);
            return new Response(true, responseNext.getBody());
        } catch (RestClientException e) {
            logger.debug(e.getMessage());
        }
        return new Response(false, "Подходящих профилей не найдено");
    }

    public Response regNewUser(String username, String password, String gender, String description) {

        Response serverResponse = new Response(false, "Не удалось создать учетную запись.");
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("gender", gender);
        params.put("description", description);

        URI uri = new URL().reg();
        RequestEntity<Map<String, String>> request = RequestEntity.post(uri).body(params);

        ResponseEntity<String> response = restTemplate.exchange(request, String.class);
        if (response.getStatusCode() == HttpStatus.CREATED) {
            serverResponse = new Response(true,
                    "Успехъ. Ваша анкета:\n" + params.get("username") + " " + params.get("description"));
        }
        return serverResponse;
    }

    public Response logIn(String username, String password) {
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);

        URI uri = new URL().logIn();
        RequestEntity<Map<String, String>> request = RequestEntity.post(uri).body(params);

        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        return response.getStatusCode() == HttpStatus.OK ?
                new Response(true, "|| Успехъ ||\n") :
                new Response(false, response.getBody());
    }


    public Response changeDescription(String message) {

//        Response serverResponse = new Response(false, response.getBody());

        URI uri = new URL().edit();
        RequestEntity<String> request = RequestEntity.put(uri).contentType(MediaType.APPLICATION_JSON).body(message);

        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        return response.getStatusCode() == HttpStatus.OK ?
                new Response(true, response.getBody()) :
                new Response(false, response.getBody());
//        if (response.getStatusCode() == HttpStatus.OK) {
//            serverResponse = new Response(true, response.getBody());
//        }
//        return serverResponse;
    }


    public Response like(Long id) {

        URI uri = new URL().right();
        RequestEntity<Long> request = RequestEntity.post(uri).contentType(MediaType.APPLICATION_JSON).body(id);

        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        return response.getStatusCode() == HttpStatus.CREATED ?
                new Response(true, response.getBody()) :
                new Response(false, response.getBody());
    }


    public Response dislike(Long id) {

        URI uri = new URL().left();
        RequestEntity<Long> request = RequestEntity.post(uri).contentType(MediaType.APPLICATION_JSON).body(id);

        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        return response.getStatusCode() == HttpStatus.CREATED ?
                new Response(true, response.getBody()) :
                new Response(false, response.getBody());

    }


    public Response AllMyMatch() {
        Map<String, String> currentUser = getCurrentUserAsMap();
        if (currentUser == null) {
            return new Response(false, "Пользователь не авторизован");
        }
        Iterable<User> allMatch = AllMyMatchIter(currentUser);
        List<User> userList = likeMatchUsersAsList(allMatch);
        return userList.isEmpty() ?
                new Response(false, "матчи не найдены") :
                new Response(true, showResult.createMatchesList(userList));
    }

//        public Response AllMyMatch() {
//        Map<Integer, User> allMatch = AllMyMatchAsMap();
//        return allMatch.isEmpty() ?
//                new Response(false, "Пользователь не авторизован || матчи не найдены") :
//                new Response(true, showResult.createMatchesList(allMatch.values()));
//    }
//


    public Iterable<User> AllMyMatchIter(Map<String, String> currentUser) {

        URI uri = new URL().allMyMatch(Long.parseLong(currentUser.get("id")));
        var request = RequestEntity.get(uri).build();

        ResponseEntity<Iterable<User>> restResponse = restTemplate.exchange(request,
                new ParameterizedTypeReference<Iterable<User>>() {});
        return restResponse.getBody();//возвращается null или MAP (номер юзера, User)
    }

//        public Map<Integer, User> AllMyMatchAsMap() {
//
//        Map<String, String> currentUser = getCurrentUserAsMap();
//        if (currentUser == null) {
//            return null;
//        }
//
//        URI uri = new URL().allMyMatch(Long.parseLong(currentUser.get("id")));
//        var request = RequestEntity.get(uri).build();
//
//        ResponseEntity<Map<Integer, User>> restResponse = restTemplate.exchange(request,
//                new ParameterizedTypeReference<Map<Integer, User>>() {
//                });
//        return restResponse.getBody();//возвращается null или MAP (номер юзера, User)
//    }
//
//


    public Response getOneMatch(int number) {
        Map<String, String> currentUser = getCurrentUserAsMap();
        if (currentUser == null) {
            return new Response(false, "Пользователь не авторизован");
        }
        Iterable<User> myMatch = AllMyMatchIter(currentUser);
        List<User> userList = likeMatchUsersAsList(myMatch);
        if (number > userList.size() || number < 1) {
            return new Response(false, "Нет такого номера");
        }
        User matchUser = userList.get(number - 1);

        return new Response(true, matchUser.getUsername() + " " + matchUser.getDescription());
    }

        public List<User> likeMatchUsersAsList(Iterable<User> likeMatchUsers) {
        int i = 1;
            List<User> likedUsersList = new LinkedList<>();

            likeMatchUsers.forEach(likedUsersList::add);
//        likeMatchUsers.forEach(user -> likedUsersMap.put(i + 1, user));
        return likedUsersList;
    }


//        public Response getOneMatch(int number) {
//        Map<Integer, User> myMatch = AllMyMatchAsMap();
//        if (number > myMatch.size() || number < 1) {
//            return new Response(false, "Нет такого номера");
//        }
//        User matchUser = myMatch.get(number);
//
//        return new Response(true, matchUser.getUsername() + " " + matchUser.getDescription());
//    }
//
//

    public Response deleteProfile() {

        Map<String, String> currentUser = getCurrentUserAsMap();
        if (currentUser == null) {
            return new Response(false, "Пользователь не авторизован");
        }
        URI uri = new URL().delete();
        RequestEntity<Long> request = RequestEntity.post(uri).body(Long.parseLong(currentUser.get("id")));

        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        return response.getBody() != null ?
                new Response(true, response.getBody()) :
                new Response(false, response.getBody());
    }


    public void breakLoggedUser() {
        URI uri = new URL().breakLoggedUser();
        RequestEntity<String> request = RequestEntity.post(uri).body(" ");
        ResponseEntity<String> response = restTemplate.exchange(request, String.class);
        System.out.println(response.getBody());
    }

    public Response logOut() {
        Map<String, String> currentUser = getCurrentUserAsMap();
        if (currentUser == null) {
            return new Response(false, "Пользователь не авторизован");
        }
        URI uri = new URL().logOut();
        RequestEntity<String> request = RequestEntity.post(uri).body(" ");
        ResponseEntity<String> response = restTemplate.exchange(request, String.class);
        return new Response(true, response.getBody());
    }
}

