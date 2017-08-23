/*
 * Copyright (C) 2017 martijn.
 *
 * For license details, see LICENSE.txt and README.txt.
 * This project is licensed under Creative Commons NC-NC-ND.
 */
package com.martijncourteaux.supplychainplanner.generic.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The model for a shipment task (also known as consignment). This at least 
 * contains the source and destination locations and the contained goods.
 * 
 * Extra options and parameters (like priority) can be defined in the your
 * application specific implementation.
 * 
 * @author martijn
 * @param <ALoc> The location model for shipments within your application.
 * @param <AGoods> The goods model within your application.
 */
public abstract class AbstractShipment<ALoc extends AbstractLocation, AGoods extends AbstractGoods> {
    
    /**
     * The source location for the shipment task.
     */
    public ALoc source;
    /**
     * The destination location for the shipment task.
     */
    public ALoc destination;

    /**
     * A list of goods within this shipment.
     */
    public List<AGoods> goods = new ArrayList<>();
}
