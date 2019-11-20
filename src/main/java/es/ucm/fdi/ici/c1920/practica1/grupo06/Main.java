package es.ucm.fdi.ici.c1920.practica1.grupo06;


import pacman.Executor;
import pacman.controllers.*;
import pacman.game.util.Stats;

public class Main {

	public static void main(String[] args) {

		Executor executor = new Executor.Builder()
		.setTickLimit(4000)
		.setGhostPO(false)
		.setPacmanPO(false)
		.setGhostsMessage(false)
		.setVisual(true)
		.setScaleFactor(2.0)
		.build();
		
		//PacmanController pacMan = new MsPacMan();
		PacmanController pacMan = new HumanController(new KeyBoardInput());
		GhostController ghosts = new Ghosts();
		
//		Stats[] a= executor.runExperiment(pacMan, ghosts, 50, "");
//		System.out.println(a[0].getAverage());
//		
		System.out.println(
				executor.runGame(pacMan, ghosts, 50)
		);
		
	}
}
