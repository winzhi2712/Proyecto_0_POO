package GUI;

import java.awt.*;
import java.time.DayOfWeek;
import java.util.List;
import javax.swing.*;

import control.Control;
import logica.Cita;
import logica.Cliente;
import logica.HorarioAtencion;
import logica.Servicio;

public class JFrameAdmin extends JFrame {

	private Control control = Control.getInstancia();

	//Pestaña de servicios (a veces me digo para qué comentar tanto, el profe entiende, mis compadres son 
	//personajes principales, ni modo... que sea todo muy Veni Vidi Vici)
	
	private DefaultListModel<Servicio> modeloServicios = new DefaultListModel<>();
	private JList<Servicio> listaServicios = new JList<>(modeloServicios);
	private JTextField campoServicio = new JTextField(20);

	//Pestaña de horario (una fila por cada día de la semana)
	private DayOfWeek[] dias = DayOfWeek.values(); // LUNES ... DOMINGO, en ese orden
	private String[] nombresDias = { "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo" };
	private JTextField[] camposApertura = new JTextField[7];
	private JTextField[] camposCierre = new JTextField[7];

	//Pestaña de notificaciones
	private JTextArea areaPendientes = new JTextArea();

	public JFrameAdmin() {
		initialize();
	}

	public void initialize() {
		this.setTitle("SECCION DE LA ADMINISTRACION");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setSize(800, 500);
		this.setLocationRelativeTo(null);

		JPanel panel = new JPanel(new BorderLayout());
		JLabel mesage = new JLabel("SECCION DE LA ADMINISTRACION", JLabel.CENTER);

		// Armar las pestañas
		JTabbedPane pestanas = new JTabbedPane();
		pestanas.addTab("Tipos de servicio", crearPanelServicios());
		pestanas.addTab("Horario de atención", crearPanelHorario());
		pestanas.addTab("Notificaciones", crearPanelNotificaciones());

		JButton button1 = createButton("REGRESAR");
		button1.setPreferredSize(new Dimension(120, 40));
		JPanel panelButton = new JPanel(new FlowLayout());
		panelButton.add(button1);

		panel.add(mesage, BorderLayout.NORTH);
		panel.add(pestanas, BorderLayout.CENTER);
		panel.add(panelButton, BorderLayout.SOUTH);

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
	}

	//--------------------------------------------------------------------
	// PESTAÑA 1: TIPOS DE SERVICIO
	//-------------------------------------------------------------------
	private JPanel crearPanelServicios() {
		JPanel p = new JPanel(new BorderLayout());
		listaServicios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JPanel abajo = new JPanel(new FlowLayout());
		JButton botonCrear = createButton("CREAR");
		JButton botonModificar = createButton("MODIFICAR");
		JButton botonBorrar = createButton("BORRAR");
		abajo.add(new JLabel("Descripción:"));
		abajo.add(campoServicio);
		abajo.add(botonCrear);
		abajo.add(botonModificar);
		abajo.add(botonBorrar);

		p.add(new JScrollPane(listaServicios), BorderLayout.CENTER);
		p.add(abajo, BorderLayout.SOUTH);

		// Al elegir un servicio, su nombre aparece en la cajita (CONSULTAR TIPO DE SERVICIO)
		listaServicios.addListSelectionListener(e -> {
			Servicio s = control.consultarServicio(listaServicios.getSelectedIndex());
			if (s != null) {
				campoServicio.setText(s.getTipoServicio());
			}
		});

		botonCrear.addActionListener(e -> {
			boolean ok = control.crearServicio(campoServicio.getText());
			mostrarResultado(ok);
			if (ok) {
				campoServicio.setText("");
				actualizarServicios();
			}
		});

		botonModificar.addActionListener(e -> {
			boolean ok = control.modificarServicio(listaServicios.getSelectedIndex(), campoServicio.getText());
			mostrarResultado(ok);
			actualizarServicios();
		});

		botonBorrar.addActionListener(e -> {
			boolean ok = control.borrarServicio(listaServicios.getSelectedIndex());
			mostrarResultado(ok);
			if (ok) {
				campoServicio.setText("");
			}
			actualizarServicios();
		});

		actualizarServicios();
		return p;
	}

	private void actualizarServicios() {
		modeloServicios.clear();
		for (Servicio s : control.getServicios()) {
			modeloServicios.addElement(s); // se muestra el toString() de Servicio
		}
	}

	//---------------------------------------------------------------------
	// PESTAÑA 2: HORARIO DE ATENCIÓN
	// --------------------------------------------------------------------
	private JPanel crearPanelHorario() {
		JPanel p = new JPanel(new BorderLayout());

		p.add(new JLabel("Escriba horas enteras (0 a 24). Si apertura y cierre son iguales, ese día está CERRADO.",
				JLabel.CENTER), BorderLayout.NORTH);

		// Tabla de 8 filas x 3 columnas: encabezado + 7 días
		JPanel tabla = new JPanel(new GridLayout(8, 3, 10, 10));
		tabla.add(new JLabel("Día"));
		tabla.add(new JLabel("Hora de apertura"));
		tabla.add(new JLabel("Hora de cierre"));

		for (int i = 0; i < 7; i++) {
			camposApertura[i] = new JTextField();
			camposCierre[i] = new JTextField();

			// Mostrar el horario ya guardado (si no hay, queda 0 y 0 = cerrado)
			HorarioAtencion h = control.getHorario(dias[i]);
			camposApertura[i].setText(h == null ? "0" : String.valueOf(h.getHoraApertura()));
			camposCierre[i].setText(h == null ? "0" : String.valueOf(h.getHoraCierre()));

			tabla.add(new JLabel(nombresDias[i]));
			tabla.add(camposApertura[i]);
			tabla.add(camposCierre[i]);
		}

		JButton botonGuardar = createButton("GUARDAR HORARIO");
		botonGuardar.setPreferredSize(new Dimension(180, 40));
		JPanel abajo = new JPanel(new FlowLayout());
		abajo.add(botonGuardar);

		botonGuardar.addActionListener(e -> guardarHorario());

		p.add(tabla, BorderLayout.CENTER);
		p.add(abajo, BorderLayout.SOUTH);
		return p;
	}

	private void guardarHorario() {
		for (int i = 0; i < 7; i++) {
			try {
				// Integer.parseInt convierte el texto en número; falla si escribieron letras
				int apertura = Integer.parseInt(camposApertura[i].getText().trim());
				int cierre = Integer.parseInt(camposCierre[i].getText().trim());
				if (!control.establecerHorarioAtencion(dias[i], apertura, cierre)) {
					JOptionPane.showMessageDialog(this, nombresDias[i] + ": " + control.getUltimoMensaje(), "Error",
							JOptionPane.ERROR_MESSAGE);
					return; // se detiene en el primer error (los días anteriores ya quedaron guardados)
				}
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(this, nombresDias[i] + ": escriba solo números enteros.", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		JOptionPane.showMessageDialog(this, "Horario guardado correctamente.");
	}


	// PESTAÑA 3: NOTIFICACIONES DE CONFIRMACIÓN

	private JPanel crearPanelNotificaciones() {
		JPanel p = new JPanel(new BorderLayout());
		areaPendientes.setEditable(false);

		JPanel abajo = new JPanel(new FlowLayout());
		JButton botonActualizar = createButton("ACTUALIZAR LISTA");
		JButton botonEnviar = createButton("ENVIAR CORREOS");
		botonActualizar.setPreferredSize(new Dimension(160, 40));
		botonEnviar.setPreferredSize(new Dimension(160, 40));
		abajo.add(botonActualizar);
		abajo.add(botonEnviar);

		p.add(new JLabel("Citas de mañana que todavía no han sido confirmadas:", JLabel.CENTER), BorderLayout.NORTH);
		p.add(new JScrollPane(areaPendientes), BorderLayout.CENTER);
		p.add(abajo, BorderLayout.SOUTH);

		botonActualizar.addActionListener(e -> cargarPendientes());
		botonEnviar.addActionListener(e -> enviarConfirmaciones());

		cargarPendientes();
		return p;
	}

	private void cargarPendientes() {
		List<Cita> pendientes = control.citasPorConfirmarManana();
		if (pendientes.isEmpty()) {
			areaPendientes.setText("No hay citas programadas para el día siguiente.");
			return;
		}
		StringBuilder texto = new StringBuilder();
		for (Cita c : pendientes) {
			Cliente cliente = control.getClienteDeCita(c);
			texto.append(c.getHora()).append(":00  ").append(cliente.getNombre()).append(" (")
					.append(cliente.getEmail()).append(") - ").append(c.getServicio().getTipoServicio()).append("\n");
		}
		areaPendientes.setText(texto.toString());
	}

	// Envía un correo por cada cita. Si uno falla, el usuario decide: reintentar, continuar o abortar.
	private void enviarConfirmaciones() {
		List<Cita> pendientes = control.citasPorConfirmarManana();
		if (pendientes.isEmpty()) {
			JOptionPane.showMessageDialog(this, "No hay citas programadas para el día siguiente.", "Aviso",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		String[] opciones = { "Reintentar", "Continuar", "Abortar" };

		for (Cita cita : pendientes) {
			boolean enviado = control.enviarNotificacionConfirmacion(cita);

			while (!enviado) { // mientras falle, preguntar qué hacer
				int eleccion = JOptionPane.showOptionDialog(this,
						"No se pudo enviar el correo: " + control.getUltimoMensaje() + "\n¿Qué desea hacer?",
						"Error de envío", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, opciones, opciones[0]);

				if (eleccion == 0) {
					enviado = control.enviarNotificacionConfirmacion(cita); // reintentar
				} else if (eleccion == 1) {
					break; // continuar con la siguiente cita
				} else {
					return; // abortar (también si cierran la ventanita)
				}
			}
		}
		JOptionPane.showMessageDialog(this, "Proceso de envío terminado.");
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