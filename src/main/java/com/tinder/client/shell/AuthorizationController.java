package com.tinder.client.shell;

import com.tinder.client.support.Response;
import com.tinder.client.service.SendRequestReceiveResponse;
import com.tinder.client.service.StatusOfClient;
import com.tinder.client.support.ShowResult;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.*;

import java.util.Map;

@ShellComponent
public class AuthorizationController {
    StatusOfClient statusOfClient;
    SendRequestReceiveResponse sendRequestReceiveResponse;
    StartController startController;
    ShowResult showResult;

    public AuthorizationController(SendRequestReceiveResponse sendRequestReceiveResponse, StatusOfClient statusOfClient, StartController startController, ShowResult showResult) {
        this.sendRequestReceiveResponse = sendRequestReceiveResponse;
        this.statusOfClient = statusOfClient;
        this.startController = startController;
        this.showResult = showResult;
    }

    @ShellMethod(key = {"новая"}, value = "создание учетной записи")
    @ShellMethodAvailability("checkAvailability")
    public String register(@ShellOption(defaultValue = "") String gender, @ShellOption(defaultValue = "") String username,
                           @ShellOption(defaultValue = "") String password, @ShellOption(defaultValue = "") String description) {

        if (username.isEmpty() || password.isEmpty()
                || gender.isEmpty() || description.isEmpty())
            return "Вы \"сударь\" иль \"сударыня\"? Как вас величать? Ваш секретный шифръ? Какіе вы и что вы ищите?";
        Response responseReg = sendRequestReceiveResponse.registration(username, password, gender, description);
        if (responseReg.isStatus()) {
            statusOfClient.startPanel();
            Response responseNextProfile = sendRequestReceiveResponse.getNextProfile();
            Map<String, String> info = (Map<String, String>) responseNextProfile.getAddition();
            if (info.get("id") != null) {
                startController.id = Long.parseLong(info.get("id"));
            }
            return responseReg.getAddition().toString() + "\n" +
                    showResult.createView(info.get("description"));
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
            Map<String, String> info = (Map<String, String>) responseNextProfile.getAddition();
            if (info.get("id") != null) {
                startController.id = Long.parseLong(info.get("id"));
            }
            if (!responseNextProfile.isStatus()) {
                statusOfClient.matchPanel();
                return responseLogin.getAddition().toString() + "\n"
                        + showResult.createView(info.get("description")) + "\n"
                        + "Переводим вас в меню любимцев!\n\n"
                        + sendRequestReceiveResponse.AllMatch().getAddition().toString() + "\n";
            }
            return responseLogin.getAddition().toString() + "\n" + showResult.createView(info.get("description"));
        }
        return "\n|| Неудача, попробуйте снова ||\n";
    }

    @ShellMethod(key = {"изменить"}, value = "Изменить описание профиля")
    @ShellMethodAvailability("checkAvailability")
    public String changeDesc(@ShellOption(defaultValue = "") String desc) {
        Response responseDesc = sendRequestReceiveResponse.changeDescription(desc);

        Response responseNextProfile = sendRequestReceiveResponse.getNextProfile();
        Map<String, String> info = (Map<String, String>) responseNextProfile.getAddition();
        if (info.get("id") != null) {
            startController.id = Long.parseLong(info.get("id"));
        }

        if (responseDesc.isStatus()) {
            statusOfClient.startPanel();
            return "|| Успехъ. Ваша анкета: ||\n" + responseDesc.getAddition().toString() + "\n" + showResult.createView(info.get("description"));
        } else {
            return responseDesc.getAddition().toString();
        }
    }

    @ShellMethod(key = {"удалить"}, value = "Удаление профиля")
    @ShellMethodAvailability("checkAvailability")
    public String delProfile() {
        Response responseDel = sendRequestReceiveResponse.deleteProfile();
        if (responseDel.isStatus()) {
            statusOfClient.startPanel();
            Response responseNextProfile = sendRequestReceiveResponse.getNextProfile();
            Map<String, String> info = (Map<String, String>) responseNextProfile.getAddition();
            if (info.get("id") != null) {
                startController.id = Long.parseLong(info.get("id"));
            }
            return responseDel.getAddition().toString() + "\n" + showResult.createView(info.get("description"));
        }

        return responseDel.getAddition().toString();
    }

    @ShellMethod(key = {"выйти"}, value = "Выход из учетной записи")
    @ShellMethodAvailability("checkAvailability")
    public String logOut() {
        Response responseDel = sendRequestReceiveResponse.logOut();
        if (responseDel.isStatus()) {
            Response responseNextProfile = sendRequestReceiveResponse.getNextProfile();
            Map<String, String> info = (Map<String, String>) responseNextProfile.getAddition();
            if (info.get("id") != null) {
                startController.id = Long.parseLong(info.get("id"));
            }
            statusOfClient.startPanel();
            return responseDel.getAddition().toString() + "\n" + showResult.createView(info.get("description"));
        }
        return responseDel.getAddition().toString();
    }


    public Availability checkAvailability() {
        return statusOfClient.isAuth()
                ? Availability.available()
                : Availability.unavailable("Вы не в меню авторизации");
    }
}
