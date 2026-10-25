package logica;

import java.time.LocalDate;

public class Cita {

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
	
	public boolean confirmar() {
		return !confirmacion;
	}
}
