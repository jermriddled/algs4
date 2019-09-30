/* *****************************************************************************
 *  Name: Jeremy Steinberg
 *  Date: 9/27/19
 *  Description: Boggle
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BoggleSolver {

    private static final int R = 26;
    private final RWayTrie dictionary;
    private Set<String> boggleWords;
    private int rows;
    private int cols;
    private ArrayList<ArrayList<Integer>> adj;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        this.dictionary = new RWayTrie();
        String[] copy = new String[dictionary.length];
        for (int i = 0; i < dictionary.length; i++) {
            copy[i] = dictionary[i];
        }
        for (int i = 0; i < dictionary.length; i++) {
            this.dictionary.put(copy[i], i + 1);
        }
    }

    private static class Node {
        private Object val;
        private Node[] next = new Node[R];
    }

    private class RWayTrie {

        private Node root;
        private static final int ASCIIOffset = 65;

        public RWayTrie() {
            root = new Node();
        }

        public void put(String key, Object val) {
            root = put(root, key, val, 0);
        }

        private Node put(Node x, String key, Object val, int d) {
            if (x == null) x = new Node();
            if (d == key.length()) {
                x.val = val;
                return x;
            }
            char c = key.charAt(d);
            x.next[c - ASCIIOffset] = put(x.next[c - ASCIIOffset], key, val, d + 1);
            return x;
        }

        public boolean contains(String key) {
            return get(key) != null;
        }

        public Object get(String key) {
            Node x = get(root, key, 0);
            if (x == null) return null;
            return x.val;
        }

        // Attempts to retrieve a prefix from the trie
        public Node getPrefix(String key) {
            Node x = get(root, key, 0);
            if (x == null) return null;
            return x;
        }

        private Node get(Node x, String key, int d) {
            if (x == null) return null;
            if (d == key.length()) return x;
            char c = key.charAt(d);
            return get(x.next[c - ASCIIOffset], key, d + 1);
        }
    }

    // Checks whether a string is a prefix of a word in the dictionary OR is a word itself
    private boolean isPrefix(String prefix) {
        Node x = dictionary.getPrefix(prefix);
        if (x == null) return false;

        // if node has a value, it is a word
        if (x.val != null) return true;

        // If node does not has a value but has at least one non-null branch in next, it is a pefix
        for (int i = 0; i < R; i++) {
            if (x.next[i] != null) return true;
        }
        return false;
    }

    // Custom version of Princeton ALGS4 DepthFirstSearch method
    // credit Robert Robert Sedgewick and Kevin Wayne
    // https://algs4.cs.princeton.edu/41graph/DepthFirstSearch.java.html
    private class CustomDepthFirstSearch {
        private boolean[] marked;
        private int count;
        private boolean[] path;
        private char currentLetter;
        private int thisRound;
        private StringBuilder word;

        public CustomDepthFirstSearch(BoggleBoard B, int s) {
            marked = new boolean[cols * rows];
            path = new boolean[cols * rows];
            int y = s / cols;
            int x = s % cols;
            if (rows == 1) {
                y = 0;
                x = s;
            }
            else if (cols == 1) {
                y = s;
                x = 0;
            }
            currentLetter = B.getLetter(y, x);
            thisRound = s;
            word = new StringBuilder();
            dfs(B, s);
        }

        private Iterable<Integer> adj(int x) {
            return adj.get(x);
        }

        // Depth first search from v
        private void dfs(BoggleBoard B, int s) {
            count++;
            marked[s] = true;
            path[s] = true;
            int y = s / cols;
            int x = s % cols;
            if (rows == 1) {
                y = 0;
                x = s;
            }
            else if (cols == 1) {
                y = s;
                x = 0;
            }
            char letter = B.getLetter(y, x);

            // Corner case for a "Qu" tile
            if (letter == 'Q') {
                word.append('Q');
                word.append('U');
            }
            else word.append(letter);

            // If current string is not a prefix, unmark the index of the character currently
            // being examined so that it can be examined for the next possible prefix
            if (!isPrefix(word.toString())) {
                path[s] = false;
                marked[s] = false;
            }
            else {

                // If it's a word in the dictionary of a length greater than two, add it to the
                // boggle words
                if (dictionary.get(word.toString()) != null && word.length() > 2)
                    boggleWords.add(word.toString());
                for (int w : adj(s)) {

                    // This is used to reset the stringbuilder back to the the starting character
                    // once the recursive function exhausts all possible prefixes for a certain
                    // direction
                    if (s == thisRound) {
                        word = new StringBuilder();
                        if (currentLetter == 'Q') {
                            word.append('Q');
                            word.append('U');
                        }
                        else word.append(currentLetter);
                    }

                    // If unmarked, check recursively for prefixes down each possible path; after
                    // each check is finished, unmark the path so that future exploration of
                    // other prefixes is possible
                    if (!marked[w]) {
                        dfs(B, w);
                        marked[w] = false;
                        if (word.charAt(word.length() - 1) == 'U'
                                && word.charAt(word.length() - 2) == 'Q') {
                            word.deleteCharAt(word.length() - 1);
                            word.deleteCharAt(word.length() - 1);
                        }
                        else word.deleteCharAt(word.length() - 1);
                    }
                    if (s == thisRound) {
                        for (int z : adj(s)) marked[z] = false;
                    }
                }
            }
        }

        /**
         * Is there a path between the source vertex {@code s} and vertex {@code v}?
         *
         * @return {@code true} if there is a path, {@code false} otherwise
         * @throws IllegalArgumentException unless {@code 0 <= v < V}
         */
        public boolean marked(BoggleBoard B, int x, int y) {
            validateVertex(B, x, y);
            return true;
        }

        /**
         * Returns the number of vertices connected to the source vertex {@code s}.
         *
         * @return the number of vertices connected to the source vertex {@code s}
         */
        public int count() {
            return count;
        }

        // throw an IllegalArgumentException unless {@code 0 <= v < V}
        private void validateVertex(BoggleBoard B, int x, int y) {
            int X = B.cols();
            int Y = B.rows();
            if (x < 0 || x >= X)
                throw new IllegalArgumentException(
                        "coordinate " + x + " is not between 0 and " + (X - 1));
            if (y < 0 || y >= Y)
                throw new IllegalArgumentException(
                        "coordinate " + y + " is not between 0 and " + (Y - 1));
        }

    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        boggleWords = new HashSet<>();
        rows = board.rows();
        cols = board.cols();
        adj = new ArrayList<>();

        // build the adjacency list for each boggle tile
        for (int i = 0; i < rows * cols; i++) {
            adj.add(new ArrayList<Integer>());
            if (rows == 1 && cols == 1) {
                break;
            }
            else if (rows == 1 || cols == 1) {
                if (i == 0) adj.get(i).add(i + 1);
                else if (rows == 1 && i == cols - 1) adj.get(i).add(i - 1);
                else if (cols == 1 && i == rows - 1) adj.get(i).add(i - 1);
                else {
                    adj.get(i).add(i - 1);
                    adj.get(i).add(i + 1);
                }
            }
            else if (i % cols == 0) {
                if (i < cols) {
                    adj.get(i).add(i + 1);
                    adj.get(i).add(i + cols);
                    adj.get(i).add(i + cols + 1);
                }
                else if (i >= cols * (rows - 1)) {
                    adj.get(i).add(i - cols);
                    adj.get(i).add(i - cols + 1);
                    adj.get(i).add(i + 1);
                }
                else {
                    adj.get(i).add(i - cols);
                    adj.get(i).add(i - cols + 1);
                    adj.get(i).add(i + 1);
                    adj.get(i).add(i + cols + 1);
                    adj.get(i).add(i + cols);
                }
            }
            else if ((i + 1) % cols == 0) {
                if (i < cols) {
                    adj.get(i).add(i - 1);
                    adj.get(i).add(i + cols);
                    adj.get(i).add(i + cols - 1);
                }
                else if (i >= cols * (rows - 1)) {
                    adj.get(i).add(i - 1);
                    adj.get(i).add(i - cols);
                    adj.get(i).add(i - cols - 1);
                }
                else {
                    adj.get(i).add(i - cols - 1);
                    adj.get(i).add(i - cols);
                    adj.get(i).add(i - 1);
                    adj.get(i).add(i + cols - 1);
                    adj.get(i).add(i + cols);
                }
            }
            else {
                if (i < cols) {
                    adj.get(i).add(i - 1);
                    adj.get(i).add(i + 1);
                    adj.get(i).add(i + cols - 1);
                    adj.get(i).add(i + cols);
                    adj.get(i).add(i + cols + 1);
                }
                else if (i >= cols * (rows - 1)) {
                    adj.get(i).add(i - 1);
                    adj.get(i).add(i + 1);
                    adj.get(i).add(i - cols - 1);
                    adj.get(i).add(i - cols);
                    adj.get(i).add(i - cols + 1);
                }
                else {
                    adj.get(i).add(i - 1);
                    adj.get(i).add(i + 1);
                    adj.get(i).add(i - cols - 1);
                    adj.get(i).add(i - cols);
                    adj.get(i).add(i - cols + 1);
                    adj.get(i).add(i + cols - 1);
                    adj.get(i).add(i + cols);
                    adj.get(i).add(i + cols + 1);
                }
            }
        }

        for (int i = 0; i < cols * rows; i++) {
            CustomDepthFirstSearch cdfs = new CustomDepthFirstSearch(board, i);
        }
        // CustomDepthFirstSearch cdfs = new CustomDepthFirstSearch(board, 23);
        return boggleWords;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (dictionary.get(word) != null) {
            if (word.length() == 3 || word.length() == 4) return 1;
            else if (word.length() == 5) return 2;
            else if (word.length() == 6) return 3;
            else if (word.length() == 7) return 5;
            else if (word.length() >= 8) return 11;
            else return 0;
        }
        return 0;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
    // public static void main(String[] args) {
    //     In in = new In(args[0]);
    //     String[] dictionary = in.readAllStrings();
    //     BoggleSolver solver = new BoggleSolver(dictionary);
    //     for (int i = 0; i < 1000; i++) {
    //         BoggleBoard board = new BoggleBoard();
    //         int score = 0;
    //         for (String word : solver.getAllValidWords(board)) {
    //             // StdOut.println(word);
    //             score += solver.scoreOf(word);
    //         }
    //         StdOut.println("Score = " + score);
    //     }
    // }
}

