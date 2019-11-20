package es.ucm.fdi.ici.c1920.practica1.grupo06;

import java.util.EnumMap;
import java.util.Random;

import pacman.controllers.GhostController;
import pacman.game.Constants.DM;
import pacman.game.Game;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

public class GhostsAggresive extends GhostController {
	private EnumMap<GHOST, MOVE> moves = new EnumMap<GHOST, MOVE>(GHOST.class);
	private MOVE[] allMoves = MOVE.values();
	private Random rnd = new Random();
	
	@Override
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		moves.clear();
		int indexPacman = game.getPacmanCurrentNodeIndex();
		for (GHOST ghostType : GHOST.values()) {
			
			if (game.doesGhostRequireAction(ghostType)) {
				int indexGhost = game.getGhostCurrentNodeIndex(ghostType);
				MOVE lastmove = game.getGhostLastMoveMade(ghostType);
				moves.put(ghostType, game.getNextMoveTowardsTarget(indexGhost, indexPacman, lastmove, DM.MANHATTAN));
			}
		}
		return moves;
	}
}