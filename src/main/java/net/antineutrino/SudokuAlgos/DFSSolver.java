package net.antineutrino.SudokuAlgos;

import java.util.*;

public class DFSSolver extends Solver {
	private byte[][] current;
	private byte[][][] possibles = new byte[9][9][1];
	private List<int[]> unsolved = new ArrayList<int[]>();

	/**
	 * Solve a Sudoku puzzle using a simple Depth-First Search approach.
	 */
	public byte[][] solve(byte[][] start) throws NoSolutionExistsException {
		current = start.clone();

		check_for_duplicates();
		build_possibles();
		build_unsolved();
		dsf_solve();

		return current;
	}

	/**
	 * If at the start the puzzle the same value in a row or column, immediately
	 * fail that puzzle: it has no valid solution.
	 * 
	 * NOTE: This could also check within blocks, but this is meant to be fast.
	 * 
	 * @throws NoSolutionExistsException
	 */
	private void check_for_duplicates() throws NoSolutionExistsException {
		for (int r = 0; r < 9; r++) {
			for (int c = 0; c < 9; c++) {
				if (current[r][c] != 0) {
					if (!Rules.isPossible(current, r, c, current[r][c])) {
						throw new NoSolutionExistsException(
								"The puzzle is not solvable.");
					}
				}
			}
		}
	}

	/**
	 * Solve any puzzle that can be solved, using a DFS approach.
	 * 
	 * @throws NoSolutionExistsException
	 */
	private void dsf_solve() throws NoSolutionExistsException {
		int i = 0;

		// loop through the unsolved cells list
		while (i > -1 && i < unsolved.size()) {
			// System.out.println(i);
			int[] u = unsolved.get(i);
			byte[] poss = possibles[u[0]][u[1]];

			// if current cell is empty, easy.
			if (current[u[0]][u[1]] == 0) {
				boolean found1 = false;
				for (int k = 0; k < poss.length; k++) {
					if (Rules.isPossible(current, u[0], u[1], poss[k])) {
						current[u[0]][u[1]] = poss[k];
						i += 1;
						found1 = true;
						break;
					}
				}

				if (!found1) {
					current[u[0]][u[1]] = 0;
					i -= 1;
				}
			} else {
				// if current cell is not empty, figure out where its value lies
				// in its possibles list
				int j = ArrayUtils.findIndex(poss, current[u[0]][u[1]]);

				// if we've run past the end of the possibles list, take a step
				// back in the unsolved list
				if (j == poss.length - 1) {
					current[u[0]][u[1]] = 0;
					i -= 1;
				} else {
					// Go to the next valid cell in the possibles list
					boolean found2 = false;
					for (int k = j + 1; k < poss.length; k++) {
						if (Rules.isPossible(current, u[0], u[1], poss[k])) {
							current[u[0]][u[1]] = poss[k];
							i += 1;
							found2 = true;
							break;
						}
					}

					if (!found2) {
						current[u[0]][u[1]] = 0;
						i -= 1;
					}
				}
			}
		}

		if (i <= 0) {
			throw new NoSolutionExistsException("The puzzle is not solvable.");
		}
	}

	/**
	 * Create a 3D array with a list of all the possible values at each cell in
	 * the puzzle.
	 * 
	 * @throws NoSolutionExistsException
	 */
	private void build_possibles() throws NoSolutionExistsException {
		for (int r = 0; r < 9; r++) {
			for (int c = 0; c < 9; c++) {
				if (current[r][c] != 0) {
					possibles[r][c][0] = current[r][c];
				} else {
					// Determine which values are still possible for this cell
					List<Byte> temp = new ArrayList<Byte>();
					for (byte i = 1; i < 10; i++) {
						if (Rules.isPossible(current, r, c, i)) {
							temp.add(i);
						}
					}

					// if nothing is possible, the puzzle has no solution
					if (temp.size() == 0) {
						throw new NoSolutionExistsException(
								"The puzzle is not solvable.");
					} else if (temp.size() == 1) {
						// If there is only one possibility, fix it now.
						current[r][c] = temp.get(0);
					}

					// Add those values to the possibles array.
					byte[] poss = new byte[temp.size()];
					for (int i = 0; i < temp.size(); i++) {
						poss[i] = temp.get(i).byteValue();
					}
					possibles[r][c] = poss;
				}
			}
		}
	}

	/**
	 * Find all the unsolved cells in the puzzle.
	 * 
	 * @throws NoSolutionExistsException
	 */
	private void build_unsolved() throws NoSolutionExistsException {
		for (int r = 0; r < 9; r++) {
			for (int c = 0; c < 9; c++) {
				if (current[r][c] == 0) {
					unsolved.add(new int[] { r, c });
				}
			}
		}
	}
}