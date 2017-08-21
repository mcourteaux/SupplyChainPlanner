/* 
 * Copyright (C) 2017 Martijn Courteaux.
 *
 * For license details, see LICENSE.txt and README.txt.
 * This project is licensed under Creative Commons NC-NC-ND.
 */
package com.martijncourteaux.supplychainplanner.poc.shortestpaths;

import com.martijncourteaux.supplychainplanner.ConsignmentDetails;
import edu.asu.emit.algorithm.graph.Path;
import edu.asu.emit.algorithm.graph.abstraction.BaseVertex;
import edu.asu.emit.algorithm.graph.shortestpaths.YenTopKShortestPathsAlg;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author martijn
 */
public class ShortestPathsSolver {

    private final ConsignmentDetails consignmentDetails;
    private AttributedGraph<VertexAttribute> attributedGraph;
    private BaseVertex vertexFrom;
    private BaseVertex vertexTo;
    private YenTopKShortestPathsAlg topKShortestPathsAlg;
    private final List<TransportPath> topKShortestPaths;

    public ShortestPathsSolver(ConsignmentDetails details) {
        consignmentDetails = details;
        topKShortestPaths = new ArrayList<>();
    }

    public void buildGraph(Connection conn) throws SQLException {
        GraphInstantiator gi = new GraphInstantiator();
        AttributedGraph<VertexAttribute> g = gi.instantiateGraph(conn, consignmentDetails);
        attributedGraph = g;
        vertexFrom = g.getVertexForLocation(consignmentDetails.location_from);
        vertexTo = g.getVertexForLocation(consignmentDetails.location_to);
        topKShortestPathsAlg = new YenTopKShortestPathsAlg(g.getGraph(),
                vertexFrom, vertexTo);
    }

    public int searchPaths(int num) {
        for (int i = 0; i < num; ++i) {
            if (topKShortestPathsAlg.hasNext()) {
                Path p = topKShortestPathsAlg.next();
                topKShortestPaths.add(new TransportPath(p, attributedGraph));
            } else {
                return i;
            }
        }
        return num;
    }

    public List<TransportPath> getTopKShortestPaths() {
        return topKShortestPaths;
    }

    public AttributedGraph<VertexAttribute> getAttributedGraph() {
        return attributedGraph;
    }
}
