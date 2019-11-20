package es.ucm.fdi.ici.c1920.practica2.grupo06.maquinaestados;

import pacman.game.Game;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;

public class SelectState {
	private Game game;
	
	public SelectState(Game g) {
		this.game=g;
	}
	
	public int select() {
		int pacmanIndex = game.getPacmanCurrentNodeIndex();// actual nodo del pacman
		int nodoPowerPill=0;
		PacmanUtils.setLimit(30);
		GHOST nearestGh = PacmanUtils.getNearestChasingGhost(game, 30, pacmanIndex);// devuelve el fantasma mas cercano a pacman
		if (nearestGh != null) {// si hay un fantasma cerca
			if (game.isGhostEdible(nearestGh)) // Si es comestible
				return 0;//estado 0--->Perseguir Fantasma
			else {// si no es comestible
				// Obtengo el nodo de la pildora de poder mas cercana a pacman
				nodoPowerPill= game.getClosestNodeIndexFromNodeIndex(pacmanIndex, game.getActivePowerPillsIndices(),DM.PATH);
				// si hay una pildora de poder cerca y la pildora de poder no coincide con el fantasma, entonces ira a la pildora de poder
				if (PacmanUtils.isPacmanNearPowPill(game, pacmanIndex, 30) && nodoPowerPill != game.getGhostCurrentNodeIndex(nearestGh))
					return 1;//estado1--->moverse hacia la powerPill
				else // Si la pildora de poder esta lejos devuelve el movimiento de pacman lejos del fantasma
					return 2;
			}
		}
		else {// no hay un fantasma cerca
			PacmanUtils.setLimit(200);
			GHOST nGh1 = PacmanUtils.getNearestChasingGhost(game, 200, pacmanIndex);
			// Obtengo el nodo de la pildora de poder mas cercana a pacman
			nodoPowerPill= game.getClosestNodeIndexFromNodeIndex(pacmanIndex, game.getActivePowerPillsIndices(), DM.PATH);
			if (nGh1 != null) {//si hay un fantasma cerca
				if (game.isGhostEdible(nGh1)) {// Si es comestible
					return 0;
				}
			}
			PacmanUtils.setLimit(81);
			GHOST nGH2 = PacmanUtils.getNearestChasingGhost(game, 81, pacmanIndex);// devuelve el fantasma mas cercano a pacman
			//si no hay un fantasma poco lejos y quedan powerpills se aleja de la pildora de poder
			if (nGH2 == null && game.getActivePowerPillsIndices().length != 0) {
				return 3;
			}
			//si no hay un nearesgGh cerca
			//si no hay  nGHEdible cerca || 
			//si no hay un nGH cerca && si no quedan powerpills
			//se dirige a las pills normales
			return 4;
		}
	}
}
