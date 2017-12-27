package hw2;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private boolean[][] percArray;
    private int openSites;
    private int n;
    private WeightedQuickUnionUF unionUF;
    private WeightedQuickUnionUF topUF;
    private int top;
    private int top2;
    private int bottom;

    public Percolation(int N) {
        if (N <= 0) {
            throw new IllegalArgumentException("Percolation Error");
        } else {
            percArray = new boolean[N][N];
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    percArray[i][j] = false;
                }
            }
            n = N;
            openSites = 0;
            top = n * n;
            bottom = ((n * n) + 1);
            unionUF = new WeightedQuickUnionUF((N * N) + 2);
            topUF = new WeightedQuickUnionUF((N * N) + 2);
        }
    }

    public void open(int row, int col) {
        if (row > n - 1 || col > n - 1) {
            throw new IndexOutOfBoundsException("Open Error");
        } else if (!percArray[row][col]) {
            percArray[row][col] = true;
            openSites++;

            //checks to see if opening space in top or bottom.
            if (row == 0) {
                int item = translate(row, col);
                topUF.union(item, top);
                unionUF.union(item, top);
            } else if (row == (n - 1)) {
                int item = translate(row, col);
                unionUF.union(item, bottom);
            }
            if (row == 0 && col == 0 && n == 1) {
                unionUF.union(top, bottom);
            }

            //now for the fun stuff...
            int curr = translate(row, col);
            int above = translate((row - 1), col);
            int below = translate((row + 1), col);
            int left = translate(row, (col - 1));
            int right = translate(row, (col + 1));

            //checking above:
            if (above >= 0) {
                if (isOpen((row - 1), col)) {
                    unionUF.union(curr, above);
                    topUF.union(curr, above);
                }
                if (isFull((row - 1), col)) {
                    topUF.union(curr, above);
                }
            }
            //checking below:
            if (below <= (n * n) - 1) {
                if (isOpen((row + 1), col)) {
                    unionUF.union(curr, below);
                    topUF.union(curr, below);
                }
                if (isFull((row + 1), col)) {
                    topUF.union(curr, below);
                }
            }
            //checking left:
            if (lookLeft(col)) { 
                if (isOpen(row, (col - 1))) {
                    unionUF.union(curr, left);
                    topUF.union(curr, left);
                }
                if (isFull(row, (col - 1))) {
                    topUF.union(curr, left);
                }
            }
            //checking right:
            if (lookRight(col, n)) {
                if (isOpen(row, (col + 1))) {
                    unionUF.union(curr, right);
                    topUF.union(curr, right);
                }
                if (isFull(row, (col + 1))) {
                    topUF.union(curr, right);
                }
            }
        }
    }

    public boolean isOpen(int row, int col) {
        if (row < 0 || row > n + 1 || col < 0 || col > n + 1) {
            throw new IndexOutOfBoundsException("isOpen Error");
        } else {
            return percArray[row][col];
        }
    }

    public boolean isFull(int row, int col) {
        if (row < 0 || row > n || col < 0 || col > n) {
            throw new IndexOutOfBoundsException("isFull Error");
        } else {
            int index = translate(row, col);
            return topUF.connected(top, index);
        }
    }

    public int numberOfOpenSites() {
        return openSites;
    }

    public boolean percolates() {
        return unionUF.connected(top, bottom);
    }

    public int translate(int row, int col) {
        int val = ((row * n) + col);
        return val;
    }

    public boolean lookLeft(int col) {
        return ((col - 1) >= 0);
    }

    public boolean lookRight(int col, int size) {
        return ((col + 1) < size);
    }
}                       
