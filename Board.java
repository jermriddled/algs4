/* *****************************************************************************
 *  Name: Jeremy Steinberg
 *  Date: 8/9/19
 *  Description: Board for 8 puzzle
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;

import java.util.Iterator;

public final class Board {

    private final int[][] tiles;
    private final int n;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        this.tiles = new int[tiles.length][tiles.length];
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles.length; j++) {
                this.tiles[i][j] = tiles[i][j];
            }
        }
        n = tiles.length;
    }

    // string representation of this board
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(n + "\n");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                s.append(String.format("%d ", tiles[i][j]));
            }
            s.append("\n");
        }
        return s.toString();
    }

    // board dimension n
    public int dimension() {
        return n;
    }

    // number of tiles out of place
    public int hamming() {
        int hamming = 0;
        int tile = 1;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] != 0 && tiles[i][j] != tile) hamming++;
                tile++;
            }
        }
        return hamming;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int manhattan = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] != 0) {
                    int x = (tiles[i][j] - 1) / n;
                    int y = (tiles[i][j] - 1) % n;
                    int dx = i - x;
                    int dy = j - y;
                    manhattan += Math.abs(dx) + Math.abs(dy);
                }
            }
        }
        return manhattan;
    }

    // is this board the goal board?
    public boolean isGoal() {
        int tile = 1;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // check that last tile is 0
                if (i == n - 1 && j == n - 1) {
                    if (tiles[i][j] != 0) return false;
                }
                // check that every tile is in sequential order and that tile value corresponds to tile number
                else if (tiles[i][j] != tile) return false;
                tile++;
            }
        }
        return true;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == this) return true;
        if (y == null) return false;
        else if (this.getClass() != y.getClass()) return false;

        Board board = (Board) y;
        if (this.n != board.n) return false;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (this.tiles[i][j] != board.tiles[i][j]) return false;
            }
        }
        return true;


    }

    // create all possible neighbors of a board (neighbors are any board in which exactly one tile
    // has been switched with the empty ("0") tile
    private Iterator<Board> makeNeighbors() {
        Stack<Board> neighbors = new Stack<>();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] == 0) {
                    if (i == 0) {
                        if (j == 0) {
                            Board board1 = new Board(tiles);
                            exch(board1, i, j, i, j + 1);
                            Board board2 = new Board(tiles);
                            exch(board2, i, j, i + 1, j);
                            neighbors.push(board1);
                            neighbors.push(board2);
                        }
                        else if (j == tiles.length - 1) {
                            Board board1 = new Board(tiles);
                            exch(board1, i, j, i, j - 1);
                            Board board2 = new Board(tiles);
                            exch(board2, i, j, i + 1, j);
                            neighbors.push(board1);
                            neighbors.push(board2);
                        }
                        else {
                            Board board1 = new Board(tiles);
                            exch(board1, i, j, i, j - 1);
                            Board board2 = new Board(tiles);
                            exch(board2, i, j, i + 1, j);
                            Board board3 = new Board(tiles);
                            exch(board3, i, j, i, j + 1);
                            neighbors.push(board1);
                            neighbors.push(board2);
                            neighbors.push(board3);
                        }
                        break;
                    }
                    else if (i == tiles.length - 1) {
                        if (j == 0) {
                            Board board1 = new Board(tiles);
                            exch(board1, i, j, i, j + 1);
                            Board board2 = new Board(tiles);
                            exch(board2, i, j, i - 1, j);
                            neighbors.push(board1);
                            neighbors.push(board2);
                        }
                        else if (j == tiles.length - 1) {
                            Board board1 = new Board(tiles);
                            exch(board1, i, j, i, j - 1);
                            Board board2 = new Board(tiles);
                            exch(board2, i, j, i - 1, j);
                            neighbors.push(board1);
                            neighbors.push(board2);
                        }
                        else {
                            Board board1 = new Board(tiles);
                            exch(board1, i, j, i, j - 1);
                            Board board2 = new Board(tiles);
                            exch(board2, i, j, i - 1, j);
                            Board board3 = new Board(tiles);
                            exch(board3, i, j, i, j + 1);
                            neighbors.push(board1);
                            neighbors.push(board2);
                            neighbors.push(board3);
                        }
                        break;
                    }
                    else {
                        if (j == 0) {
                            Board board1 = new Board(tiles);
                            exch(board1, i, j, i, j + 1);
                            Board board2 = new Board(tiles);
                            exch(board2, i, j, i + 1, j);
                            Board board3 = new Board(tiles);
                            exch(board3, i, j, i - 1, j);
                            neighbors.push(board1);
                            neighbors.push(board2);
                            neighbors.push(board3);
                        }
                        else if (j == tiles.length - 1) {
                            Board board1 = new Board(tiles);
                            exch(board1, i, j, i, j - 1);
                            Board board2 = new Board(tiles);
                            exch(board2, i, j, i + 1, j);
                            Board board3 = new Board(tiles);
                            exch(board3, i, j, i - 1, j);
                            neighbors.push(board1);
                            neighbors.push(board2);
                            neighbors.push(board3);
                        }
                        else {
                            Board board1 = new Board(tiles);
                            exch(board1, i, j, i, j + 1);
                            Board board2 = new Board(tiles);
                            exch(board2, i, j, i, j - 1);
                            Board board3 = new Board(tiles);
                            exch(board3, i, j, i + 1, j);
                            Board board4 = new Board(tiles);
                            exch(board4, i, j, i - 1, j);
                            neighbors.push(board1);
                            neighbors.push(board2);
                            neighbors.push(board3);
                            neighbors.push(board4);
                        }
                        break;
                    }
                }
            }
        }
        return neighbors.iterator();
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        return new Iterable<Board>() {

            @Override
            public Iterator<Board> iterator() {
                return makeNeighbors();
            }
        };
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        Board board = new Board(tiles);
        int randomNumber = 0;
        if (board.tiles[randomNumber][randomNumber] == 0) {
            randomNumber = 1;
        }
        // ensure that neither of the tiles being switched are the empty tile
        if (randomNumber == 0) {
            if (board.tiles[randomNumber + 1][randomNumber] == 0) {
                exch(board, randomNumber, randomNumber, randomNumber, randomNumber + 1);
            }
            else if (board.tiles[randomNumber][randomNumber + 1] == 0) {
                exch(board, randomNumber, randomNumber, randomNumber + 1, randomNumber);
            }
            else {
                exch(board, randomNumber, randomNumber, randomNumber, randomNumber + 1);
            }
        }
        else {
            if (board.tiles[randomNumber - 1][randomNumber] == 0) {
                exch(board, randomNumber, randomNumber, randomNumber, randomNumber - 1);
            }
            else {
                exch(board, randomNumber, randomNumber, randomNumber - 1, randomNumber);
            }
        }
        return board;
    }


    private void exch(Board a, int i, int k, int j, int m) {
        int temp = a.tiles[i][k];
        a.tiles[i][k] = a.tiles[j][m];
        a.tiles[j][m] = temp;
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);
        Board board = new Board(tiles);
        System.out.println(initial.toString());
        System.out.println(initial.twin().toString());
        System.out.println(board.twin().toString());
        // System.out.println("Hamming: " + initial.hamming());
        // System.out.println("Manhattan: " + initial.manhattan());
        // System.out.println("Is goal: " + initial.isGoal());
        // System.out.println("equal: " + initial.equals(board));
        // Iterator<Board> i = initial.neighbors().iterator();
        // while (i.hasNext()) System.out.println("Neighbor is: \n" + i.next());
        // System.out.println("Twin is: ");
        System.out.println(initial.twin().toString());
    }
}
