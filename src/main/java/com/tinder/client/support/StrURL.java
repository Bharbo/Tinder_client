package com.tinder.client.support;

public enum StrURL {
    RIGHT("http://localhost:8080/login/like"),
    LEFT("http://localhost:8080/login/dislike"),
    NEXT("http://localhost:8080/login/users/next"),
    ALLMATCH("http://localhost:8080/me/like/matching/"),
    LOGIN("http://localhost:8080/login"),
    REG("http://localhost:8080/register"),
    EDIT("http://localhost:8080/login/edit"),
    DELETE("http://localhost:8080/login/edit/delete"),

    BREAKLOGGEDUSER("http://localhost:8080/breakuser"),
    CURRENTUSER("http://localhost:8080/login/currentuser"),
    LOGOUT("http://localhost:8080/logout");

    public final String label;

    StrURL(String label) {
        this.label = label;
    }
}
