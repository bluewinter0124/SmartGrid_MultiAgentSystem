package Behaviours;

import Agents.GlobalIntelligentAgent;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

public class TradeOperation extends TickerBehaviour{

	GlobalIntelligentAgent GIA = null;
	
	public TradeOperation(Agent a, long period) {
		super(a, period);
		// TODO Auto-generated constructor stub
		this.GIA = (GlobalIntelligentAgent)a;
	}

	@Override
	protected void onTick() {
		// TODO Auto-generated method stub
		GIA.DemandAmountList.clear();
		GIA.DemandNameList.clear();
		GIA.SupplyAmountList.clear();
		GIA.SupplyNameList.clear();
		
		System.out.println("---------------------------------- Period : "+ GIA.currentPeriod +" start----------------------------");
		System.out.println(GIA.getLocalName() + " : Optimization start!");
		
		int members = GIA.MIAList.size();
		
		for(int i = 0;i < members;i++)
		{
			GIA.SendMessage(0,GIA.getLocalName(),GIA.MIAList.get(i),"ENERGY_PRICE"+","+GIA.energyPrice);
			GIA.SendMessage(0,GIA.getLocalName(),GIA.MIAList.get(i),"TRADE_START");
		}

	}

}
