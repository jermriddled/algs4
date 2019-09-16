/* *****************************************************************************
 *  Name: Jeremy Steinberg
 *  Date: 7/15/19
 *  Description: Assignment #1
 **************************************************************************** */

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {

    private final double mean;
    private final double stddev;
    private final double confidenceLo;
    private final double confidenceHi;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException("Invalid entry for grid size or trials");
        }
        double[] results = new double[trials];
        for (int i = 0; i < trials; i++) {
            Percolation perc = new Percolation(n);
            while (!perc.percolates()) {
                int randomNumber1 = StdRandom.uniform(1, n + 1);
                int randomNumber2 = StdRandom.uniform(1, n + 1);
                perc.open(randomNumber1, randomNumber2);

            }
            results[i] = (double) perc.numberOfOpenSites() / (double) (n * n);
        }
        double constant = 1.96;
        mean = StdStats.mean(results);
        stddev = StdStats.stddev(results);
        confidenceLo = mean - constant * stddev / Math.sqrt(results.length);
        confidenceHi = mean + constant * stddev / Math.sqrt(results.length);
        // for (double d : results) System.out.println(d);
    }

    // sample mean of percolation threshold
    public double mean() {
        return mean;
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return stddev;
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return confidenceLo;
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return confidenceHi;
    }

    // test client (see below)
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int t = Integer.parseInt(args[1]);
        PercolationStats percStats = new PercolationStats(n, t);
        System.out.printf("%-25s %s %.15f %n", "mean", " = ", percStats.mean());
        System.out.printf("%-25s %s %.15f %n", "stddev", " = ", percStats.stddev());
        System.out.printf("%-25s %s %s%.15f%s %.15f%s %n", "95% confidence interval", " = ", "[",
                          percStats.confidenceLo(), ",", percStats.confidenceHi(), "]");

        // System.out.println(percStats.)

    }

}
