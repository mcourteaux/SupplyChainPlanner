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
 * A Context that is passed to most of the abstract functions to be implemented
 * by the user of this framework. The context can be used for storing cached
 * results.
 *
 * @author martijn
 */
public abstract class AbstractContext {

    public Connection connection;

    /**
     * Allows the user to set up context. Should at least call
     * <code>connectDatabase()</code>.
     * 
     * @throws Exception 
     */
    public abstract void prepareContext() throws Exception;

    /**
     * Allows the user to tear down the context. Should at least call
     * <code>disconnectDatabase()</code>.
     * 
     * @throws Exception 
     */
    public abstract void destroyContext() throws Exception;

    /**
     * Establishes a <code>java.sql.Connection</code> to the database, and
     * stores the result in the class member <code>connection</code>.
     *
     * @throws SQLException 
     */
    public abstract void connectDatabase() throws SQLException;

    /**
     * Calls <code>close()</code> on the SQL connection.
     * @throws SQLException 
     */
    public void disconnectDatabase() throws SQLException {
        connection.close();
    }
}
