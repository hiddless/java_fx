package com.hiddless.java_fx.utils;

import com.hiddless.java_fx.dto.UserDTO;

public class SessionManager {
    public static UserDTO currentUser;

    public static void setCurrentUser(UserDTO user) {
        currentUser = user;
    }

    public static UserDTO getCurrentUser() {
        return currentUser;
    }

    public static void clear() {
        currentUser = null;
    }
}
