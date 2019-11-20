package es.ucm.fdi.ici.c1920.practica2.grupo06.maquinaestados;

import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class moveToPill extends StatePacman {

	@Override
	public MOVE move(Game game) {
		int pacmanIndex = game.getPacmanCurrentNodeIndex();// actual nodo del pacman
		return game.getNextMoveTowardsTarget(pacmanIndex,PacmanUtils.getNearestPill(game, pacmanIndex), DM.PATH);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "moveToPill";
	}
	


}
