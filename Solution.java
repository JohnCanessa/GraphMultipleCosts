import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Stack;
import java.util.Map.Entry;


/**
 * Node for graph class.
 * Supports multiple edges with different costs between vertices.
 */
class Node {

    // **** members ****
    public int v;
    HashMap<Integer, ArrayList<Integer>> adj;


    /**
     * constructor
     */
    public Node(int v) {
        this.v = v;
        this.adj = new HashMap<Integer, ArrayList<Integer>>();
    }


    /**
     * Return a string with the representation of this node.
     */
    @Override
    public String toString() {

        // **** ****
        StringBuilder sb = new StringBuilder();

        // **** ****
        sb.append(" v: " + this.v);
        sb.append(" adj: ");
        adj.entrySet().stream().forEach(e -> sb.append("u: " + e.getKey() + " c: " + e.getValue() + " "));

        // **** return string ****
        return sb.toString();
    }
}


/**
 * Graph class.
 */
class Graph {

    // **** graph representation ****
    HashMap<Integer, Node> graph = null;


    /**
     * Constructor
     */
    public Graph() {
        this.graph = new HashMap<Integer, Node>();
    }


    /**
     * Get the number of vertices in the graph.
     */
    public int getSize() {
        return this.graph.size();
    }


    /**
     * Add node to graph.
     */
    public Node addNode(int v) {

        // **** lookup an existing node ****
        if (graph.containsKey(v))
            return graph.get(v);

        // **** create a new node ****
        Node node = new Node(v);

        // **** add the new node into the graph ****
        graph.put(v, node);

        // **** return the new node ****
        return node;
    }


    /**
     * Returns a string representing the graph.
     */
    @Override
    public String toString() {

        // **** ****
        StringBuilder sb = new StringBuilder();

        // **** ****
        Iterator<Entry<Integer, Node>> it = this.graph.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Integer, Node> e = it.next();
            sb.append(e.getValue().toString());
        }

        // **** return string representation ****
        return sb.toString();
    }


    /**
     * Get an existing node form the graph.
     */
    public Node getNode(int v) {
        return graph.get(v);
    }


    /**
     * Add v to graph if not present.
     * Add u to graph if not present.
     * Add adjacent vertex u to v with specified cost c. 
     * Add adjacent vertex v to u with specified cost c.
     */
    public Node addVerticesAndEdge(int v, int u, int c) {

        // **** get the v node ****
        Node vn = getNode(v);

        // **** check if we need to create the v node ****
        if (vn == null) {
            vn = addNode(v);
        }

        // **** get the u node ****
        Node un = getNode(u);

        // **** check if we need to create the u node ****
        if (un == null) {
            un = addNode(u);
        }

        // **** get the specified adjacent u vertex from vertex v ****
        ArrayList<Integer> alUV = vn.adj.get(u);

        // **** add adjacent vertex u with cost c ****
        if (alUV == null) {
            ArrayList<Integer> al = new ArrayList<Integer>();
            al.add(c);
            vn.adj.put(u, al);
        }

        // **** add cost c to adjacent vertex u **** 
        else {
            alUV.add(c);
        }

        // **** get the specified adjacent v vertex from u ****
        ArrayList<Integer> alVu = un.adj.get(v);

        // **** add adjacent u with c ****
        if (alVu == null) {
            ArrayList<Integer> al = new ArrayList<Integer>();
            al.add(c);
            un.adj.put(v, al);
        }

        // **** return v node ****
        return vn;
    }
}

/**
 * 
 */
public class Solution {

    // **** set of unvisited vertices ****
    static HashSet<Integer> unVisited = new HashSet<Integer>();


    /**
     * Select the unvisited vertex with the SMALLEST known distance from the start vertex.
     */
    static int smallestCostVertex(int[] vertex, int[] sda, int[] pv) {

        // **** looking for smallest value ****
        int scv = Integer.MAX_VALUE;

        // **** select the vertex with the shortest distance to vertex: a ****
        for (int i = 1; i < sda.length; i++) {

            // **** skip visited vertices ****
            if (!unVisited.contains(i)) {
                continue;
            }

            // **** check this vertex ****
            if (sda[i] < scv)
                scv = vertex[i];
        }

        // **** return smallest cost vertex ****
        return scv;
    }


    /**
     * Find the shortest paths from vertex: a to every other vertex in the specified
     * graph.
     */
    static void shortestPathFrom(Graph g, int a, int[] vertex, int[] sda, int[] pv, int[] vc) {

        // **** set the distance from the start vertex: a to vertex: a ****
        sda[a] = 0;

        // **** loop while all vertices have not been visited ****
        do {

            // **** 1. visit the unvisited vertex (with the SMALLEST known distance) from the start vertex ****
            int v = smallestCostVertex(vertex, sda, pv);

            // **** check if no more vertices are available ****
            if (v == Integer.MAX_VALUE) {
                return;
            }

            // **** ****
            Node node = g.getNode(v);

            // **** 2. from the current vertex v, examine its unvisited neighbors u ****
            for (Integer u : node.adj.keySet()) {

                // **** check if we have already visited this vertex ****
                if (!unVisited.contains(u))
                    continue;

                // **** get the list of costs associatesd with vertex u ****
                ArrayList<Integer> al = node.adj.get(u);

                // **** for this edge u, compute and update the cost to a (if needed) ****
                for (Integer c : al) {

                    // **** 3. calculate the cost ****
                    int cost = sda[v] + c;

                    // **** ****
                    if (cost < sda[u]) {

                        // **** 4. update cost from u to a ****
                        sda[u] = cost;

                        // **** 5. update the previous vertex (cost has been updated) ****
                        pv[u] = v;

                        // **** 6. update the cost to this vertex in the table ****
                        vc[u] = c;
                    }
                }
            }

            // **** 7. remove this vertex from the unvisited list ****
            unVisited.remove(v);

        } while (unVisited.size() != 0);

    }


    /**
     * Get a stack with the path from vertex b to vertex a (is implicit).
     */
    static Stack<Integer> getPathBA(int[] vertex, int[] sda, int[] pv, int a, int b) {

        // **** vertex stack ****
        Stack<Integer> stackV = new Stack<Integer>();

        // **** push last vertex ****
        stackV.push(b);

        // **** loop pushing costs ****
        do {

            // **** push vertex ****
            stackV.push(pv[b]);

            // **** set next vertex ****
            b = pv[b];
        } while (stackV.peek() != vertex[a]);

        // **** return stack with vertices in the path ****
        return stackV;
    }


    /**
     * Return the sum of the costs from a to b.
     */
    static int getCostBA(Stack<Integer> stack, int[] vc) {

        // **** for starters ****
        int cost = 0;

        // **** traverse the stack ****
        for (int i = stack.size() - 1; i > 0; i--) {

            // **** ****
            int u = stack.elementAt(i - 1);

            // **** add the cost from vn to u ****
            cost += vc[u];
        }

        // **** return the sum of the costs ****
        return cost;
    }


    /**
     * Display auxiliary table contents side by side.
     */
    static void displayTables(int[] vertex, int[] sda, int[] pv, int[] vc) {

        // **** display header ****
        System.out.println("vertex\tsda\tpv\tvc");

        // **** loop displaying table contents side by side ****
        for (int i = 1; i < vertex.length; i++) {
            System.out.printf("%d\t%d\t%d\t%d\n", vertex[i], sda[i], pv[i], vc[i]);
        }
    }


    /**
     * Compute and display the path and cost for the shortest path between two vertices.
     */
    static void shortestPath(int[][] edges, int a, int b) {

        // **** declare the graph ****
        Graph g = new Graph();

        // **** add vertices and costs to graph ****
        for (int i = 0; i < edges.length; i++) {
            g.addVerticesAndEdge(edges[i][0], edges[i][1], edges[i][2]);
        }

        // **** array (table) holding all vertices in the graph ****
        int[] vertex = new int[g.getSize() + 1];

        // **** array (table) holding the shortest distance from v ****
        int[] sda = new int[g.getSize() + 1];

        // **** array (table) holding previous vertex ****
        int[] pv = new int[g.getSize() + 1];

        // **** array (table) holding the cost to this vertex ****
        int[] vc = new int[g.getSize() + 1];

        // **** initialize data structures ****
        for (int i = 0; i < g.getSize() + 1; i++) {

            // **** ****
            vertex[i] = i;

            // **** fill in shortest distance from a ****
            sda[i] = Integer.MAX_VALUE;

            // **** populate unvisited vertices ****
            if (i != 0)
                unVisited.add(i);
        }

        // **** find shortest path in the graph from vertex: a to every other vertex ****
        shortestPathFrom(g, a, vertex, sda, pv, vc);

        // **** get a stack with the path from a to b ****
        Stack<Integer> stack = getPathBA(vertex, sda, pv, a, b);

        // **** display path between vertices ****
        System.out.print("shortestPath <<< path: ");
        for (int i = stack.size() - 1; i >= 0; i--) {
            System.out.print(stack.elementAt(i));
            if (i > 0)
                System.out.print(" -> ");
        }
        System.out.println();

        // **** compute cost from A to B ****
        int cost = getCostBA(stack, vc);

        // **** display cost ****
        System.out.println("shortestPath <<< cost: " + cost);
    }


    /**
     * Test scaffolding.
     * 
     * @throws FileNotFoundException
     */
    public static void main(String[] args) {
        
        // **** open scanner ****
        Scanner sc = new Scanner(System.in);

        // **** read number of nodes and edges ****
        String[] nm = sc.nextLine().trim().split(" ");
        int m = Integer.parseInt(nm[1]);

        // **** graph edges (includes cost) ****
        int[][] edges = new int[m][3];

        // **** loop reading vertices and associated cost ****
        for (int i = 0; i < m; i++) {
            String[] edgeCost = sc.nextLine().trim().split(" ");
            edges[i][0] = Integer.parseInt(edgeCost[0]);
            edges[i][1] = Integer.parseInt(edgeCost[1]);
            edges[i][2] = Integer.parseInt(edgeCost[2]);
        }

        // **** read the start and end vertices ****
        String[] ab = sc.nextLine().trim().split(" ");
        int A = Integer.parseInt(ab[0]);
        int B = Integer.parseInt(ab[1]);

        // **** find and display path and cost ****
        shortestPath(edges, A, B);

        // **** close scanner ****
        sc.close();
    }
}