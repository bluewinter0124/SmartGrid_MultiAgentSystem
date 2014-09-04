package Agents;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import Behaviours.MessageReceive;

public class AddressAllocateAgent extends SmartGridAgent {

	String AAAIP ;
	int LAPort = 10000;
	int GAPort = 20000;
	int SAPort = 30000;
	
	HashMap<String,String> agentAddress = new HashMap<String,String>();
	
	public void Start()
	{
		this.agentType = this.AGENT_TYPE_AAA;
	}
	
	public void Information()
	{
		String reciverMsg[] = this.receive_Message.split(";");
		String form = reciverMsg[1];
		String to = reciverMsg[2];
		String content[] = reciverMsg[3].split(",");
		
		switch(content[0])
		{		
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
		int port;
		switch(agentType)
		{
			case "5" :
				port = this.LAPort++;
				this.SendMessage(0,this.getLocalName(),name,"PORT_ALLOCATE"+","+port);
				break;
			case "6" :
				port = this.GAPort++;
				this.SendMessage(0,this.getLocalName(),name,"PORT_ALLOCATE"+","+port);
				break;
			case "7" :
				port = this.SAPort++;
				this.SendMessage(0,this.getLocalName(),name,"PORT_ALLOCATE"+","+port);
				break;
				
			default: 
				System.out.println(this.getLocalName() + " : Can't allocata port to Agent type : " + agentType);
		}
		
	}

}
