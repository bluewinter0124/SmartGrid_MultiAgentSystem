package Agents;

import Behaviours.MessageReceive;
import old.Device;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

public class SmartGridAgent extends Agent{
	
	
	int agentType = -1;
	
	public final int MESSAGE_TYPE_INFORMATION = 0,MESSAGE_TYPE_DEMAND = 1,MESSAGE_TYPE_SUPPLY = 2,MESSAGE_TYPE_REQUEST = 3,
			MESSAGE_TYPE_COMMAND = 4,MESSAGE_TYPE_QUERY = 5,MESSAGE_TYPE_RESPONSE= 6;
	
	public final int AGENT_TYPE_AAA = 0,AGENT_TYPE_UA = 1,AGENT_TYPE_GIA = 2,AGENT_TYPE_CCA = 3,AGENT_TYPE_MIA = 4,
			AGENT_TYPE_LA = 5,AGENT_TYPE_GA = 6,AGENT_TYPE_SA = 7;
	
	
	
	public String masterAgent = "";
	public String receive_Message = "";
	
	public void Information()
	{
		
	}
	public void Demand()
	{

	}
	public void Supply()
	{

	}
	public void Request()
	{

	}
	public void Command()
	{

	}
	public void Query()
	{

	}
	public void Response()
	{
		
	}
	
	protected void setup() 
	{
		Object[] args = getArguments();
		masterAgent = (String)args[0];
		
		addBehaviour(new MessageReceive(this));
		
		Start();
		InitialJoinRequest();
	}
	
	public void Start()
	{

	}
	
	public void MessageHandle()
	{
		//System.out.println(this.getLocalName() + ":" + receive_Message);
		int type = Integer.parseInt(receive_Message.split(";")[0]);
		
		if(type == this.MESSAGE_TYPE_INFORMATION)
		{
			Information();
		}
		else if(type == this.MESSAGE_TYPE_DEMAND)
		{
			Demand();
		}
		else if(type == this.MESSAGE_TYPE_SUPPLY)
		{
			Supply();
		}
		else if(type == this.MESSAGE_TYPE_REQUEST)
		{
			Request();
		}
		else if(type == this.MESSAGE_TYPE_COMMAND)
		{
			Command();
		}
		else if(type == this.MESSAGE_TYPE_QUERY)
		{
			Query();
		}
		else if(type == this.MESSAGE_TYPE_RESPONSE)
		{
			Response();
		}
	}
	
	public void InitialJoinRequest()
	{
		if(!masterAgent.equals("NO"))
			this.SendMessage(MESSAGE_TYPE_REQUEST,this.getLocalName(),masterAgent,this.agentType+"");
	}
	
	public void SendMessage(int type,String form,String to,String content)
	{
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setContent(type+";"+form+";"+to+";"+content);
		msg.addReceiver(new AID(to, AID.ISLOCALNAME));
		this.send(msg);
	}

	
	
}
