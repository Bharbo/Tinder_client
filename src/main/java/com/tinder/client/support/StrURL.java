package com.tinder.client.support;

public enum StrURL {
    RIGHT("http://localhost:8080/login/like"),
    LEFT("http://localhost:8080/login/dislike"),
    NEXT("http://localhost:8080/users/next"),
    ALLMATCH("http://localhost:8080/me/like/matching/"),
    LOGIN("http://localhost:8080/login"),
    REG("http://localhost:8080/register"),
    EDIT("http://localhost:8080/login/edit"),
    DELETE("http://localhost:8080/login/edit/delete"),

    CURRENTUSER("http://localhost:8080/login/currentuser");

    public final String label;

    StrURL(String label) {
        this.label = label;
    }
}






















//    AUTHNEXT("http://localhost:8080/users/next"),
//ONEMATCH("http://localhost:8080/users/like/matching/"),