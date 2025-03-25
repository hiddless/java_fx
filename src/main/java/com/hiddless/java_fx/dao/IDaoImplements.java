package com.hiddless.java_fx.dao;

import java.sql.Connection;

public interface IDaoImplements<T> extends ICrud<T>,IGenericsMethod<T>,ILogin<T>{

    default Connection iDaoImplementsDatabaseConnection() {
        return SingletonDBConnection.getInstance().getConnection();
    }
}
