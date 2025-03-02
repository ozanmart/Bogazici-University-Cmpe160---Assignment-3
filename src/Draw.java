import java.util.*;

public class Draw {
    Tile[][] tiles;
    int column;
    int row;
    int cellSize;

    final String CHARACTER = "assets/berkgokberk.png";
    final String OBJECTIVE = "assets/computer.png";
    final String GRASS = "assets/grass1.jpeg";
    final String OBSTACLE = "assets/tree_ground.jpeg";
    final String MUD = "assets/sand.png";

    Draw (Tile[][] tiles, int column, int row, int cellSize) {
        this.tiles = tiles;
        this.column = column;
        this.row = row;
        this.cellSize = cellSize;
        StdDraw.enableDoubleBuffering();
        StdDraw.setCanvasSize(column * cellSize, row * cellSize);
        StdDraw.setXscale(0, column * cellSize);
        StdDraw.setYscale(0, row * cellSize);
    }

    public void drawMap(Pair<Integer, Integer> sourceCoordinate,
                        ArrayList<Pair<Integer, Integer>> objectives,
                        ArrayList<Pair<Integer, Integer>> impassableObjectives) {
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

        drawTile(tiles[sourceCoordinate.getValue()][sourceCoordinate.getKey()], CHARACTER);
        for (Pair<Integer, Integer> objective: objectives) {
            drawTile(tiles[objective.getValue()][objective.getKey()], OBJECTIVE);
        }
        for (Pair<Integer, Integer> impassableObjective: impassableObjectives) {
            drawTile(tiles[impassableObjective.getValue()][impassableObjective.getKey()], OBJECTIVE);
        }
        StdDraw.show();
    }

    private void drawTile(Tile tile, String picture) {
        StdDraw.picture((tile.col + 0.5) * cellSize, (row - tile.row - 0.5) * cellSize,
                picture, cellSize, cellSize);
    }
}
