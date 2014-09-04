package Agents;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class MicrogridIntelligentAgent extends SmartGridAgent{

	int MIANumber = -1;
	
	double thresholdPrice = 3.5;
	double energyPrice = 3.0;
	
	boolean loadShedding = false;
	
	ArrayList<String> LAList = new ArrayList<String>();
	ArrayList<Double> LoadAmountList = new ArrayList<Double>();
	public int LAReceiveCount = 0;
	
	ArrayList<String> GAList = new ArrayList<String>();
	ArrayList<Double> GenerationAmountList = new ArrayList<Double>();
	public int GAReceiveCount = 0;
	
	ArrayList<String> SAList = new ArrayList<String>();

	
	@Override
	public void Start()
	{
		this.agentType = this.AGENT_TYPE_MIA;
		MIANumber = Integer.parseInt(this.getLocalName().split("_")[1]);
	}
		
	@Override
	public void Information()
	{
		String reciverMsg[] = this.receive_Message.split(";");
		String form = reciverMsg[1];
		String to = reciverMsg[2];
		String content[] = reciverMsg[3].split(",");
		
		switch(content[0])
		{
			case "REQUEST_ACCEPT" :
				System.out.println(this.getLocalName() + " : Success Join to " + this.masterAgent);
				break;
			case "TRADE_START" :
				//System.out.println(this.getLocalName() + " : Receive trade start info");
				TradeStart();
				break;
			case "ENERGY_PRICE" :
				//System.out.println(this.getLocalName() + " : Receive Energy Price :" + content[1]);
				energyPrice = Double.parseDouble(content[1]);
				
				//load shedding on/off
				if(energyPrice > thresholdPrice && !this.loadShedding)
				{
					this.loadShedding = true;
					System.out.println(this.getLocalName() + " : Load Shedding!");
				}
				else if(energyPrice <= thresholdPrice && this.loadShedding)
				{
					this.loadShedding = false;
				}
				
				//announce now mode
				if(this.loadShedding)
				{
					for(int n = 0;n<this.LAList.size();n++)
					{
						SendMessage(0,this.getLocalName(),this.LAList.get(n),"LOAD_SHEDDING");
					}
				}
				else
				{
					for(int n = 0;n<this.LAList.size();n++)
					{
						SendMessage(0,this.getLocalName(),this.LAList.get(n),"LOAD_NORMAL");
					}
				}
				
				break;
				
			default: 
				System.out.println(this.getLocalName() + " : Unknow Information : " + content);
		}
		
	}
	
	public void Request()
	{
		String reciverMsg[] = this.receive_Message.split(";");
		String name = reciverMsg[1];
		String agentType = reciverMsg[3];
		
		switch(agentType)
		{
			case "5" :
				this.LAList.add(name);
				this.SendMessage(0,this.getLocalName(),name,"REQUEST_ACCEPT");
				break;
			case "6" :
				this.GAList.add(name);
				this.SendMessage(0,this.getLocalName(),name,"REQUEST_ACCEPT");
				break;
			case "7" :
				this.SAList.add(name);
				this.SendMessage(0,this.getLocalName(),name,"REQUEST_ACCEPT");
				break;
				
			default: 
				System.out.println(this.getLocalName() + " : Can't Add Agent Type : " + agentType);
		}
	}
	
	public void Supply()
	{
		String reciverMsg[] = this.receive_Message.split(";");
		String name = reciverMsg[1];
		double amount = Double.parseDouble((reciverMsg[3]));
		
		//System.out.println(this.getLocalName() + " : Receive Supply " + amount +" form : " + name);

		this.GenerationAmountList.add(amount);
		
		GAReceiveCount++;
		
		if(LAReceiveCount >= LAList.size() && GAReceiveCount >= GAList.size() )
		{
			ReplyToGIA();
		}
	}
	
	@Override
	public void Demand()
	{
		String reciverMsg[] = this.receive_Message.split(";");
		String name = reciverMsg[1];
		double amount = Double.parseDouble(reciverMsg[3]);
		
		//System.out.println(this.getLocalName() + " : Receive Demand " + amount +" form : " + name);
		
		LoadAmountList.add(amount);

		LAReceiveCount++;
		
		if(LAReceiveCount >= LAList.size() && GAReceiveCount >= GAList.size() )
		{
			ReplyToGIA();
		}
	}
	
	public void TradeStart()
	{
		this.LoadAmountList.clear();
		this.GenerationAmountList.clear();
		
		QueryToAllAgent();
		
	}
	public void QueryToAllAgent()
	{
		for(int a = 0;a<this.LAList.size();a++)
		{
			this.SendMessage(this.MESSAGE_TYPE_QUERY, this.getLocalName(),LAList.get(a),"AMOUNT");
		}
		
		for(int a = 0;a<this.GAList.size();a++)
		{
			this.SendMessage(this.MESSAGE_TYPE_QUERY, this.getLocalName(),GAList.get(a),"AMOUNT");
		}
	}
	
	public void ReplyToGIA()
	{
		this.LAReceiveCount = 0;
		this.GAReceiveCount = 0;
		
		double totalLoad = 0,totalGeneration = 0;
		
		DecimalFormat df=new DecimalFormat("#.#"); 
		
		for(int a = 0;a<this.LoadAmountList.size();a++)
		{
			totalLoad += this.LoadAmountList.get(a);
		}
		
		for(int a = 0;a<this.GenerationAmountList.size();a++)
		{
			totalGeneration += this.GenerationAmountList.get(a);	
		}
		
		if(totalLoad > totalGeneration)
		{
			double amount = totalLoad - totalGeneration;
			this.SendMessage(this.MESSAGE_TYPE_DEMAND, this.getLocalName(), this.masterAgent, df.format(amount)+"");
		}
		else
		{
			double amount = totalGeneration - totalLoad ;
			this.SendMessage(this.MESSAGE_TYPE_SUPPLY, this.getLocalName(), this.masterAgent, df.format(amount)+"");
		}
		
	}
	
	
}
