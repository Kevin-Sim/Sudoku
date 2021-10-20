package sudoku;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;

import resources.ResourceLoader;

public class Gui extends JFrame implements Observer {

	private JPanel contentPane;
	private JLabel lblUnfilled;
	private int highlightValue = 0;
	private JSlider slider;
	private JButton btnUndo;
	private boolean drawPecilMarks = false;
	private boolean showSingleOccupancy = false;
	private Celebrate celebtrate = null;
	private boolean showErrors = false;
	public static final int CELLSIZE = 80;
	public Image[] IMAGES;
	public Image[] PENCILIMAGES;
	private JPanel sudokuPanel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Gui frame = new Gui();
					frame.setVisible(true);
					frame.repaint();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private static Image[] getImages(int size) {
		Image[] images = new Image[9];
		for (int i = 1; i <= 9; i++) {
			Image image = ResourceLoader.loadImage("" + i + ".png");
			image = image.getScaledInstance(size - 6, size - 6, Image.SCALE_SMOOTH);
			images[i - 1] = image;
		}
		return images;
	}

	/**
	 * Create the frame.
	 */
	public Gui() {
		loadImages();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(400, 50, 100 + 10 * CELLSIZE, 10 * CELLSIZE);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		sudokuPanel = new JPanel() {

			@Override
			public void paint(Graphics g) {
				super.paint(g);
				Graphics2D g2 = (Graphics2D) g;
				g2.setFont(Settings.font);

				if (SudokuChecker.board == null) {
					return;
				}

				for (int row = 0; row < 9; row++) {
					if (row % 3 == 0) {
						g2.setStroke(new BasicStroke(3));
					} else {
						g2.setStroke(new BasicStroke(1));
					}
					g2.setColor(Settings.gridColor);
					g2.drawLine(0, row * CELLSIZE, CELLSIZE * 9, row * CELLSIZE);
					for (int col = 0; col < 9; col++) {
						if (row == 0) {
							if (col % 3 == 0) {
								g2.setStroke(new BasicStroke(3));
							} else {
								g2.setStroke(new BasicStroke(1));
							}
							g2.setColor(Settings.gridColor);
							g2.drawLine(col * CELLSIZE, 0, col * CELLSIZE, CELLSIZE * 9);
						}
						int x = CELLSIZE / 2 - 10 + col * CELLSIZE;
						int y = (CELLSIZE * 3) / 4 - 10 + row * CELLSIZE;
						if (SudokuChecker.board[row][col] != 0) {
							g2.setColor(Settings.foreColor);
							g2.setFont(Settings.font);
							if (SudokuChecker.board[row][col] == highlightValue) {
								g2.setColor(Color.RED);
								g2.fillRect(col * CELLSIZE, row * CELLSIZE, CELLSIZE, CELLSIZE);
							}

							if (Settings.useImages) {
								g2.drawImage(IMAGES[SudokuChecker.board[row][col] - 1], col * CELLSIZE + 3,
										row * CELLSIZE + 3, null);
							} else {
								g2.drawString("" + SudokuChecker.board[row][col], x, y);
							}
						} else {
							if (drawPecilMarks) {
								HashSet<Integer> possibleValues = SudokuChecker.getPossibleValues(row, col,
										SudokuChecker.board);
								g2.setColor(Color.GREEN);
								g2.setFont(g2.getFont().deriveFont((float) 16.0));
								for (int i = 1; i <= 9; i++) {
									if (possibleValues.contains(i)) {
										int x2 = col * CELLSIZE + 10 + ((i - 1) % 3) * (CELLSIZE - 10) / 3;
										int y2 = row * CELLSIZE + (1 + (i - 1) / 3) * (CELLSIZE - 10) / 3;
										if (Settings.useImages) {
											g2.drawImage(PENCILIMAGES[i - 1], x2 - CELLSIZE / 20, y2 - CELLSIZE / 5,
													null);
										} else {
											g2.drawString("" + i, x2, y2);
										}
									}
								}
								g2.setFont(Settings.font);
							}

						}
						// show single occupancy in rows (number only appears as pencil mark in one cell
						// in row)
						if (showSingleOccupancy && col == 8) {
							// do each row only once for col 8 overwrite green pencil
							HashMap<Integer, ArrayList<Point>> map = new HashMap<>();
							g2.setColor(Color.RED);
							g2.setFont(g2.getFont().deriveFont((float) 16.0));
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
									int x2 = p.x * CELLSIZE + 10 + ((key - 1) % 3) * (CELLSIZE - 10) / 3;
									int y2 = row * CELLSIZE + (1 + (key - 1) / 3) * (CELLSIZE - 10) / 3;
									if (Settings.useImages) {
										g2.drawImage(PENCILIMAGES[key - 1], x2 - CELLSIZE / 20, y2 - CELLSIZE / 5,
												null);
										g2.setColor(new Color(255, 0, 0, 128));
										g2.fillRect(x2 - CELLSIZE / 20, y2 - CELLSIZE / 5, (CELLSIZE - 10) / 3 - 4,
												(CELLSIZE - 10) / 3 - 4);
									} else {
										g2.setColor(Color.RED);
										g2.drawString("" + key, x2, y2);
									}
								}
							}

							// cols
						}
						// show single occupancy in columns (number only appears as pencil mark in one
						// cell in column)
						if (showSingleOccupancy && row == 8) {
							// do each col only once for row 8 overwrite green pencil
							HashMap<Integer, ArrayList<Point>> map = new HashMap<>();
							g2.setColor(Color.RED);
							g2.setFont(g2.getFont().deriveFont((float) 16.0));
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
									int x2 = col * CELLSIZE + 10 + ((key - 1) % 3) * (CELLSIZE - 10) / 3;
									int y2 = p.y * CELLSIZE + (1 + (key - 1) / 3) * (CELLSIZE - 10) / 3;
									if (Settings.useImages) {
										g2.drawImage(PENCILIMAGES[key - 1], x2 - CELLSIZE / 20, y2 - CELLSIZE / 5,
												null);
										g2.setColor(new Color(255, 0, 0, 64));
										g2.fillRect(x2 - CELLSIZE / 20, y2 - CELLSIZE / 5, (CELLSIZE - 10) / 3 - 4,
												(CELLSIZE - 10) / 3 - 4);
									} else {
										g2.setColor(Color.RED);
										g2.drawString("" + key, x2, y2);
									}
								}
							}

						}

						// show single occupancy in Blocks (number only appears as pencil mark in one
						// cell in Block)
						if (showSingleOccupancy && (row + 1) % 3 == 0 & (col + 1) % 3 == 0) {
							// do each block only once for bottom right cell of block
							HashMap<Integer, ArrayList<Point>> map = new HashMap<>();
							g2.setColor(Color.RED);
							g2.setFont(g2.getFont().deriveFont((float) 16.0));
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
									int x2 = p.x * CELLSIZE + 10 + ((key - 1) % 3) * (CELLSIZE - 10) / 3;
									int y2 = p.y * CELLSIZE + (1 + (key - 1) / 3) * (CELLSIZE - 10) / 3;
									if (Settings.useImages) {
										g2.drawImage(PENCILIMAGES[key - 1], x2 - CELLSIZE / 20, y2 - CELLSIZE / 5,
												null);
										g2.setColor(new Color(255, 0, 0, 64));
										g2.fillRect(x2 - CELLSIZE / 20, y2 - CELLSIZE / 5, (CELLSIZE - 10) / 3 - 4,
												(CELLSIZE - 10) / 3 - 4);
									} else {
										g2.setColor(Color.RED);
										g2.drawString("" + key, x2, y2);
									}
								}
							}
						}
					}

				}
				if (showErrors) {
					for (int r = 0; r < 9; r++) {
						for (int c = 0; c < 9; c++) {
							if (SudokuChecker.getConflicts(r, c, SudokuChecker.board) > 0) {
								g2.setColor(new Color(255, 0, 0, 64));
								g2.fillRect(c * CELLSIZE, r * CELLSIZE, CELLSIZE, CELLSIZE);
							}
						}
					}
				}
				g2.setStroke(new BasicStroke(3));
				g2.setColor(Settings.gridColor);
				g2.drawLine(0, CELLSIZE * 9, CELLSIZE * 9, CELLSIZE * 9);
				g2.drawLine(CELLSIZE * 9, 0, CELLSIZE * 9, CELLSIZE * 9);
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
					if (celebtrate == null) {
						celebtrate = new Celebrate();
						celebtrate.addObserver(Gui.this);
						Thread t = new Thread(celebtrate);
						t.start();
					}
					lblUnfilled.setBackground(Color.GREEN);
					lblUnfilled.setOpaque(true);
					Random rnd = new Random();
					for (int row = 0; row < 9; row++) {
						for (int col = 0; col < 9; col++) {
							g2.setColor(new Color(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255), 128));
							g2.fillRect(col * CELLSIZE, row * CELLSIZE, CELLSIZE, CELLSIZE);
						}
					}

				} else if (count == 0 && !SudokuChecker.isValid(SudokuChecker.board)) {
					lblUnfilled.setBackground(Color.RED);
					lblUnfilled.setOpaque(true);
					for (int row = 0; row < 9; row++) {
						for (int col = 0; col < 9; col++) {
							if (SudokuChecker.getConflicts(row, col, SudokuChecker.board) > 0) {
								g2.setColor(new Color(255, 0, 0, 128));
								g2.fillRect(col * CELLSIZE, row * CELLSIZE, CELLSIZE, CELLSIZE);
							}
						}
					}
				} else {
					lblUnfilled.setBackground(Color.LIGHT_GRAY);
				}
				if (SudokuChecker.history.size() > 0) {
					btnUndo.setEnabled(true);
				} else {
					btnUndo.setEnabled(false);
				}
			}
		};

		sudokuPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int col = e.getX() / CELLSIZE;
				int row = e.getY() / CELLSIZE;
				if (e.getButton() == 3) {
					SudokuChecker.addToHistory();
					SudokuChecker.board[row][col] = 0;
					repaint();
					return;
				}
				System.out.println("Row " + row + " Column " + col + "\t" + e.getX());
				if (row >= 0 && row < 9 && col >= 0 && col < 9) {
					if (SudokuChecker.board[row][col] != 0) {
						if (SudokuChecker.board[row][col] == highlightValue) {
							highlightValue = 0;
						} else {
							highlightValue = SudokuChecker.board[row][col];
						}
					} else {
						HashSet<Integer> possibleValues = SudokuChecker.getPossibleValues(row, col,
								SudokuChecker.board);
						if (!Gui.this.drawPecilMarks) {
							for (int i = 1; i <= 9; i++) {
								possibleValues.add(i);
							}
						}
						int val = 0;
						int x = Gui.this.getBounds().x + e.getX();
						int y = Gui.this.getBounds().y + e.getY();
						if(y > Gui.this.getBounds().height - CELLSIZE * 3) {
							y = Gui.this.getBounds().height - CELLSIZE * 3;
						}
						val = new NumberChooserPanel(Gui.this, possibleValues, x, y).run();
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
		controlPanel.setPreferredSize(new Dimension(120, 0));
		contentPane.add(controlPanel, BorderLayout.WEST);

		JButton btnFillSingles = new JButton("fill singles");
		btnFillSingles.setPreferredSize(new Dimension(100, 32));
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
				if (!showSingleOccupancy) {
					if (!changed) {
						JOptionPane.showMessageDialog(Gui.this, "No Change From Pencil Marks");
						return;
					}
					SudokuChecker.addToHistory();
					SudokuChecker.board = newBoard;
					repaint();
					return;
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
		btnGenNew.setPreferredSize(new Dimension(100, 32));
		btnGenNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SudokuChecker.board = SudokuChecker.generateBoard(81 - getSlider().getValue());
				SudokuChecker.history.clear();
				highlightValue = 0;
				celebtrate = null;
				repaint();
				Gui.this.requestFocus();
//				JOptionPane.showMessageDialog(Gui.this, "Board Generated");
			}
		});
		controlPanel.add(btnGenNew);

		slider = new JSlider();
		slider.setValue(45);
		slider.setPreferredSize(new Dimension(100, 50));
		slider.setMaximum(64);
		slider.setMinimum(25);
		Hashtable<Integer, JLabel> table = new Hashtable<Integer, JLabel>();
		table.put(25, new JLabel("Easy"));
		table.put(45, new JLabel("Med"));
		table.put(64, new JLabel("Hard"));
		slider.setLabelTable(table);
		slider.setPaintLabels(true);
		controlPanel.add(slider);

		lblUnfilled = new JLabel("unfilled");
		lblUnfilled.setPreferredSize(new Dimension(100, 32));
		controlPanel.add(lblUnfilled);

		JButton btnSave = new JButton("Save");
		btnSave.setPreferredSize(new Dimension(100, 32));
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
		btnLoad.setPreferredSize(new Dimension(100, 32));
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
					celebtrate = null;
					lblUnfilled.setBackground(Color.LIGHT_GRAY);
					repaint();
					repaint();
				}
			}
		});
		controlPanel.add(btnLoad);

		btnUndo = new JButton("Undo");
		btnUndo.setPreferredSize(new Dimension(100, 32));
		btnUndo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SudokuChecker.undo();
				Gui.this.repaint();
			}
		});
		controlPanel.add(btnUndo);

		JCheckBox chckbxNewCheckBox = new JCheckBox("Pencil Marks");
		chckbxNewCheckBox.setPreferredSize(new Dimension(100, 32));
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
		checkBox.setPreferredSize(new Dimension(100, 32));
		controlPanel.add(checkBox);

		JCheckBox chckbxErrors = new JCheckBox("Errors");
		chckbxErrors.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showErrors = chckbxErrors.isSelected();
				Gui.this.repaint();
			}
		});
		chckbxErrors.setPreferredSize(new Dimension(100, 32));
		controlPanel.add(chckbxErrors);

		JButton btnAbout = new JButton("About");
		btnAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(btnAbout, "\u00a9 Kevin Sim 2021");
			}
		});
		btnAbout.setPreferredSize(new Dimension(100, 32));
		controlPanel.add(btnAbout);

		JButton btnSettings = new JButton("Settings");
		btnSettings.setPreferredSize(new Dimension(100, 32));
		btnSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Settings(Gui.this);
				sudokuPanel.setBackground(Settings.backColor);
				if (Settings.useImages) {
					loadImages();
				}
				Gui.this.revalidate();
				Gui.this.repaint();
				Gui.this.sudokuPanel.revalidate();
				Gui.this.sudokuPanel.repaint();
			}
		});

		controlPanel.add(btnSettings);
	}

	private void loadImages() {
		String dir = Settings.imageDir;
		IMAGES = getImages(CELLSIZE);
		PENCILIMAGES = getImages(CELLSIZE / 3);

		//make sure scaled images loaded in getImages have finished scaling 
		BufferedImage dummy = new BufferedImage(CELLSIZE, CELLSIZE, BufferedImage.TYPE_INT_RGB);
		Graphics g = dummy.createGraphics();
		for (Image image : IMAGES) {
			while (!g.drawImage(image, 0, 0, CELLSIZE, CELLSIZE, null)) {

			}
		}
		for (Image image : PENCILIMAGES) {
			g.drawImage(image, 0, 0, CELLSIZE / 3, CELLSIZE / 3, null);
			while (!g.drawImage(image, 0, 0, CELLSIZE, CELLSIZE, null)) {

			}
		}		
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

	@Override
	public void update(Observable o, Object arg) {
		repaint();
	}

	class Celebrate extends Observable implements Runnable {

		public boolean running = false;

		@Override
		public void run() {
			running = true;
			for (int i = 0; i < 200; i++) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				setChanged();
				notifyObservers(this);
			}
			running = false;
			setChanged();
			notifyObservers(this);
		}
	}

	public JPanel getSudokuPanel() {
		return sudokuPanel;
	}
}
