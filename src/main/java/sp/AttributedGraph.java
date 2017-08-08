/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sp;

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
