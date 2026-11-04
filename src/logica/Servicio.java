package logica;

import java.io.Serializable;

public class Servicio implements Serializable{
	private static final long serialVersionUID = 1L;
	private String tipoServicio;

	public Servicio(String tipoServicio) {
		this.tipoServicio = tipoServicio;
	}

	public String toString() {
		return "Servicio: " + tipoServicio;
	}
	
	public String getTipoServicio() { // buenas, Axel acá, le agregué esto para poder realizar la validación.
	    return tipoServicio;
	}
	
	public void setTipoServicio(String tipoServicio) {
	    this.tipoServicio = tipoServicio;
	}
}
