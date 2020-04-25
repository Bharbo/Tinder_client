package com.tinder.client.domain;

import org.apache.commons.httpclient.URIException;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilderFactory;
//import org.apache.commons.httpclient.util.URIUtil;
import java.net.URI;
import java.net.URISyntaxException;

@Component
public class URL {

    UriBuilderFactory factory = new DefaultUriBuilderFactory();

    public URI right() {
        return factory.uriString("http://localhost:8080")
                .fragment("login")
                .fragment("like")
                .build();
    }

    public URI left() {
        return factory.uriString("http://localhost:8080")
                .fragment("login")
                .fragment("dislike")
                .build();
    }

        public URI next() {
        return factory.uriString("http://localhost:8080")
                .fragment("users")
                .fragment("next")
                .build();
    }

    public URI allMatch() {
        return factory.uriString("http://localhost:8080")
                .fragment("users")
                .fragment("like")
                .fragment("matching")
                .build();
    }

    public URI oneMatch(Long id) {
        return factory.uriString("http://localhost:8080")
                .fragment("users")
                .fragment("like")
                .fragment("matching")
                .queryParam("id", id)
                .build();
    }

    public URI logIn() {
        return factory.uriString("http://localhost:8080")
                .fragment("login")
                .build();
    }

    public URI reg() {
        return factory.uriString("http://localhost:8080")
                .fragment("register")
                .build();
    }

    public URI edit() {
        return factory.uriString("http://localhost:8080")
                .fragment("login")
                .fragment("edit")
                .build();
    }

    public URI delete() {
        return factory.uriString("http://localhost:8080")
                .fragment("login")
                .fragment("edit")
                .fragment("delete")
                .build();
    }
}
