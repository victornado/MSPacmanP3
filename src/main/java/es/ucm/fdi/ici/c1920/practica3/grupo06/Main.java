package es.ucm.fdi.ici.c1920.practica3.grupo06;


import pacman.Executor;
import pacman.controllers.*;

public class Main {

	public static void main(String[] args) {

		Executor executor = new Executor.Builder()
		.setTickLimit(4000)
		.setGhostPO(true)
		.setPacmanPO(true)
		.setGhostsMessage(false)
		.setVisual(true)
		.setScaleFactor(2.0)
		.build();
		
		
		
		
		PacmanController pacMan = new MsPacMan();
		//PacmanController pacMan = new HumanController(new KeyBoardInput());

		GhostController ghosts = new Ghosts();
		
		/*
		//PROBAR VARIAS VECES
		int num = 10;
		double aver = 0;
		double mem[] = new double[num];
		for (int i = 0; i < num; i++) {
			Stats[] a= executor.runExperiment(pacMan, ghosts, 1, "");
			aver += a[0].getAverage();
			mem[i] = a[0].getAverage();
		}
		
		for (int i = 0; i < mem.length; i++) {
			System.out.println(mem[i]);
		}
		
		System.out.println("--------");
		System.out.println(aver/num);
		*/
		
		
		//PROBAR DOS VERSIONES DEL MISMO PACMAN/GHOSTS
		/*
		double mem1[] = new double[10];
		double mem2[] = new double[10]; 
		 
		double media1 = 0, media2 = 0;
		for (int i = 0; i < 10; i++) {
			Stats[] a= executor.runExperiment(pacMan1, ghosts, 50, "");
			mem1[i] = a[0].getAverage();
			media1 += a[0].getAverage();
		}
		
		
		for (int i = 0; i < 10; i++) {
			Stats[] a= executor.runExperiment(pacMan2, ghosts, 50, "");
			mem2[i] = a[0].getAverage();
			media2 += a[0].getAverage();
		}
		
		for (int i = 0; i < mem1.length; i++) {
			System.out.println(mem1[i] + "      " + mem2[i]);
		}
		
		System.out.println("-------------------------");
		System.out.println(media1/mem1.length + "      " + media2/mem2.length);
		*/
		
		
		//PARA VER
		
		System.out.println(
				executor.runGame(pacMan, ghosts, 50)
		);
		
		
	}
}
