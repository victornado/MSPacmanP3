package es.ucm.fdi.ici.c1920.practica3.grupo06;

import pacman.game.Game;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;

public class PacmanUtils {
	
	public static final int cerca=40;
	public static final int lejos=80;
	
	public static GHOST getNearestChasingGhost(Game game, double limit, int pacmanIndex) {
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
	
	public static boolean isPacmanNearPowPill(Game game, int indexPacman, int limit) {

		int[] PowPillIndexes = game.getActivePowerPillsIndices();
		boolean near = false;
		int i = 0;
		while (!near && i < PowPillIndexes.length) {
			near = (game.getDistance(indexPacman, PowPillIndexes[i], DM.MANHATTAN) <= limit);
			i++;
		}
		return near;
	}
	
	public static int getNearestPill(Game game, int pacmanIndex) {
		int[] pillsIndices = game.getActivePillsIndices();// indice de todas las pills
		return game.getClosestNodeIndexFromNodeIndex(pacmanIndex, pillsIndices, DM.MANHATTAN);// selecciona la pill mas
																								// // cercana
	}
}
