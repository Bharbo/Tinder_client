package com.tinder.client.service;

import com.tinder.client.domain.Response;
import com.tinder.client.domain.URL;
import com.tinder.client.domain.User;
import com.tinder.client.support.ShowResult;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Component
public class SendRequestReceiveResponse {//Служба генерации запросов и получения ответов от сервера
    Set<User> allProfiles;
    User currentUser;
    Map<Integer, String> matches;
    URL url;
    RestTemplate restTemplate;
    ShowResult showResult;

    @Autowired
    public SendRequestReceiveResponse(ShowResult showResult/*, RestTemplate restTemplate*/) {
        allProfiles = null;
        currentUser = null;
        matches = null;
        this.url = new URL();
        this.showResult = showResult;
    }




    public Response regNewUser(String username, String password, String gender, String profileMessage) {

        Response serverResponse = new Response(false, "Не удалось создать учетную запись.");
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("gender", gender);
        params.put("profileMessage", profileMessage);

        RequestEntity<Map<String, String>> request = RequestEntity.post(url.reg()).body(params);

        ResponseEntity<String> response = restTemplate.exchange(request, String.class);
        if (response.getStatusCode() == HttpStatus.CREATED) {
            serverResponse = new Response(true,
                    "Успехъ. Ваша анкета:\n" + params.get("username") + " " + params.get("profileMessage"));
            params.put("id", response.getBody());
            saveProfile(params);
        }
        return serverResponse;
    }

    public void saveProfile(Map<String, String> params) {
        allProfiles.add(new User(Long.parseLong(params.get("id")), params.get("gender"),
                params.get("username"), params.get("password"), params.get("profileMessage")));
    }


    public Response logIn(String username, String password) {
        Response serverResponse = new Response(false, "Неудача, попробуйте снова");
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);

        RequestEntity<Map<String, String>> request = RequestEntity.post(url.logIn()).body(params);

        ResponseEntity<String> response = restTemplate.exchange(request, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            serverResponse = new Response(true, "Успехъ\n");
            currentUser = allProfiles.stream()
                    .filter(profile -> profile.getId().equals(Long.parseLong(Objects.requireNonNull(response.getBody()))))
                    .findFirst()
                    .orElse(null);
        }
        return serverResponse;
    }


    public Response changeDescription(String message) {

        Response serverResponse = new Response(false, "Не удалось изменить описание профиля");

        RequestEntity<String> request = RequestEntity.put(url.edit()).body(message);

        ResponseEntity<String> response = restTemplate.exchange(url.edit(), HttpMethod.PUT, request, String.class);
//        ResponseEntity<String> response = restTemplate.exchange(request, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            serverResponse = new Response(true, response.getBody());
        }
        return serverResponse;
    }


    public Response getNextProfile() {
        RequestEntity<Long> request = RequestEntity.post(url.next()).body(currentUser.getId());
        ResponseEntity<String> response = restTemplate.exchange(request, String.class);
        return new Response(true, response.getBody());
    }


    public Response like() {

        RequestEntity<Long> request = RequestEntity.post(url.right()).body(currentUser.getId());

        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        return response.getStatusCode() == HttpStatus.CREATED ?
                new Response(true, response.getBody()) :
                new Response(false, response.getBody());
    }


    public Response dislike() {

        RequestEntity<Long> request = RequestEntity.post(url.left()).body(currentUser.getId());

        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        return response.getStatusCode() == HttpStatus.CREATED ?
                new Response(true, response.getBody()) :
                new Response(false, response.getBody());

    }


    public Response showAllMatch() {

        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("id", currentUser.getId().toString());

        RequestEntity<Map<String, String>> request = RequestEntity.post(url.allMatch()).body(requestParams);

        ResponseEntity<Map<Integer, String>> response = restTemplate.exchange(request,
                new ParameterizedTypeReference<Map<Integer, String>>() {
                });

        matches = response.getBody();

        return response.getStatusCode() == HttpStatus.CREATED ?
                new Response(true, showResult.createMatchesList(matches.values().toArray())) :
                new Response(false, "Авторизуйтесь для получения списка любимцев");

    }

    public Response getOneMatch(int number) {
        if (number > matches.size() || number < 1)
            return new Response(false, "Нет такого номера!");
        String username = matches.get(number);
        User user = Objects.requireNonNull(allProfiles.stream()///Objects.requireNonNull возможно противоречит .orElse(null)
                .filter(profile -> profile.getUsername().equals(username))
                .findFirst()
                .orElse(null));///

        var request = RequestEntity.get(url.oneMatch(user.getId())).build();

        ResponseEntity<Map<String, String>> response = restTemplate.exchange(request,
                new ParameterizedTypeReference<Map<String, String>>() {
                });

        return response.getStatusCode() == HttpStatus.OK ?
                new Response(true, response.getBody()) :
                new Response(false, "Авторизуйтесь для получения списка любимцев");
    }

    public Response deleteProfile() {

        RequestEntity<Long> request = RequestEntity.post(url.delete()).body(currentUser.getId());

        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            allProfiles.remove(currentUser);
            matches.clear();
            currentUser = null;
            return new Response(true, response.getBody());
        } else {
            return new Response(false, response.getBody());
        }
    }
}
