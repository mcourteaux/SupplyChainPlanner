/*
 * Copyright (C) 2017 martijn.
 *
 * For license details, see LICENSE.txt and README.txt.
 * This project is licensed under Creative Commons NC-NC-ND.
 */
package com.martijncourteaux.supplychainplanner.generic.shortestpaths;

import com.martijncourteaux.supplychainplanner.generic.model.AbstractLocation;
import com.martijncourteaux.supplychainplanner.generic.model.AbstractTransport;
import edu.asu.emit.algorithm.graph.Graph;
import edu.asu.emit.algorithm.graph.Vertex;
import java.util.Map;

/**
 *
 * @author martijn
 * @param <ALoc>
 * @param <ATransport>
 */
public class TransportGraph<ALoc extends AbstractLocation, ATransport extends AbstractTransport<ALoc>> {

    private final Graph graph;
    private final Vertex source;
    private final Vertex sink;
    private final ALoc sourceLocation;
    private final ALoc sinkLocation;

    private final Map<Integer, ATransport> transportMap;

    public TransportGraph(Graph graph, Vertex source, Vertex sink, ALoc sourceLocation, ALoc sinkLocation, Map<Integer, ATransport> transportMap) {
        this.graph = graph;
        this.source = source;
        this.sink = sink;
        this.sourceLocation = sourceLocation;
        this.sinkLocation = sinkLocation;
        this.transportMap = transportMap;
    }

    public Graph getGraph() {
        return graph;
    }

    public Vertex getSource() {
        return source;
    }

    public Vertex getSink() {
        return sink;
    }

    public ALoc getSinkLocation() {
        return sinkLocation;
    }

    public ALoc getSourceLocation() {
        return sourceLocation;
    }

    public ATransport getTransport(int vertexId_fromLocation) {
        return transportMap.get(vertexId_fromLocation);
    }
}
