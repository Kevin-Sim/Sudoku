package say.swing;

import java.awt.Font;

import javax.swing.JDialog;

public class Test {

	public static void main(String[] args) {
		JFontChooser jfc = new JFontChooser();
		if(jfc.showDialog(null) == JFontChooser.OK_OPTION) {
			Font font = jfc.getSelectedFont();
			System.out.println(font);
		};
	}

}
