/*
 * Copyright (C) 2017 martijn.
 *
 * For license details, see LICENSE.txt and README.txt.
 * This project is licensed under Creative Commons NC-NC-ND.
 */
package com.martijncourteaux.supplychainplanner.generic.model;

/**
 * Models a transport option for a shipment. This contains at least the source
 * and destination locations for this transport option.
 * 
 * A real-world currency valued <code>cost</code> should be defined.
 * Also a <code>weight</code> function can be specified for weighting these 
 * transport options differently (e.g.: penalize time).
 * 
 * @author martijn
 * @param <ALoc> The location model used for transports within your application.
 */
public abstract class AbstractTransport<ALoc extends AbstractLocation> {

    /**
     * The source location for this transport option.
     */
    public ALoc source;
    /**
     * The destination location for this transport option.
     */
    public ALoc destination;

    /**
     * Produces a single-line string representation for debugging purposes.
     * This should probably include:
     * <ul>
     *  <li>Source location</li>
     *  <li>Cost</li>
     *  <li>Weight</li>
     *  <li>Destination location</li>
     * </ul>
     * @return A single-line string representation.
     */
    public String toSingleLineString() {
        return toString();
    }

    /**
     * A real-world currency cost associated with this transport option.
     * 
     * @return A double value representing the cost.
     */
    public abstract double cost();

    /**
     * A custom weight function used to change the weighting of arcs within the
     * graph search algorithm. Allows you to penalize or promote certain aspects.
     * 
     * E.g.: penalize time, or favor ferry transports, penalize sloppy
     * sub-contractors.
     * 
     * Defaults to the <code>cost()</code> function.
     *
     * @return A double value representing the weight.
     */
    public double weight() {
        return cost();
    }
}
