package com.tinder.client.shell;

import com.tinder.client.support.Response;
import com.tinder.client.service.SendRequestReceiveResponse;
import com.tinder.client.service.StatusOfClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.*;

@ShellComponent
public class AuthorizationController {
    StatusOfClient statusOfClient;
    SendRequestReceiveResponse sendRequestReceiveResponse;

//    @Autowired
    public AuthorizationController(SendRequestReceiveResponse sendRequestReceiveResponse, StatusOfClient statusOfClient) {
        this.sendRequestReceiveResponse = sendRequestReceiveResponse;
        this.statusOfClient = statusOfClient;
    }

    @ShellMethod(key = {"новая"}, value = "создание учетной записи")
    @ShellMethodAvailability("checkAvailability")
    public String register(@ShellOption(defaultValue = "") String username, @ShellOption(defaultValue = "") String password,
                           @ShellOption(defaultValue = "") String gender, @ShellOption(defaultValue = "") String description) {
        if (username.isEmpty() || password.isEmpty()
                || gender.isEmpty() || description.isEmpty())
            return "Вы \"сударь\" иль \"сударыня\"? Как вас величать? Ваш секретный шифръ? Какіе вы и что вы ищите?";
        Response response = sendRequestReceiveResponse.regNewUser(username, password, gender, description);
        if (response.isStatus()) {
            statusOfClient.startPanel();
        }
        return response.getAddition().toString();
    }

    @ShellMethod(key = {"войти"}, value = "Ввести свои логин и пароль")
    @ShellMethodAvailability("checkAvailability")
    public String logIn(@ShellOption(defaultValue = "") String username,
                        @ShellOption(defaultValue = "") String password) {
        if (username.isEmpty() || password.isEmpty())
            return "Как вас величать? Ваш секретный шифръ?";
        Response response = sendRequestReceiveResponse.logIn(username, password);
        if (response.isStatus()) {
            statusOfClient.startPanel();
        }
        return response.getAddition().toString();
    }

    @ShellMethod(key = {"изменить"}, value = "Изменить описание профиля")
    @ShellMethodAvailability("checkAvailability")
    public String changeDesc(@ShellOption(defaultValue = "") String desc) {
        Response response = sendRequestReceiveResponse.changeDescription(desc);
        return response.getAddition().toString();
    }

    @ShellMethod(key = {"удалить"}, value = "Удаление профиля")
    @ShellMethodAvailability("checkAvailability")
    public String delProfile() {
        Response response = sendRequestReceiveResponse.deleteProfile();

        if (!response.isStatus()) {
            return response.getAddition().toString();
        } else {
            return response.getAddition().toString() + "\n" +
                    sendRequestReceiveResponse.getNextProfile().getAddition().toString() + "\n";
        }
    }


    public Availability checkAvailability() {
        return statusOfClient.isAuth()
                ? Availability.available()
                : Availability.unavailable("Вы не в меню авторизации");
    }

}
