package es.ucm.fdi.ici.c1920.practica2.grupo06;

public class Pair<T1, T2> {
	
	private T1 first;
	private T2 second;
	
	public Pair(T1 first, T2 second) {
		super();
		this.first = first;
		this.second = second;
	}
	
	public T1 first() {
		return first;
	}
	
	public T2 second() {
		return second;
	}
	public T1 getKey() {
		return first;
	}
	
	public T2 getValue() {
		return second;
	}
	
}
