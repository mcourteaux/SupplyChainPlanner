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
 *
 * @author martijn
 */
public abstract class AbstractShipment<ALoc extends AbstractLocation, AGoods extends AbstractGoods> {
    
    public ALoc source;
    public ALoc destination;

    public List<AGoods> goods = new ArrayList<>();
}
