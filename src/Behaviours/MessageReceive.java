package Behaviours;

import Agents.SmartGridAgent;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class MessageReceive extends Behaviour{

	SmartGridAgent agent = null;
	
	public MessageReceive(SmartGridAgent call_Agent)
	{
		agent = call_Agent;
	}
	
	@Override
	public void action() {
		//System.out.println("add message receive");
		ACLMessage msg = agent.receive();
		if(msg!=null)
        {
			agent.receive_Message = msg.getContent();
			//System.out.println(msg.getContent());
			agent.MessageHandle();
        }
	}

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return false;
	}

}
