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
 * A tiny wrapper around <code>k-shortest-paths-java-version</code> repository.
 * 
 * @author martijn
 */
public class ShortestPathsSolver {

    /**
     * The transport graph in which to find paths.
     */
    private final TransportGraph graph;
    /**
     * The algorithm itself.
     */
    private final YenTopKShortestPathsAlg topKShortestPathsAlg;
    /**
     * A list of the shortest paths.
     */
    private final List<TransportPath> topKShortestPaths;

    /**
     * Default constructor.
     * @param graph The TransportGraph to use.
     */
    public ShortestPathsSolver(TransportGraph graph) {
        this.graph = graph;
        this.topKShortestPathsAlg = new YenTopKShortestPathsAlg(graph.getGraph(), graph.getSource(), graph.getSink());
        this.topKShortestPaths = new ArrayList<>();
    }

    /**
     * Tries to find the next <code>num</code> shortest paths in the graph.
     * If it can't find the request amount of paths, a number lower than
     * <code>num</code> will be returned.
     * 
     * @param num The amount of new shortest paths that should be searched for.
     * 
     * @return The number of found paths.
     */
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

    /**
     * Returns the list of paths found so far.
     * 
     * @return The list of paths found so far.
     */
    public List<TransportPath> getTopKShortestPaths() {
        return topKShortestPaths;
    }

}
