/* *****************************************************************************
 *  Name: Jeremy Steinberg
 *  Date: 7/15/19
 *  Description: Assignment #1
 **************************************************************************** */

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    private final WeightedQuickUnionUF structure;
    private final WeightedQuickUnionUF structure2;
    private boolean[][] grid;
    private final int rowLength;
    private int openSites;
    private final int virtualTop;
    private final int virtualBottom;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0) throw new IllegalArgumentException("n must be greater than 0");
        structure = new WeightedQuickUnionUF((n * n) + 2);
        structure2 = new WeightedQuickUnionUF((n * n) + 1);
        grid = new boolean[n + 1][n + 1];
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n; j++) {
                grid[i][j] = false;
            }
        }
        rowLength = n;
        openSites = 0;
        virtualTop = 0;
        virtualBottom = (n * n) + 1;
    }

    // transforms x, y coordinates in grid to an index in WeightedQuickUnionUF structure
    private int xyTo1D(int row, int col) {
        return (col) + ((row - 1) * rowLength);
    }

    // checks whether indices provided are valid
    private void validIndex(int row, int col) {
        if (row <= 0 || row > rowLength || col <= 0 || col > rowLength) {
            throw new IllegalArgumentException(
                    "Invalid entry for row or column...row = " + row + " " + "col = " + col);
        }
    }

    // opens the site (row, col) if it is not open already
    // @edu.umd.cs.findbugs.annotations.SuppressFBWarnings("UCF_USELESS_CONTROL_FLOW_NEXT_LINE")
    public void open(int row, int col) {
        validIndex(row, col);
        int cursor = xyTo1D(row, col);
        if (!isOpen(row, col)) {
            grid[row][col] = true;
            openSites++;
            if (row == 1) {
                structure.union(cursor, virtualTop);
                structure2.union(cursor, virtualTop);
                if (col != 1 && isOpen(row, col - 1)) {
                    structure.union(cursor - 1, cursor);
                    structure2.union(cursor - 1, cursor);
                }
                if (!(row >= rowLength) && isOpen(row + 1, col)) {
                    structure.union(cursor, cursor + rowLength);
                    structure2.union(cursor, cursor + rowLength);
                }
                if (col != rowLength) {
                    if (isOpen(row, col + 1)) {
                        structure.union(cursor, cursor + 1);
                        structure2.union(cursor, cursor + 1);
                    }
                }
            }
            else {
                if (col != 1 && isOpen(row, col - 1)) {
                    structure.union(cursor - 1, cursor);
                    structure2.union(cursor - 1, cursor);
                }
                if (isOpen(row - 1, col)) {
                    structure.union(cursor - rowLength, cursor);
                    structure2.union(cursor - rowLength, cursor);
                }
                if (row != rowLength) {
                    if (isOpen(row + 1, col)) {
                        structure.union(cursor + rowLength, cursor);
                        structure2.union(cursor + rowLength, cursor);
                    }
                }
                if (col != rowLength) {
                    if (isOpen(row, col + 1)) {
                        structure.union(cursor + 1, cursor);
                        structure2.union(cursor + 1, cursor);
                    }
                }
            }
            if (row == rowLength) structure.union(cursor, virtualBottom);

        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        validIndex(row, col);
        return grid[row][col];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        validIndex(row, col);
        int index = xyTo1D(row, col);
        return (isOpen(row, col) && structure2.find(index) == structure2.find(virtualTop));
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return openSites;
    }

    // does the system percolate?
    public boolean percolates() {
        return (structure.find(virtualTop) == structure.find(virtualBottom));
    }

    // test client (optional)
    public static void main(String[] args) {
        Percolation perc = new Percolation(1);
        perc.open(1, 1);
        // System.out.println(perc.isFull(1, 1));
    }
}
