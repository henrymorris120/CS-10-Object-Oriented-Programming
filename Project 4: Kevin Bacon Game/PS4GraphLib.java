import java.util.*;


/**
 * PS4 Graph Library
 * PS4
 * @author Henry Morris and Izzy Axinn, Dartmouth CS 10, Spring 2022
 */

public class PS4GraphLib {

    /**
     * creates and returns a bfs graph for inputted graph g
     * @param g             graph to perform bfs on
     * @param source        root for the bfs
     * @return              breadth first search graph
     */

    public static <V,E> Graph<V,E> bfs(Graph<V,E> g, V source) {
        // create graph and insert vertex with parameter source
        Graph<V,E> graph = new AdjacencyMapGraph<V,E>();
        graph.insertVertex(source);
        Set<V> visited = new HashSet<V>(); //Set to track which vertices have already been visited
        Queue<V> queue = new LinkedList<V>(); //queue to implement BFS
        queue.add(source); //enqueue start vertex
        visited.add(source); //add start to visited Set
        while (!queue.isEmpty()) { //loop until no more vertices
            V u = queue.remove(); //dequeue
            for (V v : g.outNeighbors(u)) { //loop over out neighbors
                if (!visited.contains(v)) { //if neighbor not visited, then neighbor is discovered from this vertex
                    visited.add(v); //add neighbor to visited Set
                    queue.add(v); //enqueue neighbor
                    // create vertex on graph
                    graph.insertVertex(v);
                    // create directed edge from the neighbor to its source
                    graph.insertDirected(v,u, g.getLabel(v, u));
                }
            }
        }
        return graph;
    }

    /**
     * returns shortest path from v to the root of tree
     * @param tree         bfs graph
     * @param v            vertex in graph where path starts
     * @return             shortest path from vertex to root of the graph inputted as parameter
     */
    public static <V,E> List<V> getPath(Graph<V,E> tree, V v) {
        // create new list and parameter v
        ArrayList<V> path = new ArrayList<V>();
        path.add(v);
        // add each vertex that current vertex points towards, then setting new current vertex, until reach the root
        V current = v;
        while(tree.outDegree(current) != 0) {
            for (V vertex: tree.outNeighbors(current)) {
                current = vertex;
                path.add(current);
            }
        }
        return path;
    }

    /**
     * returns a set a vertices that are only in graph, not subgraph
     * @param graph             main graph
     * @param subgraph          subgraph of graph
     * @return                  set of vertices in graph but not subgraph
     */
    public static <V,E> Set<V> missingVertices(Graph<V,E> graph, Graph<V,E> subgraph) {
        // create set
        Set<V> missing = new HashSet<>();
        // check if every vertex of graph is a vertex in subgraph
        // if not true, add to set missing
        for(V v : graph.vertices()) {
            if(!subgraph.hasVertex(v)) {
                missing.add(v);
            }
        }
        return missing;
    }

    /**
     * returns the average separation between root and every other node in tree
     * @param tree          BFS graph used to find average separation
     * @param root          root of the BFS tree
     * @return              double representing the average separation between root and every other node in tree
     */
    public static <V,E> double averageSeparation(Graph<V,E> tree, V root) {
        int separation = averageSeparationHelper(tree, root, 0);
        double averageSeparation = separation / (double)tree.numVertices();
        return averageSeparation;
    }

    /**
     * Returns the sum of every vertex's distance in number of edges away from the root
     * @param tree                      BFS graph with shortest path for every vertex to the root
     * @param location                  Location on the BFS graph
     * @param degreeOfSeparation        How many edges away from the root of the graph the location is
     * @return
     */
    public static <V,E> int averageSeparationHelper(Graph<V,E> tree, V location, int degreeOfSeparation) {
        // set sum to degreeOfSeparation
        int sum = degreeOfSeparation;
        // if location has at least one vertex with a directed edge to it
        if (tree.inDegree(location) != 0) {
            // add sum to what is returned when this method is called on every one of the vertices pointed towards it
            // increase degreeOfSeparation by 1 because these vertices are 1 edge further from the root of tree
            for (V vertex: tree.inNeighbors(location)) {
                sum += averageSeparationHelper(tree, vertex, degreeOfSeparation + 1);
            }
        }
        return sum;
    }

}
