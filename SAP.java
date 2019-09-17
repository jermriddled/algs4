/* *****************************************************************************
 *  Name: Jeremy Steinberg
 *  Date: 9/5/19
 *  Description: SAP
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {
    private final Digraph digraph;
    private int singleAncestor;
    private final int vertices;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) throw new IllegalArgumentException("Digraph cannot be null");
        vertices = G.V();
        digraph = new Digraph(G);
    }

    /**
     * @author Robert Sedgewick
     * @author Kevin Wayne
     * @source: https://algs4.cs.princeton.edu/42digraph/BreadthFirstDirectedPaths.java.html The
     * {@code BreadthDirectedFirstPaths} class represents a data type for finding shortest paths
     * (number of edges) from a source vertex <em>s</em> (or set of source vertices) to every other
     * vertex in the digraph.
     * <p>
     * This implementation uses breadth-first search. The constructor takes time proportional to
     * <em>V</em> + <em>E</em>, where <em>V</em> is the number of vertices and <em>E</em> is the
     * number of edges. Each call to {@link #distTo(int)} and {@link #hasPathTo(int)} takes constant
     * time; each call to {@link #pathTo(int)} takes time proportional to the length of the path. It
     * uses extra space (not including the digraph) proportional to <em>V</em>.
     * <p>
     * For additional documentation, see <a href="https://algs4.cs.princeton.edu/42digraph">Section
     * 4.2</a> of
     * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
     */
    private class BreadthFirstDirectedPaths {
        private static final int INFINITY = Integer.MAX_VALUE;
        private boolean[] marked;  // marked[v] = is there an s->v path?
        private int[] edgeTo;      // edgeTo[v] = last edge on shortest s->v path
        private int[] distTo;      // distTo[v] = length of shortest s->v path

        /**
         * Computes the shortest path from {@code s} and every other vertex in graph {@code G}.
         *
         * @param G the digraph
         * @param s the source vertex
         * @throws IllegalArgumentException unless {@code 0 <= v < V}
         */
        public BreadthFirstDirectedPaths(Digraph G, int s) {
            marked = new boolean[G.V()];
            distTo = new int[G.V()];
            edgeTo = new int[G.V()];
            for (int v = 0; v < G.V(); v++)
                distTo[v] = INFINITY;
            validateVertex(s);
            bfs(G, s);
        }

        /**
         * Computes the shortest path from any one of the source vertices in {@code sources} to
         * every other vertex in graph {@code G}.
         *
         * @param G       the digraph
         * @param sources the source vertices
         * @throws IllegalArgumentException unless each vertex {@code v} in {@code sources}
         *                                  satisfies {@code 0 <= v < V}
         */
        public BreadthFirstDirectedPaths(Digraph G, Iterable<Integer> sources) {
            marked = new boolean[G.V()];
            distTo = new int[G.V()];
            edgeTo = new int[G.V()];
            for (int v = 0; v < G.V(); v++)
                distTo[v] = INFINITY;
            validateVertices(sources);
            bfs(G, sources);
        }

        // BFS from single source
        private void bfs(Digraph G, int s) {
            Queue<Integer> q = new Queue<Integer>();
            marked[s] = true;
            distTo[s] = 0;
            q.enqueue(s);
            while (!q.isEmpty()) {
                int v = q.dequeue();
                for (int w : G.adj(v)) {
                    if (!marked[w]) {
                        edgeTo[w] = v;
                        distTo[w] = distTo[v] + 1;
                        marked[w] = true;
                        q.enqueue(w);
                    }
                }
            }
        }

        // BFS from multiple sources
        private void bfs(Digraph G, Iterable<Integer> sources) {
            Queue<Integer> q = new Queue<Integer>();
            for (int s : sources) {
                marked[s] = true;
                distTo[s] = 0;
                q.enqueue(s);
            }
            while (!q.isEmpty()) {
                int v = q.dequeue();
                for (int w : G.adj(v)) {
                    if (!marked[w]) {
                        edgeTo[w] = v;
                        distTo[w] = distTo[v] + 1;
                        marked[w] = true;
                        q.enqueue(w);
                    }
                }
            }
        }

        /**
         * Is there a directed path from the source {@code s} (or sources) to vertex {@code v}?
         *
         * @param v the vertex
         * @return {@code true} if there is a directed path, {@code false} otherwise
         * @throws IllegalArgumentException unless {@code 0 <= v < V}
         */
        public boolean hasPathTo(int v) {
            validateVertex(v);
            return marked[v];
        }

        /**
         * Returns the number of edges in a shortest path from the source {@code s} (or sources) to
         * vertex {@code v}?
         *
         * @param v the vertex
         * @return the number of edges in a shortest path
         * @throws IllegalArgumentException unless {@code 0 <= v < V}
         */
        public int distTo(int v) {
            validateVertex(v);
            return distTo[v];
        }

        /**
         * Returns a shortest path from {@code s} (or sources) to {@code v}, or {@code null} if no
         * such path.
         *
         * @param v the vertex
         * @return the sequence of vertices on a shortest path, as an Iterable
         * @throws IllegalArgumentException unless {@code 0 <= v < V}
         */
        public Iterable<Integer> pathTo(int v) {
            validateVertex(v);

            if (!hasPathTo(v)) return null;
            Stack<Integer> path = new Stack<Integer>();
            int x;
            for (x = v; distTo[x] != 0; x = edgeTo[x])
                path.push(x);
            path.push(x);
            return path;
        }

        // throw an IllegalArgumentException unless {@code 0 <= v < V}
        private void validateVertex(int v) {
            int V = marked.length;
            if (v < 0 || v >= V)
                throw new IllegalArgumentException(
                        "vertex " + v + " is not between 0 and " + (V - 1));
        }

        // throw an IllegalArgumentException unless {@code 0 <= v < V}
        private void validateVertices(Iterable<Integer> vertices) {
            if (vertices == null) {
                throw new IllegalArgumentException("argument is null");
            }
            int V = marked.length;
            for (int v : vertices) {
                if (v < 0 || v >= V) {
                    throw new IllegalArgumentException(
                            "vertex " + v + " is not between 0 and " + (V - 1));
                }
            }
        }


        /**
         * Unit tests the {@code BreadthFirstDirectedPaths} data type.
         *
         * @param args the command-line arguments
         */
        // public void main(String[] args) {
        //     In in = new In(args[0]);
        //     Digraph G = new Digraph(in);
        //     // StdOut.println(G);
        //
        //     int s = Integer.parseInt(args[1]);
        //     BreadthFirstDirectedPaths bfs = new BreadthFirstDirectedPaths(G, s);
        //
        //     for (int v = 0; v < G.V(); v++) {
        //         if (bfs.hasPathTo(v)) {
        //             StdOut.printf("%d to %d (%d):  ", s, v, bfs.distTo(v));
        //             for (int x : bfs.pathTo(v)) {
        //                 if (x == s) StdOut.print(x);
        //                 else StdOut.print("->" + x);
        //             }
        //             StdOut.println();
        //         }
        //
        //         else {
        //             StdOut.printf("%d to %d (-):  not connected\n", s, v);
        //         }
        //
        //     }
        // }
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (v > vertices || w > vertices)
            throw new IllegalArgumentException("Vertices cannot be out of range");

        BreadthFirstDirectedPaths bfdpForV = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths bfdpForW = new BreadthFirstDirectedPaths(digraph, w);
        int distance = Integer.MAX_VALUE;

        // check for a directed path from v to w or from w to v
        if (bfdpForV.marked[w]) {
            int nearestDistance = bfdpForV.distTo(w);
            if (nearestDistance < distance) {
                // if this is the shortest distance found, mark this as common ancestor
                singleAncestor = w;
                distance = nearestDistance;
            }
        }
        if (bfdpForW.marked[v]) {
            int nearestDistance = bfdpForW.distTo(v);
            if (nearestDistance < distance) {
                // if this is the shortest distance found, mark this as common ancestor
                singleAncestor = v;
                distance = nearestDistance;
            }
        }

        // iterate through all vertices and if bfs for v and w both have this vertex marked,
        // then that the means that vertex is a common ancestor and provides an ancestral path
        for (int i = 0; i < vertices; i++) {
            if (bfdpForV.marked[i] && bfdpForW.marked[i]) {
                int nearestDistance = bfdpForV.distTo(i) + bfdpForW.distTo(i);
                if (nearestDistance < distance) {
                    // if this is the shortest distance found, mark this as common ancestor
                    distance = nearestDistance;
                    singleAncestor = i;
                }
            }
        }

        // if distance variable was not modified, then no common path exists
        // if no common path exists, then no common ancestor exists
        if (distance != Integer.MAX_VALUE) return distance;
        else {
            singleAncestor = -1;
        }
        return -1;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        if (v > vertices || w > vertices)
            throw new IllegalArgumentException("Vertices cannot be out of range");
        // calling the length method automatically finds and stores the common ancestor
        length(v, w);
        return singleAncestor;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new IllegalArgumentException("Iterable cannot be null");
        for (Integer i : v) {
            if (i == null) throw new IllegalArgumentException("Item in iterable cannot be null");
            if (i > vertices) throw new IllegalArgumentException("Vertices cannot be out of range");
        }
        for (Integer i : w) {
            if (i == null) throw new IllegalArgumentException("Item in iterable cannot be null");
            if (i > vertices) throw new IllegalArgumentException("Vertices cannot be out of range");
        }

        // same as length method for individual arguments, but use bfs method for iterables
        BreadthFirstDirectedPaths bfdpForV = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths bfdpForW = new BreadthFirstDirectedPaths(digraph, w);
        int distance = Integer.MAX_VALUE;
        for (int i = 0; i < vertices; i++) {
            if (bfdpForV.marked[i] && bfdpForW.marked[i]) {
                int nearestDistance = bfdpForV.distTo(i) + bfdpForW.distTo(i);
                if (nearestDistance < distance) {
                    distance = nearestDistance;
                    singleAncestor = i;
                }
            }
        }
        if (distance != Integer.MAX_VALUE) return distance;
        else {
            singleAncestor = -1;
        }
        return -1;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new IllegalArgumentException("Iterable cannot be null");
        for (Integer i : v) {
            if (i == null) throw new IllegalArgumentException("Item in iterable cannot be null");
            if (i > vertices) throw new IllegalArgumentException("Vertices cannot be out of range");
        }
        for (Integer i : w) {
            if (i == null) throw new IllegalArgumentException("Item in iterable cannot be null");
            if (i > vertices) throw new IllegalArgumentException("Vertices cannot be out of range");
        }

        // same as ancestor method for single arguments; length method for iterable arguments
        // takes care of the work
        length(v, w);
        return singleAncestor;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            // ArrayList<Integer> v = new ArrayList<>();
            // ArrayList<Integer> w = new ArrayList<>();
            // v.add(StdIn.readInt());
            // v.add(StdIn.readInt());
            // w.add(StdIn.readInt());
            // w.add(StdIn.readInt());
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}


