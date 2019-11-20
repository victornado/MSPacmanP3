package es.ucm.fdi.ici.c1920.practica2.grupo06.maquinaestados;

import pacman.game.Game;
import pacman.game.Constants.GHOST;

public abstract class Transactions {
	public abstract boolean check(Game input);
	public abstract String toString();
	public abstract  GHOST getGhost();
}
