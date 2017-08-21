/*
 * Copyright (C) 2017 martijn.
 *
 * For license details, see LICENSE.txt and README.txt.
 * This project is licensed under Creative Commons NC-NC-ND.
 */
package com.martijncourteaux.supplychainplanner.generic.shortestpaths;

import com.martijncourteaux.supplychainplanner.generic.AbstractContext;
import com.martijncourteaux.supplychainplanner.generic.model.AbstractLocation;
import com.martijncourteaux.supplychainplanner.generic.model.AbstractTransport;
import edu.asu.emit.algorithm.graph.Graph;
import edu.asu.emit.algorithm.graph.VariableGraph;
import edu.asu.emit.algorithm.graph.Vertex;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author martijn
 * @param <AContext>
 * @param <ALoc>
 * @param <ATransport>
 */
public class GraphBuilder<AContext extends AbstractContext, ALoc extends AbstractLocation, ATransport extends AbstractTransport<? extends ALoc>> {

    public GraphBuilder() {
    }

    public TransportGraph buildGraph(AContext ctx, List<ATransport> transports, ALoc source, ALoc sink) {

        Graph g = new VariableGraph();
        g.setInitialFanoutCapacity(2048 * 4);

        // Source & Sink vertices
        Vertex sourceVertex = new Vertex();
        Vertex sinkVertex = new Vertex();
        g.addVertex(sourceVertex);
        g.addVertex(sinkVertex);

        final int capacity = 2048;
        List<ALoc> sourceLocs = new ArrayList<>(capacity);
        List<ALoc> destLocs = new ArrayList<>(capacity);
        List<Vertex> sourceVertices = new ArrayList<>(capacity);
        List<Vertex> destVertices = new ArrayList<>(capacity);

        Map<Integer, ATransport> transportMap = new HashMap<>(1024 * 64);

        int validCounter = 0;

        for (int t = 0; t < transports.size(); ++t) {
            ATransport trans = transports.get(t);

            if (trans.destination == null || trans.source == null) {
                System.out.println("Warning: transport has null source or destination");
                continue;
            }

            Vertex sourceLoc = new Vertex();
            Vertex destLoc = new Vertex();
            g.addVertex(sourceLoc);
            g.addVertex(destLoc);

            g.addEdge(sourceLoc.getId(), destLoc.getId(), trans.weight());

            sourceLocs.add(trans.source);
            sourceVertices.add(sourceLoc);
            destLocs.add(trans.destination);
            destVertices.add(destLoc);

            transportMap.put(sourceLoc.getId(), trans);

            validCounter++;
        }


        /* Connect compatible */
        System.out.println("Connecting compatibles");
        for (int i = 0; i < validCounter; ++i) {

            if (i % 100 == 0) {
                System.out.printf("%n%4d / %4d  ", i, validCounter);
            }
            if (i % 10 == 0) {
                System.out.print('.');
            }

            ALoc dest = destLocs.get(i);
            Vertex destVertex = destVertices.get(i);
            for (int j = 0; j < validCounter; ++j) {
                ALoc src = sourceLocs.get(j);
                Vertex srcVertex = sourceVertices.get(j);

                if (dest.isCompatible(src)) {
                    // TODO Insert crossdocking cost here
                    g.addEdge(destVertex.getId(), srcVertex.getId(), 0.0);
                }
            }
        }
        System.out.println();


        /* Connect source to the graph */
        System.out.println("Connecting Source");
        for (int i = 0; i < sourceLocs.size(); ++i) {
            if (source.isCompatible(sourceLocs.get(i))) {
                g.addEdge(sourceVertex.getId(), sourceVertices.get(i).getId(), 0.0);
            }
        }

        /* Connect sink to the graph */
        System.out.println("Connecting Sink");
        for (int i = 0; i < destLocs.size(); ++i) {
            if (destLocs.get(i).isCompatible(sink)) {
                g.addEdge(destVertices.get(i).getId(), sinkVertex.getId(), 0.0);
            }
        }

        return new TransportGraph(g, sourceVertex, sinkVertex, source, sink, transportMap);
    }

}
