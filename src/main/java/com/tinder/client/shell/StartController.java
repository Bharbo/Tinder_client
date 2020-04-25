package com.tinder.client.shell;

import com.tinder.client.domain.Response;
import com.tinder.client.service.SendRequestReceiveResponse;
import com.tinder.client.service.StatusOfClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

@ShellComponent
public class StartController {
    StatusOfClient statusOfClient;
    SendRequestReceiveResponse sendRequestReceiveResponse;

    @Autowired
    public StartController(SendRequestReceiveResponse sendRequestReceiveResponse, StatusOfClient statusOfClient) {
        this.sendRequestReceiveResponse = sendRequestReceiveResponse;
        this.statusOfClient = statusOfClient;
        initial();
    }

    public void initial() {
        System.out.println("Вы вошли как неизвестный. Создайте свой аккаунт или войдите в существующий.\n");
        Response response = sendRequestReceiveResponse.getNextProfile();

        System.out.println(response.getAddition().toString() + "\n");
    }

    @ShellMethod(key = {"вправо"}, value = "Свайп вправо для проявления симпатии")
    @ShellMethodAvailability("checkAvailability")
    public String right() {
        Response response = sendRequestReceiveResponse.like();
        if (!response.isStatus()) {
            return sendRequestReceiveResponse.getNextProfile().getAddition().toString() + "\n";
        }
        return response.getAddition().toString() + "\n" +
                sendRequestReceiveResponse.getNextProfile().getAddition().toString() + "\n";
    }


    @ShellMethod(key = {"влево"}, value = "Свайп влево для проявления антипатии")
    @ShellMethodAvailability("checkAvailability")
    public String left() {
        Response response = sendRequestReceiveResponse.dislike();
        if (!response.isStatus()) {
            return sendRequestReceiveResponse.getNextProfile().getAddition().toString() + "\n";
        }
        return response.getAddition().toString() + "\n" +
                sendRequestReceiveResponse.getNextProfile().getAddition().toString() + "\n";
    }

    @ShellMethod(key = {"анкета"}, value = "Перейти в меню авторизации")
    @ShellMethodAvailability("checkAvailability")
    public String profile() {
        statusOfClient.goAuth();
        return "\nПерешли в меню авторизации\n";
    }

    @ShellMethod(key = {"любимцы"}, value = "Перейти в меню любимцев")
    @ShellMethodAvailability("checkAvailability")
    public String matches() {
        statusOfClient.goMatch();
        return "\nПерешли в меню любимцев\n\n"
                + sendRequestReceiveResponse.showAllMatch().getAddition().toString() + "\n";
    }

    @ShellMethod(key = {"уйти"}, value = "Вернуться в стартовое меню")
    @ShellMethodAvailability("checkNotInMain")
    public String back() {
        statusOfClient.goStart();
        return "\nВернулись в стартовое меню\n";
    }




    public Availability checkAvailability() {
        return statusOfClient.isStart()
                ? Availability.available()
                : Availability.unavailable("вы не находитесь въ «Главномъ управленіи»");
    }

    public Availability checkNotInMain() {
        return !statusOfClient.isStart()
                ? Availability.available()
                : Availability.unavailable("вы уже находитесь въ «Главномъ управленіи»");
    }
}
