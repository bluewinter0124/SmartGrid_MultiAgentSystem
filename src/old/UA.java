package old;


import java.util.Scanner;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class UA extends Agent {
	private int[] cost = { 2, 2, 2, 2, 2, 2, 2, 2, 4, 4, 4, 4, 4, 4, 4, 4, 4,
			4, 4, 4, 4, 4, 2, 2 };
	private int hour = 0;
	AMSAgentDescription[] agents = null;
	private boolean island = false;

	protected void setup() {

		addBehaviour(new TickerBehaviour(this, 5000) { // TIMER
			protected void onTick() {
				try {
					SearchConstraints c = new SearchConstraints();
					c.setMaxResults(new Long(-1));
					agents = AMSService.search(myAgent,
							new AMSAgentDescription(), c);
				} catch (Exception e) {
					System.out.println("Problem searching AMS: " + e);
					e.printStackTrace();
				}
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				if (!island) {
					msg.setConversationId("START");
					msg.setContent(Integer.toString(cost[hour])+" "+Integer.toString(hour));
				} else if (island) {
					msg.setConversationId("ISLAND");
					msg.setContent(Integer.toString(hour));
				}
				for (int i = 0; i < agents.length; i++) {
					msg.addReceiver(agents[i].getName());
				}
				send(msg);
				// System.out.println("send");
				hour++;
				if (hour == 24) {
					hour = 0;
				}
			}
		});
		addBehaviour(new CyclicBehaviour(this) {
			public void action() {
				Object[] args = getArguments();
				if (args[0].equals("1")) {
					island = false;
				} else if (args[0].equals("2")) {
					island = true;
				} else if (args[0].equals("3")) {
					if (hour == 6 ) {
						 //System.out.println("mode : Utility");
						island = false;
					} else if (hour == 3) {
						// System.out.println("mode : island");
						island = true;
					}
				}

			}
		});
	}
}