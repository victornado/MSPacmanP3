package es.ucm.fdi.ici.c1920.practica3.grupo06;


import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;

import pacman.controllers.GhostController;
import pacman.game.Game;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

public class GhostsOld extends GhostController {
	
	private EnumMap<GHOST, MOVE> moves = new EnumMap<GHOST, MOVE>(GHOST.class);
	private Random rnd = new Random();
	private MOVE[] allMoves = MOVE.values();
	
	private int ticks = 0; //cada vez que no veo a pacman, + 1
	private int lastNActivePowPill = 4;//nº de Pow Pill activas en el tick anterior
	
	private int lastSeePacman = -1; //última pos donde vi a pacman.
	private int lastEdibleGhosts = 0; //nº de fantasmas comestibles en el tick anterior
	private int lastMaze = -1; //index del anterior maze
	private MOVE lastMovePacman = null;
	
	//Posiciones importantes de los maze, en orden: nw, ne, sw, se, pipe1i, pipe2i, pipe1d, pipe2d.
	private int[][] mazeIndexes = {{0, 78, 1191, 1291}, 
			{0, 86, 1217, 1317}, 
			{0, 78, 1300, 1378}, 
			{0, 100, 1218, 1307}};
	
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
     	
		
		
		System.out.println(game.getPacmanLastMoveMade());
		
		
		if (lastMaze == -1) {
			lastMaze = game.getMazeIndex();
		}
		
		if (lastMaze != game.getMazeIndex())
			init(game);
		
		int pacmanIndex = getPacmanLastIndex(game);
		
		for (GHOST gh: GHOST.values()) {
			moves.put(gh, game.getNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(gh), pacmanIndex, DM.PATH));
			//System.out.println(gh.name() + " --> " + game.getGhostCurrentNodeIndex(gh));
		}
		
		return moves;
	}


	private void init(Game game) {
		ticks = 0; //cada vez que no veo a pacman, + 1
		lastSeePacman = game.getPacManInitialNodeIndex(); //última pos donde vi a pacman.
		lastEdibleGhosts = 0; //nº de fantasmas comestibles en el tick anterior
		lastNActivePowPill = 4;//nº de Pow Pill activas en el tick anterior
		lastMaze = game.getMazeIndex();
		
	}


	private int getPacmanLastIndex(Game game) {
		int pacmanIndex = game.getPacmanCurrentNodeIndex();
		
		//Mantener clara la posicion de pacman
		if(game.wasPacManEaten())
			lastSeePacman = -1;
		
		if(pacmanIndex == -1) {
			ticks++;
			if(lastSeePacman != -1)
				pacmanIndex = lastSeePacman;
			else
				pacmanIndex = game.getPacManInitialNodeIndex();
		}
		else {
			ticks = 0;
			lastSeePacman = pacmanIndex;
		}
		
		return pacmanIndex;
	}
	
	private void searchPacman(Game game) {
		for (GHOST gh: GHOST.values()) {
			
			switch(gh) {
			
			case BLINKY: //da vueltas en donde sale pacman
				moves.put(gh, game.getNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(gh), game.getPacManInitialNodeIndex(), DM.PATH));
				break;
			case PINKY:  //da vueltas en donde sale ghost
				moves.put(gh, game.getNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(gh), game.getGhostInitialNodeIndex(), DM.PATH));
				break;	 
			case INKY:	//Siempre se mueve a la derecha (para pasar por los pipes)
				moves.put(gh, MOVE.RIGHT);
				break;
			case SUE:	//Se mueve al azar
				moves.put(gh, allMoves[rnd.nextInt(allMoves.length)]);
			}
		}
	}
}