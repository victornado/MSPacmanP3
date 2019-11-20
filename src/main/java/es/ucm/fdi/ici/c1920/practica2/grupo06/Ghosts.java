package es.ucm.fdi.ici.c1920.practica2.grupo06;


import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;

import pacman.controllers.GhostController;
import pacman.game.Game;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

public class Ghosts extends GhostController {
	
	private final int NUMCORNERS = 4;
	private EnumMap<GHOST, MOVE> moves = new EnumMap<GHOST, MOVE>(GHOST.class);
	private MOVE[] allMoves = MOVE.values();
	private Random rnd = new Random();
	
	/*flags para la estrategia attackStrategyBlockCorner, el indice de acceso se corresponde al enum GHOST
	 -1 -> sin valor asignado
	 N+ -> junction al que se tiene que mover
	 -2 -> ha llegado al junction y tiene que perseguir a pacman 
	 */
	private int[] info_ghosts = {-1, -1, -1, -1};
	//info_ghosts ha sido calculado o no en attackStrategyBlockCorner
	boolean calculado = false;
	
	//Posiciones importantes de los maze, en orden: nw, ne, sw, se, pipe1i, pipe2i, pipe1d, pipe2d.
	//Si alguna posición no existe en un maze, el index será -1
	private int[][] mazeIndexes = {{0, 78, 1191, 1291, 348, 694, 399, 754}, 
			{0, 86, 1217, 1317, 24, 941, 62, 1002}, 
			{0, 78, 1300, 1378, 412, -1, 439, -1}, 
			{0, 100, 1218, 1307, 573, 673, 578, 678}}; 
	
	@Override
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		
		
		moves.clear();
		
		int pacmanIndex = game.getPacmanCurrentNodeIndex();
		
		if(isSomeGhostEdible(game)) {
			defensiveStrategyEdibleGhost(game, pacmanIndex);
			return moves;
		}
		else if(game.getNumberOfActivePills() <= 7) {
			defensiveStrategyDefendPill(game, pacmanIndex);
			return moves;
		}
		else{
			
			
			int cornerIndex = PacmanNearestCornerWithoutPowerPill(game, pacmanIndex, 28);
			
			if(cornerIndex != -1) {
				attackStrategyBlockCorner(game, cornerIndex, pacmanIndex);
				return moves;
			}
			
			
			//cambiamos los flags de la estrategia attackStrategyBlockCorner
			for (int i = 0; i < info_ghosts.length; i++) {
				info_ghosts[i] = -1;
			}
			calculado = false;
			
			int pipeIndex = SimetricPacmanNearPipe(game, pacmanIndex, 13);
			
			if (pipeIndex != -1) {
				attackStrategyBlockPipe(game, pipeIndex, pacmanIndex);
				return moves;
			}
			
			
			for (GHOST gh : GHOST.values()) {
				
				//si pacman cerca de powerpill
				if(isPacmanNearPowPill(game, pacmanIndex, 10)){
					moves.put(gh, game.getNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(gh), pacmanIndex, DM.PATH));
				}
				//si no
				else
					moves.put(gh, game.getNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(gh), pacmanIndex, DM.PATH));		
				
			} 
		
		
	    return moves;
		
		}
}
	/*
	Dependiendo del numero de fantasmas comestibles se realizan distintas acciones
		1 -> 2 fantasmas defienden al comestible y el restante persigue a pacman. El comestible se dirige a la carcel.
		2 -> 1 fantasma defiende a un comestible y el restante persigue a pacman.
			 1 fantasma comestible va a la carcel y el restante se aleja de pacman.
		3 -> El fantasma persigue a pacman.
			 1 fantasma comestible va a la carcel y los 2 restantes se aleja de pacman.
		4 -> 1 fantasma comestible va a la carcel y los 3 restantes se aleja de pacman.
	*/	
	private void defensiveStrategyEdibleGhost(Game game, int pacmanIndex) {
		boolean lair = false;
		int indexEdibleGhost = -1;
		List<GHOST> notEdibleGhost = new ArrayList<GHOST>() ;
		
		for (GHOST gh : GHOST.values()) {
			
			if(game.isGhostEdible(gh)) {
				indexEdibleGhost = game.getGhostCurrentNodeIndex(gh);
				if(!lair) {
					lair = true;
					moves.put(gh, game.getNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(gh), game.getGhostInitialNodeIndex(), DM.PATH));
				}
				else
					moves.put(gh, game.getNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(gh), pacmanIndex, DM.PATH));
			}
			else
				notEdibleGhost.add(gh);
		}
		
		if (notEdibleGhost.isEmpty()) {
			return;
		}
		
		GHOST[] aux = new GHOST[notEdibleGhost.size()]; 
		GHOST pursuitGh = nearestGhostTo(game, pacmanIndex, notEdibleGhost.toArray(aux), DM.PATH);
		moves.put(pursuitGh, game.getNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(pursuitGh), pacmanIndex, DM.PATH));
		for (GHOST gh : notEdibleGhost) {
			if(!gh.equals(pursuitGh)) {
				moves.put(gh, moveTowardUnlessPacmanIsNear(game, gh, indexEdibleGhost, DM.PATH, pacmanIndex, 5));	
			}
		}
		
	}
	
	//3 fantasmas dan vueltas alrededor de una pill, y el restante persigue a pacman.
	private void defensiveStrategyDefendPill(Game game, int pacmanIndex) {
		moves.put(GHOST.BLINKY, game.getNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(GHOST.BLINKY), pacmanIndex, DM.PATH));
		for (GHOST gh : GHOST.values()) {
			if(!gh.equals(GHOST.BLINKY))
				moves.put(gh, moveTowardUnlessPacmanIsNear(game, gh, game.getActivePillsIndices()[game.getActivePillsIndices().length/2], DM.EUCLID, pacmanIndex, 5)); 
		}
	}
	
	//Si pacman esta cerca de una tuberia, un fantasma se dirige a la salida de dicha tuberia
	private void attackStrategyBlockPipe(Game game, int pipeIndex, int pacmanIndex) {
		GHOST blockingGh = nearestGhostTo(game, pipeIndex, null, DM.EUCLID);
		
		if(game.getDistance(pacmanIndex, pipeIndex, DM.PATH) <= 5) 
			moves.put(blockingGh, game.getNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(blockingGh), pacmanIndex, DM.PATH));
		else
			moves.put(blockingGh, game.getNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(blockingGh), pipeIndex, game.getGhostLastMoveMade(blockingGh), DM.EUCLID));
		
		for (GHOST gh : GHOST.values()) {
			if(!gh.equals(blockingGh))
				moves.put(gh, game.getNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(gh), pacmanIndex, DM.PATH));
		}
	}
	
	//Si pacman esta en una esquina, cada fantasma se dirige a una de las 4 intersecciones más cercanas a la esquina
	private void attackStrategyBlockCorner(Game game, int cornerIndex, int pacmanIndex) {
		
		if(!calculado) {
			Pair<Integer,Double>[] nearJunct = getFourNearestJunctionsTo(game, cornerIndex);
			
			List<GHOST> ghost_list = new ArrayList<GHOST>();
			for (GHOST gh : GHOST.values()) {
				ghost_list.add(gh);
			}
			GHOST[] ghosts_array = new GHOST[ghost_list.size()];
			
			for (int i = 0; i < 3; i++) {
				GHOST aux = nearestGhostTo(game, nearJunct[i].first(), (GHOST[])ghost_list.toArray(ghosts_array), DM.EUCLID);
				ghost_list.remove(aux);
				info_ghosts[aux.ordinal()] = nearJunct[i].first();
				ghosts_array = new GHOST[ghost_list.size()];
			}
			
			info_ghosts[ghost_list.get(0).ordinal()] = nearJunct[3].first(); 
			calculado = true;
		}
		
		for (GHOST gh : GHOST.values()) {
			if(info_ghosts[gh.ordinal()] != -2 && 
					game.getDistance(game.getGhostCurrentNodeIndex(gh), info_ghosts[gh.ordinal()], DM.EUCLID) == 0) {
				info_ghosts[gh.ordinal()] = -2;
				moves.put(gh, game.getNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(gh), pacmanIndex, DM.PATH));
			}	
			else if(info_ghosts[gh.ordinal()] == -2)
				moves.put(gh, game.getNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(gh), pacmanIndex, DM.PATH));
			else
				moves.put(gh, game.getNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(gh), info_ghosts[gh.ordinal()], DM.PATH));
		}
		
	}

	private Pair<Integer,Double>[] getFourNearestJunctionsTo(Game game, int fromIndex) {
		
		Pair<Integer,Double>[] nearJunct = (Pair<Integer,Double>[]) new Pair[4];
		int[] junctions = game.getJunctionIndices();
		nearJunct[0] = new Pair<Integer,Double>(junctions[0], game.getDistance(fromIndex, junctions[0], DM.EUCLID));
		int size = 1;
		
		
		
		for (int i = 1; i < junctions.length; i++) {
			Pair<Integer,Double> newest = new Pair<Integer,Double>(junctions[i], game.getDistance(fromIndex, junctions[i], DM.EUCLID));
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
		Devuelve la tuberia contraria a la más cercana a pacman, si está dentro del limite. Si no, devuelve -1
		@Params
			game -> clase game con las funciones.
			pacmanIndex -> index de pacman
			limit -> distancia limite de pacman a la tuberia 
	*/
	private int SimetricPacmanNearPipe(Game game, int pacmanIndex, int limit) {

		double minDist = limit;
		int minIndexPipe = -1;
		
		for (int i = 4; i < 8; i++) {
			if(mazeIndexes[game.getMazeIndex()][i] != -1) {
				double aux = game.getDistance(pacmanIndex, mazeIndexes[game.getMazeIndex()][i], DM.PATH);
				if(aux <= minDist) {
					minDist = aux;
					if(i >= 6)
						minIndexPipe = mazeIndexes[game.getMazeIndex()][i-2];
					else
						minIndexPipe = mazeIndexes[game.getMazeIndex()][i+2];
				}
			}
		}
		return minIndexPipe;
	}

	/*
		Comprueba si esta dentro de la distancia a una power pill
	 	@Params
			game -> clase game con las funciones.
			pacmanIndex -> index de pacman
			limit -> distancia limite de pacman a la power pill
	 */
	private boolean isPacmanNearPowPill(Game game, int indexPacman, int limit) {
		
		int[] PowPillIndexes = game.getActivePowerPillsIndices();
				
		boolean near = false; 
		
		int i = 0;
		while(!near && i < PowPillIndexes.length){
			near = (game.getDistance(indexPacman, PowPillIndexes[i], DM.PATH) <= limit);
			i++;
		}
		
		return near;
	}
	
	/*
		Devuelve el index de la esquina más cercana a pacman sin power pill dentro de un limite. 
		Si no se encuentra dentro del limite, devuelve -1
	 	@Params
			game -> clase game con las funciones.
			pacmanIndex -> index de pacman
			limit -> distancia limite de pacman a la esquina
	 */
	private int PacmanNearestCornerWithoutPowerPill(Game game, int indexPacman, int limit) {
		
		int[] powPills = game.getActivePowerPillsIndices();
		
		if(powPills.length == NUMCORNERS)
			return -1;
		
		List<Integer> cornWithoutPowPill = new ArrayList<Integer>();
		
		
		for (int i = 0; i < 4; i++) {
		
			boolean valid = true;
			for (int j = 0; j < powPills.length; j++) {
				
				if(game.getDistance(powPills[j], mazeIndexes[game.getMazeIndex()][i], DM.EUCLID) < 20)
					valid = false;
					
			}
			
			if(valid)
				cornWithoutPowPill.add(mazeIndexes[game.getMazeIndex()][i]);
		}
		
		
		double dist;
		
		for (Integer cornerIndex : cornWithoutPowPill) {
			
			dist = game.getDistance(indexPacman, cornerIndex, DM.EUCLID);
			if (dist <= limit) {
				return cornerIndex;
			}
		}
		
		return -1;
	}
	
	/*
		Devuelve el fantasma más cercano a un index. 
		@Params
			game -> clase game con las funciones.
			index -> index al que se quiere comprobar la distancia.
			ghosts -> fantasmas que se quieren comprobar. Si el array es null, se comprueban todos los fantasmas
			dm -> tipo de distancia
	*/
	private GHOST nearestGhostTo(Game game, int index, GHOST[] ghosts, DM dm) {
		GHOST minGh = null;
		double minDist = Integer.MAX_VALUE;
		if (ghosts == null)
			ghosts = GHOST.values();
		for (GHOST gh : ghosts) {

				double dist = game.getDistance(index, game.getGhostCurrentNodeIndex(gh), dm);

				if (dist <= minDist) {
					minDist = dist;
					minGh = gh;
				}
		}
		
		return minGh;
	}
	
	/*
	 	Comprueba si hay un fantasma comestible.
	 	@Params
			game -> clase game con las funciones.
	*/
	private boolean isSomeGhostEdible(Game game) {
		return game.isGhostEdible(GHOST.BLINKY) || game.isGhostEdible(GHOST.INKY) || game.isGhostEdible(GHOST.PINKY) || game.isGhostEdible(GHOST.SUE);
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
	private MOVE moveTowardUnlessPacmanIsNear(Game game, GHOST gh, int toIndex, DM dm, int pacmanIndex, int limit) {
		
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
}
