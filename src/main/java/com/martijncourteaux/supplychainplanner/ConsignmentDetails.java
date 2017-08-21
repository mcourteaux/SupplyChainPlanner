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

    @Override
    public String toString() {
        return "ConsignmentDetails{\n"
                + " location_from=" + location_from + ",\n"
                + " location_to=" + location_to + ",\n"
                + " basic_cost_weight=" + basic_cost_weight + ",\n"
                + " cost_per_kg_weight=" + cost_per_kg_weight + ",\n"
                + " cost_per_m3_weight=" + cost_per_m3_weight + ",\n"
                + " cost_per_pallet_weight=" + cost_per_pallet_weight + ",\n"
                + " duration_hours_weight=" + duration_hours_weight + ",\n"
                + " allow_ferry=" + allow_ferry + ",\n"
                + " allow_roads=" + allow_roads + ",\n"
                + " weight_kg=" + weight_kg + ",\n"
                + " volume_m3=" + volume_m3 + ",\n"
                + " pallets=" + pallets + ",\n"
                + " disallowed_agents=" + disallowed_agents + "\n"
                + "}";
    }

}
