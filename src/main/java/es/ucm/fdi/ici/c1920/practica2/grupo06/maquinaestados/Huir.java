package es.ucm.fdi.ici.c1920.practica2.grupo06.maquinaestados;

import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class Huir extends StatePacman {


	@Override
	public MOVE move(Game game) {
		//int cerca=30;
		int pacmanIndex = game.getPacmanCurrentNodeIndex();// actual nodo del pacman
		GHOST nearestGh = PacmanUtils.getNearestChasingGhost(game, 200, pacmanIndex);// devuelve el fantasma mas cercano a pacman
		//devuelve los nodos vecinos sin tener en cuenta el opuesto
		int[] vecinos= game.getNeighbouringNodes(pacmanIndex, game.getPacmanLastMoveMade()) ;
		//De todos los nodos vecinos a pacman selecciona el nodo mas lejano al fantasma
		int nodoFarthest=game.getFarthestNodeIndexFromNodeIndex(game.getGhostCurrentNodeIndex(nearestGh), vecinos, DM.EUCLID);
		//realiza el movimiento segun el nodo mas lejos del fantasma
		MOVE mvs=game.getNextMoveTowardsTarget(pacmanIndex,nodoFarthest,DM.PATH);
		return mvs;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Huir";
	}
}
