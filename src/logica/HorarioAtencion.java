package logica;

import java.time.DayOfWeek;
import java.util.List;
import java.util.ArrayList;

public class HorarioAtencion {
	private DayOfWeek dia;
	private int horaApertura;
	private int horaCierre;
	public HorarioAtencion(DayOfWeek dia, int horaApertura, int horaCierre) {
		this.dia = dia;
		this.horaApertura = horaApertura;
		this.horaCierre = horaCierre;
	}
	public DayOfWeek getDia() {
		return dia;
	}
	public int getHoraApertura() {
		return horaApertura;
	}
	public void setHoraApertura(int horaApertura) {
		this.horaApertura = horaApertura;
	}
	public int getHoraCierre() {
		return horaCierre;
	}
	public void setHoraCierre(int horaCierre) {
		this.horaCierre = horaCierre;
	}
	public Boolean esHoraValida(int hora) {
		return hora >= horaApertura && hora <= horaCierre;
	}
	public Boolean estaCerrado() {
		// Los dias que esté cerrado se abre y se cierra a la misma hora, entonces devuelve true si se cumple esto.
		return horaApertura == horaCierre;
	}
	public List<Integer> obtenerHorasDia(){
		List<Integer> horas = new ArrayList<>();
		for (int h = horaApertura; h < horaCierre; h++) {
			horas.add(h);
		}
		return horas;
	}
	

}
