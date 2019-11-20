package es.ucm.fdi.ici.c1920.practica2.grupo06;

import java.util.Random;

import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public final class MsPacManRandom extends PacmanController {
	
	private Random rnd = new Random();
	private MOVE[] allMoves = MOVE.values();
	
	@Override
	public MOVE getMove(Game game, long timeDue) {
		MOVE[] norev = game.getPossibleMoves(game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade());
		return norev[rnd.nextInt(norev.length)];
	}

}
