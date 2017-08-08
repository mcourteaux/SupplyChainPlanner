/* 
 * Copyright (C) 2017 Martijn Courteaux.
 *
 * For license details, see LICENSE.txt and README.txt.
 * This project is licensed under Creative Commons NC-NC-ND.
 */
package com.martijncourteaux.supplychainplanner;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author martijn
 */
public class ConsignmentDetails {

    /* Traval information */
    public int location_from;
    public int location_to;
    
    /* Weights to determine cost. */
    public double basic_cost_weight = 1.0;
    public double cost_per_kg_weight;
    public double cost_per_m3_weight;
    public double cost_per_pallet_weight;
    public double duration_hours_weight;

    /* Restrictions on transport. */
    public boolean allow_ferry = true;
    public boolean allow_roads = true;

    /* Actual consignment details. */
    public double weight_kg;
    public double volume_m3;
    public int pallets;

    /* Politics */
    public List<Integer> disallowed_agents = new ArrayList<>();
}
