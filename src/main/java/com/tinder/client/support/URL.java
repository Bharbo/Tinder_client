package com.tinder.client.support;

import java.net.URI;

public class URL {

    public URI right() {
        return URI.create(StrURL.RIGHT.label);
    }

    public URI left() {
        return URI.create(StrURL.LEFT.label);
    }

    public URI noAuthNext(int num) {
        return URI.create(StrURL.NEXT.label + "?num=" + num);
    }

    public URI authNext() {
        return URI.create(StrURL.NEXT.label);
    }

    public URI allMyMatch(Long id) {
        return URI.create(StrURL.ALLMATCH.label + id);
    }

    public URI logIn() {
        return URI.create(StrURL.LOGIN.label);
    }

    public URI reg() {
        return URI.create(StrURL.REG.label);
    }

    public URI edit() {
        return URI.create(StrURL.EDIT.label);
    }

    public URI delete() {
        return URI.create(StrURL.DELETE.label);
    }

    public URI currentUser() {
        return URI.create(StrURL.CURRENTUSER.label);
    }

    public URI breakLoggedUser() {
        return URI.create(StrURL.BREAKLOGGEDUSER.label);
    }

    public URI logOut() {
        return URI.create(StrURL.LOGOUT.label);
    }
}
