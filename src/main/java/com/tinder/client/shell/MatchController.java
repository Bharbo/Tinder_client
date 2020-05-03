package com.tinder.client.shell;

import com.tinder.client.service.SendRequestReceiveResponse;
import com.tinder.client.service.StatusOfClient;
import com.tinder.client.support.ShowResult;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.*;

@ShellComponent
public class MatchController {

    StatusOfClient statusOfClient;
    SendRequestReceiveResponse sendRequestReceiveResponse;
    ShowResult showResult;

    public MatchController(SendRequestReceiveResponse sendRequestReceiveResponse, StatusOfClient statusOfClient, ShowResult showResult) {
        this.sendRequestReceiveResponse = sendRequestReceiveResponse;
        this.statusOfClient = statusOfClient;
        this.showResult = showResult;
    }

    @ShellMethod(key = {"список"}, value = "Показать людей ответивших взаимностью")
    @ShellMethodAvailability("checkAvailability")
    public String showAllMatches() {
        return sendRequestReceiveResponse.AllMatch().getAddition().toString();
    }

    @ShellMethod(key = {"№"}, value = "Показать анкету ответившего взаимностью юзера")
    @ShellMethodAvailability("checkAvailability")
    public String showOne(@ShellOption(defaultValue = "") int number) {
        return showResult.createView(sendRequestReceiveResponse.getOneMatch(number).getAddition().toString());
    }


    public Availability checkAvailability() {
        return statusOfClient.isMatch()
                ? Availability.available()
                : Availability.unavailable("Вы не в меню любимцев");
    }
}
