package sudoku;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import ea.Parameters;

public class SudokuChecker {

	/**
	 * problem from jcf practical
	 */
//	public static int[][] board = new int[][] { { 9, 8, 0, 0, 1, 0, 5, 0, 0 }, { 6, 7, 1, 0, 5, 4, 0, 9, 0 },
//			{ 2, 0, 0, 8, 3, 0, 7, 0, 1 }, { 0, 0, 0, 7, 0, 0, 0, 0, 0 }, { 7, 0, 0, 0, 6, 0, 0, 0, 3 },
//			{ 0, 0, 0, 0, 0, 2, 0, 0, 0 }, { 8, 3, 4, 5, 2, 1, 6, 7, 9 }, { 5, 6, 7, 3, 9, 8, 1, 2, 4 },
//			{ 1, 9, 2, 4, 7, 6, 8, 3, 5 } };

	public static int[][] board = new int[9][9];
	public static LinkedList<int[][]> history = new LinkedList<>();
	public static void main(String[] args) {
//		System.out.println(isValid(board));
//
//		for (int row = 0; row < 9; row++) {
//			for (int col = 0; col < 9; col++) {
//				HashSet<Integer> possibleValues = getPossibleValues(row, col, board);
//				System.out.print("Possible for " + row + ", " + col + "\t");
//				for (int i : possibleValues) {
//					System.out.print(i + ", ");
//				}
//				System.out.println();
//			}
//		}		
		
		int target = 20;

		for (int i = 100; i < 1000; i++) {
			board = generateValidBoard();
			int bestUnfilledCells = 0;
			int attemptsWithNoImprov = 0;			
			while(getUnfilledCells(board) < 81 - target && attemptsWithNoImprov < 5000) {	
				if(getUnfilledCells(board) > bestUnfilledCells) {
					bestUnfilledCells = getUnfilledCells(board);
					//System.out.println(i + "\t" + bestUnfilledCells);
				}
				//System.out.println(i + "\t" + bestUnfilledCells + "\t" + attemptsWithNoImprov);
				int r = 0;
				int c = 0;
				int val = 0;
				while(val == 0) {
					r = Parameters.rnd.nextInt(9);
					c = Parameters.rnd.nextInt(9);
					val = board[r][c];
				}				
				board[r][c] = 0;
				int cellsWithoutGuesses = attemptToSolve();
				if(cellsWithoutGuesses != 0) {
					board[r][c] = val;
					attemptsWithNoImprov++;
//					board = fillSingles(board);
				}else {
					attemptsWithNoImprov = 0;
				}
				
			}
			int cellsWithoutGuesses = attemptToSolve();
			System.out.println(i + "\t" + (81 - getUnfilledCells(board)) + "\tTarget " + target);
			if(cellsWithoutGuesses == 0 && getUnfilledCells(board) <= 21) {
				DecimalFormat format = new DecimalFormat("0000");
				save("" + format.format(i) + "_" + (getUnfilledCells(board)) + ".sud");
				System.out.println("Found");
			}			
		}
		Gui.main(null);
		
	}

	public static boolean isValid(int[][] board) {
		// Rows
		for (int row = 0; row < 9; row++) {
			HashSet<Integer> uniqueRowValues = new HashSet<>();
			for (int col = 0; col < 9; col++) {
				if (board[row][col] != 0) {
					if (!uniqueRowValues.add(board[row][col])) {
						return false;
					}
				}
			}
		}
		// Columns
		for (int col = 0; col < 9; col++) {
			HashSet<Integer> uniqueColValues = new HashSet<>();
			for (int row = 0; row < 9; row++) {
				if (board[row][col] != 0) {
					if (!uniqueColValues.add(board[row][col])) {
						return false;
					}
				}
			}
		}
		// Blocks
		for (int block = 0; block < 9; block++) {
			HashSet<Integer> uniqueBlockValues = new HashSet<>();
//			System.out.println("Block " + block);
			for (int row = (block / 3) * 3; row < (1 + (block / 3)) * 3; row++) {
				for (int col = (block % 3) * 3; col < (1 + block % 3) * 3; col++) {
//					System.out.println(row + "\t" + col);
					if (board[row][col] != 0) {
						if (!uniqueBlockValues.add(board[row][col])) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	public static HashSet<Integer> getPossibleValues(int aRow, int aCol, int[][] board) {
		HashSet<Integer> possibleValues = new HashSet<>();
		if (board[aRow][aCol] != 0) {
			return possibleValues;
		}
		for (int i = 1; i <= 9; i++) {
			possibleValues.add(i);
		}
		// row
		for (int col = 0; col < 9; col++) {
			if (board[aRow][col] != 0) {
				possibleValues.remove(board[aRow][col]);
			}
		}
		// col
		for (int row = 0; row < 9; row++) {
			if (board[row][aCol] != 0) {
				possibleValues.remove(board[row][aCol]);
			}
		}
		// block
		for (int row = (aRow / 3) * 3; row < ((aRow / 3) + 1) * 3; row++) {
			for (int col = (aCol / 3) * 3; col < (1 + aCol / 3) * 3; col++) {
				if (board[row][col] != 0) {
					possibleValues.remove(board[row][col]);
				}
			}
		}
		return possibleValues;
	}

	/**
	 * conflicts = num in rows + cols + blocks
	 * 
	 * @param board
	 * @return
	 */
	public static int getFitness(int[][] board) {
		int fitness = 0;
		// Rows
		for (int row = 0; row < 9; row++) {
			HashSet<Integer> uniqueRowValues = new HashSet<>();
			for (int col = 0; col < 9; col++) {
				if (board[row][col] != 0) {
					if (!uniqueRowValues.add(board[row][col])) {
						fitness++;
					}
				}
			}
		}
		// Columns
		for (int col = 0; col < 9; col++) {
			HashSet<Integer> uniqueColValues = new HashSet<>();
			for (int row = 0; row < 9; row++) {
				if (board[row][col] != 0) {
					if (!uniqueColValues.add(board[row][col])) {
						fitness++;
					}
				}
			}
		}
		// Blocks
		for (int block = 0; block < 9; block++) {
			HashSet<Integer> uniqueBlockValues = new HashSet<>();
//					System.out.println("Block " + block);
			for (int row = (block / 3) * 3; row < (1 + (block / 3)) * 3; row++) {
				for (int col = (block % 3) * 3; col < (1 + block % 3) * 3; col++) {
//							System.out.println(row + "\t" + col);
					if (board[row][col] != 0) {
						if (!uniqueBlockValues.add(board[row][col])) {
							fitness++;
						}
					}
				}
			}
		}
		return fitness;
	}

	/**
	 * Conflicts on a cell
	 * 
	 * @param aRow
	 * @param aCol
	 * @param board
	 * @return
	 */
	public static int getConflicts(int aRow, int aCol, int[][] board) {
		int conflicts = 0;

		if (board[aRow][aCol] == 0) {
			return 0;
		}

		// row
		for (int col = 0; col < 9; col++) {
			if (board[aRow][col] != 0 && col != aCol && board[aRow][col] == board[aRow][aCol]) {
				conflicts++;
			}
		}
		// col
		for (int row = 0; row < 9; row++) {
			if (board[row][aCol] != 0 && row != aRow && board[row][aCol] == board[aRow][aCol]) {
				conflicts++;
			}
		}
		// block
		for (int row = (aRow / 3) * 3; row < ((aRow / 3) + 1) * 3; row++) {
			for (int col = (aCol / 3) * 3; col < (1 + aCol / 3) * 3; col++) {
				if (board[row][col] != 0 && row != aRow && col != aCol && board[row][col] == board[aRow][aCol]) {
					conflicts++;
				}
			}
		}
		return conflicts;
	}

	public static int[][] generateValidBoard() {
		int[][] board = new int[9][9];
		ArrayList<Integer> values = new ArrayList<>();
		for (int i = 1; i < 10; i++) {
			values.add(i);
		}
		for (int i = 0; i < 9; i++) {
			int value = values.remove(Parameters.rnd.nextInt(values.size()));
			for (int j = 0; j < 9; j++) {
				ArrayList<Point> validPoints = getValidPoints(board, value);
//				System.out.println(validPoints.size());
				if (validPoints.size() != 0) {
					Point p = validPoints.get(Parameters.rnd.nextInt(validPoints.size()));
					board[p.y][p.x] = value;
				} else {
					ArrayList<Point> validNonZeroPoints = getValidNonZeroPoints(value, board);
					if (validNonZeroPoints.size() > 0) {
						Point p = validNonZeroPoints.get(0);
						int oldValue = board[p.y][p.x];
						values.add(oldValue);
						board = removeValue(board, oldValue);
						i--;
						board[p.y][p.x] = value;
					} else {
						SudokuChecker.board = board;
						Gui.main(null);
						System.out.print("");
					}
				}
			}
		}
		return board;
	}

	private static int[][] removeValue(int[][] board, int value) {

		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				if (board[row][col] == value) {
					board[row][col] = 0;
				}
			}
		}
		return board;
	}

	private static ArrayList<Point> getValidNonZeroPoints(int value, int[][] board) {
		ArrayList<Point> validNonZeroPoints = new ArrayList<>();
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				if (board[row][col] != 0 && board[row][col] != value) {
					int oldValue = board[row][col];
					board[row][col] = value;
					if (getConflicts(row, col, board) == 0) {
						Point p = new Point(col, row);
						validNonZeroPoints.add(p);
					}
					board[row][col] = oldValue;
				}
			}
		}
		return validNonZeroPoints;
	}

	/**
	 * Point x = col Point y = row
	 * 
	 * @param board
	 * @param value
	 * @return
	 */
	private static ArrayList<Point> getValidPoints(int[][] board, int value) {
		ArrayList<Point> validPoints = new ArrayList<>();
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				if (board[row][col] == 0) {
					board[row][col] = value;
					if (getConflicts(row, col, board) == 0) {
						Point p = new Point(col, row);
						validPoints.add(p);
					}
					board[row][col] = 0;
				}
			}
		}
		return validPoints;
	}

	public static void save(String filename) {

		//System.out.println("Selected file: " + file.getAbsolutePath());
		try {
			File file = new File(filename);
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			String str = "";
			for (int[] row : board) {
				for (int i : row) {
					str += i + ",";
				}
				str = str.substring(0, str.length() - 1) + "\r\n";
			}
			writer.write(str);
			writer.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			System.out.println("error parsing file");
		} catch (IndexOutOfBoundsException e1) {
			// TODO Auto-generated catch block
			System.out.println("error parsing file");
		}
	}

	public static int[][] load(String filename) {
		int[][] board = new int[9][9];
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
			String line = null;
			int r = 0;
			while((line = reader.readLine()) != null) {
				String[] cols = line.split(",");
				int c = 0;
				for(String col : cols) {
					Integer val = Integer.parseInt(col.trim());
					board[r][c] = val; 
					c++;
				}
				r++;
			}
			reader.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			System.out.println("error parsing file");
		}catch (IndexOutOfBoundsException e1) {
			// TODO Auto-generated catch block
			System.out.println("error parsing file");
		}
		history.clear();		
		return board;
	}
	
	public static void undo() {
		if(history.size() > 0) {
			board = history.pop();			
			System.out.print("");
		}
	}
	
	public static void addToHistory() {
		int[][] historyItem = new int[9][9];
		for(int row = 0; row < 9; row++) {
			for(int col = 0; col < 9; col++) {
				historyItem[row][col] = board[row][col];
			}
		}
		history.addFirst(historyItem);
	}
	
	public static int[][] fillSingles(int[][] board) {
		boolean changed = false;
		int[][] newBoard = new int[9][9];
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				if (board[row][col] != 0) {
					newBoard[row][col] = board[row][col];
				} else {
					HashSet<Integer> possibleValues = SudokuChecker.getPossibleValues(row, col,
							board);
					if (possibleValues.size() == 1) {
						changed = true;
						newBoard[row][col] = (int) possibleValues.toArray()[0];
					}
				}
			}
		}

		// single occupancy rows
		for (int row = 0; row < 9; row++) {
			HashMap<Integer, ArrayList<Point>> map = new HashMap<>();
			for (int col = 0; col < 9; col++) {
				if (board[row][col] == 0) {
					HashSet<Integer> possibleValues1 = SudokuChecker.getPossibleValues(row, col,
							board);
					for (int i : possibleValues1) {
						ArrayList<Point> points = new ArrayList<>();
						if (map.containsKey(i)) {
							points = map.get(i);
						}
						points.add(new Point(col, row));
						map.put(i, points);
					}
				}
			}
			for (int key : map.keySet()) {
				if (map.get(key).size() == 1) {
					Point p = map.get(key).get(0);
					newBoard[p.y][p.x] = key;
					changed = true;
				}
			}
		}
		// single occupancy in columns
		for (int col = 0; col < 9; col++) {
			HashMap<Integer, ArrayList<Point>> map = new HashMap<>();
			for (int row = 0; row < 9; row++) {
				if (board[row][col] == 0) {
					HashSet<Integer> possibleValues1 = SudokuChecker.getPossibleValues(row, col,
							board);
					for (int i : possibleValues1) {
						ArrayList<Point> points = new ArrayList<>();
						if (map.containsKey(i)) {
							points = map.get(i);
						}
						points.add(new Point(col, row));
						map.put(i, points);
					}
				}
			}
			for (int key : map.keySet()) {
				if (map.get(key).size() == 1) {
					Point p = map.get(key).get(0);
					newBoard[p.y][p.x] = key;
					changed = true;
				}
			}

		}

		// single occupancy in Blocks
		for (int row = 2; row < 9; row += 3) {
			for (int col = 2; col < 9; col += 3) {

				// do each block only once for bottom right cell of block
				HashMap<Integer, ArrayList<Point>> map = new HashMap<>();
				for (int row1 = row - 2; row1 <= row; row1++) {
					for (int col1 = col - 2; col1 <= col; col1++) {
						if (board[row1][col1] == 0) {
							HashSet<Integer> possibleValues1 = SudokuChecker.getPossibleValues(row1, col1,
									board);
							for (int i : possibleValues1) {
								ArrayList<Point> points = new ArrayList<>();
								if (map.containsKey(i)) {
									points = map.get(i);
								}
								points.add(new Point(col1, row1));
								map.put(i, points);
							}
						}
					}
				}
				for (int key : map.keySet()) {
					if (map.get(key).size() == 1) {
						Point p = map.get(key).get(0);
						newBoard[p.y][p.x] = key;
						changed = true;
					}
				}
			}
		}
//		System.out.println(changed);
		return newBoard;
	}
	
	public static int attemptToSolve() {
		
		int[][] newBoard = new int[9][9];
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				newBoard[row][col] = board[row][col];
			}
		}
		int unfilled = getUnfilledCells(newBoard);
		boolean improved = true;
		while(improved) {
			int[][] candidateBoard = fillSingles(newBoard);
			if(getUnfilledCells(candidateBoard) < unfilled) {
				unfilled = getUnfilledCells(newBoard);
				newBoard = candidateBoard;
			}else {
				improved = false;
			}
		}
		return unfilled;
	}
	
	public static int getUnfilledCells(int[][] board) {
		int result = 0;
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				if(board[row][col] == 0) {
					result++;
				}
			}
		}
		return result;
	}
}
