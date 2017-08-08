
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


# Implementation

## kShortestPaths
A CostModel defines the characteristics of the consignment: weight, volume,
etc... This results in a formula combining the cost parameters for an offer,
used in the database query to produce a "cost" column. This result set is
converted to an in-JVM-memory graph implemented on a modified version of:
[yan-qi/k-shortest-paths-java-version](https://github.com/yan-qi/k-shortest-paths-java-version)

I put it on GitHub here:
[mcourteaux/k-shortest-paths-java-version](https://github.com/mcourteaux/k-shortest-paths-java-version)


