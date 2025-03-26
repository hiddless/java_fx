package com.hiddless.java_fx.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Builder


public class UserDTO {

    /// Field
    private Integer id;
    private String username;
    private String password;
    private String email;

    public UserDTO(Integer id, String username, String password, String email) {
        this.id= id;
        this.username = username;
        this.password = password;
        this.email = email;

    }
}
