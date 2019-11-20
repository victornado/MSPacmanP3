package es.ucm.fdi.ici.c1920.practica2.grupo06.maquinaestados;

import pacman.game.Constants.DM;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class HuirPowerPill extends StatePacman {

	@Override
	public MOVE move(Game game) {
		int pacmanIndex = game.getPacmanCurrentNodeIndex();// actual nodo del pacman
		// Obtengo el nodo de la pildora de poder mas cercana a pacman
		int nodoPowerPill= game.getClosestNodeIndexFromNodeIndex(pacmanIndex, game.getActivePowerPillsIndices(), DM.PATH);
		return game.getNextMoveAwayFromTarget(pacmanIndex, nodoPowerPill, DM.PATH);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "HuirPowerPill";
	}

}
