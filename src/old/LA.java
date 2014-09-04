package old;



import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class LA extends Agent {
	boolean transaction = false;

	protected void setup() {
		int num = (int) (Math.random() * 10+1);		
		//System.out.println("Hello! My name is " + getLocalName());
		final Device[] devices = new Device[num];
		for (int i = 0; i < num; i++) {
			devices[i] = new Device(Device.table[(int) (Math.random() * 9)],
					true, 2, false);
		}	
		addBehaviour(new CyclicBehaviour(this){		//SEND MSG TO MIA
			int total=0;
			int hour = 0;
			Object[] args = getArguments();
			public void action() {
				if (transaction == true) {
					transaction = false;
					/*for (int i = 0; i < devices.length; i++) {
						//System.out.println(hour + ":00 " + devices[i].getName() + " need " + devices[i].getConsumption() + " KW");
						total += devices[i].getRealConsumption();
					}*/
					int random = (int) (Math.random() * 100 + 1)*10;
					ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
					msg.setConversationId("LOAD1");
					//msg.setContent(new String(Integer.toString(total)));
					msg.setContent(new String(Integer.toString(random)));
					msg.addReceiver(new AID((String) args[0], AID.ISLOCALNAME));
					myAgent.send(msg);
					hour += 2;
					total = 0;
					if (hour == 26) {
						hour = 0;
					}					
				}
				
			}
		});
		addBehaviour(new LARec(this));
		addBehaviour(new CyclicBehaviour() {	//RECEIVE MSG FROM TIMER
			public void action() {
				MessageTemplate mt = MessageTemplate.or(
						MessageTemplate.MatchConversationId("ISLAND"),
						MessageTemplate.MatchConversationId("START"));
				ACLMessage msg = myAgent.receive(mt);
				if (msg != null) {
					transaction = true;
				}
			}
		});
	}
}

class LARec extends CyclicBehaviour // RECEIVE MSG FROM MIA
{
	Agent agent;
	int i = 0;
	public LARec(Agent a) {
		agent = a;
	}
	public void action() {
		MessageTemplate mt = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.INFORM),
				MessageTemplate.MatchConversationId("MIASnd2"));
		ACLMessage msg = agent.receive(mt);
		if (msg != null) {
			//System.out.println(i + "get supply");
			i++;
		}
	}
}