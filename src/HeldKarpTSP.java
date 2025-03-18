import java.util.*;

public class HeldKarpTSP {
    private static final double INF = Double.POSITIVE_INFINITY;
    private final double[][] dist;
    private final int n;

    // dp[mask][j] = minimum cost to visit all cities in 'mask' with j as the last city
    private final double[][] dp;

    // parent[mask][j] = the city visited before j in the optimal path that visits 'mask' with j last
    private final int[][] parent;

    public HeldKarpTSP(double[][] dist) {
        this.dist = dist;
        this.n = dist.length;
        this.dp = new double[1 << n][n];
        this.parent = new int[1 << n][n];
        for (int mask = 0; mask < (1 << n); mask++) {
            Arrays.fill(dp[mask], INF);
            Arrays.fill(parent[mask], -1);
        }
    }

    public Pair<Double, String> solve() {
        // Base case: start from 0 -> j
        // We consider subsets that include city 0. We'll treat city 0 as the fixed start.
        dp[1][0] = 0.0;  // 1 means bitmask with only 0th city included

        // Iterate over all subsets of the n cities
        for (int mask = 1; mask < (1 << n); mask++) {
            // Consider each city 'j' that could be the last visited in 'mask'
            for (int j = 0; j < n; j++) {
                if ((mask & (1 << j)) == 0) {
                    // city j not in subset 'mask'
                    continue;
                }
                // If dp[mask][j] was never set (still INF), skip
                double currentCost = dp[mask][j];
                if (currentCost == INF) continue;

                // Try extending path from j to some next city k not yet in mask
                for (int k = 0; k < n; k++) {
                    if ((mask & (1 << k)) != 0) {
                        // city k already in subset
                        continue;
                    }
                    int nextMask = mask | (1 << k);
                    double newCost = currentCost + dist[j][k];
                    if (newCost < dp[nextMask][k]) {
                        dp[nextMask][k] = newCost;
                        parent[nextMask][k] = j;
                    }
                }
            }
        }

        // The endMask is when we've visited all n cities
        int endMask = (1 << n) - 1;
        double bestCost = INF;
        int bestLastCity = -1;

        // Find the best way to return to the start city (0)
        for (int j = 1; j < n; j++) {
            double cost = dp[endMask][j] + dist[j][0];
            if (cost < bestCost) {
                bestCost = cost;
                bestLastCity = j;
            }
        }

        // Reconstruct path
        List<Integer> path = reconstructPath(bestLastCity, endMask);
        path.add(0);  // return to start

        String optimalPath = formatPath(path);
        return new Pair<>(bestCost, optimalPath);
    }

    private List<Integer> reconstructPath(int last, int mask) {
        List<Integer> path = new ArrayList<>();
        while (last != -1) {
            path.add(last);
            int prev = parent[mask][last];
            mask = mask & ~(1 << last);  // remove 'last' from mask
            last = prev;
        }
        Collections.reverse(path);
        return path;
    }

    private String formatPath(List<Integer> path) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            sb.append(path.get(i));
            if (i < path.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}