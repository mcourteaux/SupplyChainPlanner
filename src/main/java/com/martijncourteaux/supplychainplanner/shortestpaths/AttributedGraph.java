/* 
 * Copyright (C) 2017 Martijn Courteaux.
 *
 * For license details, see LICENSE.txt and README.txt.
 * This project is licensed under Creative Commons NC-NC-ND.
 */
package com.martijncourteaux.supplychainplanner.shortestpaths;

import edu.asu.emit.algorithm.graph.Graph;
import edu.asu.emit.algorithm.graph.Vertex;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author martijn
 * @param <E> Attribute type
 */
public class AttributedGraph<E> {

    private final Graph graph;
    private final Map<Integer, E> attributes;
    private final Map<Integer, Vertex> location_vertex_map;

    public AttributedGraph(Graph g) {
        this.graph = g;
        this.attributes = new HashMap<>();
        this.location_vertex_map = new HashMap<>();
    }

    public Graph getGraph() {
        return graph;
    }

    public void setAttribute(int vertexId, E att) {
        attributes.put(vertexId, att);
    }

    public E getAttribute(int vertexId) {
        return attributes.get(vertexId);
    }

    public Vertex getVertexForLocation(int location_id) {
        return location_vertex_map.get(location_id);
    }

    public void setVertexForLocation(int location_id, Vertex v) {
        location_vertex_map.put(location_id, v);
    }

}
