package old;


import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GA extends Agent {
	boolean transaction = false;

	protected void setup() {
		//System.out.println("Hello! My name is " + getLocalName());
		addBehaviour(new CyclicBehaviour(this) { // SEND MSG TO MIA
			int hour = 0;
			Object[] args = getArguments();
			public void action() {
				if (transaction == true) {
					transaction = false;
					
					int random = (int) ((Math.random() * 100 + 1)*10)%800+200;
					//System.out.println(hour + ":00 " + getLocalName() + " supply " + random + " KW");
					ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
					msg.setConversationId("GENERATOR1");
					msg.setContent(new String(Integer.toString(random)));
					msg.addReceiver(new AID((String) args[0], AID.ISLOCALNAME));
					myAgent.send(msg);
					hour += 2;
					if (hour == 26) {
						hour = 0;
					}
				}

			}
		});
		addBehaviour(new GARec(this));
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
class GARec extends CyclicBehaviour // RECEIVE MSG FROM MIA
{
	Agent agent;
	int i = 0;
	public GARec(Agent a) {
		agent = a;
	}
	public void action() {
		MessageTemplate mt = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.INFORM),
				MessageTemplate.MatchConversationId("MIASnd2"));
		ACLMessage msg = agent.receive(mt);
		if (msg != null) {
			//System.out.println(i + "get money");
			i++;
		}
	}
}