import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class NumberChooserPanel extends JDialog {

	

	private static HashSet<Integer> validValues;

	private final JPanel contentPanel;

	public int value;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		HashSet<Integer> validValues = new HashSet<>();
		validValues.addAll(Arrays.asList(1,2,3,4,5,6,7,8,9));
		int val = new NumberChooserPanel(null, validValues, 100, 100).run();
		System.out.println(val);
	}

	/**
	 * Create the dialog.
	 * @param y 
	 * @param x 
	 */
	public NumberChooserPanel(Frame parent, HashSet<Integer> validValues, int x, int y) {		
		super(parent,"Enter data",true);
		this.validValues = validValues;
		setBounds(x, y, 300, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel = new JPanel() {
			@Override
			public void paint(Graphics g) {
				// TODO Auto-generated method stub
				super.paint(g);
				g.setFont(new Font("TimesRoman", Font.PLAIN, 30));
				for(int i = 0; i < 9; i++) {
					int x = 30 + i % 3 * 100;
					int y = 30 + i / 3 * 100;
					if(validValues.contains(i + 1)) {
						g.drawString("" + (i + 1), x, y);
					}
				}
			}
		};
		contentPanel.addMouseListener(new MouseListener() {
			
			

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
				int col = e.getX() / 100;
				int row = e.getY() / 100;
				value = row * 3 + col + 1;
				if(validValues.contains(value)) {
					NumberChooserPanel.this.dispose();
				}				
			}
		});
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
	}

//	public static int getNumber(HashSet<Integer> hashSet) {
//		validValues = hashSet;
//		NumberChooserPanel dialog = new NumberChooserPanel();
//		dialog.setUndecorated(true);
//		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//		dialog.setVisible(true);
//		while(!clicked) {
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		dialog.dispose();
//		return dialog.value;
//	}
	
	public int run() {
	      this.setVisible(true);
	      return value;
	 }
}
