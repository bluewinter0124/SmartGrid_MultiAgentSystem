package Agents;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.NumberFormat;

import HomeOptimization.MonitorData;

public class GeneratorAgent extends SmartGridAgent{
	
	int GANumber = -1;
	int agentScale = 1;
	String agentName = "";
	MonitorData monitorData;
	
	double currentGeneration = 0.0;
	
	String IP;
	int Port;
	ServerSocket S_socket;
	Socket socket;
	BufferedReader message_in = null;
	String inputFormDM = "";
	
	@Override
	public void Start()
	{
		Object[] args = getArguments();
		
		agentType = this.AGENT_TYPE_GA;
		agentName = this.getLocalName(); 
		GANumber = Integer.parseInt(this.getLocalName().split("_")[1]);
		agentScale = Integer.parseInt((String)args[1]);
		
		monitorData = new  MonitorData();
		
		this.SendMessage(this.MESSAGE_TYPE_REQUEST,this.getLocalName(),"AAA",this.agentType+"");
		
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
						//System.out.println("Receive Genaration : " + inputFormDM + " from DM");
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
				double amountOfGeneration = 0;
				
				if(this.monitorData.GetLatestData("GENERATION") != null)
					amountOfGeneration = Double.parseDouble(this.monitorData.GetLatestData("GENERATION"));

				amountOfGeneration *= agentScale;
				
				this.SendMessage(this.MESSAGE_TYPE_SUPPLY, getLocalName(), masterAgent, amountOfGeneration + "");
				
				break;
		}
		
	}
}
