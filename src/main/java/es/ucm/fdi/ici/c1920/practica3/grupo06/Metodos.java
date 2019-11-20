package es.ucm.fdi.ici.c1920.practica3.grupo06;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import es.ucm.fdi.ici.c1920.practica2.grupo06.Pair;
import pacman.game.Game;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;



public class Metodos {

	//Posiciones importantes de los maze, en orden: nw, ne, sw, se.
	//Si alguna posición no existe en un maze, el index será -1
	private static int[][] mazeIndexes = {{0, 78, 1191, 1291}, 
				{0, 86, 1217, 1317}, 
				{0, 78, 1300, 1378}, 
				{0, 100, 1218, 1307}};
	private static MOVE[] allMoves = MOVE.values();
	private static Random rnd = new Random();
	
	
	/*
	Devuelve el index de la esquina más cercana a pacman sin power pill dentro de un limite. 
	Si no se encuentra dentro del limite, devuelve -1
 	@Params
		game -> clase game con las funciones.
		pacmanIndex -> index de pacman
		limit -> distancia limite de pacman a la esquina
    */
	public static int PacmanNearestCornerWithPowerPill(Game game, int indexPacman, int limit) {

		int[] powPills = game.getActivePowerPillsIndices();
		
		if(powPills.length == 0)
			return -1;
		
		List<Integer> cornWithPowPill = new ArrayList<Integer>();
		
		
		for (int i = 0; i < 4; i++) {
		
			boolean valid = false;
			for (int j = 0; j < powPills.length; j++) {
				
				if(game.getDistance(powPills[j], mazeIndexes[game.getMazeIndex()][i], DM.EUCLID) < 20)
					valid = true;
					
			}
			
			if(valid)
				cornWithPowPill.add(mazeIndexes[game.getMazeIndex()][i]);
		}
		
		
		double dist;
		
		for (Integer cornerIndex : cornWithPowPill) {
			
			dist = game.getDistance(indexPacman, cornerIndex, DM.EUCLID);
			if (dist <= limit) {
				return cornerIndex;
			}
		}
		
		return -1;
	}
		
	/*
	Devuelve el junction al que se dirige pacman al entrar en un camino. 
	@Params
	game -> clase game con las funciones.
	pacmanIndez -> el index de pacman
	*/
	public static int getNextJunctionPacman(Game game, int pacmanIndex) {
		
		if(game.isJunction(pacmanIndex))
			return pacmanIndex;
		
		
		int nextJunction = pacmanIndex;
		MOVE lastMove = game.getPacmanLastMoveMade();
		
		while(true) {
			
			int aux = game.getNeighbour(nextJunction, lastMove);
			
			if(aux == -1) {
				
				int i = 0; boolean jumpOut = false; 
				while (i < allMoves.length && !jumpOut) {
					if(allMoves[i].opposite() != lastMove) {
						aux = game.getNeighbour(nextJunction, allMoves[i]);
						if(aux != -1) {
							nextJunction = aux;
							lastMove = allMoves[i];
							jumpOut = true;
						}
					}
					i++;
					
				}
			}
			else if(game.isJunction(aux)) {
				return aux;
			}
			else {
				nextJunction = aux;	
			}
		}
		
	}
	
	/*
	Devuelve un array de 4 pares <nodo, distancia>, siendo estos 4 los junctions más cercanos a pacman 
	@Params
	game -> clase game con las funciones.
	*/
	public static Pair<Integer,Double>[] getFourNearestJunctionsTo(Game game, int fromIndex) {
		
		Pair<Integer,Double>[] nearJunct = (Pair<Integer,Double>[]) new Pair[4];
		int[] junctions = game.getJunctionIndices();
		nearJunct[0] = new Pair<Integer,Double>(junctions[0], game.getDistance(fromIndex, junctions[0], DM.PATH));
		int size = 1;
		
		
		
		for (int i = 1; i < junctions.length; i++) {
			Pair<Integer,Double> newest = new Pair<Integer,Double>(junctions[i], game.getDistance(fromIndex, junctions[i], DM.PATH));
			Pair<Integer,Double> aux;
			for (int j = 0; j < size; j++) {
				
				
				if(newest.second() <= nearJunct[j].second()) {
					aux = nearJunct[j];
					nearJunct[j] = newest;
					newest = aux;
				}
					
			}
			
			if(size < 4) {
				nearJunct[size] = newest;
				size++;
			}
		}
		
		return nearJunct;
	}
	
	/*
	Devuelve el fantasma más cercano a un index. 
	@Params
		game -> clase game con las funciones.
		index -> index al que se quiere comprobar la distancia.
		ghosts -> fantasmas que se quieren comprobar. Si el array es null, se comprueban todos los fantasmas
		dm -> tipo de distancia
	*/
	public static GHOST nearestGhostTo(Game game, int index, GHOST[] ghosts, DM dm) {
		
		GHOST minGh = null;
		double minDist = Integer.MAX_VALUE;
		if (ghosts == null)
			ghosts = GHOST.values();
		for (GHOST gh : ghosts) {
			if(game.getGhostLairTime(gh) == 0) {
				double dist = game.getDistance(index, game.getGhostCurrentNodeIndex(gh), dm);
		
				if (dist <= minDist) {
					minDist = dist;
					minGh = gh;
				}
			}
		}
		if(minGh == null)
			return GHOST.BLINKY;
		return minGh;
	}

	public static GHOST[] xNearestGhostsTo(Game game, int index, GHOST[] ghosts, DM dm, int num) {
		GHOST[] minGh = new GHOST[num];
		double minDist[] = new double[num];
		for (int i = 0; i < minDist.length; i++) {
			minDist[i] = Double.MAX_VALUE;
			minGh[i] = null;
		}
		if (ghosts == null)
			ghosts = GHOST.values();
		
		for (GHOST gh : ghosts) {
			
			if(game.getGhostLairTime(gh) == 0) {
				double dist = game.getDistance(index, game.getGhostCurrentNodeIndex(gh), dm);
				double aux1;
				GHOST aux2;
				for (int j = 0; j < num; j++) {
					
						if(dist <= minDist[j]) {
							aux1 = minDist[j];
							minDist[j] = dist;
							dist = aux1;
							
							aux2 = minGh[j];
							minGh[j] = gh;
							gh = aux2;
						}
						
				}
			}
			
		}
		
		for (int i = 0; i < minDist.length; i++) {
			if(minGh[i] == null)
				minGh[i] = GHOST.BLINKY;
		}
		
		
		return minGh;
	}
	/*
	Devuelve el movimiento hacia un index de un fantasma, a no ser que pacman este dentro de un radio limit. En ese caso, devuelve el movimiento hacia pacman.
	@Params
		game -> clase game con las funciones.
		gh -> fantasma del que se quiere calcular el movimiento
		toindex -> index al que se quiere mover.
		dm -> tipo de distancia
		pacmanIndex -> index de pacman.
		limit -> limite del fantasma hasta pacman.
	*/
	public static MOVE moveTowardUnlessPacmanIsNear(Game game, GHOST gh, int toIndex, DM dm, int pacmanIndex, int limit) {
	
		int ghostIndex = game.getGhostCurrentNodeIndex(gh);
		MOVE ghostMove = game.getGhostLastMoveMade(gh);
		switch(dm) {
		case MANHATTAN:
			if(game.getDistance(ghostIndex, pacmanIndex, DM.PATH) > limit)
				return game.getNextMoveTowardsTarget(ghostIndex, toIndex, ghostMove, dm);
			else
				return game.getNextMoveTowardsTarget(ghostIndex, pacmanIndex ,ghostMove, dm);
		case EUCLID:
			if(game.getDistance(ghostIndex, pacmanIndex, DM.PATH) > limit)
				return game.getNextMoveTowardsTarget(ghostIndex, toIndex, ghostMove, dm);
			else
				return game.getNextMoveTowardsTarget(ghostIndex, pacmanIndex ,ghostMove, dm);
		default:
			if(game.getDistance(ghostIndex, pacmanIndex, DM.PATH) > limit)
				return game.getNextMoveTowardsTarget(ghostIndex, toIndex, dm);
			else
				return game.getNextMoveTowardsTarget(ghostIndex, pacmanIndex, dm);
		}

	}
	
	public static int getNearestPill(Game game, int pacmanIndex) {
		int[] pillsIndices = game.getActivePillsIndices();// indice de todas las pills
		return game.getClosestNodeIndexFromNodeIndex(pacmanIndex, pillsIndices, DM.MANHATTAN);// selecciona la pill mas
																								// // cercana
	}

	public static GHOST getNearestChasingGhost(Game game, double limit, int pacmanIndex) {
		GHOST minGh = null;
		double minDist = limit;
		for (GHOST gh : GHOST.values()) {
			double dist = game.getDistance(pacmanIndex, game.getGhostCurrentNodeIndex(gh), DM.MANHATTAN);
			if (dist <= minDist) {
				minDist = dist;
				minGh = gh;
			}
		}
		return minGh;
	}
	
	
	public static boolean isPacmanNearPowPill(Game game, int indexPacman, int limit) {
		int[] PowPillIndexes = game.getActivePowerPillsIndices();
		boolean near = false;
		int i = 0;
		while (!near && i < PowPillIndexes.length) {
			near = (game.getDistance(indexPacman, PowPillIndexes[i], DM.MANHATTAN) <= limit);
			i++;
		}
		return near;
	}
	
	public static boolean GhostCanBlockMe(Game game, int pacmanIndex, MOVE moveMade) {
		int juncDest = getNextJunctionPacman(game, pacmanIndex);
		double pacmanDist = game.getDistance(game.getNeighbour(pacmanIndex, moveMade), juncDest, DM.PATH);
		for (GHOST gh: GHOST.values()) {
			if(game.getDistance(game.getGhostCurrentNodeIndex(gh), juncDest, DM.PATH) < pacmanDist &&  game.getGhostLastMoveMade(gh) != game.getPacmanLastMoveMade())
				return true;
		}
		
		return false;
	}
	
	
	public static MOVE makeMoveUnlessDie(Game game, MOVE possibleMove, int pacmanIndex) {
		List<MOVE> moves = new ArrayList<MOVE>();
		if(!GhostCanBlockMe(game, pacmanIndex, possibleMove))
			return possibleMove;
		else {
			moves = new ArrayList<MOVE>();
			for (int i = 0; i < allMoves.length; i++) {
				if(allMoves[i] != possibleMove && allMoves[i] != game.getPacmanLastMoveMade().opposite() 
						&& !GhostCanBlockMe(game, pacmanIndex, allMoves[i]))
					moves.add(allMoves[i]);
			}
		}
		if(moves.isEmpty())
			return MOVE.UP;
		return moves.get(rnd.nextInt(moves.size()));
	}
	
	public static GHOST[] getNotEdibleGhosts(Game game) {
		
		ArrayList<GHOST> ghosts = new ArrayList<>();
		
		for(GHOST gh: GHOST.values()) {
			if(!game.isGhostEdible(gh))
				ghosts.add(gh);
		}
		
		return ghosts.toArray(new GHOST[ghosts.size()]);
	}
	
	public static GHOST[] getEdibleGhosts(Game game) {
		
		ArrayList<GHOST> ghosts = new ArrayList<>();
		
		for(GHOST gh: GHOST.values()) {
			if(game.isGhostEdible(gh))
				ghosts.add(gh);
		}
		
		return ghosts.toArray(new GHOST[ghosts.size()]);
	}
	
	public static int getNearestPowerPill(Game game, int pacmanIndex) {
		int[] PowpillsIndices = game.getActivePowerPillsIndices();
		return game.getClosestNodeIndexFromNodeIndex(pacmanIndex, PowpillsIndices, DM.MANHATTAN);
	}
	
	public static Integer[] nextExitsToPacman() {
		ArrayList<Integer> solList = new ArrayList<Integer>();
		return null;
		
	}
}
