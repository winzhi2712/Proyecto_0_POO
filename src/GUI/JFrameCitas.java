package GUI;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import javax.swing.*;

import control.Control;
import logica.Cita;
import logica.Cliente;
import logica.Servicio;

public class JFrameCitas extends JFrame {

	private Control control = Control.getInstancia();

	//Lista de citas, guardamos el TEXTO de cada cita. La posición en la lista es la misma posición que tiene la cita dentro de Control.
	private DefaultListModel<String> modeloLista = new DefaultListModel<>();
	private JList<String> lista = new JList<>(modeloLista);

	// Formulario
	private JTextField campoFecha = new JTextField();
	private JComboBox<Integer> comboHora = new JComboBox<>();       // lista desplegable pa horas
	private JComboBox<Cliente> comboCliente = new JComboBox<>();    // lista desplegable pa clientes
	private JComboBox<Servicio> comboServicio = new JComboBox<>();  // lista desplegable pa servicios

	public JFrameCitas() {
		initialize();
	}

	public void initialize() {
		this.setTitle("SECCION DE CITAS");
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
		JLabel mesage = new JLabel("SECCION DE CITAS", JLabel.CENTER);

		//Llenar las listas desplegables
		for (int h = 0; h < 24; h++) {
			comboHora.addItem(h);
		}
		for (Cliente c : control.getClientes()) {
			comboCliente.addItem(c);
		}
		for (Servicio s : control.getServicios()) {
			comboServicio.addItem(s);
		}
		campoFecha.setText(LocalDate.now().toString()); 

		//CENTRO: lista de citas
		lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scroll = new JScrollPane(lista);

		//DERECHA: formulario
		JPanel formulario = new JPanel(new GridLayout(8, 1, 5, 5));
		formulario.setPreferredSize(new Dimension(260, 0));
		formulario.add(new JLabel("Fecha (AAAA-MM-DD):"));
		formulario.add(campoFecha);
		formulario.add(new JLabel("Hora (0-23):"));
		formulario.add(comboHora);
		formulario.add(new JLabel("Cliente:"));
		formulario.add(comboCliente);
		formulario.add(new JLabel("Tipo de servicio:"));
		formulario.add(comboServicio);

		//ABAJO: botones
		JPanel panelButton = new JPanel(new FlowLayout());
		JButton botonCrear = createButton("CREAR");
		JButton botonModificar = createButton("MODIFICAR");
		JButton botonBorrar = createButton("BORRAR");
		JButton botonConfirmar = createButton("CONFIRMAR");
		JButton botonCalendario = createButton("CALENDARIO");
		JButton button1 = createButton("REGRESAR");
		JButton[] botones = { botonCrear, botonModificar, botonBorrar, botonConfirmar, botonCalendario, button1 };
		for (JButton b : botones) {
			b.setPreferredSize(new Dimension(115, 40));
			panelButton.add(b);
		}

		panel.add(mesage, BorderLayout.NORTH);
		panel.add(scroll, BorderLayout.CENTER);
		panel.add(formulario, BorderLayout.EAST);
		panel.add(panelButton, BorderLayout.SOUTH);

		// ----- ACCIONES -----

		// Al elegir una cita de la lista, se muestran sus datos en el formulario (CONSULTAR CITA)
		lista.addListSelectionListener(e -> {
			int pos = lista.getSelectedIndex();
			if (pos >= 0) {
				Cita cita = control.getCitas().get(pos);
				campoFecha.setText(cita.getFecha().toString());
				comboHora.setSelectedItem(cita.getHora());
				comboServicio.setSelectedItem(cita.getServicio());
				comboCliente.setSelectedItem(control.getClienteDeCita(cita));
			}
		});

		// CREAR
		botonCrear.addActionListener(e -> {
			LocalDate fecha = leerFecha();
			if (fecha == null) {
				return; // la fecha estaba mal escrita; leerFecha() ya avisó
			}
			int hora = (Integer) comboHora.getSelectedItem();
			Servicio servicio = (Servicio) comboServicio.getSelectedItem();
			Cliente cliente = (Cliente) comboCliente.getSelectedItem();

			boolean ok = control.crearCita(fecha, hora, servicio, cliente);
			mostrarResultado(ok);
			if (ok) {
				actualizarLista();
			}
		});

		// MODIFICAR (el cliente de una cita no se puede cambiar, solo fecha, hora y servicio)
		botonModificar.addActionListener(e -> {
			int pos = lista.getSelectedIndex();
			if (!hayCitaElegida(pos)) {
				return;
			}
			LocalDate fecha = leerFecha();
			if (fecha == null) {
				return;
			}
			int hora = (Integer) comboHora.getSelectedItem();
			Servicio servicio = (Servicio) comboServicio.getSelectedItem();

			boolean ok = control.modificarCita(fecha, hora, servicio, pos);
			mostrarResultado(ok);
			if (ok) {
				actualizarLista();
				lista.setSelectedIndex(pos);
			}
		});

		// BORRAR
		botonBorrar.addActionListener(e -> {
			int pos = lista.getSelectedIndex();
			if (!hayCitaElegida(pos)) {
				return;
			}
			int respuesta = JOptionPane.showConfirmDialog(this, "¿Seguro que desea borrar esta cita?", "Confirmar", JOptionPane.YES_NO_OPTION);
			if (respuesta == JOptionPane.YES_OPTION) {
				boolean ok = control.borrarCita(pos);
				mostrarResultado(ok);
				actualizarLista();
			}
		});

		// CONFIRMAR
		botonConfirmar.addActionListener(e -> {
			int pos = lista.getSelectedIndex();
			if (!hayCitaElegida(pos)) {
				return;
			}
			boolean ok = control.confirmarCita(pos);
			mostrarResultado(ok);
			actualizarLista();
			lista.setSelectedIndex(pos);
		});

		botonCalendario.addActionListener(e -> mostrarCalendario());

		button1.addActionListener(e -> volverAlMenu());

		this.add(panel);
		actualizarLista();
	}

	//CALENDARIO: semana o mes, empezando en la fecha escrita en el formulario -----
	private void mostrarCalendario() {
		String[] opciones = { "Semana", "Mes" };
		int eleccion = JOptionPane.showOptionDialog(this,
				"¿Cómo desea ver las citas?\n(Se empieza a contar desde la fecha escrita en el formulario)",
				"Ver calendario", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
		if (eleccion < 0) {
			return; // cerró la ventanita
		}
		LocalDate inicio = leerFecha();
		if (inicio == null) {
			return;
		}

		// eleccion == 1 significa "Mes"
		Map<LocalDate, List<Cita>> calendario = control.verCalendarioCitas(inicio, eleccion == 1);

		StringBuilder texto = new StringBuilder();
		if (calendario.isEmpty()) {
			texto.append("No hay citas en ese periodo.");
		}
		for (Map.Entry<LocalDate, List<Cita>> dia : calendario.entrySet()) {
			texto.append(dia.getKey()).append("\n");
			for (Cita c : dia.getValue()) {
				Cliente cliente = control.getClienteDeCita(c);
				String nombre = (cliente == null) ? "(sin cliente)" : cliente.getNombre();
				String estado = c.estaConfirmada() ? "CONFIRMADA" : "PENDIENTE";
				texto.append("    ").append(c.getHora()).append(":00  ").append(nombre).append(" - ")
						.append(c.getServicio().getTipoServicio()).append(" [").append(estado).append("]\n");
			}
			texto.append("\n");
		}
		JTextArea area = new JTextArea(texto.toString(), 18, 40);
		area.setEditable(false);
		JOptionPane.showMessageDialog(this, new JScrollPane(area), "Calendario de citas", JOptionPane.PLAIN_MESSAGE);
	}

	//Métodos de ayuda

	// Convierte el texto de la cajita en una fecha. Si está mal escrito, avisa y devuelve null.
	private LocalDate leerFecha() {
		try {
			return LocalDate.parse(campoFecha.getText().trim());
		} catch (DateTimeParseException ex) {
			JOptionPane.showMessageDialog(this, "Escriba la fecha con el formato AAAA-MM-DD, por ejemplo 2026-11-15.",
					"Fecha inválida", JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}

	private boolean hayCitaElegida(int pos) {
		if (pos < 0) {
			JOptionPane.showMessageDialog(this, "Seleccione una cita de la lista.");
			return false;
		}
		return true;
	}

	private void actualizarLista() {
		modeloLista.clear();
		for (int i = 0; i < control.getCitas().size(); i++) {
			modeloLista.addElement(control.consultarCita(i)); // texto de la cita número i
		}
	}

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