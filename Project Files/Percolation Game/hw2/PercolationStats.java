package hw2;                       

import edu.princeton.cs.introcs.StdRandom;
import edu.princeton.cs.introcs.StdStats;                 

public class PercolationStats {
    private double[] numOpens;
    private Percolation percSample;
    private int value;

    public PercolationStats(int N, int T) {
        if (N <= 0 || T <= 0) {
            throw new IllegalArgumentException();
        }
        numOpens = new double[T];
        for (int i = 0; i < T; i++) {
            percSample = new Percolation(N);
            value = 0;
            while (!percSample.percolates()) {
                int row = StdRandom.uniform(N);
                int col = StdRandom.uniform(N);
                if (!percSample.isOpen(row, col)) {
                    percSample.open(row, col);
                    value++;
                }
            }
            numOpens[i] = (double) value / (N * N);
        }
    }
    public double mean() {
        return StdStats.mean(numOpens);
    }
    public double stddev() {
        return StdStats.stddev(numOpens);
    }
    public double confidenceLow() {
        return (mean() - ((1.96 * stddev())) / Math.sqrt(numOpens.length));
    }
    public double confidenceHigh() {
        return (mean() + ((1.96 * stddev())) / Math.sqrt(numOpens.length));
    }
}                       
