package es.ucm.fdi.ici.c1920.practica3.grupo06;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import pacman.controllers.GhostController;
import pacman.controllers.POGhostController;
import pacman.game.Game;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

public class Ghosts extends POGhostController {
	
	private EnumMap<GHOST, MOVE> moves = new EnumMap<GHOST, MOVE>(GHOST.class);
	private Random rnd = new Random();
	private MOVE[] allMoves = MOVE.values();
	
	private int ticks = 0; //cada vez que no veo a pacman, + 1
	private int lastNActivePowPill = 4;//nº de Pow Pill activas en el tick anterior
	
	private int lastSeePacman = -1; //última pos donde vi a pacman.
	private int lastEdibleGhosts = 0; //nº de fantasmas comestibles en el tick anterior
	private int lastMaze = -1; //index del anterior maze
	private MOVE lastMovePacman = null;
	
	
	
	
	
	FuzzyEngine fe = new FuzzyEngine(FuzzyEngine.FUZZY_CONTROLLER.GHOSTS);
	HashMap<String, Double> input = new HashMap<String, Double>();   HashMap<String, Double> output = new HashMap<String, Double>();
	int num = 0;
	
	//Posiciones importantes de los maze, en orden: nw, ne, sw, se, pipe1i, pipe2i, pipe1d, pipe2d.
	private int[][] mazeIndexes = {{0, 78, 1191, 1291}, 
			{0, 86, 1217, 1317}, 
			{0, 78, 1300, 1378}, 
			{0, 100, 1218, 1307}};

	public void preCompute(Game game) {
		/*
		if(game.getPacmanLastMoveMade() == null)
			System.out.println("No lo se");
		else
			System.out.println(game.getPacmanLastMoveMade());
		
		for (GHOST gh: GHOST.values()) {
			System.out.println(gh.name() + "--> " + game.getPacmanCurrentNodeIndex());
		}
		*/
	}
	
	@Override
	public MOVE getMove(GHOST ghost, Game game, long timeDue) {
		
		
        input.clear(); output.clear();
        
        switch(num++ % 4) {
        case 0:
        	input.put("ticks", 15.0);
        	input.put("cornerDistance", 15.0);
        	input.put("ghostEdible", 0.0);
        	input.put("powPillsLeft", 4.0);
        	break;
        case 1:
        	input.put("ticks", 80.0);
        	input.put("cornerDistance", 15.0);
        	input.put("ghostEdible", 1.0);
        	input.put("powPillsLeft", 4.0);
        	break;
        case 2:
        	input.put("ticks", 5.0);
        	input.put("cornerDistance", 15.0);
        	input.put("ghostEdible", 0.0);
        	input.put("powPillsLeft", 4.0);
        	break;
        case 3:
        	input.put("ticks", 5.0);
        	input.put("cornerDistance", 1.0);
        	input.put("ghostEdible", 1.0);
        	input.put("powPillsLeft", 1.0);
        	break;
        default:
        	input = null;
        }
        
        fe.evaluate("FuzzyGhosts", input, output);
		double runaway = output.get("strategy");
		if(runaway < 10) 
			System.out.println((num - 1)%4 + "search" + runaway);
		else if(runaway < 20 ) 
			System.out.println((num - 1)%4 + "defend" + runaway);
		else if(runaway < 30) 
			System.out.println((num - 1)%4 + "attack" + runaway);
		else if(runaway < 40) 
			System.out.println((num - 1)%4 + "run" + runaway);
		else  
			System.out.println((num - 1)%4 + "random" + runaway);
		
		return MOVE.UP;
		
		
		
	}
	
	
}