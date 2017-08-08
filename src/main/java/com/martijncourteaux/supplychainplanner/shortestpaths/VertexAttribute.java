/* 
 * Copyright (C) 2017 Martijn Courteaux.
 *
 * For license details, see LICENSE.txt and README.txt.
 * This project is licensed under Creative Commons NC-NC-ND.
 */
package com.martijncourteaux.supplychainplanner.shortestpaths;

/**
 *
 * @author martijn
 */
public class VertexAttribute {

    public boolean is_auxiliary_offer_out_vertex;
    public double cost;
    public int offer_id;
    public int agent_id;
    public String line_code;
    public int line_from;
    public int line_to;
    public int duration;
    public String line_modality;

    public boolean is_location_vertex;
    public int location_id;
    public String location_code;
}
