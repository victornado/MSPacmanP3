package es.ucm.fdi.ici.c1920.practica3.grupo06;

import java.util.HashMap;
import java.util.Random;

import pacman.controllers.POGhostController;
import pacman.game.Game;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

public class DummieGhosts extends POGhostController {
	
	private Random rnd = new Random();
	private MOVE[] allMoves = MOVE.values();
	
	private double ticks = 0; //cada vez que no veo a pacman, + 1
	private double lastNActivePowPill = 4;//nº de Pow Pill activas en el tick anterior
	private double cornerDistance;
	private double ghostEdible;
	
	private int lastSeePacman = -1; //última pos donde vi a pacman.
	private int lastMaze = 0; //index del anterior maze
	private MOVE lastMovePacman = null;
	
	
	
	private String[] salida = {"search", "attack", "defend", "run"};
	private boolean chase = false;
	
	
	FuzzyEngine fe = new FuzzyEngine(FuzzyEngine.FUZZY_CONTROLLER.GHOSTS);
	HashMap<String, Double> input = new HashMap<String, Double>();   HashMap<String, Double> output = new HashMap<String, Double>();
	
	
	//Posiciones importantes de los maze, en orden: nw, ne, sw, se, pipe1i, pipe2i, pipe1d, pipe2d.
	private int[][] mazeIndexes = {{0, 78, 1191, 1291}, 
			{0, 86, 1217, 1317}, 
			{0, 78, 1300, 1378}, 
			{0, 100, 1218, 1307}};

	public void preCompute(Game game) {

	}
	
	@Override
	public MOVE getMove(GHOST ghost, Game game, long timeDue) {
		if (ghost == GHOST.BLINKY)
			System.out.println(game.getGhostEdibleTime(ghost));
		return game.getNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost), 0, DM.PATH);
	}
}