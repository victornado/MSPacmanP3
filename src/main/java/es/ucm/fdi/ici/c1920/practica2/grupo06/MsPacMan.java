package es.ucm.fdi.ici.c1920.practica2.grupo06;
import es.ucm.fdi.ici.c1920.practica2.grupo06.maquinaestados.StateEngine;
import pacman.controllers.PacmanController;
import pacman.game.Game;
import pacman.game.Constants.*;

public class MsPacMan extends PacmanController {
	private MOVE move=MOVE.NEUTRAL;
	
	
	@Override
	public MOVE getMove(Game game, long timeDue) {
		move=StateEngine.getInstance().run(game);
		return move;
	}
}
