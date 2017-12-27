import java.awt.Color;
import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
    private Picture picture;
    private double[][] energy;
    private int[][] edge;

    public SeamCarver(Picture picture) {
        this.picture = new Picture(picture);
    }

    public Picture picture() {
        return new Picture(this.picture);
    }

    public int width() {
        return picture.width();
    }

    public int height() {
        return picture.height();
    }

    // energy of pixel at COLUMN x and ROW y
    public double energy(int x, int y) {
        int a = x + 1;
        int b = x - 1;
        int c = y + 1;
        int d = y - 1;

        if (x < 0 || y < 0 || x >= width() || y >= height()) {
            throw new java.lang.IndexOutOfBoundsException();
        }

        if (x == 0) {
            b = width() - 1;
        }

        if (y == 0) {
            d = height() - 1;
        }

        if (x == width() - 1) {
            a = 0;
        }

        if (y == height() - 1) {
            c = 0;
        }

        return distanceHelp(picture.get(a, y), picture.get(b, y))
             + distanceHelp(picture.get(x, c), picture.get(x, d));
    }

    private int distanceHelp(Color x, Color y) {
        int r = x.getRed() - y.getRed();
        int g = x.getGreen() - y.getGreen();
        int b = x.getBlue() - y.getBlue();

        return r * r + g * g + b * b;
    }

    public int[] findHorizontalSeam() {
        Picture og = picture;
        Picture copy = new Picture(og.height(), og.width());

        for (int row = 0; row < copy.width(); row++) {
            for (int col = 0; col < copy.height(); col++) {
                copy.set(row, col, og.get(col, row));
            }
        }

        this.picture = copy;
        int[] seam = findVerticalSeam();
        this.picture = og;
        return seam;
    }

    public int[] findVerticalSeam() {
        energy = new double[width()][height()];
        edge = new int[width()][height()];

        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                energy[x][y] = Double.POSITIVE_INFINITY;
            }
        }

        for (int x = 0; x < width(); x++) {
            energy[x][0] = energy(x, 0);
        }

        for (int y = 0; y < height() - 1; y++) {
            for (int x = 0; x < width(); x++) {
                if (x > 0) {
                    relax(x, y, x - 1, y + 1);
                }
                relax(x, y, x, y + 1);
                if (x < width() - 1) {
                    relax(x, y, x + 1, y + 1);
                }
            }
        }

        double minErgy = Double.POSITIVE_INFINITY;
        int minErgyX = -1;
        for (int w = 0; w < width(); w++) {
            if (energy[w][height() - 1] < minErgy) {
                minErgyX = w;
                minErgy = energy[w][height() - 1];
            }
        }
        assert minErgyX != -1;

        int[] seam = new int[height()];
        seam[height() - 1] = minErgyX;
        int prev = edge[minErgyX][height() - 1];

        for (int h = height() - 2; h >= 0; h--) {
            seam[h] = prev;
            prev = edge[prev][h];
        }
        return seam;
    }

    private void relax(int a, int b, int c, int d) {
        if (energy[c][d] > energy[a][b] + energy(c, d)) {
            energy[c][d] = energy[a][b] + energy(c, d);
            edge[c][d] = a;
        }
    }   

    public void removeHorizontalSeam(int[] seam) {
        this.picture = SeamRemover.removeHorizontalSeam(picture, seam);
    }

    public void removeVerticalSeam(int[] seam) {
        this.picture = SeamRemover.removeVerticalSeam(picture, seam);
    }
}
