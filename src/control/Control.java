package control;

import logica.Cliente;
import logica.Cita;
import logica.Servicio;
import logica.HorarioAtencion;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/*
 * RESUMEN DE CAMBIOS PARA QUE NO SE ME PIERDAN JUGADORES
 * 		lo que se agregó está marcado con "NUEVO" o "CAMBIO"):
 *  - Los métodos que eran void ahora devuelven boolean (true = éxito, false = error).
 *  - Los mensajes ya no solo se imprimen: también se guardan en "ultimoMensaje"
 *    para que la interfaz los muestre en una ventanita (getUltimoMensaje()).
 *  - Getters nuevos para que la GUI llene sus listas.
 *  - Validaciones del enunciado que faltaban.
 *  - Mikel es la pura oshtia si o qué?
 */
public class Control {
    
   
    private static Control instancia;

    // Colecciones (Listas) 
    private List<Cliente> clientes;
    private List<Cliente> listaEspera;
    private List<Cita> citas;
    private List<HorarioAtencion> horarios;
    private List<Servicio> servicios;

    // NUEVO: aquí se guarda el texto del resultado de la última operación (éxito o error)
    private String ultimoMensaje = "";

    // Constructor privado para el Singleton
    private Control() {
        this.clientes = new ArrayList<>();
        this.listaEspera = new ArrayList<>();
        this.citas = new ArrayList<>();
        this.horarios = new ArrayList<>();
        this.servicios = new ArrayList<>();
    }

    // Método estático para obtener la única instancia
    public static Control getInstancia() {
        if (instancia == null) {
            instancia = new Control();
        }
        return instancia;
    }

    // NUEVO: MENSAJES Y GETTERS PARA LA INTERFAZ
    public String getUltimoMensaje() {
        return ultimoMensaje;
    }

    // Guarda (y sigue imprimiendo en consola) un mensaje de error; devuelve false
    private boolean fallo(String mensaje) {
        System.out.println(mensaje);
        this.ultimoMensaje = mensaje;
        return false;
    }

    // Guarda (y sigue imprimiendo en consola) un mensaje de éxito; devuelve true
    private boolean exito(String mensaje) {
        System.out.println(mensaje);
        this.ultimoMensaje = mensaje;
        return true;
    }

    public List<Cliente> getClientes() {
        return this.clientes;
    }

    public List<Servicio> getServicios() {
        return this.servicios;
    }

    public List<Cita> getCitas() {
        return this.citas;
    }

    // GESTIÓN DE CLIENTES
    public boolean crearCliente(String nombre, String email, String numero) {
    	// NUEVO: quitar espacios sobrantes y pedir un nombre
    	nombre = nombre.trim();
    	email = email.trim();
    	numero = numero.trim();
    	if (nombre.isEmpty()) {
            return fallo("Error: El nombre no puede estar vacío.");
        }
    	// NUEVO: el cliente no debe existir ya (se comprueba con el email)
    	if (existeEmail(email, null)) {
            return fallo("Error: El cliente ya existe (ya hay un cliente con ese correo).");
        }
    	// Validar formato antes de crear
        if (!esEmailValido(email)) {
            return fallo("Error: El correo electrónico no es válido.");
        }
        if (!esNumeroValido(numero)) {
            return fallo("Error: El número telefónico no es válido (solo números, paréntesis y guiones).");
        }
        
        // Crear y agregar el nuevo cliente a la lista
        Cliente nuevoCliente = new Cliente(nombre, email, numero);
        this.clientes.add(nuevoCliente);
        return exito("¡Cliente creado con éxito!");
    }

    public boolean modificarCliente(String email, String numero, int posicion) {
    	// Verificar que la posición sea válida dentro de la lista
        if (posicion >= 0 && posicion < this.clientes.size()) {
            Cliente clienteAModificar = this.clientes.get(posicion);
            email = email.trim();
            numero = numero.trim();
            
            // Actualizar datos si son válidos
            // CAMBIO: se revisan los dos datos ANTES de guardar; si alguno es inválido no se guarda ninguno
            if (!esEmailValido(email)) {
                return fallo("Error: El correo electrónico no es válido.");
            }
            if (existeEmail(email, clienteAModificar)) {
                return fallo("Error: Ese correo ya pertenece a otro cliente.");
            }
            if (!esNumeroValido(numero)) {
                return fallo("Error: El número telefónico no es válido (solo números, paréntesis y guiones).");
            }
            clienteAModificar.setEmail(email);
            clienteAModificar.setNumero(numero);
            return exito("¡Los datos fueron guardados correctamente!");
        } else {
            return fallo("Error: Posición de cliente inválida.");
        }
    }

    public boolean borrarCliente(int posicion) {
    	if (posicion >= 0 && posicion < this.clientes.size()) {
            Cliente clienteAEliminar = this.clientes.get(posicion);
            
            																										// No se puede borrar si tiene citas o está en lista de espera
            if (!verificarClienteSinCita(clienteAEliminar)) {
                return fallo("Error: No es posible eliminar al cliente porque tiene citas programadas.");
            }
            if (verificarClienteListaEspera(clienteAEliminar)) {
                return fallo("Error: No es posible eliminar al cliente porque se encuentra en la lista de espera.");
            }
            
            // NUEVO: si solo tenía citas pasadas, también se quitan de la lista general de citas
            this.citas.removeAll(clienteAEliminar.getCitas());
            this.clientes.remove(posicion);
            return exito("¡Cliente eliminado con éxito!");
        } else {
            return fallo("Error: Posición de cliente inválida para eliminar.");
        }
    }
    public Cliente consultarCliente(int posicion) {
    	if (posicion >= 0 && posicion < this.clientes.size()) {
            return this.clientes.get(posicion);
        }
        return null; // Si la posición no existe
    }

    //GESTIÓN DE CITAS 
    public boolean crearCita(LocalDate fecha, int hora, Servicio servicio, Cliente cliente) {
        // NUEVO: el cliente y el tipo de servicio deben existir
        if (cliente == null) {
            return fallo("Error: Debe elegir un cliente.");
        }
        if (!verificarServicio(servicio)) {
            return fallo("Error: Debe elegir un tipo de servicio que exista en el sistema.");
        }

        // Verificar que el horario esté disponible
        if (!verificarHorarioDisponible(fecha, hora)) {
            // CAMBIO: verificarHorarioDisponible ya dejó guardado el motivo exacto del error
            return false;
        }

        // Crear y agregar la nueva cita a la lista
        Cita nuevaCita = new Cita(fecha, hora, servicio);
        this.citas.add(nuevaCita);
        cliente.addCita(nuevaCita);
        return exito("¡Cita creada con éxito!");
    }

    public boolean modificarCita(LocalDate fecha, int hora, Servicio servicio, int pos) {
    	// NUEVO: citas.get(pos) lanza una excepción si la posición no existe, así que se revisa antes
    	if (pos < 0 || pos >= citas.size()) {
            return fallo("Error: No se encontró la cita.");
        }
    	// Buscar la cita asociada al cliente
        Cita citaAModificar = citas.get(pos);
        
        if (citaAModificar == null) {
            return fallo("Error: No se encontró una cita para este cliente.");
        }

        // NUEVO: el tipo de servicio elegido debe existir
        if (!verificarServicio(servicio)) {
            return fallo("Error: El tipo de servicio elegido no existe en el sistema.");
        }

        // Si cambió la fecha u hora, verificar que el nuevo espacio esté libre
        if (!citaAModificar.getFecha().equals(fecha) || citaAModificar.getHora() != hora) {
            if (!verificarHorarioDisponible(fecha, hora)) {
                // CAMBIO: el motivo exacto ya quedó guardado en ultimoMensaje
                return false;
            }
        }

        // Actualizar datos de la cita
        // CAMBIO: antes estas tres líneas estaban repetidas dentro del if; basta con hacerlo una vez aquí
        citaAModificar.setFecha(fecha);
        citaAModificar.setHora(hora);
        citaAModificar.setServicio(servicio);
        return exito("¡Cita modificada con éxito!");
    }

    public boolean borrarCita(int pos) {
    	// NUEVO: revisar que la posición exista
    	if (pos < 0 || pos >= citas.size()) {
            return fallo("Error: No se encontró una cita para eliminar con este cliente.");
        }
    	Cita citaAEliminar = citas.get(pos);
        if (citaAEliminar != null) {
            // NUEVO: también hay que quitarla de la lista de citas del cliente,
            // si no, ese cliente nunca se podría borrar
            Cliente dueno = getClienteDeCita(citaAEliminar);
            if (dueno != null) {
                dueno.getCitas().remove(citaAEliminar);
            }
            this.citas.remove(citaAEliminar);
            return exito("¡Cita eliminada con éxito!");
        } else {
            return fallo("Error: No se encontró una cita para eliminar con este cliente.");
        }
    }

    public String consultarCita(int pos) {
    	// CAMBIO: antes devolvía citas.get(pos).toString(); ahora arma un texto legible con todos los datos
    	if (pos < 0 || pos >= citas.size()) {
            return "Cita inexistente";
        }
    	Cita c = citas.get(pos);
    	Cliente cliente = getClienteDeCita(c);
    	String nombre = (cliente == null) ? "(sin cliente)" : cliente.getNombre();
    	String estado = c.estaConfirmada() ? "CONFIRMADA" : "PENDIENTE";
    	return c.getFecha() + " | " + c.getHora() + ":00 | " + nombre + " | "
    			+ c.getServicio().getTipoServicio() + " | " + estado;
    	
    }

    // NUEVO: la cita no guarda a su cliente (el cliente guarda sus citas), así que se busca así
    public Cliente getClienteDeCita(Cita cita) {
    	for (Cliente c : this.clientes) {
    		if (c.getCitas().contains(cita)) {
    			return c;
    		}
    	}
    	return null;
    }

    public boolean enviarNotificacionConfirmacion(Cita cita) { // Hay que revisar esto bien, al momento de enviar los correos
    	// Encontré unos metodos, pero no estoy seguro todavía.
    	
    	// CAMBIO: ahora recibe la cita a notificar y devuelve si se pudo enviar o no.
    	// PENDIENTE: aquí va el envío real del correo (JavaMail u otra biblioteca).
    	// Por ahora solo lo simula y siempre dice que funcionó.
    	Cliente cliente = getClienteDeCita(cita);
    	if (cliente == null) {
    		return fallo("Error: La cita no tiene un cliente asociado.");
    	}
    	return exito("(Simulado) Correo enviado a " + cliente.getEmail());
    }

    // NUEVO: citas de mañana, sin confirmar y dentro del horario de atención
    // (es la lista que pide el caso de uso "Enviar notificación de confirmación")
    public List<Cita> citasPorConfirmarManana() {
    	LocalDate manana = LocalDate.now().plusDays(1);
    	HorarioAtencion horario = getHorario(manana.getDayOfWeek());
    	List<Cita> pendientes = new ArrayList<>();

    	if (horario == null || horario.estaCerrado()) {
    		return pendientes; // lista vacía
    	}
    	for (Cita c : this.citas) {
    		if (c.getFecha().equals(manana) && !c.estaConfirmada() && horario.esHoraValida(c.getHora())) {
    			pendientes.add(c);
    		}
    	}
    	return pendientes;
    }
    	

    public Boolean confirmarCita(int pos) {
    	// NUEVO: revisar que la posición exista
    	if (pos < 0 || pos >= citas.size()) {
            return fallo("Error: No se encontró la cita.");
        }
    	Cita cita = citas.get(pos);
        if (cita != null) {
            // Aprovechamos el método confirmar() 
            // CAMBIO: confirmar() ahora devuelve false si la cita ya estaba confirmada
            if (cita.confirmar()) {
                return exito("¡Cita confirmada!");
            }
            return fallo("Error: Esa cita ya estaba confirmada.");
        }
        return false;
    }

    // CAMBIO: antes devolvía Map<LocalDate, Cita>, y si había dos citas el mismo día una
    // sobreescribía a la otra. Ahora cada fecha guarda una LISTA de citas, y se pide
    // desde qué fecha se quiere ver y si es por semana o por mes.
    public Map<LocalDate, List<Cita>> verCalendarioCitas(LocalDate inicio, boolean porMes) {
    	LocalDate fin = porMes ? inicio.plusMonths(1) : inicio.plusWeeks(1); // "fin" no se incluye
    	Map<LocalDate, List<Cita>> Calendario = new TreeMap<LocalDate, List<Cita>>(); // TreeMap = ordenado por fecha
    	for (Cita cita : citas) {
    		LocalDate fechaTemp = cita.getFecha();
    		if (!fechaTemp.isBefore(inicio) && fechaTemp.isBefore(fin)) {
    			if (!Calendario.containsKey(fechaTemp)) {
    				Calendario.put(fechaTemp, new ArrayList<Cita>());
    			}
    			Calendario.get(fechaTemp).add(cita);
    		}
    	}
    	// Ordenar las citas de cada día por hora
    	for (List<Cita> delDia : Calendario.values()) {
    		delDia.sort((a, b) -> Integer.compare(a.getHora(), b.getHora()));
    	}
    	return Calendario;
    }

    // GESTIÓN DE SERVICIOS
    public boolean crearServicio(String tipoServicio) {
    	if (tipoServicio == null || tipoServicio.trim().isEmpty()) {
            return fallo("Error: El tipo de servicio no puede estar vacío.");
        }
    	tipoServicio = tipoServicio.trim(); // NUEVO

        // Validar que no exista ya un servicio con el mismo nombre
        for (Servicio s : this.servicios) {
            if (s.getTipoServicio().equalsIgnoreCase(tipoServicio)) {
                return fallo("Error: Este tipo de servicio ya existe en el sistema.");
            }
        }

        Servicio nuevoServicio = new Servicio(tipoServicio);
        this.servicios.add(nuevoServicio);
        return exito("¡Tipo de servicio creado con éxito!");
    }

    public boolean borrarServicio(int posicion) {
    	if (posicion >= 0 && posicion < this.servicios.size()) {
            Servicio servicioAEliminar = this.servicios.get(posicion);

            //  No se puede eliminar si está siendo utilizado en alguna cita
            for (Cita c : this.citas) {
                if (c.getServicio() != null && c.getServicio().getTipoServicio().equalsIgnoreCase(servicioAEliminar.getTipoServicio())) {
                    return fallo("Error: No es posible eliminar el tipo de servicio porque está siendo utilizado en una cita programada.");
                }
            }

            this.servicios.remove(posicion);
            return exito("¡Tipo de servicio eliminado con éxito!");
        } else {
            return fallo("Error: Posición de servicio inválida para eliminar.");
        }
    }

    public boolean modificarServicio(int posicion, String tipoServicio) {
    	if (posicion >= 0 && posicion < this.servicios.size()) {
            if (tipoServicio == null || tipoServicio.trim().isEmpty()) {
                return fallo("Error: El nuevo nombre del servicio no puede estar vacío.");
            }
            tipoServicio = tipoServicio.trim(); // NUEVO
            
            Servicio servicioAModificar = this.servicios.get(posicion);
            // NUEVO: que no quede igual a otro servicio que ya existe
            for (Servicio s : this.servicios) {
                if (s != servicioAModificar && s.getTipoServicio().equalsIgnoreCase(tipoServicio)) {
                    return fallo("Error: Ya existe otro tipo de servicio con ese nombre.");
                }
            }
            servicioAModificar.setTipoServicio(tipoServicio);
            return exito("¡Tipo de servicio modificado con éxito!");
        } else {
            return fallo("Error: Posición de servicio inválida.");
        }
    }

    public Servicio consultarServicio(int posicion) {
    	if (posicion >= 0 && posicion < this.servicios.size()) {
            return this.servicios.get(posicion);
        }
        return null;
    }

    //  LISTA DE ESPERA Y HORARIOS
    public boolean agregarClienteListaEspera(int pos) {
        // NUEVO: revisar que la posición exista y que el cliente no esté ya en la lista
        if (pos < 0 || pos >= clientes.size()) {
            return fallo("Error: Seleccione un cliente.");
        }
        if (verificarClienteListaEspera(clientes.get(pos))) {
            return fallo("Error: Ese cliente ya está en la lista de espera.");
        }
        this.listaEspera.add(clientes.get(pos)); // queda en el último lugar
        return exito("¡Cliente agregado a la lista de espera!");
    }

    public boolean borrarClienteListaEspera(int posicion) {
    	// Verificar que la posición sea válida dentro de la lista de espera
        if (posicion >= 0 && posicion < this.listaEspera.size()) {
            // Remover al cliente 
            this.listaEspera.remove(posicion);
            return exito("¡Cliente eliminado de la lista de espera con éxito!");
        } else {
            return fallo("Error: La posición indicada no es válida en la lista de espera.");
        }
    }

    public List<Cliente> mostrarListaEspera() {
        return this.listaEspera;
    }

    public boolean establecerHorarioAtencion(DayOfWeek dia, int horaApertura, int horaCierre) {
    	// NUEVO: las horas deben estar entre 0 y 24
    	if (horaApertura < 0 || horaCierre > 24) {
            return fallo("Error: Las horas deben estar entre 0 y 24.");
        }
    	// Validar que la hora de cierre sea mayor o igual a la de apertura
        if (horaCierre < horaApertura) {
            return fallo("Error: La hora de cierre no puede ser menor a la de apertura.");
        }

        // Revisar si ya existe un horario registrado para ese día para actualizarlo
        HorarioAtencion horarioExistente = null;
        for (HorarioAtencion h : this.horarios) {
            if (h.getDia() == dia) {
                horarioExistente = h;
                break;
            }
        }

        if (horarioExistente != null) {
            // Si ya existe, actualizamos las horas
            horarioExistente.setHoraApertura(horaApertura);
            horarioExistente.setHoraCierre(horaCierre);
            return exito("¡Horario actualizado para el día " + dia + "!");
        } else {
            // Si no existe, creamos uno nuevo y lo agregamos a la lista
            HorarioAtencion nuevoHorario = new HorarioAtencion(dia, horaApertura, horaCierre);
            this.horarios.add(nuevoHorario);
            return exito("¡Horario establecido con éxito para el día " + dia + "!");
        }
    }

    // NUEVO: devuelve el horario de un día, o null si nunca se configuró
    public HorarioAtencion getHorario(DayOfWeek dia) {
        for (HorarioAtencion h : this.horarios) {
            if (h.getDia() == dia) {
                return h;
            }
        }
        return null;
    }

    public List<HorarioAtencion> consultarHorarioAtencion() {
        return this.horarios;
    }

    //  VALIDACIONES ---------------------------------------------------------------------------
    private Boolean verificarClienteSinCita(Cliente cliente) {
    	if (cliente == null) {
            return true; // Si el cliente es nulo, técnicamente no tiene cita
        }
    	// CAMBIO: el enunciado solo impide borrar si tiene citas en el FUTURO (hoy o después)
    	for (Cita c : cliente.getCitas()) {
    		if (!c.getFecha().isBefore(LocalDate.now()))
    			return false;
    	}
    	return true;
        
    }
    
    private Boolean verificarClienteListaEspera(Cliente cliente) {
    	if (cliente == null) {
            return false;
        }
        
        for (Cliente c : this.listaEspera) {
            if (c.equals(cliente)) {
                return true; // El cliente ya está en la lista de espera
            }
        }
        
        return false; // El cliente no está en la lista de espera
    }

    private Boolean verificarHorarioDisponible(LocalDate fecha, int hora) {
    	//  Obtener el día de la semana de la fecha
        DayOfWeek diaConsulta = fecha.getDayOfWeek();
        
        //  Buscar si existe un horario configurado para ese día
        HorarioAtencion horarioDelDia = null;
        for (HorarioAtencion h : this.horarios) {
            if (h.getDia() == diaConsulta) {
                horarioDelDia = h;
              
            }
        }
        
        // Si no hay horario ese día o está cerrado 
        if (horarioDelDia == null || horarioDelDia.estaCerrado()) {
            // CAMBIO: en vez de solo devolver false, se guarda el motivo para mostrarlo en pantalla
            return fallo("Error: La fecha se encuentra fuera del horario de atención (ese día la barbería está cerrada).");
        }
        
        //  Usar el método que ya viene en la clase HorarioAtencion
        if (!horarioDelDia.esHoraValida(hora)) {
            return fallo("Error: La hora no se encuentra dentro del horario de atención ("
                    + horarioDelDia.getHoraApertura() + ":00 a " + horarioDelDia.getHoraCierre() + ":00).");
        }
        
        //  Verificar que no exista otra cita a la misma hora en esa fecha
        for (Cita c : this.citas) {
            if (c.getFecha().equals(fecha) && c.getHora() == hora) {
                return fallo("Error: La hora ya se encuentra ocupada por otra cita."); // El espacio ya está ocupado
            }
        }
        
        // Si pasó todas las pruebas, si funciona
        return true;
    }
    

    private Boolean verificarServicio(Servicio servicio) {
    	//  Validar que el objeto servicio no sea nulo
        if (servicio == null || servicio.getTipoServicio() == null) {
            return false;
        }
        
        //  Recorrer la lista de servicios registrados en el sistema
        for (Servicio s : this.servicios) {
            // Comparamos ignorando mayúsculas/minúsculas para evitar errores 
            if (s.getTipoServicio().equalsIgnoreCase(servicio.getTipoServicio())) {
                return true; // El servicio sí existe
            }
        }
        
        // Si terminó el ciclo y no lo encontró, el servicio no es válido
        return false;
    }

    // NUEVO: ¿hay otro cliente (distinto de "ignorar") con ese email?
    private Boolean existeEmail(String email, Cliente ignorar) {
        for (Cliente c : this.clientes) {
            if (c != ignorar && c.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }
   

    private Boolean esEmailValido(String email) {
        // CAMBIO: antes solo pedía "@"; ahora pide algo + @ + algo + . + algo
        return email != null && email.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+");
    }

    private Boolean esNumeroValido(String numero) {
        // CAMBIO: además del largo mínimo, solo se aceptan números, paréntesis y guiones (según el enunciado)
        return numero != null && numero.length() >= 8 && numero.matches("[0-9()-]+");
    }
}