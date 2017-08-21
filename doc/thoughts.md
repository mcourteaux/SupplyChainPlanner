
One can model the coalescing of shipments as an arc with zero cost, but a time
cost to the shipment.

     A ------ expensive ---------> B
      \                           ^
       \--------- free ----> A --/

However, thinking about this resulted in the realisation that the limited truck
capacity can't be modelled in the graph. Truck capacity and assignment seems
closely related to the graph coloring problem. However, combining this with a
TSP seems difficult.

--------------------------------------------------------------------------------

# Layered solution technique

Let's think about a layer solution method. The layers I'm thinking of right now
are:

 - Per consignment a set short realistic paths (given the cost function).
 - Per half day/day/week solving assignments: detecting overlapping path
   segments.
 - To this point, we are only talking about loaded trucks. So, next we will
   try to solve the assignments of trucks to the scheduled transports.
 - Once a truck is useless (which we try to reduce to a minimum), it should
   probably "come back home".

Now we will discuss the several layers of the solution:

## Short paths
This layer should come up with several possible transportation scenarios for a
given consignment. Those will be fed into the next step of solution generation.
It should probably include some paths involving agents offering discounts for
large amounts.

The cost functions for these shortest path discovery will probably be influenced
by some weights (parameters). This is a point where trial and error can take
place, guided by a Guassian process (GP) surrogate model.

In order to correctly deal with offered discounts, we should take into account
that certain transports will be recurrent, thus, increasing the interest of
choosing an agent with a discount.

The most important motivation for choosing to go for a more expensive, but
discount offering agent, is the knowledge (Big Data) that a certain amount of
goods will need to be transported in the coming month/year. This knowledge most
likely comes from history: consignments of last years. Accurate prediction of
consignments is a topic on it's own, and is probably heavily influenced by the
market. So, I will assume that the prediction simply is available. Note that I'm
talking about estimates of amounts over the next month/year and not the actual
consignments itself.

If the predictions of the amounts are available, we can simply evaluate all the
possible discounts. Select discounts until the expected transportation needs are
filled by a certain safety factor (e.g.: 80%), which would be decided based on
the distribution of the transportation needs (probabilistic usage of Big Data).

Simply selecting discounts isn't enough. Somehow these amounts should be
reached. Fiddling with the numbers (prices) won't work, as alternative routes
might be still cheaper. I'll revisit this topic later.

## Selecting the actual scenarios to be used
Now, we have a set of options per consignment, produced by the previous layer.
We now have to decide with which option we will go. Here, the goal is to select
interesting combinations, and schedule them in the same truck or trucks in case
of a fleet.

The first basic step to do this, is to partition the problem by detecting
overlapping path segments in all the options. If partitions can be found, the
algorithm can be applied independently (and thus in parallel) on the two or more
partitions. This, unless this algorithm has to deal with discounts.

The partitioning step should be optional: treat all options and consignments at
once, should result in the same solution.

The actual implementation of this combination searching, I will leave blank for
now.

## Assigning physical trucks
Now that for every consignment, a scenario is selected, it remains to choose
which physical truck should be used for this. Trucks should ideally go in loops,
such that they get back home.


# Implementation PoC

On GitHub here:
[mcourteaux/SupplyChainPlanner](https://github.com/mcourteaux/SupplyChainPlanner)
Under the source folder
`src/main/java/com.courteauxmartijn.supplychainplanner.poc`.


## kShortestPaths
A `ConsigmentDetails` defines the characteristics of the consignment: weight,
volume, etc... This results in a formula combining the cost parameters for an
offer, used in the database query to produce a "cost" column. This result set is
converted to an in-JVM-memory graph implemented on a modified version of:
[yan-qi/k-shortest-paths-java-version](https://github.com/yan-qi/k-shortest-paths-java-version)

I put it on GitHub here:
[mcourteaux/k-shortest-paths-java-version](https://github.com/mcourteaux/k-shortest-paths-java-version)

I implemented query generation to represent filtering for example using:

 - weight
 - volume
 - number of pallets
 - allowing ferry
 - disallowed agents

--------------------------------------------------------------------------------

# Break and restart: Generic Framework
Instead of working around this PoC, I looked to the LSP dataset, which has
some similar but slightly different concepts. I thought it would be interesting
to provide a generic framework that can handle most scenarios, given that you
provide an implementation for that case. So for example a LSP-module for the
framework solves the problem for LSP and their datamodel.

The new generic framework is in the source folder:
`src/main/java/com.courteauxmartijn.supplychainplanner.poc`.

You are required to provide the datamodel:

 - Shipments (Consignments from and to a location, having goods to be shipped)
 - Goods (The contents of a Shipment)
 - Locations (An abtract definition of a location)
 - Transports (The ability to transport something between two locations).

A Location can be more complex than simply a node in a graph. LSP data for
example uses the concept of regions: a region is a set of countries with
zip-code ranges. Therefore, you have to implement an `isCompatible` function
that determines wether two locations are logically "compatible". I.e.: one
location overlaps with the other (or is the same). For example: Aalst is
compatible with Europe. This allows the graph in which the shortest path is
searched to make zero-cost connections between compatible locations. For
example: an available transport option delivers to a region "Flanders" and
another picks goods up in the region "Belgium". These two are compatible, which
means that these transport options can be chained together.

# Graph
Let's discuss how we will build the graph, using this abstract concept of
compatibility. One issue that arises if you connect the compatible regions
freely is that you introduce a zero-cost path: source -> Europe -> destination.
In order to overcome this issue, we will have a bipartite directed graph:
Sources left, destinations right. Sources are connected to destinations by
transports. Destinations are connected to sources by zero-cost compatibility
arcs.

    Source          |             Destination
    ------          |             -----------

    Region -----{transport}->>--- Region
                                  /  /
         .-----<<-(compat)-------^  /
        /                     .----^
       /                     /
    Region -----{transport}-----> Region
                            |     /
          .----<<-(compat)--+----/
         /                  |
        / .--<<-(compat)---^
       / /
    Region -----{transport}-----> Region
       \
        \-------<<-(compat)-------.
                                   \
                                    \
    Region -----{transport}-----> Region

The connectivity of the transports is determined by the database (provided that
the model is implemented). The connectivity of the compatible regions from
*Destination* back to *Source* is determined by the `isCompatible` and is done
after the database query.

After analysing the contracts of LSP, it looks like there are certain
agreements that specify a price per kilometer, instead of working with regions.
This will involve determining the distance over road from one region to another.
An online service will probably be able to tell this. However, the difficulty
lies in the construction of the graph for these per-kilometer transport offers.
One possibility is to calculate the distance between the source and the
destination of once the destination is known in the region compatibility
connecting phase. There are several issues with this still:

 - A region does not have a specific location, which can be used to calculate
   the distance to and from. This probably should be done in a warehouse, which
   doesn't seem to be available in this database.
 - It is difficult to chain two per-kilometer offers after one another. This is
   because in the region connecting phase, the destination of the transport
   might be known, but not the source.

   For example: consider a per-kilometer offer from Europe to Europe. This is
   basically a joker. This joker should probably be more expensive than other
   possibilities. However, calculating the distance from Europe to Europe makes
   no sense. You should know from where and to where you want to use transport
   offer. If for example you request a transport from Paris to Amsterdam, one
   can calculate the approximate distance.

   But the issue lies in combing two of these: consider a request from Paris to
   Amsterdam, and two large-region per-kilometer offers:

    - From FR/BE/DE to FR/BE/DE.
    - From BE/NE/LUX to BE/NE/LUX.

   It is obvious that we require the two of them, and should use BE as a
   cross-docking location. But where exactly in Belgium will be do this? What
   will be the number of kilometers before and after cross-docking?

To resolve these issues, I would like to work again with the situation from the
PoC: locations were very clearly defined: one could make a location per
warehouse and generate offers between warehouses in Europe, using the
per-kilometer prices from the agreements. However, this will involve creating a
full mesh of $O(n^2 k)$ connections, with $n$ the number of warehouses and $k$
the number of offers of all transport companies combined. This might become very
quickly problematic.

This raises the question, should there be some heuristic filtering be done?
Think: do we really need the transport offers going from Spain to France, when
we are trying to organize a shipment from Belgium to the Netherlands? Obviously
not, but how do we make sure we are not excluding interesting possibilities,
like cheap ferries, which might be clearly take longer (in time and kilometers),
but gets disconnected from the graph, as we are not including trajectories
towards a port, because the port is completely the opposite direction?

Next day: I believe it should make sense to use an ellipse with focal points
the source and destination as heuristic filtering area. This would allow for
following a different approach: instead of connecting compatible regions for
free, we can connect warehouses with each other, based on the available
contracts. Therefore, we have to know the location of the warehouse. In case of
LSP, a zipcode will do to determine compatibility with given contracts. This
models closely follows the earlier PoC.
