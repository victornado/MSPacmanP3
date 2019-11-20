package es.ucm.fdi.ici.c1920.practica2.grupo06.maquinaestados;

import pacman.game.Game;
import pacman.game.Constants.GHOST;

public class TheGhostIsLair extends Transactions {
	private GHOST g =null;
	@Override
	public boolean check(Game input) {
		// TODO Auto-generated method stub
		int pacmanIndex = input.getPacmanCurrentNodeIndex();// actual nodo del pacman
		GHOST nearestGh = PacmanUtils.getNearestChasingGhost(input, 200, pacmanIndex);// devuelve el fantasma mas cercano a pacman
		int nodoLair=input.getNumberOfNodes()-1;
		//Si hay un fantasma && si esta en la guarida
		if (nearestGh != null && input.getGhostCurrentNodeIndex(nearestGh)==nodoLair || input.getNumberOfActivePowerPills()==0) {
			g=nearestGh;
			return true;
		}
			
			
		return false;
	}
	
	public  GHOST getGhost() {
		return this.g;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "TheGhostIsLair";
	}

}
