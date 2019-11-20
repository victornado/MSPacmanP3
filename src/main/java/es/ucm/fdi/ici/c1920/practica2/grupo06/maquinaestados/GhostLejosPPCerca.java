package es.ucm.fdi.ici.c1920.practica2.grupo06.maquinaestados;

import pacman.game.Game;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;

public class GhostLejosPPCerca extends Transactions {
	private GHOST g =null;
	@Override
	public boolean check(Game input) {
		int pacmanIndex = input.getPacmanCurrentNodeIndex();// actual nodo del pacman
		GHOST nearestGh = PacmanUtils.getNearestChasingGhost(input, 30, pacmanIndex);// devuelve el fantasma mas cercano a pacman
		int nodoPowerPill=0;
		
		
		if (nearestGh == null) {// si no hay un fantasma cerca
			GHOST nGh1 = PacmanUtils.getNearestChasingGhost(input, 200, pacmanIndex);
			int nodoLair=input.getNumberOfNodes()-1;
			if (nGh1 != null && input.getGhostCurrentNodeIndex(nGh1)!=nodoLair) {
				PacmanUtils.setLimit(200);
				if (!input.isGhostEdible(nGh1)) { // Si no es comestible
					if(input.getNumberOfActivePowerPills()!=0) {//hay powerpill activas
						 nodoPowerPill= input.getClosestNodeIndexFromNodeIndex(pacmanIndex, input.getActivePowerPillsIndices(),DM.PATH);
						// si hay una pildora de poder cerca y la pildora de poder no coincide con el fantasma, entonces ira a la pildora de poder
						if (PacmanUtils.isPacmanNearPowPill(input, pacmanIndex, 30) && nodoPowerPill != input.getGhostCurrentNodeIndex(nGh1)) {
							g=nGh1;
							return true;//estado1--->moverse hacia la powerPill
						}
								
							
					 }
				}
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "GhostLejosPPCerca";
	}

	@Override
	public GHOST getGhost() {
		// TODO Auto-generated method stub
		return this.g;
	}

}
