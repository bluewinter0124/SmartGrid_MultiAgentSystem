package Behaviours;

import Agents.UtilityAgent;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

public class EnergyPriceAnnounce extends TickerBehaviour{
	
	UtilityAgent UA = null;

	public EnergyPriceAnnounce(Agent a, long period) {
		super(a, period);
		// TODO Auto-generated constructor stub
		this.UA = (UtilityAgent)a;
	}

	@Override
	protected void onTick() {
		// TODO Auto-generated method stub
		//System.out.println(UA.getLocalName() + " : Price Announce : " + UA.energyPrice[UA.currentPeriod]);
		
		int members = UA.GIAList.size();
		
		for(int i = 0;i < members;i++)
		{
			UA.SendMessage(0,UA.getLocalName(),UA.GIAList.get(i),"ENERGY_PRICE"+","+UA.energyPrice[UA.currentPeriod]);
		}
		
		UA.currentPeriod++;
	}

}
