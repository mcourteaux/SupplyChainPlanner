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
 *
 * @author martijn
 */
public abstract class TransportFilter<AContext extends AbstractContext, AShipment extends AbstractShipment<?, ?>, ATransport extends AbstractTransport> {

    public abstract String query(AContext ctx, AShipment shipment);

    public abstract List<ATransport> fetch(AContext ctx, AShipment shipment) throws SQLException;

    public abstract List<ATransport> postQueryFilter(AContext ctx, List<ATransport> transports, AShipment shipment);
}
