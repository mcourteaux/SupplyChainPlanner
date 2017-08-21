/*
 * Copyright (C) 2017 martijn.
 *
 * For license details, see LICENSE.txt and README.txt.
 * This project is licensed under Creative Commons NC-NC-ND.
 */
package com.martijncourteaux.supplychainplanner.poc.planning;

import com.martijncourteaux.supplychainplanner.poc.shortestpaths.ShortestPathsSolver;
import java.util.List;

/**
 *
 * @author martijn
 */
public class BatchPlanner {
    
    private final List<ShortestPathsSolver> leftToPlan;

    public BatchPlanner(List<ShortestPathsSolver> consignments) {
        this.leftToPlan = consignments;
    }


    public void plan() {

    }
    

}
