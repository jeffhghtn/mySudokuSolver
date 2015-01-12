package ca.jeffhoughton;

// The forward checker stores every possible number that can fit
// into a field on the board. For example, if a "2" is in the top
// left most area, the forward checker will mark that 2 is not
// available in the top row, the left column, and the top square.
// This will allow the program to rule out many moves very quickly.
// It will also let us check if a cell still has valid moves, so we
// can more quickly abort a move we are trying.

public class ForwardChecker {
	// Marks if a spot on the board is taken
	public boolean[][] taken = new boolean[9][9];
	// Marks if a number for a specific spot on the board is available
	public boolean[][][] isAvail = new boolean[9][9][9]; //i,j,num

	public ForwardChecker(int[][] grid){
		// Initialize all spots as available and not taken
		for (int i = 0; i < 9; i++){
			for (int j = 0; j < 9; j++){
				for (int n = 0; n < 9; n++){
					isAvail[i][j][n] = true;
				}
				taken[i][j] = false;
			}
		}
		// Now mark all spots which are taken
		for (int i = 0; i < 9; i++){
			for (int j = 0; j < 9; j++){
				if (grid[i][j] != 0){
					taken[i][j] = true;
					// If a spot is taken, mark other spots as not available
					mark(i,j,grid[i][j]);
				}
			}
		}
	}

	// Make sure the board hasn't ruled out an open cell
	// AKA, if we try a move, and it means a certain cell
	// has no valid moves, then the previous move we tried
	// was no good! We are looking ahead to make sure we don't
	// waste time.
	public boolean stillValid(){
		for (int i = 0; i < 9; i++){
			for (int j = 0; j < 9; j++){
				if (!taken[i][j]){
					boolean valid = false;
					for (int n = 0; n < 9; n++){
						if (isAvail[i][j][n]) valid = true;
					}
					if (!valid) return false;
				}
			}
		}
		return true;
	}

	// Mark other spots unavailable based on a taken spot that is passed in
	private void mark(int i, int j, int num) {
		num = num - 1;
		for (int k = 0; k < 9; k++){
			// mark the current row as having given number not available
			isAvail[i][k][num] = false;
			// mark the current column as having given number not available
			isAvail[k][j][num] = false;
		}
		// Mark the current box as having given number not available
		int ilow, ihigh, jlow, jhigh;
		if (i < 3) {ilow = 0; ihigh = 3;}
		else if (i < 6) {ilow = 3; ihigh = 6;}
		else {ilow = 6; ihigh = 9;}
		if (j < 3) {jlow = 0; jhigh = 3;}
		else if (j < 6) {jlow = 3; jhigh = 6;}
		else {jlow = 6; jhigh = 9;}
		for (int k = ilow; k < ihigh; k++){
			for (int l = jlow; l < jhigh; l++){
				isAvail[k][l][num] = false;
			}
		}
	}

	// Check if a spot is available
	public boolean check(int i, int j, int num) {
		return isAvail[i][j][num-1];
	}
	
	// Check how many options are available for a spot
	// This will help us make better choices when deciding
	// which number to try next.
	public int numOptions(int i, int j){
		int numAvail = 0;
		for (int n = 0; n < 9; n++){
			if (isAvail[i][j][n]) numAvail++;
		}
		return numAvail;
	}

	// Check how many unassigned cells in according row, column, and 3x3 are empty
	// This is to decide how many numbers we can rule out of other cells by making 
	// a single move.
	public int numUnassigned(int i, int j) {
		int numUnassigned = 0;
		for (int k = 0; k < 9; k++){
			if (!taken[i][k]) numUnassigned++;
			if (!taken[k][j]) numUnassigned++;
		}
		int ilow, ihigh, jlow, jhigh;
		if (i < 3) {ilow = 0; ihigh = 3;}
		else if (i < 6) {ilow = 3; ihigh = 6;}
		else {ilow = 6; ihigh = 9;}
		if (j < 3) {jlow = 0; jhigh = 3;}
		else if (j < 6) {jlow = 3; jhigh = 6;}
		else {jlow = 6; jhigh = 9;}
		for (int k = ilow; k < ihigh; k++){
			for (int l = jlow; l < jhigh; l++){
				if (!taken[k][l]) numUnassigned++;
			}
		}
		return numUnassigned;
	}

	// Find how common an integer c1 is in adjacent rows, columns, and 3x3s
	// This is to decide  which number will rule out the most other numbers
	public int commonRC3(Integer c1, int i, int j) {
		int numOpen = 0;
		for (int n = 0; n < 9; n++){
			if (isAvail[i][n][c1-1]) numOpen++;
			if (isAvail[n][j][c1-1]) numOpen++;
		}
		int ilow, ihigh, jlow, jhigh;
		if (i < 3) {ilow = 0; ihigh = 3;}
		else if (i < 6) {ilow = 3; ihigh = 6;}
		else {ilow = 6; ihigh = 9;}
		if (j < 3) {jlow = 0; jhigh = 3;}
		else if (j < 6) {jlow = 3; jhigh = 6;}
		else {jlow = 6; jhigh = 9;}
		for (int k = ilow; k < ihigh; k++){
			for (int l = jlow; l < jhigh; l++){
				if (isAvail[k][l][c1-1]) numOpen++;
			}
		}
		return numOpen;
	}


}
