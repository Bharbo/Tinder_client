package com.tinder.client.service;

import com.tinder.client.support.Response;
import com.tinder.client.support.URL;
import com.tinder.client.domain.User;
import com.tinder.client.support.ShowResult;
import lombok.var;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;

public class SendRequestReceiveResponse {
    Integer num;
    RestTemplate restTemplate;
    ShowResult showResult;

    public SendRequestReceiveResponse(ShowResult showResult, RestTemplate restTemplate) {
        this.showResult = showResult;
        this.restTemplate = restTemplate;
        num = 0;
    }

    /**
     * Communicating with the server
     */

    public Response registration(String username, String password, String gender, String description) {

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

    public Response deleteProfile() {

        Map<String, String> currentUser = getCurrentUser();
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

    public Response changeDescription(String message) {

        URI uri = new URL().edit();
        RequestEntity<String> request = RequestEntity.put(uri).contentType(MediaType.APPLICATION_JSON).body(message);

        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        return response.getStatusCode() == HttpStatus.OK ?
                new Response(true, response.getBody()) :
                new Response(false, response.getBody());
    }

    public Response getNextProfile() {
        Map<String, String> currentUser = getCurrentUser();
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

    public Response getNextAuth(Long id) {
        URI uri = new URL().authNext();

        RequestEntity<Long> requestEntity = RequestEntity.post(uri).contentType(MediaType.APPLICATION_JSON).body(id);
        ResponseEntity<Map<String, String>> responseNext = restTemplate
                .exchange(requestEntity, new ParameterizedTypeReference<Map<String, String>>() {
                });

        if (responseNext.getStatusCode() == HttpStatus.OK) {
            return new Response(true, responseNext.getBody());
        }
        return new Response(false, responseNext.getBody());
    }

    public Response getNextNoAuth(int num) {

        URI uri = new URL().noAuthNext(num);
        var requestEntity = RequestEntity.get(uri).build();
        ResponseEntity<Map<String, String>> response = restTemplate
                .exchange(requestEntity, new ParameterizedTypeReference<Map<String, String>>() {
                });
        return new Response(true, response.getBody());
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

    public Response AllMatch() {
        Map<String, String> currentUser = getCurrentUser();
        if (currentUser == null) {
            return new Response(false, "Пользователь не авторизован");
        }
        Iterable<User> allMatch = AllMatchResponse(currentUser);
        List<User> userList = likeMatchUsersAsList(allMatch);
        return userList.isEmpty() ?
                new Response(false, "матчи не найдены") :
                new Response(true, showResult.createMatchesList(userList));
    }

    public Response getOneMatch(int number) {
        Map<String, String> currentUser = getCurrentUser();
        if (currentUser == null) {
            return new Response(false, "Пользователь не авторизован");
        }
        Iterable<User> myMatch = AllMatchResponse(currentUser);
        List<User> userList = likeMatchUsersAsList(myMatch);
        if (number > userList.size() || number < 1) {
            return new Response(false, "Нет такого номера");
        }
        User matchUser = userList.get(number - 1);

        return new Response(true, matchUser.getUsername() + " | " + matchUser.getDescription());
    }

    public Iterable<User> AllMatchResponse(Map<String, String> currentUser) {

        URI uri = new URL().allMyMatch(Long.parseLong(currentUser.get("id")));
        var request = RequestEntity.get(uri).build();

        ResponseEntity<Iterable<User>> restResponse = restTemplate.exchange(request,
                new ParameterizedTypeReference<Iterable<User>>() {
                });
        return restResponse.getBody();//возвращается null или MAP (номер юзера, User)
    }

    public Response logOut() {
        Map<String, String> currentUser = getCurrentUser();
        if (currentUser == null) {
            return new Response(false, "Пользователь не авторизован");
        }
        URI uri = new URL().logOut();
        RequestEntity<String> request = RequestEntity.post(uri).body(" ");
        ResponseEntity<String> response = restTemplate.exchange(request, String.class);
        return new Response(true, response.getBody());
    }

    /**
     * Support
     */

    public List<User> likeMatchUsersAsList(Iterable<User> likeMatchUsers) {
        int i = 1;
        List<User> likedUsersList = new LinkedList<>();

        likeMatchUsers.forEach(likedUsersList::add);
        return likedUsersList;
    }

    public void breakLoggedUser() {
        URI uri = new URL().breakLoggedUser();
        RequestEntity<String> request = RequestEntity.post(uri).body(" ");
        ResponseEntity<String> response = restTemplate.exchange(request, String.class);
        System.out.println(response.getBody());
    }

    public Map<String, String> getCurrentUser() {

        URI uri = new URL().currentUser(/*1*/);// http://localhost:8080/login/currentuser?num=0
        var request = RequestEntity.get(uri).build();

        ResponseEntity<Map<String, String>> response = restTemplate.exchange
                (request, new ParameterizedTypeReference<Map<String, String>>() {
                });

        return response.getBody();
    }
}
