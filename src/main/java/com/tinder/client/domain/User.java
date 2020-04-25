package com.tinder.client.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    private Long id;
    private String gender;
    private String username;
    private String password;
    private String description;
}
