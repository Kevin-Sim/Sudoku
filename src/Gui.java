import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;

public class Gui extends JFrame {

	private JPanel contentPane;
	private JLabel lblUnfilled;
	private int highlightValue = 0;
	private JSlider slider;
	private JButton btnUndo;
	private boolean drawPecilMarks = false;
	protected boolean showSingleOccupancy = false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Gui frame = new Gui();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Gui() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		int cellSize = 60;
		setBounds(10, 10, 100 + 10 * cellSize, 10 * cellSize);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel sudokuPanel = new JPanel() {

			@Override
			public void paint(Graphics g) {
				super.paint(g);
				Graphics2D g2 = (Graphics2D) g;
				g2.setFont(new Font("TimesRoman", Font.PLAIN, 30));

				if (SudokuChecker.board == null) {
					return;
				}

				for (int row = 0; row < 9; row++) {
					if (row % 3 == 0) {
						g2.setStroke(new BasicStroke(3));
					} else {
						g2.setStroke(new BasicStroke(1));
					}
					g2.drawLine(0, row * cellSize, cellSize * 9, row * cellSize);
					for (int col = 0; col < 9; col++) {
						if (row == 0) {
							if (col % 3 == 0) {
								g2.setStroke(new BasicStroke(3));
							} else {
								g2.setStroke(new BasicStroke(1));
							}
							g2.drawLine(col * cellSize, 0, col * cellSize, cellSize * 9);
						}
						int x = cellSize / 2 - 10 + col * cellSize;
						int y = (cellSize * 3) / 4 - 10 + row * cellSize;
						if (SudokuChecker.board[row][col] != 0) {
							g2.setColor(Color.BLACK);
							if (SudokuChecker.board[row][col] == highlightValue) {
								g2.setColor(Color.RED);
							}
							g2.drawString("" + SudokuChecker.board[row][col], x, y);
							g2.setColor(Color.BLACK);
						} else {							
							if (drawPecilMarks) {
								HashSet<Integer> possibleValues = SudokuChecker.getPossibleValues(row, col,
										SudokuChecker.board);
								g2.setColor(Color.GREEN);
								g2.setFont(new Font("TimesRoman", Font.PLAIN, 16));
								for (int i = 1; i <= 9; i++) {
									if (possibleValues.contains(i)) {
										int x2 = col * cellSize + 10 + ((i - 1) % 3) * (cellSize - 10) / 3;
										int y2 = row * cellSize + (1 + (i - 1) / 3) * (cellSize - 10) / 3;
										g2.drawString("" + i, x2, y2);
									}
								}
								g2.setFont(new Font("TimesRoman", Font.PLAIN, 30));
								g2.setColor(Color.BLACK);
							}

						}
						// show single occupancy in rows (number only appears as pencil mark in one cell
						// in row)
						if (showSingleOccupancy && col == 8) {
							// do each row only once for col 8 overwrite green pencil
							HashMap<Integer, ArrayList<Point>> map = new HashMap<>();
							g2.setColor(Color.RED);
							g2.setFont(new Font("TimesRoman", Font.PLAIN, 16));
							for (int col1 = 0; col1 < 9; col1++) {
								if (SudokuChecker.board[row][col1] == 0) {
									HashSet<Integer> possibleValues1 = SudokuChecker.getPossibleValues(row, col1,
											SudokuChecker.board);
									for (int i : possibleValues1) {
										ArrayList<Point> points = new ArrayList<>();
										if (map.containsKey(i)) {
											points = map.get(i);
										}
										points.add(new Point(col1, row));
										map.put(i, points);
									}
								}
							}
							for (int key : map.keySet()) {
								if (map.get(key).size() == 1) {
									Point p = map.get(key).get(0);
									int x2 = p.x * cellSize + 10 + ((key - 1) % 3) * (cellSize - 10) / 3;
									int y2 = row * cellSize + (1 + (key - 1) / 3) * (cellSize - 10) / 3;
									g2.drawString("" + key, x2 - 5, y2 - 5);
								}
							}
							g2.setColor(Color.BLACK);
							g2.setFont(new Font("TimesRoman", Font.PLAIN, 30));
							// cols
						}
						// show single occupancy in columns (number only appears as pencil mark in one
						// cell in column)
						if (showSingleOccupancy && row == 8) {
							// do each col only once for row 8 overwrite green pencil
							HashMap<Integer, ArrayList<Point>> map = new HashMap<>();
							g2.setColor(Color.BLUE);
							g2.setFont(new Font("TimesRoman", Font.PLAIN, 16));
							for (int row1 = 0; row1 < 9; row1++) {
								if (SudokuChecker.board[row1][col] == 0) {
									HashSet<Integer> possibleValues1 = SudokuChecker.getPossibleValues(row1, col,
											SudokuChecker.board);
									for (int i : possibleValues1) {
										ArrayList<Point> points = new ArrayList<>();
										if (map.containsKey(i)) {
											points = map.get(i);
										}
										points.add(new Point(col, row1));
										map.put(i, points);
									}
								}
							}
							for (int key : map.keySet()) {
								if (map.get(key).size() == 1) {
									Point p = map.get(key).get(0);
									int x2 = col * cellSize + 10 + ((key - 1) % 3) * (cellSize - 10) / 3;
									int y2 = p.y * cellSize + (1 + (key - 1) / 3) * (cellSize - 10) / 3;
									g2.drawString("" + key, x2, y2);
								}
							}
							g2.setColor(Color.BLACK);
							g2.setFont(new Font("TimesRoman", Font.PLAIN, 30));

						}

						// show single occupancy in Blocks (number only appears as pencil mark in one
						// cell in Block)
						if (showSingleOccupancy && (row + 1) % 3 == 0 & (col + 1) % 3 == 0) {
							// do each block only once for bottom right cell of block
							HashMap<Integer, ArrayList<Point>> map = new HashMap<>();
							g2.setColor(Color.MAGENTA);
							g2.setFont(new Font("TimesRoman", Font.PLAIN, 16));
							for (int row1 = row - 2; row1 <= row; row1++) {
								for (int col1 = col - 2; col1 <= col; col1++) {
									if (SudokuChecker.board[row1][col1] == 0) {
										HashSet<Integer> possibleValues1 = SudokuChecker.getPossibleValues(row1, col1,
												SudokuChecker.board);
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
									int x2 = p.x * cellSize + 10 + ((key - 1) % 3) * (cellSize - 10) / 3;
									int y2 = p.y * cellSize + (1 + (key - 1) / 3) * (cellSize - 10) / 3;
									g2.drawString("" + key, x2 + 5, y2 + 5);
								}
							}
							g2.setColor(Color.BLACK);
							g2.setFont(new Font("TimesRoman", Font.PLAIN, 30));

						}
					}
				}
				g2.setStroke(new BasicStroke(3));
				g2.drawLine(0, cellSize * 9, cellSize * 9, cellSize * 9);
				g2.drawLine(cellSize * 9, 0, cellSize * 9, cellSize * 9);
				int count = 0;
				for (int row = 0; row < 9; row++) {
					for (int col = 0; col < 9; col++) {
						if (SudokuChecker.board[row][col] == 0) {
							count++;
						}
					}
				}
				lblUnfilled.setText("" + count);
				if (count == 0 && SudokuChecker.isValid(SudokuChecker.board)) {
					lblUnfilled.setBackground(Color.GREEN);
					lblUnfilled.setOpaque(true);
					Gui.this.repaint();
				}
				if (SudokuChecker.history.size() > 0) {
					btnUndo.setEnabled(true);
				} else {
					btnUndo.setEnabled(false);
				}
			}
		};

		sudokuPanel.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				int col = e.getX() / cellSize;
				int row = e.getY() / cellSize;
				int button = e.getButton();
				if (e.getButton() == 3) {
					SudokuChecker.addToHistory();
					SudokuChecker.board[row][col] = 0;
					repaint();
					return;
				}
				System.out.println("Row " + row + " Column " + col);
				if (row >= 0 && row < 9 && col >= 0 && col < 9) {
					if (SudokuChecker.board[row][col] != 0) {
						highlightValue = SudokuChecker.board[row][col];
					} else {
						HashSet<Integer> possibleValues = SudokuChecker.getPossibleValues(row, col,
								SudokuChecker.board);
						if (!Gui.this.drawPecilMarks) {
							for (int i = 1; i <= 9; i++) {
								possibleValues.add(i);
							}
						}
						int val = 0;
						val = new NumberChooserPanel(Gui.this, possibleValues, e.getX(), e.getY()).run();
						if (possibleValues.contains(val)) {
							SudokuChecker.addToHistory();
							SudokuChecker.board[row][col] = val;
						}
					}
				} else {
					highlightValue = 0;
				}
				repaint();
			}
		});

		contentPane.add(sudokuPanel, BorderLayout.CENTER);

		JPanel controlPanel = new JPanel();
		controlPanel.setPreferredSize(new Dimension(100, 0));
		contentPane.add(controlPanel, BorderLayout.WEST);

		JButton btnFillSingles = new JButton("fill singles");
		btnFillSingles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean changed = false;
				int[][] newBoard = new int[9][9];
				for (int row = 0; row < 9; row++) {
					for (int col = 0; col < 9; col++) {
						if (SudokuChecker.board[row][col] != 0) {
							newBoard[row][col] = SudokuChecker.board[row][col];
						} else {
							HashSet<Integer> possibleValues = SudokuChecker.getPossibleValues(row, col,
									SudokuChecker.board);
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
						if (SudokuChecker.board[row][col] == 0) {
							HashSet<Integer> possibleValues1 = SudokuChecker.getPossibleValues(row, col,
									SudokuChecker.board);
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
						if (SudokuChecker.board[row][col] == 0) {
							HashSet<Integer> possibleValues1 = SudokuChecker.getPossibleValues(row, col,
									SudokuChecker.board);
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
								if (SudokuChecker.board[row1][col1] == 0) {
									HashSet<Integer> possibleValues1 = SudokuChecker.getPossibleValues(row1, col1,
											SudokuChecker.board);
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

				if (changed) {
					SudokuChecker.addToHistory();
				}
				SudokuChecker.board = newBoard;
				repaint();

			}
		});
		controlPanel.add(btnFillSingles);

		JButton btnGenNew = new JButton("genNew");
		btnGenNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SudokuChecker.board = SudokuChecker.generateValidBoard();
				SudokuChecker.history.clear();
				for (int i = 0; i < getSlider().getValue(); i++) {
					int r = Parameters.rnd.nextInt(9);
					int c = Parameters.rnd.nextInt(9);
					SudokuChecker.board[r][c] = 0;
					highlightValue = 0;
					lblUnfilled.setBackground(Color.LIGHT_GRAY);
					repaint();
				}
			}
		});
		controlPanel.add(btnGenNew);

		slider = new JSlider();
		slider.setValue(65);
		slider.setPreferredSize(new Dimension(100, 50));
		slider.setMaximum(100);
		slider.setMinimum(10);
		controlPanel.add(slider);

		lblUnfilled = new JLabel("unfilled");
		lblUnfilled.setPreferredSize(new Dimension(100, 50));
		controlPanel.add(lblUnfilled);

		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File("."));
				int result = fileChooser.showOpenDialog(btnSave);
				if (result == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					SudokuChecker.save(file.getAbsolutePath());
				}
			}
		});
		controlPanel.add(btnSave);

		JButton btnLoad = new JButton("Load");
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[][] board = new int[9][9];
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File("."));
				int result = fileChooser.showOpenDialog(btnLoad);
				if (result == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					System.out.println("Selected file: " + file.getAbsolutePath());
					board = SudokuChecker.load(file.getAbsolutePath());
					SudokuChecker.board = board;
					highlightValue = 0;
					lblUnfilled.setBackground(Color.LIGHT_GRAY);
					repaint();
				}
			}
		});
		controlPanel.add(btnLoad);

		btnUndo = new JButton("Undo");
		btnUndo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SudokuChecker.undo();
				Gui.this.repaint();
			}
		});
		controlPanel.add(btnUndo);

		JCheckBox chckbxNewCheckBox = new JCheckBox("Pencil Marks");
		chckbxNewCheckBox.setPreferredSize(new Dimension(100, 50));
		chckbxNewCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawPecilMarks = chckbxNewCheckBox.isSelected();
				Gui.this.repaint();

			}
		});
		controlPanel.add(chckbxNewCheckBox);

		JCheckBox checkBox = new JCheckBox("Single Occupancy");
		checkBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showSingleOccupancy = checkBox.isSelected();
				Gui.this.repaint();
			}
		});
		checkBox.setPreferredSize(new Dimension(100, 50));
		controlPanel.add(checkBox);

	}

	public JLabel getLblUnfilled() {
		return lblUnfilled;
	}

	public JSlider getSlider() {
		return slider;
	}

	public JButton getBtnUndo() {
		return btnUndo;
	}
}