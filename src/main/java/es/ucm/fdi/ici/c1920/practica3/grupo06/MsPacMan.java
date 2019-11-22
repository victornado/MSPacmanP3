package es.ucm.fdi.ici.c1920.practica3.grupo06;

import pacman.controllers.PacmanController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;


/*
 * The Class RandomPacMan.
 */
public final class MsPacMan extends PacmanController {

	//private static final Double RUNAWAY_LIMIT = 15.0;
	//private static final Double MAX_DISTANCE = 200.0;

	FuzzyEngine fe;
	HashMap<String, Double> input;
	HashMap<String, Double> output;
	
	private int current;
	private HashSet<Integer> pillList;
	private double result;
	GHOST nearestGhost;
	double gDistance;
	int ticks;
	
	public MsPacMan() {
		fe = new FuzzyEngine(FuzzyEngine.FUZZY_CONTROLLER.MSPACMAN);
		input = new HashMap<String, Double>();
		output = new HashMap<String, Double>();
		pillList=new HashSet<Integer>();
		result=0;
		nearestGhost=null;
		gDistance=200;
		ticks =0;
	}

	@Override
	public MOVE getMove(Game game, long timeDue) {
		if(ticks==100) {
			nearestGhost =null;
			gDistance=200;
		}
		else  if(nearestGhost!=null)ticks++;
		current = game.getPacmanCurrentNodeIndex();
		//MOVE sol=MOVE.NEUTRAL;//solucion del FuzzyEngine
		
		//metemos las pills que veamos si estamos en una junction
		if(game.isJunction(current)) {
			int[]pills=game.getActivePillsIndices();
			for(int i:pills) {
				pillList.add(i);
			}
		}
		
		//FIN
		if(pillList.contains(current) )pillList.remove(current);
		input.clear();
		output.clear();
		
		double isEdible = -1;
		
		GHOST ghost = Metodos.getNearestChasingGhost(game, 200, current);//buscamos al fantasma mas cercano
		
		if(ghost!=null) {
			nearestGhost=ghost;
			gDistance = game.getDistance(game.getGhostCurrentNodeIndex(ghost), current, DM.PATH);
			if (game.isGhostEdible(nearestGhost))
				isEdible = 1;
		}
		
		input.put("GhostDistance", gDistance);
		input.put("isEdible", isEdible);
		fe.evaluate("FuzzyMsPacMan", input, output);
		result = output.get("result");
		//CON IF's PONER TODAS LAS ESTRATEGIAS SEGUN EL PARAMETRO RUNAWAY
		//System.out.print(result+"--> ");
		if(result==0) {
			
			//System.out.println("paramos");
		}
		if(result > 25)
			return escape(game); 
		else if (result>15 && result<25)
			return chassingGhost(game);
		else
			return goToPills(game);
		
	}

	public MOVE escape(Game game) {
		//System.out.println(" escape" );
		//int cerca=30;
		//int pacmanIndex = game.getPacmanCurrentNodeIndex();// actual nodo del pacman
		
		//GHOST nearestGh = PacmanUtils.getNearestChasingGhost(game, 200, pacmanIndex);// devuelve el fantasma mas cercano a pacman
		//devuelve los nodos vecinos sin tener en cuenta el opuesto
		//int[] vecinos= game.getNeighbouringNodes(pacmanIndex, game.getPacmanLastMoveMade()) ;
		//De todos los nodos vecinos a pacman selecciona el nodo mas lejano al fantasma
		//int nodoFarthest=game.getFarthestNodeIndexFromNodeIndex(game.getGhostCurrentNodeIndex(nearestGhost), vecinos, DM.EUCLID);
		//realiza el movimiento segun el nodo mas lejos del fantasma
		MOVE mvs=game.getNextMoveAwayFromTarget(current, game.getGhostCurrentNodeIndex(nearestGhost), DM.PATH)/*.getNextMoveTowardsTarget(pacmanIndex,nodoFarthest,DM.PATH)*/;
		return mvs;
	}

	private MOVE chassingGhost(Game game) {
		//System.out.println(" chase" );
		if(nearestGhost!=null ) {//si hay fantasma cerca
			return game.getNextMoveTowardsTarget(current, game.getGhostCurrentNodeIndex(nearestGhost), DM.PATH);//va a por el
		}
		else return goToPills(game);//va a por pills
	}
	
	private MOVE goToPills(Game game) {
		//System.out.println("  goToPills ");
		Random rng= new Random();
		//int []vecinos=game.getNeighbouringNodes(current);
		int[] vecinos= game.getNeighbouringNodes(current, game.getPacmanLastMoveMade()) ;
		int node=vecinos[rng.nextInt(vecinos.length)];
		int[] pills = game.getActivePillsIndices();
		if(pills.length==0) {//no ve pills cerca
			if(!pillList.isEmpty()) {//la lista de pills que ha visto anteriormente no esta vacia
				for (int i : pillList) {
					 node =i;
					 pillList.remove(i);
					break;
				}
			}
			//else va un vecino aleatorio
		}
		else {//ve pills cerca
			node=game.getClosestNodeIndexFromNodeIndex(current, pills, DM.MANHATTAN);
		}
		return game.getNextMoveTowardsTarget(current,node, DM.PATH);
	}
	
	
}