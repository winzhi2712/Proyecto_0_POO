package GUI;

import java.awt.*;
import javax.swing.*;

import control.Control;
import logica.Cliente;

public class JFrameClientes extends JFrame {
	private Control control = Control.getInstancia();
	//la lista que se ve en pantalla. 
	private DefaultListModel<Cliente> modeloLista = new DefaultListModel<>();
	private JList<Cliente> lista = new JList<>(modeloLista);

	//cajitas fabulosas de texto del formulario
	private JTextField campoNombre = new JTextField();
	private JTextField campoEmail = new JTextField();
	private JTextField campoNumero = new JTextField();

	public JFrameClientes() {
		initialize();
	}
	public void initialize() {
		this.setTitle("SECCION DE CLIENTES");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setSize(800, 500);
		this.setLocationRelativeTo(null);

		JPanel panel = new JPanel(new BorderLayout());
		JLabel mesage = new JLabel("SECCION DE CLIENTES", JLabel.CENTER);

		//CENTRO: la lista de clientes
		lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // solo se elige uno a la vez
		JScrollPane scroll = new JScrollPane(lista);

		//DERECHA: el formulario
		JPanel formulario = new JPanel(new GridLayout(6, 1, 5, 5));
		formulario.setPreferredSize(new Dimension(280, 0));
		formulario.add(new JLabel("Nombre completo:"));
		formulario.add(campoNombre);
		formulario.add(new JLabel("Email:"));
		formulario.add(campoEmail);
		formulario.add(new JLabel("Teléfono (números, paréntesis y guiones):"));
		formulario.add(campoNumero);

		//ABAJO: botones
		JPanel panelButton = new JPanel(new FlowLayout());
		JButton botonCrear = createButton("CREAR");
		JButton botonModificar = createButton("MODIFICAR");
		JButton botonBorrar = createButton("BORRAR");
		JButton botonLimpiar = createButton("LIMPIAR");
		JButton button1 = createButton("REGRESAR");
		botonCrear.setPreferredSize(new Dimension(120, 40));
		botonModificar.setPreferredSize(new Dimension(120, 40));
		botonBorrar.setPreferredSize(new Dimension(120, 40));
		botonLimpiar.setPreferredSize(new Dimension(120, 40));
		button1.setPreferredSize(new Dimension(120, 40));
		panelButton.add(botonCrear);
		panelButton.add(botonModificar);
		panelButton.add(botonBorrar);
		panelButton.add(botonLimpiar);
		panelButton.add(button1);

		panel.add(mesage, BorderLayout.NORTH);
		panel.add(scroll, BorderLayout.CENTER);
		panel.add(formulario, BorderLayout.EAST);
		panel.add(panelButton, BorderLayout.SOUTH);

		
		//Al hacer clic en un cliente de la lista, se muestran sus datos (CONSULTAR CLIENTE)
		lista.addListSelectionListener(e -> {
			Cliente c = control.consultarCliente(lista.getSelectedIndex()); //Devuelve null si no hay nada elegido
			if (c != null) {
				campoNombre.setText(c.getNombre());
				campoEmail.setText(c.getEmail());
				campoNumero.setText(c.getNumero());
				campoNombre.setEditable(false); 
			}
		});

		// CREAR
		botonCrear.addActionListener(e -> {
			if (!campoNombre.isEditable()) {
				JOptionPane.showMessageDialog(this, "Presione LIMPIAR antes de crear un cliente nuevo.");
				return;
			}
			boolean ok = control.crearCliente(campoNombre.getText(), campoEmail.getText(), campoNumero.getText());
			mostrarResultado(ok);
			if (ok) { // si hubo error no se limpia, para que el usuario corrija los datos porque todos cometemos errores
				limpiar();
				actualizarLista();
			}
		});

		//MODIFICAR (usa la posición del cliente elegido en la lista)
		botonModificar.addActionListener(e -> {
			boolean ok = control.modificarCliente(campoEmail.getText(), campoNumero.getText(), lista.getSelectedIndex());
			mostrarResultado(ok);
			if (ok) {
				int pos = lista.getSelectedIndex();
				actualizarLista();
				lista.setSelectedIndex(pos); 
			}
		});

		//BORRAR
		botonBorrar.addActionListener(e -> {
			int pos = lista.getSelectedIndex();
			if (pos >= 0) {
				int respuesta = JOptionPane.showConfirmDialog(this, "¿Seguro que desea borrar este cliente?", "Confirmar", JOptionPane.YES_NO_OPTION);
				if (respuesta != JOptionPane.YES_OPTION) {
					return;
				}
			}
			boolean ok = control.borrarCliente(pos);
			mostrarResultado(ok);
			if (ok) {
				limpiar();
				actualizarLista();
			}
		});

		botonLimpiar.addActionListener(e -> limpiar());

		button1.addActionListener(e -> volverAlMenu());

		this.add(panel);
		actualizarLista(); 
	}

	
	private void actualizarLista() {
		modeloLista.clear();
		for (Cliente c : control.getClientes()) {
			modeloLista.addElement(c);
		}
	}

	//Deja el formulario en blanco
	private void limpiar() {
		lista.clearSelection();
		campoNombre.setText("");
		campoEmail.setText("");
		campoNumero.setText("");
		campoNombre.setEditable(true);
	}

	// Muestra el mensaje que dejó Control en una ventanita (verde/info si salió bien, roja sangre si falló)
	private void mostrarResultado(boolean ok) {
		JOptionPane.showMessageDialog(this, control.getUltimoMensaje(), ok ? "Listo" : "Error",
				ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
	}

	private void volverAlMenu() {
		this.dispose();
		try {
			MainFrame mainFrame = new MainFrame();
			mainFrame.show();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	private JButton createButton(String nombre) {
		JButton button = new JButton(nombre);
		button.setFocusable(false);
		return button;
	}
}