import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class Environment {
    private Tile[][] tiles;
    private int row;
    private int column;
    private final int cellSize = 40;
    private final int PAUSE_TIME = 150;
    private Pair<Integer, Integer> sourceCoordinate;
    private ArrayList<Pair<Integer, Integer>> objectives;
    private ArrayList<Pair<Integer, Integer>> impassableObjectives;
    private Draw draw;


    public Environment() {
        objectives = new ArrayList<>();
        impassableObjectives = new ArrayList<>();
    }

    public void generateMap(String mapData, String mapDistanceData,
                            String objectiveData, String outputFile, int type) {
        readMapData(mapData);
        readMapDistanceData(mapDistanceData);
        readObjectiveData(objectiveData);
        if (type == 0) {
            regularExecution(outputFile);
        } else {
            bonusExecution(outputFile);
        }
    }

    private void readMapData(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;

            if ((line = br.readLine()) != null) {
                String[] sizeParts = line.split(" ");
                if (sizeParts.length == 2) {
                    column = Integer.parseInt(sizeParts[0]); // Number of rows
                    row = Integer.parseInt(sizeParts[1]); // Number of columns
                    tiles = new Tile[row][column]; // Initialize the tiles array
                }
            }

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" "); // Split by space
                if (parts.length == 3) {
                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);
                    int type = Integer.parseInt(parts[2]);
                    tiles[y][x] = new Tile(x, y, type);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }

    private void readMapDistanceData(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 5) {
                    int x1 = Integer.parseInt(parts[0]);
                    int y1 = Integer.parseInt(parts[1]);
                    int x2 = Integer.parseInt(parts[2]);
                    int y2 = Integer.parseInt(parts[3]);
                    double distance = Double.parseDouble(parts[4]);

                    Tile tile1 = tiles[y1][x1];
                    Tile tile2 = tiles[y2][x2];

                    tile1.adjacentTiles.put(tile2, distance);
                    tile2.adjacentTiles.put(tile1, distance);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }

    private void readObjectiveData(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 2) {
                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);
                    if (isFirstLine) {
                        sourceCoordinate = new Pair<>(x, y);
                        isFirstLine = false;
                    } else {
                        objectives.add(new Pair<>(x, y));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }

    }
    private Pair<ArrayList<Pair<Tile, Double>>, Double> findShortestPath(Tile source, Tile destination) {
        PriorityQueue<Pair<Tile, Double>> pq = new PriorityQueue<>(Comparator.comparingDouble(Pair::getValue));
        Map<Tile, Double> distances = new HashMap<>();
        Map<Tile, Tile> previous = new HashMap<>();
        Set<Tile> visited = new HashSet<>();

        for (Tile[] row : tiles) {
            for (Tile tile : row) {
                if (tile != null) {
                    distances.put(tile, Double.POSITIVE_INFINITY);
                    previous.put(tile, null);
                }
            }
        }

        distances.put(source, 0.0);
        pq.add(new Pair<>(source, 0.0));

        // Dijkstra's Algorithm
        while (!pq.isEmpty()) {
            Pair<Tile, Double> currentPair = pq.poll();
            Tile currentTile = currentPair.getKey();

            if (visited.contains(currentTile)) continue;
            visited.add(currentTile);

            if (currentTile == destination) break;

            for (Map.Entry<Tile, Double> entry : currentTile.adjacentTiles.entrySet()) {
                Tile neighbor = entry.getKey();
                double cost = entry.getValue();
                if (visited.contains(neighbor)) continue;

                double newDist = distances.get(currentTile) + cost;
                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    previous.put(neighbor, currentTile);
                    pq.add(new Pair<>(neighbor, newDist));
                }
            }
        }

        if (distances.get(destination) == Double.POSITIVE_INFINITY) {
            return new Pair<>(new ArrayList<>(), Double.POSITIVE_INFINITY);  // No valid path found
        }

        ArrayList<Tile> path = new ArrayList<>();
        Tile step = destination;
        while (step != null) {
            path.add(step);
            step = previous.get(step);
        }
        Collections.reverse(path);

        ArrayList<Pair<Tile, Double>> pathWithCosts = new ArrayList<>();
        for (Tile t : path) {
            pathWithCosts.add(new Pair<>(t, distances.get(t)));
        }


        if (path.isEmpty() || path.get(0) != source) {
            return new Pair<>(new ArrayList<>(), Double.POSITIVE_INFINITY);
        }

        return new Pair<>(pathWithCosts, distances.get(destination));  // Returns the shortest path and distance from source to destination
    }

    private void animation(ArrayList<Tile> path, int type) {
        int counter = 0;
        Pair<Integer, Integer> destinationCoordinate = new Pair<>(path.get(path.size() - 1).col, path.get(path.size() - 1).row);
        Tile tile = path.get(counter);
        Pair<Integer, Integer> step = new Pair<>(tile.col, tile.row);
        while (!step.equals(destinationCoordinate)) {
            StdDraw.pause(PAUSE_TIME);
            StdDraw.clear();
            draw.drawMap(step, objectives, impassableObjectives, path, type);
            counter ++;
            step = new Pair<>(path.get(counter).col, path.get(counter).row);
        }
        StdDraw.pause(PAUSE_TIME);
        StdDraw.clear();
        draw.drawMap(step, objectives, impassableObjectives, null, type);
    }

    private void writeOutput(ArrayList<String> output, String outputFile) {
        try {
            Files.write(Path.of(outputFile), output, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    private void regularExecution(String outputFile) {
        draw = new Draw(tiles, column, row, cellSize);
        ArrayList<String> output = new ArrayList<>();
        String str;
        int counter = 1;
        int totalStep = 0;
        double totalCost = 0.0;
        while (!objectives.isEmpty()) {
            boolean firstLine = true;
            draw.drawMap(sourceCoordinate, objectives, impassableObjectives, null, 0);
            Pair<Integer, Integer> destination = objectives.get(0);
            Pair<ArrayList<Pair<Tile, Double>>, Double> input = findShortestPath(tiles[sourceCoordinate.getValue()][sourceCoordinate.getKey()],
                    tiles[destination.getValue()][destination.getKey()]);
            ArrayList<Pair<Tile, Double>> inputPath = input.getKey();
            ArrayList<Tile> path = new ArrayList<>();
            for (Pair<Tile, Double> tilePair : inputPath) {
                path.add(tilePair.getKey());
            }
            if (path.isEmpty()) {
                str = String.format("Objective %d cannot be reached!", counter);
                output.add(str);
                counter++;
                Pair<Integer, Integer> impassableObject = objectives.remove(0);
                impassableObjectives.add(impassableObject);
                continue;
            }
            int stepCount = 1;
            totalCost += input.getValue();
            for (Pair<Tile, Double> tile : inputPath) {
                if (firstLine) {
                    str = String.format("Starting position: (%d, %d)", tile.getKey().col, tile.getKey().row);
                    if (!output.contains(str))
                        output.add(str);
                    firstLine = false;
                } else {
                    str = String.format("Step Count: %d, move to (%d, %d). Total Cost: %.2f.", stepCount, tile.getKey().col, tile.getKey().row, tile.getValue());
                    output.add(str);
                    stepCount++;
                    totalStep++;
                }
            }
            str = String.format("Objective %d reached!", counter);
            if (!output.contains(str))
                output.add(str);
            animation(path, 0);
            sourceCoordinate = destination;
            objectives.remove(0);
            counter ++;
        }
        draw.drawMap(sourceCoordinate, objectives, impassableObjectives, null, 0);
        output.add(String.format("Total Step: %d, Total Cost: %.2f", totalStep, totalCost));
        writeOutput(output, outputFile);
    }

    private void bonusExecution(String outputFile) {
        draw = new Draw(tiles, column, row, cellSize);
        draw.drawMap(sourceCoordinate, objectives, impassableObjectives, null, 1);
        int size = objectives.size() + 1;
        double[][] distanceMatrix = new double[size][size];
        HashMap<Pair<Integer, Integer>, ArrayList<Pair<Tile, Double>>> paths = new HashMap<>();
        ArrayList<String> output = new ArrayList<>();

        ArrayList<Tile> allPoints = new ArrayList<>();
        allPoints.add(tiles[sourceCoordinate.getValue()][sourceCoordinate.getKey()]);
        for (Pair<Integer, Integer> objective : objectives) {
            allPoints.add(tiles[objective.getValue()][objective.getKey()]);
        }

        // Compute the shortest paths between all pairs
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == j) {
                    distanceMatrix[i][j] = 0;
                    paths.put(new Pair<>(i,j), new ArrayList<>());
                } else {
                    Pair<ArrayList<Pair<Tile, Double>>, Double> result = findShortestPath(allPoints.get(i), allPoints.get(j));
                    paths.put(new Pair<>(i, j), result.getKey());
                    distanceMatrix[i][j] = result.getValue();
                }
            }
        }

        HeldKarpTSP heldKarpTSP = new HeldKarpTSP(distanceMatrix);
        Pair<Double, String> result = heldKarpTSP.solve();
        String shortestPath = result.getValue();
        Double shortestPathLength = result.getKey();
        String[] pathParts = shortestPath.split(",");
        ArrayList<Integer> path = new ArrayList<>();

        for (String part : pathParts) {
            path.add(Integer.parseInt(part));
        }

        int stepCount = 1;
        double cumulativeCost;
        double lastCost = 0.0;

        for (int i = 0; i < path.size() - 1; i++) {
            draw.drawMap(sourceCoordinate, objectives, impassableObjectives, null, 1);
            ArrayList<Pair<Tile, Double>> currentPathPair = paths.get(new Pair<>(path.get(i),path.get(i+1)));
            ArrayList<Tile> currentPath = new ArrayList<>();

            for (int j = 1; j < currentPathPair.size(); j++) {
                Tile tile = currentPathPair.get(j).getKey();
                cumulativeCost = currentPathPair.get(j).getValue() + lastCost; // cumulative cost to reach this tile
                // Step Count: 7, move to (5, 17). Total Cost: 22.32.
                String moveStr = String.format("Step count: %d, move to (%d, %d). Total cost: %.2f. ",
                        stepCount, tile.col, tile.row, cumulativeCost);
                output.add(moveStr);
                stepCount++;

                currentPath.add(tile);
            }
            lastCost += currentPathPair.get(currentPathPair.size() - 1).getValue();
            if (i != path.size() - 2)
                output.add(String.format("Objective %d reached!", path.get(i+1)));

            animation(currentPath, 1);
            sourceCoordinate = new Pair<>(currentPath.get(currentPath.size() - 1).col, currentPath.get(currentPath.size() - 1).row);
        }
        // Total Step: 96, Total Cost: 389.53
        output.add(String.format("Total Step: %d, Total Cost: %.2f", stepCount - 1, lastCost));

        // System.out.println("Shortest Path Length: " + shortestPathLength);
        // System.out.println("Shortest Path: " + path);

        writeOutput(output, outputFile);
    }
}
