package logica;

public class Cliente {
	private String nombre;
	private String email;
	private String numero;
	
	
	public Cliente(String nombre, String email, String numero) {
		this.nombre = nombre;
		this.email = email;
		this.numero = numero;
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
	
	
}
