package es.ucm.fdi.ici.c1920.practica2.grupo06.maquinaestados;

import pacman.game.Game;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;

public class GhostLejosPPLejos extends Transactions {
	private GHOST g =null;
	@Override
	public boolean check(Game input) {
		int nodoPowerPill=0;
		int pacmanIndex = input.getPacmanCurrentNodeIndex();
		GHOST nearestGh = PacmanUtils.getNearestChasingGhost(input, 30, pacmanIndex);// devuelve el fantasma mas cercano a pacman
		if (nearestGh == null) {// si no hay un fantasma cerca
			GHOST nGh1 = PacmanUtils.getNearestChasingGhost(input, 200, pacmanIndex);
			int nodoLair=input.getNumberOfNodes()-1;
			//si hay un fantasma lejos y no esta en la guarida
			if (nGh1 != null && input.getGhostCurrentNodeIndex(nGh1)!=nodoLair) {
				PacmanUtils.setLimit(200);
				if (!input.isGhostEdible(nGh1)) { // Si no es comestible
					if(input.getNumberOfActivePowerPills()!=0) {//hay powerpill activas
						nodoPowerPill= input.getClosestNodeIndexFromNodeIndex(pacmanIndex, input.getActivePowerPillsIndices(),DM.PATH);
						// si hay una pildora de poder lejos y la pildora de poder no coincide con el fantasma, entonces ira a la pildora de poder
						if (!PacmanUtils.isPacmanNearPowPill(input, pacmanIndex, 30) && nodoPowerPill != input.getGhostCurrentNodeIndex(nGh1)) {
							g=nGh1;
							return true;
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
		return "GhostLejosPPLejos";
	}

	@Override
	public GHOST getGhost() {
		// TODO Auto-generated method stub
		return this.g;
	}

}
