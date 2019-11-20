package es.ucm.fdi.ici.c1920.practica2.grupo06.maquinaestados;

import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class Perseguir extends StatePacman {

	@Override
	public MOVE move(Game game) {
		int pacmanIndex = game.getPacmanCurrentNodeIndex();
		
		GHOST nearestGh = PacmanUtils.getNearestChasingGhost(game,PacmanUtils.getLimit() , pacmanIndex);// devuelve el fantasma mas cercano a pacman
		return game.getNextMoveTowardsTarget(pacmanIndex, game.getGhostCurrentNodeIndex(nearestGh), DM.PATH);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Perseguir";
	}
	
	
}
