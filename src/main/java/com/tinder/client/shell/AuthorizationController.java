package com.tinder.client.shell;

import com.tinder.client.support.Response;
import com.tinder.client.service.SendRequestReceiveResponse;
import com.tinder.client.service.StatusOfClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.*;

import java.util.Map;

@ShellComponent
public class AuthorizationController {
    StatusOfClient statusOfClient;
    SendRequestReceiveResponse sendRequestReceiveResponse;
    StartController startController;

    //    @Autowired
    public AuthorizationController(SendRequestReceiveResponse sendRequestReceiveResponse, StatusOfClient statusOfClient, StartController startController) {
        this.sendRequestReceiveResponse = sendRequestReceiveResponse;
        this.statusOfClient = statusOfClient;
        this.startController = startController;
    }

    @ShellMethod(key = {"новая"}, value = "создание учетной записи")
    @ShellMethodAvailability("checkAvailability")
    public String register(@ShellOption String gender, @ShellOption String username,
                           @ShellOption String password, @ShellOption String description) {
//            public String register(@ShellOption(defaultValue = "") String username, @ShellOption(defaultValue = "") String password,
//                           @ShellOption(defaultValue = "") String gender, @ShellOption(defaultValue = "") String description) {
//
        if (username.isEmpty() || password.isEmpty()
                || gender.isEmpty() || description.isEmpty())
            return "Вы \"сударь\" иль \"сударыня\"? Как вас величать? Ваш секретный шифръ? Какіе вы и что вы ищите?";
        Response responseReg = sendRequestReceiveResponse.regNewUser(username, password, gender, description);
        if (responseReg.isStatus()) {
            statusOfClient.startPanel();
            Response responseNextProfile = sendRequestReceiveResponse.getNextProfile();
            Map<String, String> info = (Map<String, String>)responseNextProfile.getAddition();
            if (info.get("id") != null) {
                startController.id = Long.parseLong(info.get("id"));
            }
            return responseReg.getAddition().toString() + "\n" +
                    ((Map<String, String>) responseNextProfile.getAddition()).get("description") + "\n";
        }
        return responseReg.getAddition().toString();
    }

    @ShellMethod(key = {"войти"}, value = "Ввести свои логин и пароль")
    @ShellMethodAvailability("checkAvailability")
    public String logIn(@ShellOption(defaultValue = "") String username,
                        @ShellOption(defaultValue = "") String password) {
        if (username.isEmpty() || password.isEmpty())
            return "Как вас величать? Ваш секретный шифръ?";
        Response responseLogin = sendRequestReceiveResponse.logIn(username, password);
        if (responseLogin.isStatus()) {
            statusOfClient.startPanel();
            Response responseNextProfile = sendRequestReceiveResponse.getNextProfile();
            Map<String, String> info = (Map<String, String>)responseNextProfile.getAddition();
            if (info.get("id") != null) {
                startController.id = Long.parseLong(info.get("id"));
            }

            return responseLogin.getAddition().toString() + "\n" + info.get("description") + "\n";

//            return responseLogin.getAddition().toString() + "\n" +
//                    sendRequestReceiveResponse.getNextProfile().getAddition().toString() + "\n";
        }
        return "\n|| Неудача, попробуйте снова ||\n";
    }

    @ShellMethod(key = {"изменить"}, value = "Изменить описание профиля")
    @ShellMethodAvailability("checkAvailability")
    public String changeDesc(@ShellOption/*(defaultValue = "") */ String desc) {
        Response responseDesc = sendRequestReceiveResponse.changeDescription(desc);

        Response responseNextProfile = sendRequestReceiveResponse.getNextProfile();
        Map<String, String> info = (Map<String, String>)responseNextProfile.getAddition();
        if (info.get("id") != null) {
            startController.id = Long.parseLong(info.get("id"));
        }

        if (responseDesc.isStatus()) {
            statusOfClient.startPanel();
            return "|| Успехъ. Ваша анкета: ||\n" + responseDesc.getAddition().toString() + "\n" + info.get("description") + "\n";
        } else {
            return responseDesc.getAddition().toString();
        }

        //        statusOfClient.startPanel();///
//        return responseDesc.isStatus() ?
//                "|| Успехъ. Ваша анкета: ||\n" + responseDesc.getAddition().toString() + "\n" + info.get("description") + "\n" :
//                responseDesc.getAddition().toString();
    }

    @ShellMethod(key = {"удалить"}, value = "Удаление профиля")
    @ShellMethodAvailability("checkAvailability")
    public String delProfile() {
        Response responseDel = sendRequestReceiveResponse.deleteProfile();
        if (responseDel.isStatus()) {
            statusOfClient.startPanel();
            Response responseNextProfile = sendRequestReceiveResponse.getNextProfile();
            Map<String, String> info = (Map<String, String>)responseNextProfile.getAddition();
            if (info.get("id") != null) {
                startController.id = Long.parseLong(info.get("id"));
            }
            return responseDel.getAddition().toString() + "\n" + info.get("description") + "\n";
        }

        return responseDel.getAddition().toString();
    }


    @ShellMethod(key = {"выйти"}, value = "Выход из учетной записи")
    @ShellMethodAvailability("checkAvailability")
    public String logOut() {
        Response responseDel = sendRequestReceiveResponse.logOut();
        if (responseDel.isStatus()) {
            Response responseNextProfile = sendRequestReceiveResponse.getNextProfile();
            Map<String, String> info = (Map<String, String>)responseNextProfile.getAddition();
            if (info.get("id") != null) {
                startController.id = Long.parseLong(info.get("id"));
            }
            statusOfClient.startPanel();
            return responseDel.getAddition().toString() + "\n" + info.get("description") + "\n";
        }
        return responseDel.getAddition().toString();
    }


    public Availability checkAvailability() {
        return statusOfClient.isAuth()
                ? Availability.available()
                : Availability.unavailable("Вы не в меню авторизации");
    }

}
