package GUI;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.*;

import control.Control;
import logica.Cliente;

public class JFrameEspera extends JFrame {
	// Saludos explorador del código, soy Mikel, tu nuevo animal espiritual
	// deseo que tu travesía por este pantanoso y lúgubre algoritmo te sea sumamente gratificante.
	
	// Tengo sueño, el té de manzanilla fue una mala idea... Siga viendo.

	private Control control = Control.getInstancia();

	private DefaultListModel<String> modeloLista = new DefaultListModel<>();
	private JList<String> lista = new JList<>(modeloLista);
	private JComboBox<Cliente> comboCliente = new JComboBox<>(); 

	public JFrameEspera() {
		initialize();
	}

	public void initialize() {
		this.setTitle("SECCION DE LA LISTA DE ESPERA");
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() { 
			@Override
			public void windowClosing(WindowEvent e) {
				Control.guardar();
				System.exit(0);
			}
		});
		this.setSize(800, 500);
		this.setLocationRelativeTo(null);

		JPanel panel = new JPanel(new BorderLayout());
		JLabel mesage = new JLabel("SECCION DE LA LISTA DE ESPERA", JLabel.CENTER);

		// Llenar el desplegable con todos los clientes
		for (Cliente c : control.getClientes()) {
			comboCliente.addItem(c);
		}

		lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Abajo: desplegable + botones, todos en una fila
		JPanel panelButton = new JPanel(new FlowLayout());
		JButton botonAgregar = createButton("AGREGAR");
		JButton botonBorrar = createButton("BORRAR");
		JButton button1 = createButton("REGRESAR");
		botonAgregar.setPreferredSize(new Dimension(60, 40));
		botonBorrar.setPreferredSize(new Dimension(60, 40));
		button1.setPreferredSize(new Dimension(60, 40));
		panelButton.add(new JLabel("Cliente:"));
		panelButton.add(comboCliente);
		panelButton.add(botonAgregar);
		panelButton.add(botonBorrar);
		panelButton.add(button1);

		panel.add(mesage, BorderLayout.NORTH);
		panel.add(new JScrollPane(lista), BorderLayout.CENTER);
		panel.add(panelButton, BorderLayout.SOUTH);

		// AGREGAR: se pasa la posición del cliente elegido en el desplegable
		botonAgregar.addActionListener(e -> {
			boolean ok = control.agregarClienteListaEspera(comboCliente.getSelectedIndex());
			mostrarResultado(ok);
			actualizarLista();
		});

		// BORRAR: se pasa la posición elegida en la lista de espera
		botonBorrar.addActionListener(e -> {
			boolean ok = control.borrarClienteListaEspera(lista.getSelectedIndex());
			mostrarResultado(ok);
			actualizarLista();
		});

		button1.addActionListener(e -> {
			this.dispose();
			try {
				MainFrame mainFrame = new MainFrame();
				mainFrame.show();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});

		this.add(panel);
		actualizarLista();
	}

	// Muestra la lista numerada.
	private void actualizarLista() {
		modeloLista.clear();
		List<Cliente> espera = control.mostrarListaEspera();
		for (int i = 0; i < espera.size(); i++) {
			modeloLista.addElement((i + 1) + ". " + espera.get(i).getNombre());
		}
	}

	private void mostrarResultado(boolean ok) {
		JOptionPane.showMessageDialog(this, control.getUltimoMensaje(), ok ? "Listo" : "Error",
				ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
	}

	private JButton createButton(String nombre) {
		JButton button = new JButton(nombre);
		button.setFocusable(false);
		return button;
	}
}