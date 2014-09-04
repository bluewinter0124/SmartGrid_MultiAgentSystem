package Behaviours;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import Agents.LoadAgent;
import Agents.SmartGridAgent;
import Agents.UtilityAgent;

public class LoadDataReceive extends TickerBehaviour{
	
	LoadAgent LA = null;

	String inputFormDM = "";
	
	public LoadDataReceive(Agent a, long period) {
		super(a, period);
		// TODO Auto-generated constructor stub
		this.LA = (LoadAgent)a;
	}
	
	@Override
	protected void onTick() {
	}

}
