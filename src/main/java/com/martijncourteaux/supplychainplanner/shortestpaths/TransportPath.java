/* 
 * Copyright (C) 2017 Martijn Courteaux.
 *
 * For license details, see LICENSE.txt and README.txt.
 * This project is licensed under Creative Commons NC-NC-ND.
 */
package com.martijncourteaux.supplychainplanner.shortestpaths;

import edu.asu.emit.algorithm.graph.Path;
import edu.asu.emit.algorithm.graph.abstraction.BaseVertex;

/**
 *
 * @author martijn
 */
public class TransportPath {

    private final Path path;
    private final AttributedGraph<VertexAttribute> graph;

    public TransportPath(Path path, AttributedGraph<VertexAttribute> graph) {
        this.path = path;
        this.graph = graph;
    }

    public AttributedGraph<VertexAttribute> getGraph() {
        return graph;
    }

    public Path getPath() {
        return path;
    }

    /**
     * Creates a nicely formatted multi-line string for this Path.
     *
     * @return The string representation.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Path (cost=");
        sb.append(path.getWeight());
        sb.append("; hops=");
        sb.append((path.getVertexList().size() - 1) / 2);
        sb.append(")\n");
        for (int i = 0; i < path.getVertexList().size(); ++i) {
            BaseVertex vert = path.getVertexList().get(i);
            VertexAttribute att = graph.getAttribute(vert.getId());
            if (i % 2 == 0) {
                sb.append(att.location_code);
                sb.append("\n");
            } else {
                sb.append(" ---[");
                sb.append(att.line_modality);
                sb.append("   ", 0, 6 - att.line_modality.length());
                sb.append("]-{ ");
                sb.append(String.format("%8s: agent=%2d, cost=%5.2f",
                        att.line_code, att.agent_id, att.cost));
                sb.append("  }---> ");
            }
        }
        return sb.toString();
    }

}
