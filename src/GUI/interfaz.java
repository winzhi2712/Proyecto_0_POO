package GUI;

import javax.swing.SwingUtilities;
import control.Control;

public class interfaz {
	public static void main(String[]args) {
		SwingUtilities.invokeLater(new Runnable() {
		
		@Override
			public void run() {
				MainFrame main;
				try {
					Control.cargar(); //Carga los datos guardados
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
