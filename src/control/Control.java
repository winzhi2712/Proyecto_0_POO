package control;

import logica.Cliente;
import logica.Cita;
import logica.Servicio;
import logica.HorarioAtencion;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public class Control {
    
   
    private static Control instancia;

    // Colecciones (Listas) 
    private List<Cliente> clientes;
    private List<Cliente> listaEspera;
    private List<Cita> citas;
    private List<HorarioAtencion> horarios;
    private List<Servicio> servicios;

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

    // GESTIÓN DE CLIENTES
    public void crearCliente(String nombre, String email, String numero) {
    	// Validar formato antes de crear
        if (!esEmailValido(email)) {
            System.out.println("Error: El correo electrónico no es válido.");
            return;
        }
        if (!esNumeroValido(numero)) {
            System.out.println("Error: El número telefónico no es válido.");
            return;
        }
        
        // Crear y agregar el nuevo cliente a la lista
        Cliente nuevoCliente = new Cliente(nombre, email, numero);
        this.clientes.add(nuevoCliente);
    }

    public void modificarCliente(String email, String numero, int posicion) {
    	// Verificar que la posición sea válida dentro de la lista
        if (posicion >= 0 && posicion < this.clientes.size()) {
            Cliente clienteAModificar = this.clientes.get(posicion);
            
            // Actualizar datos si son válidos
            if (esEmailValido(email)) {
                clienteAModificar.setEmail(email);
            }
            if (esNumeroValido(numero)) {
                clienteAModificar.setNumero(numero);
            }
        } else {
            System.out.println("Error: Posición de cliente inválida.");
        }
    }

    public void borrarCliente(int posicion) {
    	if (posicion >= 0 && posicion < this.clientes.size()) {
            Cliente clienteAEliminar = this.clientes.get(posicion);
            
            // No se puede borrar si tiene citas o está en lista de espera
            if (!verificarClienteSinCita(clienteAEliminar)) {
                System.out.println("Error: No es posible eliminar al cliente porque tiene citas programadas.");
                return;
            }
            if (verificarClienteListaEspera(clienteAEliminar)) {
                System.out.println("Error: No es posible eliminar al cliente porque se encuentra en la lista de espera.");
                return;
            }
            
            this.clientes.remove(posicion);
            System.out.println("¡Cliente eliminado con éxito!");
        } else {
            System.out.println("Error: Posición de cliente inválida para eliminar.");
        }
    }
    public Cliente consultarCliente(int posicion) {
    	if (posicion >= 0 && posicion < this.clientes.size()) {
            return this.clientes.get(posicion);
        }
        return null; // Si la posición no existe
    }

    // --- GESTIÓN DE CITAS ---
    public void crearCita(LocalDate fecha, int hora, Servicio servicio, Cliente cliente) {
        // Verificar que el horario esté disponible
        if (!verificarHorarioDisponible(fecha, hora)) {
            System.out.println("Error: El horario seleccionado no está disponible o la barbería está cerrada.");
            return;
        }

        // Crear y agregar la nueva cita a la lista
        Cita nuevaCita = new Cita(fecha, hora, servicio);
        this.citas.add(nuevaCita);
        cliente.addCita(nuevaCita);
        System.out.println("¡Cita creada con éxito!");
    }

    public void modificarCita(LocalDate fecha, int hora, Servicio servicio, int pos) {
    	// Buscar la cita asociada al cliente
        Cita citaAModificar = citas.get(pos);
        
        if (citaAModificar == null) {
            System.out.println("Error: No se encontró una cita para este cliente.");
            return;
        }

        // Si cambió la fecha u hora, verificar que el nuevo espacio esté libre
        if (!citaAModificar.getFecha().equals(fecha) || citaAModificar.getHora() != hora) {
            if (!verificarHorarioDisponible(fecha, hora)) {
                System.out.println("Error: El nuevo horario no está disponible.");
                return;
            }
            
        citaAModificar.setFecha(fecha);
        citaAModificar.setHora(hora);
        citaAModificar.setServicio(servicio);
        }

        // Actualizar datos de la cita
        citaAModificar.setFecha(fecha);
        citaAModificar.setHora(hora);
        citaAModificar.setServicio(servicio);
        System.out.println("¡Cita modificada con éxito!");
    }

    public void borrarCita(int pos) {
    	Cita citaAEliminar = citas.get(pos);
        if (citaAEliminar != null) {
            this.citas.remove(citaAEliminar);
            System.out.println("¡Cita eliminada con éxito!");
        } else {
            System.out.println("Error: No se encontró una cita para eliminar con este cliente.");
        }
    }

    public String consultarCita(int pos) {
    	return citas.get(pos).toString();
    	
    }

    public void enviarNotificacionConfirmacion() { // Hay que revisar esto bien, al momento de enviar los correos
    	// Encontré unos metodos, pero no estoy seguro todavía.
    	
    }
    	

    public Boolean confirmarCita(int pos) {
    	Cita cita = citas.get(pos);
        if (cita != null) {
            // Aprovechamos el método confirmar() 
            cita.confirmar();
            return true;
        }
        return false;
    }

    public List<Cita> verCalendarioCitas() {
    	return this.citas;    
    }

    // GESTIÓN DE SERVICIOS
    public void crearServicio(String tipoServicio) {
    	if (tipoServicio == null || tipoServicio.trim().isEmpty()) {
            System.out.println("Error: El tipo de servicio no puede estar vacío.");
            return;
        }

        // Validar que no exista ya un servicio con el mismo nombre
        for (Servicio s : this.servicios) {
            if (s.getTipoServicio().equalsIgnoreCase(tipoServicio)) {
                System.out.println("Error: Este tipo de servicio ya existe en el sistema.");
                return;
            }
        }

        Servicio nuevoServicio = new Servicio(tipoServicio);
        this.servicios.add(nuevoServicio);
        System.out.println("¡Tipo de servicio creado con éxito!");
    }

    public void borrarServicio(int posicion) {
    	if (posicion >= 0 && posicion < this.servicios.size()) {
            Servicio servicioAEliminar = this.servicios.get(posicion);

            //  No se puede eliminar si está siendo utilizado en alguna cita
            for (Cita c : this.citas) {
                if (c.getServicio() != null && c.getServicio().getTipoServicio().equalsIgnoreCase(servicioAEliminar.getTipoServicio())) {
                    System.out.println("Error: No es posible eliminar el tipo de servicio porque está siendo utilizado en una cita programada.");
                    return;
                }
            }

            this.servicios.remove(posicion);
            System.out.println("¡Tipo de servicio eliminado con éxito!");
        } else {
            System.out.println("Error: Posición de servicio inválida para eliminar.");
        }
    }

    public void modificarServicio(int posicion, String tipoServicio) {
    	if (posicion >= 0 && posicion < this.servicios.size()) {
            if (tipoServicio == null || tipoServicio.trim().isEmpty()) {
                System.out.println("Error: El nuevo nombre del servicio no puede estar vacío.");
                return;
            }
            
            Servicio servicioAModificar = this.servicios.get(posicion);
            servicioAModificar.setTipoServicio(tipoServicio);
            System.out.println("¡Tipo de servicio modificado con éxito!");
        } else {
            System.out.println("Error: Posición de servicio inválida.");
        }
    }

    public Servicio consultarServicio(int posicion) {
    	if (posicion >= 0 && posicion < this.servicios.size()) {
            return this.servicios.get(posicion);
        }
        return null;
    }

    //  LISTA DE ESPERA Y HORARIOS ---
    public void agregarClienteListaEspera(Cliente cliente) {
        this.listaEspera.add(cliente);
    }

    public void borrarClienteListaEspera(int posicion) {
    	// Verificar que la posición sea válida dentro de la lista de espera
        if (posicion >= 0 && posicion < this.listaEspera.size()) {
            // Remover al cliente 
            this.listaEspera.remove(posicion);
            System.out.println("¡Cliente eliminado de la lista de espera con éxito!");
        } else {
            System.out.println("Error: La posición indicada no es válida en la lista de espera.");
        }
    }

    public List<Cliente> mostrarListaEspera() {
        return this.listaEspera;
    }

    public void establecerHorarioAtencion(DayOfWeek dia, int horaApertura, int horaCierre) {
    	// Validar que la hora de cierre sea mayor o igual a la de apertura
        if (horaCierre < horaApertura) {
            System.out.println("Error: La hora de cierre no puede ser menor a la de apertura.");
            return;
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
            System.out.println("¡Horario actualizado para el día " + dia + "!");
        } else {
            // Si no existe, creamos uno nuevo y lo agregamos a la lista
            HorarioAtencion nuevoHorario = new HorarioAtencion(dia, horaApertura, horaCierre);
            this.horarios.add(nuevoHorario);
            System.out.println("¡Horario establecido con éxito para el día " + dia + "!");
        }
    }

    public List<HorarioAtencion> consultarHorarioAtencion() {
        return this.horarios;
    }

    //  VALIDACIONES 
    public Boolean verificarClienteSinCita(Cliente cliente) {
    	if (cliente == null) {
            return true; // Si el cliente es nulo, técnicamente no tiene cita
        }
    	if (cliente.getCitas().size() == 0)
    		return true;
    	return false;
        
    }
    
    public Boolean verificarClienteListaEspera(Cliente cliente) {
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

    public Boolean verificarHorarioDisponible(LocalDate fecha, int hora) {
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
            return false;
        }
        
        //  Usar el método que ya viene en la clase HorarioAtencion
        if (!horarioDelDia.esHoraValida(hora)) {
            return false;
        }
        
        //  Verificar que no exista otra cita a la misma hora en esa fecha
        for (Cita c : this.citas) {
            if (c.getFecha().equals(fecha) && c.getHora() == hora) {
                return false; // El espacio ya está ocupado
            }
        }
        
        // Si pasó todas las pruebas, si funciona
        return true;
    }
    

    public Boolean verificarServicio(Servicio servicio) {
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
   

    public Boolean esEmailValido(String email) {
        return email != null && email.contains("@");
    }

    public Boolean esNumeroValido(String numero) {
        return numero != null && numero.length() >= 8;
    }
}
