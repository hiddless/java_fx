package com.hiddless.java_fx.dao;

import java.util.List;
import java.util.Optional;

public interface ICrud<T> {

    Optional<T> create(T t);
    Optional<List<T>> list();
    Optional<T> findByName(String name);
    Optional<T> findById(int id);
    Optional<T> update(int id, T t);
    Optional<T> delete(int id);
}
