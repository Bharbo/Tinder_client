package com.tinder.client.service;

import lombok.Getter;

@Getter
public class StatusOfClient {
    private boolean start;
    private boolean match;
    private boolean auth;

    public StatusOfClient() {
        start = true;
        match = false;
        auth = false;
    }

    public void startPanel() {
        start = true;
        match = false;
        auth = false;
    }

    public void matchPanel() {
        start = false;
        match = true;
        auth = false;
    }

    public void authPanel() {
        start = false;
        match = false;
        auth = true;
    }
}
