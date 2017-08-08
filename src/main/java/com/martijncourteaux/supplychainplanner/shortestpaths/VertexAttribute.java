/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
