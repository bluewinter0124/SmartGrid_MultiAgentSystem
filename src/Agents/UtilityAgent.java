package Agents;

import java.util.ArrayList;

import Behaviours.EnergyPriceAnnounce;
import Behaviours.TradeOperation;

public class UtilityAgent extends SmartGridAgent{
	
	public double[] energyPrice = 
		{
			3.0,3.0,4.0,3.0, //am 1:00
			3.0,3.0,3.0,3.0,
			3.0,3.0,3.0,3.0,
			3.0,3.0,3.0,3.0,
			3.0,3.0,3.0,3.0,
			3.0,3.0,3.0,3.0,
			3.0,3.0,3.0,3.0,
			3.0,3.0,3.0,3.0,
			4.0,4.0,4.0,4.0, //am 9:00
			4.0,4.0,4.0,4.0,
			4.0,4.0,4.0,4.0,
			4.0,4.0,4.0,4.0,
			4.0,4.0,4.0,4.0,
			4.0,4.0,4.0,4.0,
			4.0,4.0,4.0,4.0,
			4.0,4.0,4.0,4.0,
			4.0,4.0,4.0,4.0,
			4.0,4.0,4.0,4.0, //pm 6:00
			3.0,3.0,3.0,3.0,
			3.0,3.0,3.0,3.0,
			3.0,3.0,3.0,3.0,
			3.0,3.0,3.0,3.0,
			3.0,3.0,3.0,3.0,
			3.0,3.0,3.0,3.0,
		};
	
	public int currentPeriod = 0;
	
	public ArrayList<String> GIAList = new ArrayList<String>();

	public void Start()
	{
		this.agentType = this.AGENT_TYPE_UA;
		addBehaviour(new EnergyPriceAnnounce(this,15 * 1000));
	}	
	
	@Override
	public void Request()
	{
		String reciverMsg[] = this.receive_Message.split(";");
		String name = reciverMsg[1];
		String agentType = reciverMsg[3];
		
		switch(agentType)
		{
			case "2" :
				this.GIAList.add(name);
				this.SendMessage(0,this.getLocalName(),name,"REQUEST_ACCEPT");
				break;
				
			default: 
				System.out.println(this.getLocalName() + " : Can't Add Agent Type : " + agentType);
		}
	}
	
	
}
