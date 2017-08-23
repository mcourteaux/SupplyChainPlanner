/*
 * Copyright (C) 2017 martijn.
 *
 * For license details, see LICENSE.txt and README.txt.
 * This project is licensed under Creative Commons NC-NC-ND.
 */
package com.martijncourteaux.supplychainplanner.generic.model;

/**
 * An abstract class for the user to model a location, within the application. A
 * location is used to specify most likely from where to where transport options
 * are valid. Also the location of warehouses will be modeled in here.
 *
 * @author martijn
 */
public abstract class AbstractLocation {
    
    /**
     * Will determine whether two locations (<code>this</code> and
     * <code>other</code>) locations are "compatible". Compatible means:
     * <ul>
     *  <li>It is possible to cross dock from one location to the other.</li>
     * </ul>
     * 
     * Compatibility is most likely:
     * <ul>
     *  <li>reflective</li>
     *  <li>associative</li>
     * </ul>
     * 
     * In case your application works with warehouses (which is likely),
     * compatibility should be equivalent to equality (i.e.: goods should be
     * cross-docked within the same warehouse, and are not able to teleport from
     * one warehouse to another).
     * 
     * If you define multiple subclasses of AbstractLocation within your
     * application, they must be able to determine compatibility for
     * heterogenous location types.
     * 
     * @param other The destination location for cross docking.
     * @return <code>true</code> if they are compatible, <code>false</code>
     * otherwise.
     */
    public abstract boolean isCompatible(AbstractLocation other);
}
