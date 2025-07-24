# Gold Trail: The Knight’s Path

This project is developed as Assignment 3 for CMPE160: Introduction to Object-Oriented Programming at Boğaziçi University.

It is a grid-based pathfinding visualizer where a knight collects gold coins placed across different terrains, finding the shortest path between them using cost-aware search algorithms. The visualization is handled with the [StdDraw](https://introcs.cs.princeton.edu/java/stdlib/javadoc/StdDraw.html) library.

## Project Objective

Help the knight collect all gold coins placed on a tile-based map, traversing through grass, sand, and avoiding impassable terrain. The knight moves in four directions (up, down, left, right) and each tile has an associated travel cost.

### Input Files

Your program takes 3 input files: <br>
**1.	mapData.txt** <br>
• Defines map size and the terrain type for each tile. <br>
**2.	travelCosts.txt** <br>
• Contains the exact travel costs between adjacent tiles.<br>
**3.	objectives.txt** <br>
• Defines the knight’s starting point and list of gold coin positions to reach in order.

### Output

Your program generates: <br>
	•	out/output.txt:
Logs step-by-step movement to each objective, total cost, and skipped unreachable goals. <br>
	•	out/bonus.txt (for bonus):
The shortest route that visits all objectives exactly once and returns to the starting point.

### Knight Movement Rules
	•	Allowed directions: Up, Down, Left, Right
	•	Travel Costs:
	•	Grass (type 0): 1–5 units
	•	Sand (type 1): 8–10 units
	•	Obstacle (type 2): impassable

### Bonus Part (TSP Solver)

As an optional challenge, the ShortestRoute class solves a Traveling Salesman Problem (TSP) to compute the minimum-cost path that:
	•	Starts from the initial tile
	•	Visits every objective
	•	Returns to the starting tile

The solution must complete within 3 seconds for ≥15 objectives.
