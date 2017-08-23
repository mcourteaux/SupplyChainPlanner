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
 * A graph holding extra information about the transport options it was
 * constructed from.
 * 
 * @author martijn
 * @param <ALoc> The location model used in your application.
 * @param <ATransport> The transport option model used in your application.
 */
public class TransportGraph<ALoc extends AbstractLocation, ATransport extends AbstractTransport<ALoc>> {

    private final Graph graph;
    private final Vertex source;
    private final Vertex sink;
    private final ALoc sourceLocation;
    private final ALoc sinkLocation;

    private final Map<Integer, ATransport> transportMap;

    /**
     * Default constructor.
     * 
     * @param graph The graph object.
     * @param source The source vertex.
     * @param sink The sink vertex.
     * @param sourceLocation The associated source location.
     * @param sinkLocation The associated destination location.
     * @param transportMap  The map of out-vertex-ids to transport options.
     */
    public TransportGraph(Graph graph, Vertex source, Vertex sink, ALoc sourceLocation, ALoc sinkLocation, Map<Integer, ATransport> transportMap) {
        this.graph = graph;
        this.source = source;
        this.sink = sink;
        this.sourceLocation = sourceLocation;
        this.sinkLocation = sinkLocation;
        this.transportMap = transportMap;
    }

    /**
     * @return The underlying Graph object used for the shortest paths algorithm.
     */
    public Graph getGraph() {
        return graph;
    }

    /**
     * @return The source vertex.
     */
    public Vertex getSource() {
        return source;
    }

    /**
     * @return The destination vertex.
     */
    public Vertex getSink() {
        return sink;
    }

    /**
     * @return The location associated with the destination.
     */
    public ALoc getSinkLocation() {
        return sinkLocation;
    }

    /**
     * @return The location associated with the source.
     */
    public ALoc getSourceLocation() {
        return sourceLocation;
    }

    /**
     * Look up the transport option given the vertex id of the vertex
     * representing the out-vertex of the a transport option from a given
     * location.
     *
     * @param vertexId_fromLocation 
     * 
     * @return The associated transport option with the out-vertex.
     */
    public ATransport getTransport(int vertexId_fromLocation) {
        return transportMap.get(vertexId_fromLocation);
    }
}
