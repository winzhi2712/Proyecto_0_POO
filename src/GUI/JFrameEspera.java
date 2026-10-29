package GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class JFrameEspera extends JFrame {
	public JFrameEspera() {
		initialize();
	}
	
	public void initialize() {
		this.setTitle("SECCION DE LA LISTA DE ESPERA");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setSize(800, 500);
		this.setLocationRelativeTo(null);
		
		
		JPanel panel = new JPanel(new BorderLayout());
		
		JLabel mesage = new JLabel("SECCION DE LA LISTA DE ESPERA", JLabel.CENTER);
		
		JButton button1 = createButton("REGRESAR");
		button1.setPreferredSize(new Dimension(120, 40));
		JPanel panelButton = new JPanel(new FlowLayout());
		panelButton.add(button1);
		
		panel.add(panelButton, BorderLayout.SOUTH);
		panel.add(mesage, BorderLayout.NORTH);
		
		
		button1.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				JFrameEspera.this.dispose();
				MainFrame mainFrame;
				try {
					mainFrame = new MainFrame();
					mainFrame.show();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
			
		});
		
		this.add(panel);
	}
	
	private JButton createButton(String nombre) {
		JButton button = new JButton(nombre);
		button.setFocusable(false);
		return button;
	}
}
