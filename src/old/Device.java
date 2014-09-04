package old;


public class Device {
	private String name;
	private boolean state;
	private int priority;
	private int consumption,realconsumption;
	private boolean critical;
	public static String[] table = {new String("fan"),new String("computer"),new String("refirgerator"),new String("light"),
			new String("airconditioner"),new String("washing machine"),new String("hairdryer"),new String("TV"),new String("dishwasher")};
	public Device(String a,boolean b,int c,boolean d){
		this.name = a;
		this.state = b;
		this.priority = c;
		this.critical = d;		
	}
	public String getName(){
		return name;
	}
	public boolean getState(){
		return state;
	}
	public int getPriority(){
		return priority;
	}
	public int getConsumption(){	
		this.consumption = (int) (Math.random()*100);
		realconsumption = consumption;
		return consumption;
	}
	public boolean getCritical(){
		return critical;
	}
	public int getRealConsumption(){
		return 	realconsumption;
	}
}
