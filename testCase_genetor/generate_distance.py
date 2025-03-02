import random
import sys

def read_map_data(filename):
    firstLine = True
    map_grid = {}
    with open(filename, "r") as f:
        for line in f:
            if firstLine:
                firstLine = False
                continue
            x, y, tile_type = map(int, line.strip().split())
            map_grid[(x, y)] = tile_type
    return map_grid

def get_traversal_cost(type1, type2):
    if type1 == 2 or type2 == 2:
        return None  # Impassable, no edge
    elif type1 == 0 and type2 == 0:
        return round(random.uniform(1.0, 5.0), 2)
    elif type1 == 1 and type2 == 1:
        return round(random.uniform(8.0, 10.0), 2)
    elif (type1 == 0 and type2 == 1) or (type1 == 1 and type2 == 0):
        return round(random.uniform(8.0, 10.0), 2)
    return None

def generate_distance_file(map_grid, output_filename):
    edges = {}

    with open(output_filename, "w") as f:
        for (x, y), tile_type in map_grid.items():
            neighbors = [(x+1, y), (x-1, y), (x, y+1), (x, y-1)]
            
            for nx, ny in neighbors:
                if (nx, ny) in map_grid:
                    key = tuple(sorted([(x, y), (nx, ny)]))
                    if key not in edges:
                        cost = get_traversal_cost(tile_type, map_grid[(nx, ny)])
                        if cost is not None:
                            edges[key] = cost
                            f.write(f"{x} {y} {nx} {ny} {cost}\n")
def main():
    if len(sys.argv) != 3:
        print("Usage: python3 generate_distance.py <input_file> <output_file>")
        sys.exit(1)

    
    input_file = sys.argv[1]
    output_file = sys.argv[2]

    map_data = read_map_data(input_file)
    generate_distance_file(map_data, output_file)

    print(f"Distance file '{output_file}' generated successfully.")

if __name__ == "__main__":
    main()