/*
 * Copyright (C) 2017 martijn.
 *
 * For license details, see LICENSE.txt and README.txt.
 * This project is licensed under Creative Commons NC-NC-ND.
 */
package com.martijncourteaux.supplychainplanner.generic.logic;

import com.martijncourteaux.supplychainplanner.generic.AbstractContext;
import com.martijncourteaux.supplychainplanner.generic.model.AbstractShipment;
import com.martijncourteaux.supplychainplanner.generic.model.AbstractTransport;
import java.sql.SQLException;
import java.util.List;

/**
 * This abstract class will be used to fetch available transport options from
 * the database that are compatible with the given Shipment task.
 * 
 * @author martijn
 * @param <AContext> The context for your application.
 * @param <AShipment> The shipment class for your application.
 * @param <ATransport> The transport class for your application.
 */
public abstract class TransportFilter<AContext extends AbstractContext, AShipment extends AbstractShipment<?, ?>, ATransport extends AbstractTransport> {

    /**
     * Textually builds the query for fetching the available transport options.
     * Does not perform actual query.
     * 
     * @param ctx The context of your application will be passed in.
     * @param shipment The shipment for which to fetch transport options.
     * 
     * @return The SQL query in string representation.
     */
    public abstract String query(AContext ctx, AShipment shipment);

    /**
     * Fetches the available transport options from the database, and parses the
     * result into the transport class of your application.
     * 
     * @param ctx The context of your application will be passed in.
     * @param shipment The shipment for which to fetch transport options.
     * 
     * @return A list of the available transport options.
     * @throws SQLException 
     */
    public abstract List<ATransport> fetch(AContext ctx, AShipment shipment) throws SQLException;

    /**
     * Allows you to do some post processing after the fetch. It is perfectly
     * fine to leave this method empty. This is typically used for doing some
     * more complex filtering that is not trivially possible within the SQL
     * query.
     * 
     * It's up to you to decide if you will process this list in-place or
     * construct a new list to return.
     * 
     * @param ctx The context of your application will be passed in.
     * @param transports The list of transport options fetched from the database.
     * @param shipment The shipment which was used to fetch the transport options.
     * 
     * @return A list of filtered transport options.
     */
    public abstract List<ATransport> postQueryFilter(AContext ctx, List<ATransport> transports, AShipment shipment);
}
