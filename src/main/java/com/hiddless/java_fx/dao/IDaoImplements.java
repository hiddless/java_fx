package com.hiddless.java_fx.dao;

import com.hiddless.java_fx.database.SingletonPropertiesDBConnection;

import java.sql.Connection;

public interface IDaoImplements<T> extends ICrud<T>,IGenericsMethod<T> {

    default Connection iDaoImplementsDatabaseConnection() {
        return SingletonPropertiesDBConnection.getInstance().getConnection();
    }
}