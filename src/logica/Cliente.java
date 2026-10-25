package logica;

import java.util.LinkedList;
import java.util.List;

public class Cliente {
	private String nombre;
	private String email;
	private String numero;
	private List<Cita> citas;
	
	public Cliente(String nombre, String email, String numero) {
		this.nombre = nombre;
		this.email = email;
		this.numero = numero;
		this.citas = new LinkedList<>();
	}


	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getNumero() {
		return numero;
	}


	public void setNumero(String numero) {
		this.numero = numero;
	}
	
	public List<Cita> getCitas() {
		return citas;
	}
	public void addCita(Cita cita) {
		citas.add(cita);
	}
	public void removeCita(int index) {
		citas.remove(index);
	}
}
