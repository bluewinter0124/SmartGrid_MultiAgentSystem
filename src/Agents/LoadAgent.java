package Agents;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import Behaviours.EnergyPriceAnnounce;
import Behaviours.MessageReceive;
import HomeOptimization.*;
import HomeOptimization.MonitorData;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;

public class LoadAgent extends SmartGridAgent{

	int LANumber = -1;
	int agentScale = 1;
	String agentName = "";
	MonitorData monitorData;
	
	double currentConsumption = 0.0;
	boolean loadShedding = false;
	
	String IP;
	int Port;
	ServerSocket S_socket;
	Socket socket;
	BufferedReader message_in = null;
	String inputFormDM = "";
	
	AirConditionerOptimization aco;
	LightControlOptimization lco;
	
	@Override
	public void Start()
	{
		Object[] args = getArguments();
		
		//Initial agent profile
		agentType = this.AGENT_TYPE_LA;
		agentName = this.getLocalName(); 
		LANumber = Integer.parseInt(this.getLocalName().split("_")[1]);
		agentScale = Integer.parseInt((String)args[1]);
		
		//Initial monitor data and optimization object
		monitorData = new  MonitorData();
		aco = new AirConditionerOptimization(monitorData);
		lco = new LightControlOptimization(monitorData);
		
		/*monitorData.PutData("PIR", "0_1_0_0_0_0_0_0_0_0_0_0");
		System.out.println("Light Command : " + lco.Optimize());
		monitorData.PutData("PIR", "1_1_1_0_0_0_0_0_0_0_0_0");
		System.out.println("Light Command : " + lco.Optimize());
		monitorData.PutData("PIR", "0_1_0_0_0_0_0_0_1_0_1_0");
		System.out.println("Light Command : " + lco.Optimize());*/
		
		//Get port number from agent AAA
		this.SendMessage(this.MESSAGE_TYPE_REQUEST,this.getLocalName(),"AAA",this.agentType+"");
	}

	public void OptimizationTask()
	{
		double optimizedTemperature = aco.Optimize();
		if(optimizedTemperature != -1)
		{
			if(optimizedTemperature == 0)
			{
				System.out.println("Shut down air conditioner");	
			}
			else
			{
				System.out.println("Set air conditioner to optimized temperature : " + optimizedTemperature);	
			}
		}

	}
	
	Thread DataReceive = new Thread(new Runnable() {
		 @Override
		 public void run() {

			 try {
					
					IP = InetAddress.getLocalHost().getHostAddress();
					S_socket = new ServerSocket(Port);
					
					System.out.println(agentName + " : IP : " + IP +", Port : " + Port);
					
					socket = S_socket.accept();
					System.out.println("Connect!");
					
					message_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

					String dataFormDM = "";
					
					while(true)
					{
						dataFormDM = message_in.readLine();
						String dataType = dataFormDM.split(":")[0];
						String dataValue = dataFormDM.split(":")[1];
						
						if(monitorData.PutData(dataType,dataValue))
							System.out.println("Receive data : " + dataType + " : " + dataValue);
						
						if(dataType.equals("PIR"))
						{
							String lightCommand = lco.Optimize();
							System.out.println("Light Command : " + lightCommand);
						}
					}
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 
	}});
	
	
	
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
				
			case "PORT_ALLOCATE" :
				this.Port = Integer.parseInt((content[1]));
				DataReceive.start();
				break;
				
			case "LOAD_SHEDDING" :
				this.loadShedding = true;
				break;
				
			case "LOAD_NORMAL" :
				this.loadShedding = false;
				break;
			
			default: 
				System.out.println(this.getLocalName() + " : Unknow Information : " + content);
		}

	}
	
	@Override
	public void Query()
	{
		String reciverMsg[] = this.receive_Message.split(";");
		String form = reciverMsg[1];
		String to = reciverMsg[2];
		String content[] = reciverMsg[3].split(",");
		
		System.out.println(this.getLocalName() + " : Receive query from : " + form);
		
		switch(content[0])
		{
			case "AMOUNT" :
				
				OptimizationTask();
				double amountOfConsumption = 0;
				
				if(this.monitorData.GetLatestData("CONSUMPTION") != null)
					amountOfConsumption = Double.parseDouble(this.monitorData.GetLatestData("CONSUMPTION"));
				
				if(this.loadShedding)
				{
					amountOfConsumption *= 0.7;
				}

				amountOfConsumption *= agentScale;
				
				this.SendMessage(this.MESSAGE_TYPE_DEMAND, getLocalName(), masterAgent, amountOfConsumption + "");
				
				break;
		}
		
	}
}


