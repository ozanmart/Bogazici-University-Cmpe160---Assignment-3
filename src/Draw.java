import java.awt.*;
import java.util.*;
import java.util.Random;

public class Draw {
    Tile[][] tiles;
    int column;
    int row;
    int cellSize;

    final String CHARACTER = "assets/knight.png";
    final String OBJECTIVE = "assets/coin.png";
    final String GRASS = "assets/grass1.jpeg";
    final String OBSTACLE = "assets/tree_ground.jpeg";
    final String MUD = "assets/sand.png";
    ArrayList<Tile> path;
    ArrayList<Color> colors;
    Random rand;
    Color color;

    Draw (Tile[][] tiles, int column, int row, int cellSize) {
        this.tiles = tiles;
        this.column = column;
        this.row = row;
        this.cellSize = cellSize;
        path = new ArrayList<>();
        colors = new ArrayList<>(Arrays.asList(Color.RED, Color.BLUE, Color.MAGENTA,
                Color.YELLOW, Color.DARK_GRAY, Color.ORANGE, Color.CYAN));
        rand = new Random();
        color = Color.RED;
        StdDraw.enableDoubleBuffering();
        StdDraw.setCanvasSize(column * cellSize, row * cellSize);
        StdDraw.setXscale(0, column * cellSize);
        StdDraw.setYscale(0, row * cellSize);
    }

    public void drawMap(Pair<Integer, Integer> sourceCoordinate,
                        ArrayList<Pair<Integer, Integer>> objectives,
                        ArrayList<Pair<Integer, Integer>> impassableObjectives,
                        ArrayList<Tile> shortestPath,
                        int type) {
        for (Tile[] tileRow : tiles) {
            for (Tile tile: tileRow) {
                if (tile.type == 1) {
                    drawTile(tile, MUD);
                } else if (tile.type == 2) {
                    drawTile(tile, OBSTACLE);
                } else {
                    drawTile(tile, GRASS);
                }
            }
        }

        if (type == 1 || type == 0) {
            Tile tile = tiles[sourceCoordinate.getValue()][sourceCoordinate.getKey()];
            path.add(tile);
        }

        for (Pair<Integer, Integer> objective: objectives) {
            drawTile(tiles[objective.getValue()][objective.getKey()], OBJECTIVE);
        }
        for (Pair<Integer, Integer> impassableObjective: impassableObjectives) {
            drawTile(tiles[impassableObjective.getValue()][impassableObjective.getKey()], OBJECTIVE);
        }
        int counter = 1;
        if (type == 1) {
            for (Tile tile : path) {
                if (objectives.contains(new Pair<>(tile.col, tile.row))) {
                    color = colors.get(counter % colors.size());
                    counter++;
                    continue;
                }
                drawTile(tile);
            }
        }
        if (type == 0) {
            if (shortestPath != null) {
                for (Tile tile : path) {
                    if (shortestPath.contains(tile)) {
                        drawTile(tile);
                    }
                }
            } else {
                path.clear();
            }
        }

        drawTile(tiles[sourceCoordinate.getValue()][sourceCoordinate.getKey()], CHARACTER);
        color = Color.RED;
        StdDraw.show();
    }

    private void drawTile(Tile tile, String picture) {
        StdDraw.picture((tile.col + 0.5) * cellSize, (row - tile.row - 0.5) * cellSize,
                picture, cellSize, cellSize);
    }

    private void drawTile(Tile tile) {
        StdDraw.setPenColor(color);
        StdDraw.filledCircle((tile.col + 0.5) * cellSize, (row - tile.row - 0.5) * cellSize, 5);
        StdDraw.setPenColor();
    }
}
