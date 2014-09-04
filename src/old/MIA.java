package old;


import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class MIA extends Agent {
	RecTable[] rectable1 = new RecTable[20];
	RecTable[] rectable2 = new RecTable[20];
	int req = 0, sup = 0, useinfo = 0, supinfo = 0;
	String time;
	int demand = 3;
	boolean island = false, timeout = false;
	protected boolean transaction;

	protected void setup() {
		//System.out.println(getLocalName() + " is ready");
		final Object[] args = getArguments();
		addBehaviour(new OneShotBehaviour(this) { // REGISTRY TO THE GIA
			public void action() {
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				String[] content = ((String) args[0]).split(" ");
				msg.addReceiver(new AID(content[0], AID.ISLOCALNAME));
				msg.setContent(content[1] + " " + content[2]);
				msg.setConversationId("REGISTRY");
				send(msg);
			}
		});
		addBehaviour(new CyclicBehaviour(this) // GET MESSAGE FROM LA
		{
			public void action() {
				if (!timeout) {
					MessageTemplate t1 = MessageTemplate.and(MessageTemplate
							.MatchPerformative(ACLMessage.INFORM),
							MessageTemplate.MatchConversationId("LOAD1"));
					ACLMessage msg = receive(t1);
					if (msg != null) {
						/*
						 * System.out.println(myAgent.getLocalName() +
						 * " get info " + msg.getContent() + " from " +
						 * msg.getSender());
						 */
						rectable1[req] = new RecTable(msg.getSender(),
								Integer.parseInt(msg.getContent()));
						req++;
						useinfo = useinfo + Integer.parseInt(msg.getContent());
					}
				}
			}
		});
		addBehaviour(new CyclicBehaviour(this) // GET MESSAGE FROM GA
		{
			public void action() {
				if (!timeout) {
					MessageTemplate mt = MessageTemplate.and(MessageTemplate
							.MatchPerformative(ACLMessage.INFORM),
							MessageTemplate.MatchConversationId("GENERATOR1"));
					ACLMessage msg = receive(mt);
					if (msg != null) {
						// System.out.println(myAgent.getLocalName() +
						// " get info "
						// + msg.getContent() + " from " + msg.getSender());
						rectable2[sup] = new RecTable(msg.getSender(),
								Integer.parseInt(msg.getContent()));
						sup++;
						supinfo = supinfo + Integer.parseInt(msg.getContent());
					}
				}
			}
		});
		addBehaviour(new CyclicBehaviour(this) { // SEND MSG TO GIA OR LA GA
			public void action() {
				if (timeout) {
					if (useinfo > supinfo) {
						demand = 1;
					} else if (useinfo < supinfo) {
						demand = 2;
					} else if (useinfo == supinfo) {
						demand = 3;
					}
					if (!island) { // MAIN SUPPLY
						if (demand == 1) {
							ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
							String[] content = ((String) args[0]).split(" ");
							msg.addReceiver(new AID(content[0], AID.ISLOCALNAME));
							msg.setContent(Integer.toString(useinfo - supinfo)+" "+useinfo+" "+supinfo);
							msg.setConversationId("NEED");
							send(msg);
						} else if (demand == 2) {
							ACLMessage msg2 = new ACLMessage(ACLMessage.INFORM);
							String[] content = ((String) args[0]).split(" ");
							msg2.addReceiver(new AID(content[0],
									AID.ISLOCALNAME));
							msg2.setContent(Integer.toString(supinfo - useinfo)+" "+useinfo+" "+supinfo);
							msg2.setConversationId("SUPPLY");
							send(msg2);
						}
					} else if (island) { // ISLAND MODE
						try {
							FileWriter fw = new FileWriter(
									"C:/Users/user/workspace/Jade/island.txt", true);
							BufferedWriter bw = new BufferedWriter(fw); // 將BufferedWeiter與FileWrite物件做連結
							bw.write(getLocalName() + " "
									+ Integer.toString(supinfo - useinfo) + " "
									+ time + ":00");
							bw.newLine();
							bw.close();
							System.out.println(getLocalName() + " "
									+ Integer.toString(supinfo - useinfo) + " "
									+ time + ":00");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					timeout = false;
					req = 0;
					sup = 0;
					useinfo = 0;
					supinfo = 0;
				}
			}
		});
		addBehaviour(new CyclicBehaviour() { // GET MSG FROM TIMER
			public void action() {
				MessageTemplate mt = MessageTemplate.and(
						MessageTemplate.MatchPerformative(ACLMessage.INFORM),
						MessageTemplate.MatchConversationId("START"));
				ACLMessage msg = myAgent.receive(mt);
				if (msg != null) {
					String[] content = msg.getContent().split(" ");
					time = content[1];
					myAgent.addBehaviour(new WakerBehaviour(myAgent, 1500) {
						protected void handleElapsedTimeout() {
							timeout = true;
							island = false;

						}
					});
				}
				mt = MessageTemplate.and(
						MessageTemplate.MatchPerformative(ACLMessage.INFORM),
						MessageTemplate.MatchConversationId("ISLAND"));
				msg = myAgent.receive(mt);
				if (msg != null) {
					time = msg.getContent();
					myAgent.addBehaviour(new WakerBehaviour(myAgent, 1000) {
						protected void handleElapsedTimeout() {
							timeout = true;
							island = true;
						}
					});
				}
			}
		});
		addBehaviour(new CyclicBehaviour(this) // GET MESSAGE FROM GIA
		{
			public void action() {
				MessageTemplate mt = MessageTemplate.and(
						MessageTemplate.MatchPerformative(ACLMessage.INFORM),
						MessageTemplate.MatchConversationId("REPPLY"));
				ACLMessage msg = myAgent.receive(mt);
				if (msg != null) {
					// if(demand){
					// SAVE THE DEAL TO THE FILE
					// System.out.println(getLocalName() + " " +
					// msg.getContent());
					try {
						FileWriter fw = new FileWriter(
								"C:/Users/user/workspace/Jade/need.txt", true);
						BufferedWriter bw = new BufferedWriter(fw); // 將BufferedWeiter與FileWrite物件做連結
						bw.write(getLocalName() + " " + msg.getContent() + " "
								+ time + ":00");
						bw.newLine();
						bw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					// }
					// else {
					// SAVE THE DEAL TO THE FILE

					// }
				}
				mt = MessageTemplate.and(
						MessageTemplate.MatchPerformative(ACLMessage.INFORM),
						MessageTemplate.MatchConversationId("REPPLY2"));
				msg = myAgent.receive(mt);
				if (msg != null) {
					// System.out.println(getLocalName() + msg.getContent());
					try {
						FileWriter fw = new FileWriter(
								"C:/Users/user/workspace/Jade/supply.txt", true);
						BufferedWriter bw = new BufferedWriter(fw); // 將BufferedWeiter與FileWrite物件做連結
						bw.write(getLocalName() + " " + msg.getContent() + " "
								+ time + ":00");
						bw.newLine();
						bw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

}
