/* 
 * Copyright (C) 2017 Martijn Courteaux.
 *
 * For license details, see LICENSE.txt and README.txt.
 * This project is licensed under Creative Commons NC-NC-ND.
 */
package com.martijncourteaux.supplychainplanner.poc.shortestpaths;

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

    public double weight() {
        return path.getWeight();
    }

    public double cost() {
        double cost = 0.0;
        for (int i = 0; i < path.getVertexList().size(); ++i) {
            BaseVertex vert = path.getVertexList().get(i);
            VertexAttribute att = graph.getAttribute(vert.getId());
            cost += att.cost;
        }
        return cost;
    }

    public int duration_hours() {
        int hours = 0;
        for (int i = 0; i < path.getVertexList().size(); ++i) {
            BaseVertex vert = path.getVertexList().get(i);
            VertexAttribute att = graph.getAttribute(vert.getId());
            hours += att.duration;
        }
        return hours;
    }

    /**
     * Creates a nicely formatted multi-line string for this Path.
     *
     * @return The string representation.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Path (weight=");
        sb.append(weight());
        sb.append("; cost=");
        sb.append(String.format("%.2f", cost()));
        sb.append(" EUR; hops=");
        sb.append((path.getVertexList().size() - 1) / 2);
        sb.append("; hours=");
        sb.append(duration_hours());
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
                sb.append(String.format("%8s: "
                        + "agent=%2d, "
                        + "cost=%7.2f EUR, "
                        + "weight=%7.3f, "
                        + "duration=%2dh",
                        att.line_code, att.agent_id, att.cost,
                        att.weight, att.duration));
                sb.append("  }---> ");
            }
        }
        return sb.toString();
    }

}
