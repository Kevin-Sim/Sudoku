package sudoku;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.HashSet;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;

public class NumberChooserPanel extends JDialog {

	private final JPanel contentPanel;

	private static final int CELLSIZE = Gui.CELLSIZE;

	private int value;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		HashSet<Integer> validValues = new HashSet<>();
		validValues.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
		int val = new NumberChooserPanel(null, validValues, 100, 100).run();
		System.out.println(val);
	}

	/**
	 * 
	 * Create the dialog.
	 * 
	 * @param y
	 * @param x
	 */
	public NumberChooserPanel(Frame parent, HashSet<Integer> validValues, int x, int y) {
		super(parent, "Enter data", true);
		
		setBounds(x, y, 15 + CELLSIZE * 3, 40 + CELLSIZE * 3);
		getContentPane().setLayout(new BorderLayout());
		contentPanel = new JPanel() {
			@Override
			public void paint(Graphics g) {
				// TODO Auto-generated method stub
				super.paint(g);
				g.setFont(new Font("TimesRoman", Font.BOLD, CELLSIZE / 3));
				for (int i = 0; i < 9; i++) {
					int x = CELLSIZE / 3 + i % 3 * CELLSIZE;
					int y = 10 + CELLSIZE / 3 + i / 3 * CELLSIZE;
					if (validValues.contains(i + 1)) {
						
						if(Settings.useImages) {
							((Graphics2D) g).drawImage(((Gui)parent).IMAGES[i], i % 3 * CELLSIZE + 2, i / 3 * CELLSIZE + 2, null);
						}else {
							g.drawString("" + (i + 1), x, y);
						}
					}
				}
			}
		};
		contentPanel.setForeground(new Color(75, 0, 130));
		contentPanel.setBackground(new Color(255, 235, 205));
		contentPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int col = e.getX() / CELLSIZE;
				int row = (e.getY() + 10) / CELLSIZE;
				value = row * 3 + col + 1;
//				System.err.println(value);
				if (validValues.contains(value)) {
					NumberChooserPanel.this.dispose();
				}
			}
		});
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				super.windowClosing(e);
				value = 0;
			}
		});
	}

	public int run() {
		this.setVisible(true);
		return value;
	}
}
