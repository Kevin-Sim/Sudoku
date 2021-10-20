package sudoku;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import say.swing.JFontChooser;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class Settings extends JDialog {

	private final JPanel contentPanel = new JPanel();
	public static Color foreColor = Color.BLACK;
	public static Color backColor = new Color(238, 238, 238);
	public static Color gridColor = Color.BLACK;
	public static Font font = new Font("Times New Roman", Font.PLAIN, 28);
	public static boolean useImages = false;
	public static String imageDir = "img1";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Settings dialog = new Settings(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public Settings(JFrame parent) {
		super(parent, "Settings", true);
		if(parent == null) {
			setBounds(100, 400, 450, 300);
		}else {
			setBounds(parent.getBounds().x, parent.getBounds().y + 400, 450, 300);
		}
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblBackgroundColour = new JLabel("Background Colour");
		lblBackgroundColour.setBackground(backColor);
		lblBackgroundColour.setOpaque(true);
		lblBackgroundColour.setBounds(27, 26, 122, 27);
		Border border = BorderFactory.createLineBorder(Color.BLACK, 2);        
        lblBackgroundColour.setBorder(border);        
		contentPanel.add(lblBackgroundColour);
		
		JLabel lblForegroundColour = new JLabel("Foreground Colour");
		lblForegroundColour.setOpaque(true);
		lblForegroundColour.setBackground(backColor);
		lblForegroundColour.setForeground(foreColor);
		lblForegroundColour.setBounds(27, 64, 122, 27);		       
		lblForegroundColour.setBorder(border);		
		contentPanel.add(lblForegroundColour);
		
		JButton btnFont = new JButton("Font");
		btnFont.addActionListener(new ActionListener() {			 

			public void actionPerformed(ActionEvent e) {				
				JFontChooser.DEFAULT_SELECTED_FONT = font;
				JFontChooser jfc = new JFontChooser();
				if(jfc.showDialog(Settings.this) == JFontChooser.OK_OPTION) {
					font = jfc.getSelectedFont();
				}
			}
		});
		btnFont.setBounds(27, 140, 122, 23);
		contentPanel.add(btnFont);
		
		JLabel lblGridColor = new JLabel("Grid Color");
		lblGridColor.setOpaque(true);
		lblGridColor.setForeground(Color.white);
		lblGridColor.setBackground(gridColor);
		lblGridColor.setBorder(border);	
		lblGridColor.setBounds(27, 102, 122, 27);
		contentPanel.add(lblGridColor);
		
		JCheckBox chckbxImages = new JCheckBox("Images");
		chckbxImages.setSelected(useImages);
		chckbxImages.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				useImages = chckbxImages.isSelected();
			}
		});
		chckbxImages.setBounds(27, 171, 97, 23);
		contentPanel.add(chckbxImages);
		
		SpinnerNumberModel model1 = new SpinnerNumberModel(Integer.parseInt(imageDir.substring(3)), 1, 2, 1);  
		JSpinner spinner = new JSpinner(model1);
		spinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int i = (int)spinner.getValue();
				imageDir = "img" + i;
			}
		});
		spinner.setBounds(149, 172, 40, 20);		
		contentPanel.add(spinner);

		lblBackgroundColour.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {        		
        		super.mouseClicked(e);
        		backColor = JColorChooser.showDialog(Settings.this, "Background Color", backColor);
        		lblBackgroundColour.setBackground(backColor);
        		lblForegroundColour.setBackground(backColor);
        		lblBackgroundColour.setForeground(foreColor);
        		lblForegroundColour.setForeground(foreColor);
        		Settings.this.repaint();
        	}
		});
		lblForegroundColour.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {        		
        		super.mouseClicked(e);
        		foreColor = JColorChooser.showDialog(Settings.this, "Foreground Color", foreColor);
        		lblBackgroundColour.setBackground(backColor);
        		lblForegroundColour.setBackground(backColor);
        		lblBackgroundColour.setForeground(foreColor);
        		lblForegroundColour.setForeground(foreColor);
        		Settings.this.repaint();
        	}
		});
		
		lblGridColor.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {        		
        		super.mouseClicked(e);
        		gridColor = JColorChooser.showDialog(Settings.this, "Grid Color", gridColor);
        		lblGridColor.setBackground(gridColor);
        		Settings.this.repaint();
        	}
		});
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton okButton = new JButton("OK");		
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Confirmed");
				Settings.this.dispose();
			}
		});
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton("Cancel");		
		buttonPane.add(cancelButton);
		cancelButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				reset();
				Settings.this.dispose();				
			}
		});
		System.out.println(getBackground());
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {				
				super.windowClosing(e);
				reset();				
			}
		});
		//blocking 
		setVisible(true);
		System.out.println("Stopped");
	}

	protected void reset() {
		System.out.println("Cancelled");
	}
}
