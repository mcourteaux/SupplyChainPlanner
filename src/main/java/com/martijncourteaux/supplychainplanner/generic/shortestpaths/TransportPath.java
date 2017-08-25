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
 * A path through a transport graph.
 *
 * @author martijn
 * @param <ALoc> The location model within your application.
 * @param <ATransport> The transport option model within your application.
 */
public class TransportPath<ALoc extends AbstractLocation, ATransport extends AbstractTransport<ALoc>> {

    private final Path path;
    private final TransportGraph<ALoc, ATransport> graph;

    /**
     * Default constructor.
     *
     * @param path The path object.
     * @param graph The graph object.
     */
    public TransportPath(Path path, TransportGraph<ALoc, ATransport> graph) {
        this.path = path;
        this.graph = graph;
    }

    /**
     * @return The graph associated with this path.
     */
    public TransportGraph getGraph() {
        return graph;
    }

    /**
     * @return The Path object representing the path through the graph object.
     */
    public Path getPath() {
        return path;
    }

    /**
     * @return The number of transport legs.
     */
    public int numberOfTransports() {
        return (path.getVertexList().size() - 2) / 2;
    }

    /**
     * Get the n-th transport path.
     * @param index n
     * @return The transport leg with index n.
     */
    public ATransport getTransportLeg(int index) {
        BaseVertex location_from = path.getVertexList().get(index * 2 + 1);
        ATransport transport = graph.getTransport(location_from.getId());
        return transport;
    }

    /**
     * Nicely constructs a string representation of the path, using the
     * <code>Transport::toSingleLineString()</code> method.
     *
     * @return A nice string representation.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("TransportPath from '");
        sb.append(graph.getSourceLocation());
        sb.append("' to '");
        sb.append(graph.getSinkLocation());
        sb.append("' with weight: ");
        sb.append(String.format("%5.2f", path.getWeight()));
        sb.append(" {\n");

        /* Path order is:
         *  source ->
         *  from_region --{transport}--> to_region --{xdock-cost}--->
         *  from_region --{transport}--> to_region --{xdock-cost}--->
         *  from_region --{transport}--> to_region --{xdock-cost}--->
         *  from_region --{transport}--> to_region --{xdock-cost}--->
         *  sink
         */
        int transports = (path.getVertexList().size() - 2) / 2;
        double total_cost = 0.0;
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
            total_cost += transport.cost();
        }
        sb.append("} with cost: ");
        sb.append(String.format("%5.2f EUR", total_cost));

        return sb.toString();
    }

}
