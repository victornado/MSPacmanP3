package es.ucm.fdi.ici.c1920.practica2.grupo06.maquinaestados;

import pacman.game.Game;
import pacman.game.Constants.MOVE;

public abstract class StatePacman {
	public abstract MOVE move(Game game);
	public abstract String toString();
}
