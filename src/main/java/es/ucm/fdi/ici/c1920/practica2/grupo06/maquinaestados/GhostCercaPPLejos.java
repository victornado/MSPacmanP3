package es.ucm.fdi.ici.c1920.practica2.grupo06.maquinaestados;

import pacman.game.Game;
import pacman.game.Constants.GHOST;

public class GhostCercaPPLejos extends Transactions{
	private GHOST g =null;
	@Override
	public  boolean check(Game input) {
		int nodoPowerPill=0;
		int pacmanIndex = input.getPacmanCurrentNodeIndex();// actual nodo del pacman
		GHOST nearestGh = PacmanUtils.getNearestChasingGhost(input, 30, pacmanIndex);// devuelve el fantasma mas cercano a pacman
		int nodoLair=input.getNumberOfNodes()-1;
		// si hay un fantasma cerca && no esta en la guarida
		if (nearestGh != null && input.getGhostCurrentNodeIndex(nearestGh)!=nodoLair) {
			PacmanUtils.setLimit(30);
			if (!input.isGhostEdible(nearestGh)) {// Si no es comestible
				 if(input.getNumberOfActivePowerPills()!=0) {//hay powerpill activas
					 //Si la power pill no esta cerca 
					 if (!PacmanUtils.isPacmanNearPowPill(input, pacmanIndex, 30) && nodoPowerPill == input.getGhostCurrentNodeIndex(nearestGh)) {
						 g=nearestGh;
					 }
							
						 return true;
				 }
			}
		}
		return false;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "GhostCercaPPLejos";
	}

	@Override
	public GHOST getGhost() {
		// TODO Auto-generated method stub
		return this.g;
	}

}
