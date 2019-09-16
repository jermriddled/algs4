/* *****************************************************************************
 *  Name: Jeremy Steinberg
 *  Date: 8/9/19
 *  Description: Solver for 8 puzzle
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class Solver {

    private SearchNode solution;
    private boolean solvable;

    private class SearchNode implements Comparable<SearchNode> {
        private final Board board;
        private final int moves;
        private final SearchNode previous;
        private final int manhattan;

        // initialize each node to the initial board passed in to the class and calculate its
        // manhattan distance for caching, as well as its number of moves which is based on its
        // previous nodes number of moves
        public SearchNode(Board initial, SearchNode previous) {
            board = initial;
            this.previous = previous;
            manhattan = board.manhattan();
            if (this.previous == null) {
                moves = 0;
            }
            else {
                moves = previous.moves + 1;
            }
        }

        public Board getBoard() {
            return board;
        }

        public int getMoves() {
            return moves;
        }

        public SearchNode getPrevious() {
            return previous;
        }

        public int getManhattan() {
            return manhattan;
        }

        public int compareTo(SearchNode that) {
            return (getManhattan() + getMoves()) - (that.getManhattan() + that.getMoves());
        }
    }

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {

        // ensure proper parameter
        if (initial == null) throw new IllegalArgumentException("Board is empty");

        // create two priority queues; pq to find the solution if solvable, and pq2 to determine
        // if the puzzle is unsolvable.
        MinPQ<SearchNode> pq = new MinPQ<>();
        MinPQ<SearchNode> pq2 = new MinPQ<>();

        // insert initial node containing the initial board into the solution pq
        pq.insert(new SearchNode(initial, null));
        // insert initial node containing the twin of the initial board into the "unsolvable" pq
        pq2.insert(new SearchNode(initial.twin(), null));

        solvable = false;

        while (true) {
            SearchNode previous = pq.delMin();
            // if the minimum element in the queue is the solution, store it and end the loop
            if (previous.getBoard().isGoal()) {
                solution = previous;
                solvable = true;
                break;
            }
            // if solution is found in the board's twin, solution is unsolvable; end the loop
            SearchNode previousTwin = pq2.delMin();
            if (previousTwin.getBoard().isGoal()) {
                return;
            }

            for (Board board : previous.getBoard().neighbors()) {
                // ensure that no duplicate boards are added to the queue
                if (previous.getPrevious() != null && previous.getPrevious().getBoard()
                                                              .equals(board)) continue;

                // add neighbors to queue iff the neighbor has a lower manhattan distance than
                // previous element on queue
                else {
                    SearchNode node = new SearchNode(board, previous);
                    if (node.getManhattan() == previous.getManhattan() - 1
                            || node.getManhattan() == previous.getManhattan() + 1) pq.insert(node);
                }
            }

            // same as above, but for twin board
            for (Board board : previousTwin.getBoard().neighbors()) {
                if (previousTwin.getPrevious() != null && previousTwin.getPrevious().getBoard()
                                                                      .equals(board))
                    continue;
                else {
                    SearchNode node = new SearchNode(board, previousTwin);
                    if (node.getManhattan() == previousTwin.getManhattan() - 1
                            || node.getManhattan() == previousTwin.getManhattan() + 1)
                        pq2.insert(node);
                }
            }

        }
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return solvable;
    }

    // min number of moves to solve initial board
    public int moves() {
        if (isSolvable()) return solution.moves;
        else return -1;
    }

    // sequence of boards in a shortest solution
    public Iterable<Board> solution() {
        if (isSolvable()) {
            Stack<Board> boards = new Stack<>();
            SearchNode cursor = solution;
            boards.push(cursor.getBoard());
            // navigate through queue and add every board in the solution sequence to stack
            while (cursor.getPrevious() != null) {
                cursor = cursor.getPrevious();
                boards.push(cursor.getBoard());
            }
            return boards;
        }
        else return null;
    }

    // test client (see below)
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }

}
