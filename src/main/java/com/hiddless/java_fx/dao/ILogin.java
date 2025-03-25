package com.hiddless.java_fx.dao;

import java.util.Optional;

public interface ILogin <T> {

    Optional<T> loginUser(String username, String password);
}
