package ca.jeffhoughton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Solver {
	int[][] finalGrid = new int[9][9];

	public Solver(int[][] grid){
		// Check initial input to make sure it is good,
		// if not we have an error (e.g. multiple 3s in
		// the same row)
		boolean initiallyGood = true;
		for (int i = 0; i < 9; i++){
			for (int j = 0; j < 9; j++){
				if (grid[i][j] != 0){
					if (!checkGrid(grid,i,j)) {
						error.add(new Pair(i,j));
						initiallyGood = false;
					}
				}
			}
		}

		// If it's good, solve it!
		if (initiallyGood){
			Solve(grid);
		}
	}

	// Solve recursively
	private boolean Solve(int[][] grid){
		// Initialize forward checker for this level of recursion 
		final ForwardChecker fCheck = new ForwardChecker(grid);
		// Check that the current board hasn't ruled out a cell
		// If it has, this recursion is no good, try a different
		// route.
		if (!fCheck.stillValid()) return false;
		
		// Initialize the spots open on the board
		final ArrayList<Pair> availableSpots = new ArrayList<Pair>();
		ArrayList<ArrayList<Pair>> numAvailSpots = new ArrayList<ArrayList<Pair>>();
		for (int n = 0; n < 9; n++){
			numAvailSpots.add(new ArrayList<Pair>());
		}
		// Sort based on spot with least available options,
		// this way we will rule out squares with only 1 options,
		// then squares with only 2 options, etc. So we try the fewest
		// possible moves.
		for (int i = 0; i < 9; i++){
			for (int j = 0; j < 9; j++){
				if (grid[i][j] == 0){
					numAvailSpots.get(fCheck.numOptions(i, j)-1).add(new Pair(i,j));
				}
			}
		}
		if (numAvailSpots.isEmpty()) return true;
		// Break ties with the largest number of unassigned cells in row, column, 3x3
		// In other words, if there are no cells with one option, but multiple
		// cells with two options, we will try the cell that rules out the most
		// other cells
		Comparator<Pair> comparator = new Comparator<Pair>() {
			public int compare(Pair c1, Pair c2) {
				return fCheck.numUnassigned(c1.i,c1.j)
						- fCheck.numUnassigned(c2.i,c2.j);
			}
		};
		// Finalize the now sorted spots
		for (int n = 0; n < 9; n++){
			Collections.sort(numAvailSpots.get(n),comparator);
		}
		for (int n = 0; n < 9; n++){
			availableSpots.addAll(numAvailSpots.get(n));
		}

		if (availableSpots.isEmpty()) return true;
		
		// Initialize values available for a spot, a bit of redundancy
		// due to forward checker, we are checking something in two ways,
		// but it is left in in case you were not using forward checking
		ArrayList<Integer> availableNums = new ArrayList<Integer>();
		for (int i = 1; i < 10; i++) availableNums.add(i);
		Collections.shuffle(availableNums);
		// Sort by most commonly occurring number in rows/columns/3x3
		Comparator<Integer> comparator2 = new Comparator<Integer>() {
			public int compare(Integer c1, Integer c2) {
				return fCheck.commonRC3(c1,availableSpots.get(0).i,
						availableSpots.get(0).j) -
					fCheck.commonRC3(c1,availableSpots.get(0).i,
						availableSpots.get(0).j);
			}
		};
		Collections.sort(availableNums, comparator2);

		// Loop to find next solution that works before recursing
		while (!availableSpots.isEmpty()){
			while (!availableNums.isEmpty()){
				// If spot available in forward checker, go on
				if (fCheck.check(availableSpots.get(0).i,
						availableSpots.get(0).j, availableNums.get(0))){
					// Set up grid with new cell filled in
					int[][] tempGrid = new int[9][9];
					for (int i = 0; i < 9; i++){
						for (int j = 0; j < 9; j++){
							tempGrid[i][j] = grid[i][j];
						}
					}
					tempGrid[availableSpots.get(0).i]
							[availableSpots.get(0).j] = availableNums.get(0);
					// If finished, return
					if (availableSpots.size() == 1) {
						setGrid(tempGrid);
						return true;
					}
					// Else recurse
					else {
						if (Solve(tempGrid)) return true;
					}
				}
				availableNums.remove(0); 
			}
			availableSpots.remove(0);
		}
		return false;
	}

	// Check to see if a spot is valid
	private boolean checkGrid(int[][] grid, int i, int j) {
		int num = grid[i][j];
		// Check left and right
		for (int t = 0; t < 9; t++){
			if (t != i) {
				if (grid[t][j] == num) {
					return false;
				}
			}
		}
		// Check up and down
		for (int t = 0; t < 9; t++){
			if (t != j) {
				if (grid[i][t] == num){
					return false;
				}
			}
		}
		// Check 3x3
		int ilow, ihigh, jlow, jhigh;
		if (i < 3) {ilow = 0; ihigh = 3;}
		else if (i < 6) {ilow = 3; ihigh = 6;}
		else {ilow = 6; ihigh = 9;}
		if (j < 3) {jlow = 0; jhigh = 3;}
		else if (j < 6) {jlow = 3; jhigh = 6;}
		else {jlow = 6; jhigh = 9;}
		for (int t = ilow; t < ihigh; t++){
			for (int u = jlow; u < jhigh; u++){
				if (t != i && u != j) {
					if (grid[t][u] == num){
						return false;
					}
				}
			}
		}
		return true;
	}

	// Set grid for output
	private void setGrid(int[][] grid){
		for (int i = 0; i < 9; i++){
			for (int j = 0; j < 9; j++){
				finalGrid[i][j] = grid[i][j];
			}
		}
	}
	public int[][] getGrid(){
		return finalGrid;
	}

	ArrayList<Pair> error = new ArrayList<Pair>();
	public ArrayList<Pair> getError(){
		return error;
	}

}
