import random
import sys

def generate_map_data(x_size, y_size, perc_type0, perc_type1, perc_type2, output_file):
    if perc_type0 + perc_type1 + perc_type2 != 100:
        print("Error: The sum of percentages must be 100.")
        sys.exit(1)

    choices = [0] * int(perc_type0) + [1] * int(perc_type1) + [2] * int(perc_type2)

    with open(output_file, "w") as f:
        f.write(f"{x_size} {y_size}\n")
        for y in range(y_size):
            for x in range(x_size):
                tile_type = random.choice(choices)
                f.write(f"{x} {y} {tile_type}\n")

    print(f"Map data saved to {output_file}")

def main():
    if len(sys.argv) != 7:
        print("Usage: python3 generate_map.py <x_size> <y_size> <perc_type0> <perc_type1> <perc_type2> <output_file>")
        sys.exit(1)

    x_size = int(sys.argv[1])
    y_size = int(sys.argv[2])
    perc_type0 = int(sys.argv[3])
    perc_type1 = int(sys.argv[4])
    perc_type2 = int(sys.argv[5])
    output_file = sys.argv[6]

    generate_map_data(x_size, y_size, perc_type0, perc_type1, perc_type2, output_file)

if __name__ == "__main__":
    main()