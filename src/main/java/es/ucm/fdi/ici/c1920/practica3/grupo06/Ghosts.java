package es.ucm.fdi.ici.c1920.practica3.grupo06;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import pacman.controllers.POGhostController;
import pacman.game.Game;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

public class Ghosts extends POGhostController {
	
	private Random rnd = new Random();
	private MOVE[] allMoves = MOVE.values();
	
	private double ticks = 0; //cada vez que no veo a pacman, + 1
	private double lastNActivePowPill = 4;//nº de Pow Pill activas en el tick anterior
	private double cornerDistance;
	private double ghostEdible;
	private double ghostEdibleTime;
	
	
	private int lastSeePacman = -1; //última pos donde vi a pacman.
	private int lastMaze = 0; //index del anterior maze
	private MOVE lastMovePacman = null;
	
	
	
	private String[] salida = {"search", "attack", "defend", "run"};
	private boolean chase = false;
	private int blinkyPos = 2;
	private int inkyPos = 3;
	
	
	FuzzyEngine fe = new FuzzyEngine(FuzzyEngine.FUZZY_CONTROLLER.GHOSTS);
	HashMap<String, Double> input = new HashMap<String, Double>();   HashMap<String, Double> output = new HashMap<String, Double>();
	
	
	//Posiciones importantes de los maze, en orden: nw, ne, sw, se. 0,1,3,2
	private int[][] mazeIndexes = {{0, 78, 1191, 1291, 153, 237, 1020, 936}, 
			{0, 86, 1217, 1317}, 
			{0, 78, 1300, 1378}, 
			{0, 100, 1218, 1307}};

	public void preCompute(Game game) {
		if(lastSeePacman == -1)
			lastSeePacman = game.getPacManInitialNodeIndex();
		if(lastMaze != game.getMazeIndex())
			init(game);
		if(game.wasPacManEaten()) {
			inkyPos = 3;
			blinkyPos = 2;
		}
		ticks++;
		if(game.wasPowerPillEaten())
			lastNActivePowPill--;
		ghostEdible = 0;
		ghostEdibleTime = 0;
		cornerDistance = nearestCornerPacmanDistance(game); 
	}
	
	@Override
	public MOVE getMove(GHOST ghost, Game game, long timeDue) {
		
		updatePacmanState(game, game.getPacmanCurrentNodeIndex() != -1);
        if(game.isGhostEdible(ghost)) 
        	ghostEdible = 1;
        if(game.getGhostEdibleTime(ghost) > ghostEdibleTime)
        	ghostEdibleTime = game.getGhostEdibleTime(ghost);
        
        
		input.clear(); output.clear();
        
		input.put("ticks", ticks);
    	input.put("cornerDistance", cornerDistance);
    	//input.put("ghostEdible", ghostEdible);
    	input.put("powPillsLeft", lastNActivePowPill);
    	input.put("ghostEdibleTime", ghostEdibleTime);
    	
        fe.evaluate("FuzzyGhosts", input, output);
        
        //System.out.println("--------------------------");
        double runaway = output.get("search");
        //System.out.println("search --> " + runaway);
		String solution = salida[0];
		for (int i = 1; i < salida.length; i++) {
			//System.out.println( salida[i] + " --> " + output.get(salida[i]));
			if(output.get(salida[i]) > runaway) {
				runaway = output.get(salida[i]);
				solution = salida[i];
			}
		}
		//System.out.println("--------------------------");
		
		
		switch(solution) {
		case "search":
			//System.out.println("search" + runaway);
			return searchPacman(game, ghost);
		case "attack":
			//System.out.println("attack" + runaway);
			return attackPacman(game, ghost);
		case "defend":
			//System.out.println("defend" + runaway);
			return defendPacman(game, ghost);
		case "run":
			//System.out.println("run" + runaway);
			return runPacman(game, ghost);
		default:
			return null;
		}
	}
	
	private void init(Game game) {
		ticks = 0; //cada vez que no veo a pacman, + 1
		lastSeePacman = game.getPacManInitialNodeIndex(); //última pos donde vi a pacman.
		lastNActivePowPill = 4;//nº de Pow Pill activas en el tick anterior
		lastMaze = game.getMazeIndex();
	}


	private void updatePacmanState(Game game, boolean seePacman) {
		
		if(game.wasPacManEaten()) {
			lastSeePacman = game.getPacManInitialNodeIndex();
			ticks = 0;
			lastMovePacman = MOVE.NEUTRAL;
			chase = false;
		}
		
		if(seePacman) {
			lastSeePacman = game.getPacmanCurrentNodeIndex();
			ticks = 0;
			lastMovePacman = game.getPacmanLastMoveMade();
		} 
	}
	
	private MOVE searchPacman(Game game, GHOST gh) {
		
		if(game.isGhostEdible(gh))
			return runPacman(game, gh);
		
		
		if(game.getMazeIndex() == 0) {
			switch(gh) {
			
			case BLINKY: 
				return game.getNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(gh), getSearchPos(game, gh, lastMaze), DM.PATH);
			case PINKY:  //da vueltas en donde sale ghost
				return game.getNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(gh), game.getGhostInitialNodeIndex(), DM.PATH);
			case INKY:
				return game.getNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(gh), getSearchPos(game, gh, lastMaze), DM.PATH);
			case SUE:	//Se mueve al azar
				return allMoves[rnd.nextInt(allMoves.length)];
			default:
				return MOVE.NEUTRAL;
			}
		}
		else {
		
			switch(gh) {
				
			case BLINKY: //da vueltas en donde sale pacman
				return game.getNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(gh), game.getPacManInitialNodeIndex(), DM.PATH);
			case PINKY:  //da vueltas en donde sale ghost
				return game.getNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(gh), game.getGhostInitialNodeIndex(), DM.PATH);
			case INKY:	//Siempre se mueve a la izquierda (para pasar por los pipes)
				 return MOVE.LEFT;
			case SUE:	//Se mueve al azar
				return allMoves[rnd.nextInt(allMoves.length)];
			default:
				return MOVE.NEUTRAL;
			}
		}
	}
	
	
	private MOVE attackPacman(Game game, GHOST gh) {
		
		if(game.isGhostEdible(gh))
			return runPacman(game, gh);
		
		ArrayList<GHOST> ghosts = new ArrayList<GHOST>();
		
		GHOST aux = Metodos.nearestGhostTo(game, lastSeePacman, null, DM.PATH);
		
		if(aux == gh) {
			return game.getNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(gh), lastSeePacman, DM.PATH);
		}
		
		for(GHOST ghost: GHOST.values()) {
			if(aux != ghost)
				ghosts.add(ghost);
		}
		
		ArrayList<Integer> exits = Metodos.nextExitsToPacman(game, lastSeePacman, lastMovePacman);
		
		for(int exit: exits) {
			GHOST[] conv = new GHOST[ghosts.size()];
			aux = Metodos.nearestGhostTo(game, exit, ghosts.toArray(conv), DM.PATH);
			ghosts.remove(aux);
			if(aux == gh) {
				return game.getNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(gh), exit, DM.PATH);
			}	
		}
		
		for(GHOST ghost: ghosts) {
			if (ghost == gh)
				return game.getNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost), Metodos.expandPath(game, lastSeePacman, lastMovePacman), DM.PATH);
		}
		
		return null;
	}
	
	private MOVE defendPacman(Game game, GHOST gh) {
		
		if(game.isGhostEdible(gh))
			return runPacman(game, gh);
		
		ArrayList<GHOST> ghosts = new ArrayList<GHOST>();
		
		GHOST aux = Metodos.nearestGhostTo(game, lastSeePacman, null, DM.PATH);
		
		if(aux == gh) {
			return game.getNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(gh), lastSeePacman, DM.PATH);
		}
		
		for(GHOST ghost: GHOST.values()) {
			if(aux != ghost)
				ghosts.add(ghost);
		}
		
		GHOST[] conv = new GHOST[ghosts.size()];
		
		aux = Metodos.nearestGhostTo(game, lastSeePacman, ghosts.toArray(conv), DM.PATH);
		
		if(aux == gh) {
			return game.getNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(gh), Metodos.expandPath(game, lastSeePacman, lastMovePacman), DM.PATH);
		}
		
		ghosts.remove(aux);
		
		for(GHOST ghost: ghosts) {
			if(ghost == gh)
				return game.getNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost), game.getGhostInitialNodeIndex(), DM.PATH);
		}
		
		return null;
	}
	
	private MOVE runPacman(Game game, GHOST gh) {
		
		if(!game.isGhostEdible(gh) && !chase) {
			chase = true;
			return game.getNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(gh), lastSeePacman, DM.PATH);
		}
		else if(!game.isGhostEdible(gh) && chase)
			return game.getNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(gh), Metodos.expandPath(game, lastSeePacman, lastMovePacman), DM.PATH);
		else if(!game.isGhostEdible(gh))
			return game.getNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(gh), lastSeePacman, DM.PATH);
		else {
			
			ArrayList<MOVE> possibleMoves = new ArrayList<MOVE>();
			for (MOVE mv: MOVE.values())
				if(!mv.equals(game.getNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(gh), game.getPacmanCurrentNodeIndex(), DM.PATH)) 
						&& !mv.equals(game.getGhostLastMoveMade(gh).opposite()))
					possibleMoves.add(mv);
			if(!possibleMoves.isEmpty())
				return possibleMoves.get(rnd.nextInt(possibleMoves.size()));
			return MOVE.NEUTRAL;
		}
			
	}
	
	private double nearestCornerPacmanDistance(Game game) {
		double distance = Double.MAX_VALUE;
		for(int corner: mazeIndexes[game.getMazeIndex()]) {
			double aux = game.getDistance(lastSeePacman, corner, DM.PATH);
			if(aux < distance)
				distance = aux;
		}
		return distance;
	}
	
	private int getSearchPos(Game game, GHOST gh, int maze) {
		switch(gh) {
		case BLINKY:
			if(game.getGhostCurrentNodeIndex(gh) == mazeIndexes[maze][4 + blinkyPos]) {
				blinkyPos++;
				if (blinkyPos > 3)
					blinkyPos = 0;
			}
			return mazeIndexes[maze][4 + blinkyPos];
		case INKY:
			if(game.getGhostCurrentNodeIndex(gh) == mazeIndexes[maze][4 + inkyPos]) {
				inkyPos--;
				if (inkyPos < 0)
					inkyPos = 3;
			}
			return mazeIndexes[maze][4 + inkyPos];
		default:
			return -1;
		}
	}
	
}