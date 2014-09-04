package Agents;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import Behaviours.TradeOperation;

public class GlobalIntelligentAgent extends SmartGridAgent{
	
	int GIANumber = -1;
	public int currentPeriod = 0;
	public double energyPrice = 3.0;
	public int tradeInterval = 10 * 1000;
	
	public ArrayList<String> CCAList = new ArrayList<String>();
	public ArrayList<String> MIAList = new ArrayList<String>();
	public ArrayList<String> SupplyNameList = new ArrayList<String>();
	public ArrayList<Double> SupplyAmountList = new ArrayList<Double>();
	public ArrayList<String> DemandNameList = new ArrayList<String>();
	public ArrayList<Double> DemandAmountList = new ArrayList<Double>();
	
	public int receiveCount = 0;
	
	@Override
	public void Start()
	{
		this.agentType = this.AGENT_TYPE_GIA;
		addBehaviour(new TradeOperation(this,tradeInterval));
	}	
	
	@Override
	public void Information()
	{
		String reciverMsg[] = this.receive_Message.split(";");
		String content[] = reciverMsg[3].split(",");
		
		switch(content[0])
		{
			case "REQUEST_ACCEPT" :
				System.out.println(this.getLocalName() + " : Success Join to "+this.masterAgent);
				break;
				
			case "ENERGY_PRICE" :
				//System.out.println(this.getLocalName() + " : Receive Energy Price :" + content[1]);
				energyPrice = Double.parseDouble(content[1]);
				break;
				
			default: 
				System.out.println(this.getLocalName() + " : Unknow Information : " + content);
		}
		
	}

	
	@Override
	public void Request()
	{
		String reciverMsg[] = this.receive_Message.split(";");
		String name = reciverMsg[1];
		String agentType = reciverMsg[3];
		
		switch(agentType)
		{
			case "3" :
			this.CCAList.add(name);
			this.SendMessage(0,this.getLocalName(),name,"REQUEST_ACCEPT");
			break;
		
			case "4" :
				this.MIAList.add(name);
				this.SendMessage(0,this.getLocalName(),name,"REQUEST_ACCEPT");
				break;
				
			default: 
				System.out.println(this.getLocalName() + " : Can't add agent type : " + agentType);
		}
	}
	
	@Override
	public void Supply()
	{
		String reciverMsg[] = this.receive_Message.split(";");
		String name = reciverMsg[1];
		double amount = Double.parseDouble((reciverMsg[3]));
		
		System.out.println(this.getLocalName() + " : Receive supply " + amount +" from : " + name);

		this.SupplyAmountList.add(amount);
		this.SupplyNameList.add(name);

		receiveCount++;
		if(receiveCount >= MIAList.size())
		{
			receiveCount = 0;
			Match();
		}

	}
	
	@Override
	public void Demand()
	{
		String reciverMsg[] = this.receive_Message.split(";");
		String name = reciverMsg[1];
		double amount = Double.parseDouble(reciverMsg[3]);
		
		System.out.println(this.getLocalName() + " : Receive demand " + amount +" from : " + name);
		
		this.DemandAmountList.add(amount);
		this.DemandNameList.add(name);
		
		receiveCount++;
		
		if(receiveCount >= MIAList.size())
		{
			receiveCount = 0;
			Match();
		}
	}
	
	private void Match() {
		// TODO Auto-generated method stub
		System.out.println("Matching start!");

		double totalSupply = 0;
		double totalDemand = 0;
		double diff = 0;
		
		DecimalFormat df=new DecimalFormat("#.#"); 
		
		for(int i=0;i<this.SupplyAmountList.size();i++)
		{
			totalSupply += SupplyAmountList.get(i);
		}
		
		for(int i=0;i<this.DemandAmountList.size();i++)
		{
			totalDemand += DemandAmountList.get(i);
		}
		
		System.out.println("Total supply = " + totalSupply);
		System.out.println("Total demand = " + totalDemand);
		
		
		if(totalSupply > totalDemand)
		{
			diff = totalSupply - totalDemand;
			totalDemand += diff;
			this.DemandAmountList.add(diff);
			this.DemandNameList.add("Utility");
		}
		else if (totalSupply < totalDemand)
		{
			diff = totalDemand - totalSupply;
			totalSupply += diff;
			this.SupplyAmountList.add(diff);
			this.SupplyNameList.add("Utility");
		}
		
		for(int x =0;x<SupplyAmountList.size();x++)
		{
			double supply = SupplyAmountList.get(x);
			
			for(int i=0;i<this.DemandAmountList.size();i++)
			{
				double supplyAmount = supply * this.DemandAmountList.get(i) / totalDemand;
				System.out.println(this.SupplyNameList.get(x) + " -> " + this.DemandNameList.get(i) + " : " + df.format(supplyAmount));
			}
		}
		
		System.out.println("Matching Finish!!");
		System.out.println("---------------------------------- Period : "+ this.currentPeriod++ +" end -----------------------------");
	}

	
}


