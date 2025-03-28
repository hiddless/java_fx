package com.hiddless.java_fx.dao;

import com.hiddless.java_fx.database.SingletonDBConnection;

import java.sql.Connection;

public interface IDaoImplements<T> extends ICrud<T>,IGenericsMethod<T>,ILogin<T> {

    default Connection iDaoImplementsDatabaseConnection() {
        return SingletonDBConnection.getInstance().getConnection();
    }
}