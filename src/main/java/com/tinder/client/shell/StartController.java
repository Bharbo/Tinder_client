package com.tinder.client.shell;

import com.tinder.client.support.Response;
import com.tinder.client.service.SendRequestReceiveResponse;
import com.tinder.client.service.StatusOfClient;
import com.tinder.client.support.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@ShellComponent
public class StartController {
    StatusOfClient statusOfClient;
    SendRequestReceiveResponse sendRequestReceiveResponse;
    Long id;


    public StartController(SendRequestReceiveResponse sendRequestReceiveResponse, StatusOfClient statusOfClient) {
        this.sendRequestReceiveResponse = sendRequestReceiveResponse;
        this.statusOfClient = statusOfClient;
        id = 0L;
    }


    @PostConstruct
    public void initial() {
        System.out.println("Вы вошли как неизвестный. Создайте свой аккаунт или войдите в существующий.\n");
        sendRequestReceiveResponse.breakLoggedUser();

        Response responseNextProfile = sendRequestReceiveResponse.getNextProfile();
        Map<String, String> info = (Map<String, String>) responseNextProfile.getAddition();
        if (info.get("id") != null) {
            id = Long.parseLong(info.get("id"));
        }
        if (!responseNextProfile.isStatus()) {
            System.out.println("Ошибка запуска");
        } else {
            System.out.println(info.get("description") + "\n");
        }
    }


    @ShellMethod(key = {"влево"}, value = "Свайп влево для проявления антипатии")
    @ShellMethodAvailability("checkAvailability")
    public String left() {
        Response responseDislike = sendRequestReceiveResponse.dislike(id);

        Response responseNextProfile = sendRequestReceiveResponse.getNextProfile();
        Map<String, String> info = (Map<String, String>) responseNextProfile.getAddition();
        if (info.get("id") != null) {
            id = Long.parseLong(info.get("id"));
        }

        if (!responseDislike.isStatus()) {
            return ((Map<String, String>) responseNextProfile.getAddition()).get("description") + "\n";
        }
        return responseDislike.getAddition().toString() + "\n\n" +
                ((Map<String, String>) responseNextProfile.getAddition()).get("description") + "\n";
    }


    @ShellMethod(key = {"вправо"}, value = "Свайп вправо для проявления симпатии")
    @ShellMethodAvailability("checkAvailability")
    public String right() {
        Response responseLike = sendRequestReceiveResponse.like(id);

        Response responseNextProfile = sendRequestReceiveResponse.getNextProfile();
        Map<String, String> info = (Map<String, String>) responseNextProfile.getAddition();
        if (info.get("id") != null) {
            id = Long.parseLong(info.get("id"));
        }

        if (!responseLike.isStatus()) {
            return info.get("description") + "\n";
        }
        return responseLike.getAddition().toString() + "\n\n" + info.get("description") + "\n";
    }


    @ShellMethod(key = {"анкета"}, value = "Перейти в меню авторизации")
    @ShellMethodAvailability("checkAvailability")
    public String profile() {
        statusOfClient.authPanel();
        return "\nПерешли в меню авторизации\n";
    }


    @ShellMethod(key = {"любимцы"}, value = "Перейти в меню любимцев")
    @ShellMethodAvailability("checkAvailability")
    public String matches() {
        if (sendRequestReceiveResponse.getCurrentUserAsMap() != null) {
            Response response = sendRequestReceiveResponse.AllMyMatch();
            if (response.isStatus()) {
                statusOfClient.matchPanel();
                return "\nПерешли в меню любимцев\n\n"
                        + sendRequestReceiveResponse.AllMyMatch().getAddition().toString() + "\n";
            }
            Response responseNextProfile = sendRequestReceiveResponse.getNextProfile();
            Map<String, String> info = (Map<String, String>) responseNextProfile.getAddition();
            if (info.get("id") != null) {
                id = Long.parseLong(info.get("id"));
            }
            statusOfClient.startPanel();
            return "Увы, у вас нет любимцев. Возвращаем вас к просмотру анкет, чтобы найти их.\n\n" +
                    info.get("description");
        }
        statusOfClient.authPanel();
        return "\nАвторизуйтесь для просмотра любимцев. Перешли в меню авторизации.\n";
    }


    @ShellMethod(key = {"на главную"}, value = "Вернуться в главное меню")
    @ShellMethodAvailability("checkNotInMain")
    public String back() {
        statusOfClient.startPanel();

        Response responseNextProfile = sendRequestReceiveResponse.getNextProfile();
        Map<String, String> info = (Map<String, String>) responseNextProfile.getAddition();
        if (info.get("id") != null) {
            id = Long.parseLong(info.get("id"));
        }

        return "\nВернулись в стартовое меню\n\n" +
                ((Map<String, String>) responseNextProfile.getAddition()).get("description") + "\n";
    }


    public Availability checkAvailability() {
        return statusOfClient.isStart()
                ? Availability.available()
                : Availability.unavailable("вы не находитесь в стартовом меню");
    }

    public Availability checkNotInMain() {
        return !statusOfClient.isStart()
                ? Availability.available()
                : Availability.unavailable("вы уже находитесь в стартовом меню");
    }
}
