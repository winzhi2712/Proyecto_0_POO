package GUI;

import javax.swing.SwingUtilities;

public class interfaz {
	public static void main(String[]args) {
		SwingUtilities.invokeLater(new Runnable() {
		
		@Override
			public void run() {
				MainFrame main;
				try {
					main = new MainFrame();
					main.show();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				}
			}
		});
	}
}
