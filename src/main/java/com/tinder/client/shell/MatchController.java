package com.tinder.client.shell;

import com.tinder.client.service.SendRequestReceiveResponse;
import com.tinder.client.service.StatusOfClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.*;

@ShellComponent
public class MatchController {

    StatusOfClient statusOfClient;
    SendRequestReceiveResponse sendRequestReceiveResponse;

//    @Autowired
    public MatchController(SendRequestReceiveResponse sendRequestReceiveResponse, StatusOfClient statusOfClient) {
        this.sendRequestReceiveResponse = sendRequestReceiveResponse;
        this.statusOfClient = statusOfClient;
    }


    @ShellMethod(key = {"любимцы"}, value = "Показать людей ответивших взаимностью")
    @ShellMethodAvailability("checkAvailability")
    public String showAllMatches() {
        return sendRequestReceiveResponse.AllMyMatch().getAddition().toString() + "\n";
    }


    @ShellMethod(key = {"выбор профиля"}, value = "Показать анкету ответившего взаимностью юзера")
    @ShellMethodAvailability("checkAvailability")
    public String showOne(@ShellOption int number) {
        return sendRequestReceiveResponse.getOneMatch(number).getAddition() + "\n";
    }




    public Availability checkAvailability() {
        return statusOfClient.isMatch()
                ? Availability.available()
                : Availability.unavailable("Вы не в меню любимцев");
    }
}
