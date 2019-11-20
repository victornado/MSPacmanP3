package es.ucm.fdi.ici.c1920.practica2.grupo06.maquinaestados;

import pacman.game.Constants.GHOST;
import pacman.game.Game;

public class GhostEdible extends Transactions{
	private GHOST g =null;
	@Override
	public boolean check(Game input) {
		PacmanUtils.setLimit(200);
		GHOST nearestGh=PacmanUtils.getNearestChasingGhost(input, 200, input.getPacmanCurrentNodeIndex());
		if(input.isGhostEdible(nearestGh)) {
			g=nearestGh;
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "GhostEdible";
	}

	@Override
	public GHOST getGhost() {
		// TODO Auto-generated method stub
		return this.g;
	}

}
