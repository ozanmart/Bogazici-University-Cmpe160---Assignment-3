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

    public void generateMap(String mapData, String mapDistanceData, String objectiveData, String outputFile) {
        readMapData(mapData);
        readMapDistanceData(mapDistanceData);
        readObjectiveData(objectiveData);
        draw = new Draw(tiles, column, row, cellSize);
        ArrayList<String> output = new ArrayList<>();
        String str;
        int counter = 1;
        while (!objectives.isEmpty()) {
            boolean firstLine = true;
            draw.drawMap(sourceCoordinate, objectives, impassableObjectives);
            Pair<Integer, Integer> destination = objectives.get(0);
            ArrayList<Tile> path = findShortestPath(tiles[sourceCoordinate.getValue()][sourceCoordinate.getKey()],
                    tiles[destination.getValue()][destination.getKey()]);
            if (path.isEmpty()) {
                str = String.format("Objective %d cannot be reached!", counter);
                output.add(str);
                counter ++;
                Pair<Integer, Integer> impassableObject = objectives.remove(0);
                impassableObjectives.add(impassableObject);
                continue;
            }
            for (Tile tile : path) {
                if (firstLine) {
                    str = String.format("Starting position: (%d, %d)", tile.col, tile.row);
                    if (!output.contains(str))
                        output.add(str);
                    firstLine = false;
                } else {
                    str = String.format("Move to (%d, %d)", tile.col, tile.row);
                    output.add(str);
                }
            }
            str = String.format("Objective %d reached!", counter);
            if (!output.contains(str))
                output.add(str);
            animation(destination, path);
            sourceCoordinate = destination;
            objectives.remove(0);
            counter ++;
        }
        draw.drawMap(sourceCoordinate, objectives, impassableObjectives);
        writeOutput(output, outputFile);
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
    private ArrayList<Tile> findShortestPath(Tile source, Tile destination) {
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

        ArrayList<Tile> path = new ArrayList<>();
        Tile step = destination;
        while (step != null) {
            path.add(step);
            step = previous.get(step);
        }
        Collections.reverse(path);

        if (distances.get(destination) == Double.POSITIVE_INFINITY) {
            return new ArrayList<>();  // No valid path found
        }

        if (path.isEmpty() || path.get(0) != source) {
            return new ArrayList<>();
        }

        return path;  // Returns the shortest path from source to destination
    }

    private void animation(Pair<Integer, Integer> destinationCoordinate,
                           ArrayList<Tile> path) {
        int counter = 0;
        Tile tile = path.get(counter);
        Pair<Integer, Integer> step = new Pair<>(tile.col, tile.row);
        while (!step.equals(destinationCoordinate)) {
            StdDraw.pause(PAUSE_TIME);
            StdDraw.clear();
            draw.drawMap(step, objectives, impassableObjectives);
            counter ++;
            step = new Pair<>(path.get(counter).col, path.get(counter).row);
        }
        StdDraw.pause(PAUSE_TIME);
        StdDraw.clear();
        draw.drawMap(step, objectives, impassableObjectives);
    }

    private void writeOutput(ArrayList<String> output, String outputFile) {
        try {
            Files.write(Path.of(outputFile), output, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }


}
