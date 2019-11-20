package es.ucm.fdi.ici.c1920.practica2.grupo06.maquinaestados;

import java.util.ArrayList;
import java.util.HashMap;

import es.ucm.fdi.ici.c1920.practica2.grupo06.Pair;
//import javafx.util.Pair;
import pacman.game.Game;
import pacman.game.Constants.MOVE;

public class StateEngine {

	private static StateEngine instance = null;
	private HashMap<StatePacman, ArrayList<Pair<Transactions, StatePacman>>> mapa = new HashMap<StatePacman, ArrayList<Pair<Transactions, StatePacman>>>();
	private ArrayList<StatePacman> stateList = new ArrayList<StatePacman>();
	private StatePacman currentState;

	public static StateEngine getInstance() {
		if (instance != null) {
			return instance;
		}
		instance = new StateEngine();
		return instance;
	}

	private StateEngine() {
		stateList.add(new Perseguir());// 0
		stateList.add(new moveToPowerPill());// 1
		stateList.add(new Huir());// 2
		stateList.add(new HuirPowerPill());// 3
		stateList.add(new moveToPill());// 4

		
		//Estado 0 - Perseguir
		ArrayList<Pair<Transactions, StatePacman>> auxList = new ArrayList<Pair<Transactions, StatePacman>>();
		auxList.add(new Pair<Transactions, StatePacman>(new GhostEdible(),stateList.get(0)));//perseguir
		auxList.add(new Pair<Transactions, StatePacman>(new TheGhostIsLair(),stateList.get(4)));//moveToPills
		auxList.add(new Pair<Transactions, StatePacman>(new GhostCercaPPCerca(),stateList.get(1)));//move to powerpill
		auxList.add(new Pair<Transactions, StatePacman>(new GhostCercaPPLejos(),stateList.get(2)));//huir
		auxList.add(new Pair<Transactions, StatePacman>(new GhostLejosPPCerca(),stateList.get(3)));//huir de la powerpill
		auxList.add(new Pair<Transactions, StatePacman>(new GhostLejosPPLejos(),stateList.get(4)));//moveToPills
		mapa.put(stateList.get(0), auxList);

		
		//Estado 1 - Moverse Power pill
		auxList = new ArrayList<Pair<Transactions, StatePacman>>();
		auxList.add(new Pair<Transactions, StatePacman>(new GhostEdible(),stateList.get(0)));//perseguir
		auxList.add(new Pair<Transactions, StatePacman>(new TheGhostIsLair(),stateList.get(4)));//moveToPills
		auxList.add(new Pair<Transactions, StatePacman>(new GhostCercaPPCerca(),stateList.get(1)));//move to powerpill
		auxList.add(new Pair<Transactions, StatePacman>(new GhostCercaPPLejos(),stateList.get(2)));//huir
		auxList.add(new Pair<Transactions, StatePacman>(new GhostLejosPPCerca(),stateList.get(3)));//huir de la powerpill
		auxList.add(new Pair<Transactions, StatePacman>(new GhostLejosPPLejos(),stateList.get(4)));//moveToPills
		mapa.put(stateList.get(1), auxList);

		//Estado 2 - Huir
		auxList = new ArrayList<Pair<Transactions, StatePacman>>();
		auxList.add(new Pair<Transactions, StatePacman>(new GhostEdible(),stateList.get(0)));//perseguir
		auxList.add(new Pair<Transactions, StatePacman>(new TheGhostIsLair(),stateList.get(4)));//moveToPills
		auxList.add(new Pair<Transactions, StatePacman>(new GhostCercaPPCerca(),stateList.get(1)));//move to powerpill
		auxList.add(new Pair<Transactions, StatePacman>(new GhostCercaPPLejos(),stateList.get(2)));//huir
		auxList.add(new Pair<Transactions, StatePacman>(new GhostLejosPPCerca(),stateList.get(3)));//huir de la powerpill
		auxList.add(new Pair<Transactions, StatePacman>(new GhostLejosPPLejos(),stateList.get(4)));//moveToPills
		mapa.put(stateList.get(2), auxList);

		// Estado 3 - HuirPowerPill
		auxList = new ArrayList<Pair<Transactions, StatePacman>>();
		auxList.add(new Pair<Transactions, StatePacman>(new GhostEdible(),stateList.get(0)));//perseguir
		auxList.add(new Pair<Transactions, StatePacman>(new TheGhostIsLair(),stateList.get(4)));//moveToPills
		auxList.add(new Pair<Transactions, StatePacman>(new GhostCercaPPCerca(),stateList.get(1)));//move to powerpill
		auxList.add(new Pair<Transactions, StatePacman>(new GhostCercaPPLejos(),stateList.get(2)));//huir
		auxList.add(new Pair<Transactions, StatePacman>(new GhostLejosPPCerca(),stateList.get(3)));//huir de la powerpill
		auxList.add(new Pair<Transactions, StatePacman>(new GhostLejosPPLejos(),stateList.get(4)));//moveToPills
		mapa.put(stateList.get(3), auxList);

		//Estado 4 -MoveToPill
		auxList = new ArrayList<Pair<Transactions, StatePacman>>();
		auxList.add(new Pair<Transactions, StatePacman>(new GhostEdible(),stateList.get(0)));//perseguir
		auxList.add(new Pair<Transactions, StatePacman>(new TheGhostIsLair(),stateList.get(4)));//moveToPills
		auxList.add(new Pair<Transactions, StatePacman>(new GhostCercaPPCerca(),stateList.get(1)));//move to powerpill
		auxList.add(new Pair<Transactions, StatePacman>(new GhostCercaPPLejos(),stateList.get(2)));//huir
		auxList.add(new Pair<Transactions, StatePacman>(new GhostLejosPPCerca(),stateList.get(3)));//huir de la powerpill
		auxList.add(new Pair<Transactions, StatePacman>(new GhostLejosPPLejos(),stateList.get(4)));//moveToPills
		mapa.put(stateList.get(4), auxList);
		currentState = stateList.get(0);
	}

	public MOVE run(Game game) {
		ArrayList<Pair<Transactions, StatePacman>> array = mapa.get(currentState);
		Boolean ok = false;
		int i = 0;
		while (!ok && i < array.size()) {//busco  si se cumple alguna de las transiciones
			Transactions t=array.get(i).getKey();
			ok = t.check(game);
			if (!ok) {
				i++;
			}
		}
		if(ok) {
			System.out.println(array.get(i).getKey()+" " + array.get(i).getValue());
			
			System.out.println(array.get(i).getKey().getGhost());
			
			currentState=array.get(i).getValue();
		}
		else 
		{
			System.out.println("No se ha encontrado estado siguiente");
		}
			
		return currentState.move(game);
	}

}
