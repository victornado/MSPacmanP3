package es.ucm.fdi.ici.c1920.practica3.grupo06;

import pacman.controllers.PacmanController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Ghost;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/*
 * The Class RandomPacMan.
 */
public final class MsPacMan extends PacmanController {

	private static final Double RUNAWAY_LIMIT = 15.0;
	private static final Double MAX_DISTANCE = 200.0;

	FuzzyEngine fe;
	HashMap<String, Double> input;
	HashMap<String, Double> output;
	
	private double[] distances = { MAX_DISTANCE, MAX_DISTANCE, MAX_DISTANCE, MAX_DISTANCE };// blinky pinky inky sue
	private int current;
	private HashSet<Integer> pillList;
	

	public MsPacMan() {
		fe = new FuzzyEngine(FuzzyEngine.FUZZY_CONTROLLER.MSPACMAN);
		input = new HashMap<String, Double>();
		output = new HashMap<String, Double>();
		pillList=new HashSet<Integer>();
	}

	@Override
	public MOVE getMove(Game game, long timeDue) {
		current = game.getPacmanCurrentNodeIndex();
		MOVE sol=MOVE.NEUTRAL;//solucion del FuzzyEngine
		
		//metemos las pills que veamos si estamos en una junction
		if(game.isJunction(current)) {
			int[]pills=game.getActivePillsIndices();
			for(int i:pills) {
				pillList.add(i);
			}
		}
		//FIN
		
		input.clear();
		output.clear();
		double gDistance=200;
		double isEdible = -1;
		GHOST aux = Metodos.getNearestChasingGhost(game, 200, current);//buscamos al fantasma mas cercano

		if(aux!=null) {
			gDistance = game.getDistance(game.getGhostCurrentNodeIndex(aux), current, DM.PATH);
			if (game.getGhostEdibleTime(aux) > 0)
				isEdible = 1;
		}
		
		//NO DEBERIA HACER FALTA
		//POR SI ACASO NECESITAMOS TODAS LAS DISTANCIAS DE LOS FANTASMAS EN LAS ESTRATEGIAS
		switch (aux) {
		case BLINKY: {
			distances[0] = gDistance;
			break;
		}
		case PINKY: {
			distances[1] = gDistance;
			break;
		}
		case INKY: {
			distances[2] = gDistance;
			break;
		}
		case SUE: {
			distances[3] = gDistance;
			break;
		}
		}
		// FIN
		
		input.put("GostDistance", gDistance);
		input.put("isEdible", isEdible);
		fe.evaluate("FuzzyMsPacMan", input, output);
		double runaway = output.get("runAway");
		//CON IF's PONER TODAS LAS ESTRATEGIAS SEGUN EL PARAMETRO RUNAWAY
		
		return sol;
		
		/*
		 * current = game.getPacmanCurrentNodeIndex(); input.clear(); output.clear();
		 * 
		 * for (GHOST ghost : GHOST.values()) { double distance =
		 * game.getShortestPathDistance(current, game.getGhostCurrentNodeIndex(ghost));
		 * if(distance == -1) distance = 200; if(distance != 0) // With PO visibility
		 * non observable ghosts distance is 0. distances[ghost.ordinal()] = distance;
		 * input.put(ghost.name()+"distance", distances[ghost.ordinal()]); }
		 * System.out.println(Arrays.toString(distances)); fe.evaluate("FuzzyMsPacMan",
		 * input, output); double runaway = output.get("runAway");
		 * System.out.println(runaway);
		 * 
		 * if(runaway > RUNAWAY_LIMIT) return runAwayFromClosestGhost(game); else return
		 * goToPills(game);
		 */
	}

	private MOVE runAwayFromClosestGhost(Game game) {
		System.out.println("RunAway");
		double min_distance = Double.MAX_VALUE;
		GHOST closest_ghost = null;
		for (GHOST ghost : GHOST.values()) {
			double distance = distances[ghost.ordinal()];
			if (distance < min_distance) {
				min_distance = distance;
				closest_ghost = ghost;
			}
		}
		return game.getNextMoveAwayFromTarget(current, game.getGhostCurrentNodeIndex(closest_ghost), DM.PATH);
	}

	private MOVE goToPills(Game game) {
		System.out.println("goToPills");
		int[] pills = game.getActivePillsIndices();
		int[] powerPills = game.getActivePowerPillsIndices();

		ArrayList<Integer> targets = new ArrayList<Integer>();

		for (int i = 0; i < pills.length; i++) // check which pills are available
		{
			if ((game.isPillStillAvailable(i) != null) && game.isPillStillAvailable(i) != null) {
				targets.add(pills[i]);
			}
		}

		for (int i = 0; i < powerPills.length; i++) // check with power pills are available
		{
			if ((game.isPowerPillStillAvailable(i) != null) && game.isPowerPillStillAvailable(i)) {
				targets.add(powerPills[i]);
			}
		}

		int[] targetsArray = new int[targets.size()]; // convert from ArrayList to array
		for (int i = 0; i < targetsArray.length; i++) {
			targetsArray[i] = targets.get(i);
		}
		// return the next direction once the closest target has been identified
		return game.getNextMoveTowardsTarget(current,
				game.getClosestNodeIndexFromNodeIndex(current, targetsArray, DM.PATH), DM.PATH);

	}
}