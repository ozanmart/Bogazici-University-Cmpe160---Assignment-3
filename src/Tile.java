import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Tile {
    int col;
    int row;
    int type; // 0 = plain grass, 1 = mud, 2 = obstacle
    Map<Tile, Double> adjacentTiles = new HashMap<>();

    Tile(int col, int row, int type) {
        this.col = col;
        this.row = row;
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Tile tile = (Tile) obj;
        return (col == tile.col) && (row == tile.row) && (type == tile.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(col, row, type);
    }

    @Override
    public String toString() {
        return String.format("(%d, %d) type: %d", col, row, type);
    }
}
