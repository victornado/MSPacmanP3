package es.ucm.fdi.ici.c1920.practica1.grupo06;
import java.util.Random;

import pacman.controllers.PacmanController;
import pacman.game.Game;
import pacman.game.Constants.*;

public class MsPacMan extends PacmanController {
	@Override
	public MOVE getMove(Game game, long timeDue) {
		int limit = 30;
		int pacmanIndex = game.getPacmanCurrentNodeIndex();// actual nodo del pacman
		int nodoPowerPill=0;
		GHOST nearestGh = getNearestChasingGhost(game, limit, pacmanIndex);// devuelve el fantasma mas cercano a pacman
		if (nearestGh != null) {// si hay un fantasma cerca
			if (game.isGhostEdible(nearestGh)) {// Si es comestible
				// devuelve movimiento de pacman hacia el fantasma
				return game.getNextMoveTowardsTarget(pacmanIndex, game.getGhostCurrentNodeIndex(nearestGh), DM.PATH);
			} else {// si no es comestible
				// Obtengo el nodo de la pildora de poder mas cercana a pacman
				nodoPowerPill= game.getClosestNodeIndexFromNodeIndex(pacmanIndex, game.getActivePowerPillsIndices(),DM.PATH);
				// si hay una pildora de poder cerca y la pildora de poder no coincide con el fantasma, entonces ira a la pildora de poder
				if (isPacmanNearPowPill(game, pacmanIndex, limit) && nodoPowerPill != game.getGhostCurrentNodeIndex(nearestGh)) {
					// devuelve el movimiento hacia la pildora de poder
					return game.getNextMoveTowardsTarget(pacmanIndex, nodoPowerPill, DM.PATH);
				} else {// Si la pildora de poder esta lejos devuelve el movimiento de pacman lejos del fantasma
					//devuelve los nodos vecinos sin tener en cuenta el opuesto
					int[] vecinos= game.getNeighbouringNodes(pacmanIndex, game.getPacmanLastMoveMade()) ;
					//MOVE mvs =game.getNextMoveAwayFromTarget(pacmanIndex, game.getGhostCurrentNodeIndex(nearestGh),DM.EUCLID);
					//De todos los nodos vecinos a pacman selecciona el nodo mas lejano al fantasma
					int nodoFarthest=game.getFarthestNodeIndexFromNodeIndex(game.getGhostCurrentNodeIndex(nearestGh), vecinos, DM.EUCLID);
					//realiza el movimiento segun el nodo mas lejos del fantasma
					MOVE mvs=game.getNextMoveTowardsTarget(pacmanIndex,nodoFarthest,DM.PATH);
					return mvs;
				}
			}
		} else {// no hay un fantasma cerca
			GHOST nGhEdible = getNearestChasingGhost(game, 100, pacmanIndex);
			// Obtengo el nodo de la pildora de poder mas cercana a pacman
			nodoPowerPill= game.getClosestNodeIndexFromNodeIndex(pacmanIndex, game.getActivePowerPillsIndices(), DM.PATH);
			if (nGhEdible != null) {//si hay un fantasma cerca
				if (game.isGhostEdible(nGhEdible)) {// Si es comestible
					// devuelve movimiento de pacman hacia el fantasma
					return game.getNextMoveTowardsTarget(pacmanIndex, game.getGhostCurrentNodeIndex(nGhEdible),DM.PATH);
				}
			}
			GHOST nGH = getNearestChasingGhost(game, 81, pacmanIndex);// devuelve el fantasma mas cercano a pacman
			//si no hay un fantasma cerca y quedan powerpills se aleja de la pildora de poder
			if (nGH == null && game.getActivePowerPillsIndices().length != 0)
				return game.getNextMoveAwayFromTarget(pacmanIndex, nodoPowerPill, DM.PATH);
			
			//si no hay un nearesgGhEdible cerca, si hay un fantasma gh cerca || si no hay un gh cerca && si no quedan powerpills
			//se dirige a las pills normales
			return game.getNextMoveTowardsTarget(pacmanIndex, getNearestPill(game, pacmanIndex), DM.PATH);
		}
	}


	private int getNearestPill(Game game, int pacmanIndex) {
		int[] pillsIndices = game.getActivePillsIndices();// indice de todas las pills
		return game.getClosestNodeIndexFromNodeIndex(pacmanIndex, pillsIndices, DM.MANHATTAN);// selecciona la pill mas
																								// // cercana
	}

	private GHOST getNearestChasingGhost(Game game, double limit, int pacmanIndex) {
		GHOST minGh = null;
		double minDist = limit;
		for (GHOST gh : GHOST.values()) {
			double dist = game.getDistance(pacmanIndex, game.getGhostCurrentNodeIndex(gh), DM.MANHATTAN);
			if (dist <= minDist) {
				minDist = dist;
				minGh = gh;
			}
		}
		return minGh;
	}
	
	private boolean isPacmanNearPowPill(Game game, int indexPacman, int limit) {
		int[] PowPillIndexes = game.getActivePowerPillsIndices();
		boolean near = false;
		int i = 0;
		while (!near && i < PowPillIndexes.length) {
			near = (game.getDistance(indexPacman, PowPillIndexes[i], DM.MANHATTAN) <= limit);
			i++;
		}
		return near;
	}
}
