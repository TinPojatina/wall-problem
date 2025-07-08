package org.example;

import java.util.*;

public class BrickWallSolver {
    public static void main(String[] args) {
        BrickWallSolver solver = new BrickWallSolver();

        // Test example
        long result_9_3 = solver.countWallConfigurations(9, 3);
        System.out.println("W(9, 3) = " + result_9_3 + " (should be 8)");

        // Main problem
        long startTime = System.currentTimeMillis();
        long result_32_10 = solver.countWallConfigurations(32, 10);
        long endTime = System.currentTimeMillis();

        System.out.println("W(32, 10) = " + result_32_10);
        System.out.println("Calculation took " + (endTime - startTime) + "ms");
    }

    /**
     * Generates all ways to fill a row of a given width
     */
    private List<List<Integer>> generateRowConfigurations(int width) {
        List<List<Integer>> configurations = new ArrayList<>();
        List<Integer> currentBricks = new ArrayList<>();

        backtrack(width, 0, currentBricks, configurations);
        return configurations;
    }

    /**
     * Recursive helper to build valid brick rows
     */
    private void backtrack(int targetWidth, int currentWidth,
                           List<Integer> currentBricks,
                           List<List<Integer>> configurations) {

        if (currentWidth == targetWidth) {
            configurations.add(new ArrayList<>(currentBricks));
            return;
        }

        if (currentWidth > targetWidth) return;

        // try a 2x1 brick
        if (currentWidth + 2 <= targetWidth) {
            currentBricks.add(2);
            backtrack(targetWidth, currentWidth + 2, currentBricks, configurations);
            currentBricks.remove(currentBricks.size() - 1);
        }

        // try a 3x1 brick
        if (currentWidth + 3 <= targetWidth) {
            currentBricks.add(3);
            backtrack(targetWidth, currentWidth + 3, currentBricks, configurations);
            currentBricks.remove(currentBricks.size() - 1);
        }
    }

    /**
     * Converts a brick row into its internal gap positions
     */
    private List<Integer> getGapPositions(List<Integer> bricks) {
        List<Integer> gaps = new ArrayList<>();
        int position = 0;

        // Skip final brick's end
        for (int i = 0; i < bricks.size() - 1; i++) {
            position += bricks.get(i);
            gaps.add(position);
        }

        return gaps;
    }

    /**
     * Returns true if two lists have no overlapping positions
     */
    private boolean areCompatible(List<Integer> gaps1, List<Integer> gaps2) {
        Set<Integer> gapSet1 = new HashSet<>(gaps1);

        for (Integer gap : gaps2) {
            if (gapSet1.contains(gap)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Counts number of valid wall configurations of given size.
     */
    public long countWallConfigurations(int width, int height) {
        List<List<Integer>> rowConfigs = generateRowConfigurations(width);

        List<List<Integer>> gapPatterns = new ArrayList<>();
        for (List<Integer> config : rowConfigs) {
            gapPatterns.add(getGapPositions(config));
        }

        System.out.println("Found " + rowConfigs.size() + " row configurations for width " + width);

        int n = rowConfigs.size();
        boolean[][] compatible = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                compatible[i][j] = areCompatible(gapPatterns.get(i), gapPatterns.get(j));
            }
        }

        long[] dp = new long[n];
        Arrays.fill(dp, 1);

        // Build up the wall row by row
        for (int row = 1; row < height; row++) {
            long[] newDp = new long[n];

            for (int currentConfig = 0; currentConfig < n; currentConfig++) {
                for (int prevConfig = 0; prevConfig < n; prevConfig++) {
                    if (compatible[prevConfig][currentConfig]) {
                        newDp[currentConfig] += dp[prevConfig];
                    }
                }
            }

            dp = newDp;
        }

        long totalWays = 0;
        for (long ways : dp) {
            totalWays += ways;
        }

        return totalWays;
    }

    /**
     * prints all row patterns and their gaps.
     */
    public void printRowConfigurations(int width) {
        List<List<Integer>> configs = generateRowConfigurations(width);
        System.out.println("Row configurations for width " + width + ":");

        for (int i = 0; i < configs.size(); i++) {
            List<Integer> config = configs.get(i);
            List<Integer> gaps = getGapPositions(config);
            System.out.println("  Config " + i + ": bricks=" + config + ", gaps=" + gaps);
        }
    }
}
