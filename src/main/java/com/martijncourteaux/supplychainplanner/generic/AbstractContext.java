/*
 * Copyright (C) 2017 martijn.
 *
 * For license details, see LICENSE.txt and README.txt.
 * This project is licensed under Creative Commons NC-NC-ND.
 */
package com.martijncourteaux.supplychainplanner.generic;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author martijn
 */
public abstract class AbstractContext {

    public Connection connection;

    public abstract void prepareContext() throws Exception;
    public abstract void destroyContext() throws Exception;

    public abstract void connectDatabase() throws SQLException;

    public void disconnectDatabase() throws SQLException {
        connection.close();
    }
}
