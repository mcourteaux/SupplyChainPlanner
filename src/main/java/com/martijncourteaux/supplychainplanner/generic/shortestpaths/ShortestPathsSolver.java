/*
 * Copyright (C) 2017 martijn.
 *
 * For license details, see LICENSE.txt and README.txt.
 * This project is licensed under Creative Commons NC-NC-ND.
 */
package com.martijncourteaux.supplychainplanner.generic.shortestpaths;

import edu.asu.emit.algorithm.graph.Path;
import edu.asu.emit.algorithm.graph.shortestpaths.YenTopKShortestPathsAlg;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author martijn
 */
public class ShortestPathsSolver {

    private final TransportGraph graph;
    private final YenTopKShortestPathsAlg topKShortestPathsAlg;
    private final List<TransportPath> topKShortestPaths;

    public ShortestPathsSolver(TransportGraph graph) {
        this.graph = graph;
        this.topKShortestPathsAlg = new YenTopKShortestPathsAlg(graph.getGraph(), graph.getSource(), graph.getSink());
        this.topKShortestPaths = new ArrayList<>();
    }

    public int searchPaths(int num) {
        for (int i = 0; i < num; ++i) {
            if (topKShortestPathsAlg.hasNext()) {
                Path p = topKShortestPathsAlg.next();
                topKShortestPaths.add(new TransportPath(p, graph));
            } else {
                return i;
            }
        }
        return num;
    }

    public List<TransportPath> getTopKShortestPaths() {
        return topKShortestPaths;
    }

}
