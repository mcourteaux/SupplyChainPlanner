/*
 * Copyright (C) 2017 martijn.
 *
 * For license details, see LICENSE.txt and README.txt.
 * This project is licensed under Creative Commons NC-NC-ND.
 */
package com.martijncourteaux.supplychainplanner.generic.shortestpaths;

import com.martijncourteaux.supplychainplanner.generic.model.AbstractLocation;
import com.martijncourteaux.supplychainplanner.generic.model.AbstractTransport;
import edu.asu.emit.algorithm.graph.Path;
import edu.asu.emit.algorithm.graph.abstraction.BaseVertex;

/**
 *
 * @author martijn
 */
public class TransportPath<ALoc extends AbstractLocation, ATransport extends AbstractTransport<ALoc>> {

    private final Path path;
    private final TransportGraph<ALoc, ATransport> graph;

    public TransportPath(Path path, TransportGraph<ALoc, ATransport> graph) {
        this.path = path;
        this.graph = graph;
    }

    public TransportGraph getGraph() {
        return graph;
    }

    public Path getPath() {
        return path;
    }

    @Override
    public String toString() {
        //return "TransportPath with cost: " + path.getWeight() +" number of hops: " + path.getVertexList().size();

        StringBuilder sb = new StringBuilder();
        
        sb.append("TransportPath from '");
        sb.append(graph.getSource());
        sb.append("' to '");
        sb.append(graph.getSink());
        sb.append("' with weight: ");
        sb.append(path.getWeight());
        sb.append("{\n");

        /* Path order is:
         *  source ->
         *  from_region --{transport}--> to_region --{xdock-cost}--->
         *  from_region --{transport}--> to_region --{xdock-cost}--->
         *  from_region --{transport}--> to_region --{xdock-cost}--->
         *  from_region --{transport}--> to_region --{xdock-cost}--->
         *  sink
         */
        sb.append("Source: ");
        sb.append(graph.getSourceLocation());
        sb.append('\n');
        int transports = (path.getVertexList().size() - 2) / 2;
        for (int i = 0; i < transports; ++i) {
            sb.append("  ");
            BaseVertex location_from = path.getVertexList().get(i * 2 + 1);
            BaseVertex location_till = path.getVertexList().get(i * 2 + 2);
            ATransport transport = graph.getTransport(location_from.getId());
            sb.append(String.format(" [%05d] ", location_from.getId()));
            sb.append(transport.toSingleLineString());
            sb.append(String.format(" [%05d] ", location_till.getId()));
            //sb.append(" --{ 0.0: Xdock }-->");
            sb.append('\n');
        }
        sb.append("Sink: ");
        sb.append(graph.getSinkLocation());
        sb.append('}');

        return sb.toString();
    }

}
