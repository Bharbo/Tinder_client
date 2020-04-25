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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@Component
public class SendRequestReceiveResponse {
    Set<User> allProfiles;
    User currentUser;
    Map<Integer, String> matches;
    URL url;
    RestTemplate restTemplate;
    ShowResult showResult;

    @Autowired
    public SendRequestReceiveResponse(ShowResult showResult, RestTemplate restTemplate, URL url) {
        allProfiles = null;
        currentUser = null;
        matches = null;
        this.url = url;
        this.showResult = showResult;
        this.restTemplate = restTemplate;
    }


    public Response regNewUser(String username, String password, String gender, String profileMessage) {

        Response serverResponse = new Response(false, "Не удалось создать учетную запись.");
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("gender", gender);
        params.put("profileMessage", profileMessage);

        URI uri = url.reg();
        RequestEntity<Map<String, String>> request = RequestEntity.post(uri).body(params);

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

        URI uri = url.logIn();
        RequestEntity<Map<String, String>> request = RequestEntity.post(uri).body(params);

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

        URI uri = url.edit();
        RequestEntity<String> request = RequestEntity.put(uri).body(message);

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.PUT, request, String.class);
//        ResponseEntity<String> response = restTemplate.exchange(request, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            serverResponse = new Response(true, response.getBody());
        }
        return serverResponse;
    }


    public Response getNextProfile(){
        URI uri = url.next();
        RequestEntity<String> request = RequestEntity.post(uri).body("");
        ResponseEntity<String> response = restTemplate.exchange(request, String.class);
        return new Response(true, response.getBody());
    }


    public Response like() {

        URI uri = url.right();
        RequestEntity<String> request = RequestEntity.post(uri).body("");

        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        return response.getStatusCode() == HttpStatus.CREATED ?
                new Response(true, response.getBody()) :
                new Response(false, response.getBody());
    }


    public Response dislike() {

        URI uri = url.left();
        RequestEntity<String> request = RequestEntity.post(uri).body("");

        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        return response.getStatusCode() == HttpStatus.CREATED ?
                new Response(true, response.getBody()) :
                new Response(false, response.getBody());

    }


    public Response showAllMatch() {

//        if (currentUser != null) {
            Map<String, String> requestParams = new HashMap<>();
            requestParams.put("id", currentUser.getId().toString());////////////

            URI uri = url.allMatch();
            RequestEntity<Map<String, String>> request = RequestEntity.post(uri).body(requestParams);

            ResponseEntity<Map<Integer, String>> response = restTemplate.exchange(request,
                    new ParameterizedTypeReference<Map<Integer, String>>() {
                    });

            matches = response.getBody();

            return response.getStatusCode() == HttpStatus.CREATED ?
                    new Response(true, showResult.createMatchesList(matches.values().toArray())) :
                    new Response(false, "Авторизуйтесь для получения списка любимцев");
//        }
    }

    public Response getOneMatch(int number) {
        if (number > matches.size() || number < 1)
            return new Response(false, "Нет такого номера!");
        String username = matches.get(number);
        User user = Objects.requireNonNull(allProfiles.stream()///Objects.requireNonNull возможно противоречит .orElse(null)
                .filter(profile -> profile.getUsername().equals(username))
                .findFirst()
                .orElse(null));///

        URI uri = url.oneMatch(user.getId());
        var request = RequestEntity.get(uri).build();

        ResponseEntity<Map<String, String>> response = restTemplate.exchange(request,
                new ParameterizedTypeReference<Map<String, String>>() {
                });

        return response.getStatusCode() == HttpStatus.OK ?
                new Response(true, response.getBody()) :
                new Response(false, "Авторизуйтесь для получения списка любимцев");
    }

    public Response deleteProfile() {

        URI uri = url.delete();
        RequestEntity<Long> request = RequestEntity.post(uri).body(currentUser.getId());

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
