package GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainFrame {

	private JFrame window;
	public MainFrame() throws Exception, IOException {
		window = new JFrame();
		window.setTitle("CALVESA");
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		window.setSize(800, 500);
		window.setLocationRelativeTo(null);
		
		JPanel panel = new JPanel(new BorderLayout());
		ImageIcon image = new ImageIcon("pelo.jpeg");
		Image imageScale = image.getImage().getScaledInstance(300, 200, Image.SCALE_SMOOTH);
		JLabel etiquetaImage = new JLabel(new ImageIcon(imageScale));
		
		//titulo
		
		File fontFile = new File("font.ttf");
		Font chicanoFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
		JLabel mesage = new JLabel("CALVESSA", JLabel.CENTER);
		mesage.setPreferredSize(new Dimension(400, 80));
		Font chicanoFontFinal = chicanoFont.deriveFont(Font.BOLD, 42f);
		mesage.setFont(chicanoFontFinal);
		
		
		//botones
		JPanel panelButton = new JPanel(new FlowLayout());
		JButton button1 = createButton("CLIENTES");
		button1.setPreferredSize(new Dimension(120, 40));
		panelButton.add(button1);
		
		
		JButton button2 = createButton("CITAS");
		button2.setPreferredSize(new Dimension(120, 40));
		panelButton.add(button2);
		
		JButton button3 = createButton("LISTA DE ESPERA");
		button3.setPreferredSize(new Dimension(160, 40));
		panelButton.add(button3);
		
		JButton button4 = createButton("ADMINISTRACION");
		button4.setPreferredSize(new Dimension(160, 40));
		panelButton.add(button4);
		
		JButton button5 = createButton("SALIR");
		button5.setPreferredSize(new Dimension(120, 40));
		panelButton.add(button5);
		
		panel.add(etiquetaImage, BorderLayout.CENTER);
		panel.add(panelButton, BorderLayout.SOUTH);
		panel.add(mesage, BorderLayout.NORTH);
		
		button1.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				window.dispose();
				JFrameClientes clientes = new JFrameClientes();
				clientes.setVisible(true);
			}
			
		});
		
		
		button2.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					window.dispose();
					JFrameCitas citas = new JFrameCitas();
					citas.setVisible(true);
				}
				
			});
		
		button3.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					window.dispose();
					JFrameEspera espera = new JFrameEspera();
					espera.setVisible(true);
				}
				
			});
			
		button4.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				window.dispose();
				JFrameAdmin admin = new JFrameAdmin();
				admin.setVisible(true);
			}
			
		});
		
		button5.addActionListener(new ActionListener() {
					
					public void actionPerformed(ActionEvent e) {
						System.exit(0);
					}
					
				});
		window.add(panel);
	}
	
	public void show() {
		window.setVisible(true);
	}
	
	private JButton createButton(String nombre) {
		JButton button = new JButton(nombre);
		button.setFocusable(false);
		return button;
	}
	
}
