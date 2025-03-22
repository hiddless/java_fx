package com.hiddless.java_fx.dao;

import com.hiddless.java_fx.database.SingletonDBConnection;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface IDaoImplements <T> {

    // CREATE
    Optional<T> create(T t);

    // LIST
    Optional<List<T>>list();

    // FIND
    Optional<T> findByName(String name);
    Optional<T> findById(int id);

    // UPDATE
    Optional<T> update(int id, T entity);

    // DELETE
    Optional<T> delete(int id);

    // Gövdeli Method
    default Connection iDaoImplementsDatabaseConnection(){
        // Singleton DB
        return SingletonDBConnection.getInstance().getConnection();

        // Sşingleton Config
        //return SingletonPropertiesDBConnection.getInstance().getConnection();
    }
}