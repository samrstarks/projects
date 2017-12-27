package hw3.hash;

import java.util.HashSet;
import java.util.Set;
import java.util.AbstractMap;
import java.util.HashMap;

public class HashTableVisualizer {

    public static void main(String[] args) {
        /* scale: StdDraw scale
           N:     number of items
           M:     number of buckets */

        double scale = 1.0;
        int N = 50;
        int M = 10;

        HashTableDrawingUtility.setScale(scale);
        Set<Oomage> oomies = new HashSet<Oomage>();
        for (int i = 0; i < N; i += 1) {
            oomies.add(SimpleOomage.randomSimpleOomage());
        }
        visualize(oomies, M, scale);
    }

    public static void visualize(Set<Oomage> set, int M, double scale) {
        HashTableDrawingUtility.drawLabels(M);
        HashMap bucketHash = new HashMap(M);
        for (Oomage omage : set) {
          int bucket = (omage.hashCode() & 0x7FFFFFFF) % M;
          if (bucketHash.containsKey(bucket)) {
            int length = (int) bucketHash.get(bucket);
            bucketHash.put(bucket, length + 1);
          } else {
            bucketHash.put(bucket, 0);
          }
          double x = HashTableDrawingUtility.xCoord((int) bucketHash.get(bucket));
          double y = HashTableDrawingUtility.yCoord(bucket, M);
          omage.draw(x, y, scale);
        }
    }
} 
