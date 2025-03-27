package com.hiddless.java_fx.dto;

import com.hiddless.java_fx.utils.ERole;
import lombok.*;

@Getter
@Setter
//@AllArgsConstructor
@NoArgsConstructor
@ToString


public class UserDTO {

    private Integer id;
    private String username;
    private String password;
    private String email;
    private ERole role;

    public UserDTO(Integer id, String username, String password, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }
}