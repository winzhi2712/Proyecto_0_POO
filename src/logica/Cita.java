package logica;

import java.time.LocalDate;
import java.io.Serializable;

public class Cita implements Serializable {
	private static final long serialVersionUID = 1L;
	private LocalDate fecha;
	private int hora;
	private Servicio servicio;
	private boolean confirmacion;
	
	public Cita(LocalDate fecha, int hora, Servicio servicio) {
		this.fecha = fecha;
		this.hora = hora;
		this.servicio = servicio;
		this.confirmacion = false;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public int getHora() {
		return hora;
	}

	public void setHora(int hora) {
		this.hora = hora;
	}

	public Servicio getServicio() {
		return servicio;
	}

	public void setServicio(Servicio servicio) {
		this.servicio = servicio;
	}
	
	//Mikel was here bitches
	public boolean confirmar() {
	    if (confirmacion) {
	        return false;      
	    }
	    confirmacion = true; 
	    return true;
	}

	public boolean estaConfirmada() {
	    return confirmacion;
	}
}
