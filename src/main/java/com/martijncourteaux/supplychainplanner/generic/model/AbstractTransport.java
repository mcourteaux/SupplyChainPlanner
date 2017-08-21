/*
 * Copyright (C) 2017 martijn.
 *
 * For license details, see LICENSE.txt and README.txt.
 * This project is licensed under Creative Commons NC-NC-ND.
 */
package com.martijncourteaux.supplychainplanner.generic.model;

/**
 *
 * @author martijn
 * @param <ALoc>
 */
public abstract class AbstractTransport<ALoc extends AbstractLocation> {

    public ALoc source;
    public ALoc destination;

    public String toSingleLineString() {
        return toString();
    }

    public abstract double cost();

    public double weight() {
        return cost();
    }
}
