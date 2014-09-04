package old;


import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GIA extends Agent {
	RecTable[] rectable1 = new RecTable[20];
	RecTable[] rectable2 = new RecTable[20];
	PriceTable[] table = new PriceTable[10];
	int sup = 0, req = 0, num = 0;
	String price;
	boolean timeout = false;

	protected void setup() {
		//System.out.println("My agent name is:" + getLocalName());
		addBehaviour(new CyclicBehaviour(this) { // RECEIVE REGISTRY FROM MIA
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("REGISTRY"));

			public void action() {
				ACLMessage msg = receive(mt);
				if (msg != null) {
					//System.out.println("mia regisrty");
					String[] content = msg.getContent().split(" ");
					table[num] = new PriceTable(msg.getSender(),
							Integer.parseInt(content[0]),
							Integer.parseInt(content[1]));
					num++;
				}
			}
		});
		addBehaviour(new CyclicBehaviour(this) { // RECEIVE MSG FROM MIA (NEED)
			public void action() {
				if (!timeout) {
					MessageTemplate fs = MessageTemplate.and(MessageTemplate
							.MatchPerformative(ACLMessage.INFORM),
							MessageTemplate.MatchConversationId("NEED"));
					ACLMessage msg = receive(fs);
					if (msg != null) {
						String[] content2 = msg.getSender().toString()
								.split(" ");
						int i = content2[3].indexOf('@');
						for (int j = 0; j < i; j++) {
							System.out.print(content2[3].charAt(j));
						}
						String[] content1 = msg.getContent()
								.split(" ");
						System.out.println(": request " + content1[0]+" (load:"+content1[1]+" gen:"+content1[2]+")");
						rectable1[req] = new RecTable(msg.getSender(),
								Integer.parseInt(content1[0]));
						req++;
					} else {
					}
				}
			}
		});
		addBehaviour(new CyclicBehaviour(this) { // RECEIVE MSG FROM MIA
													// (SUPPLY)
			public void action() {
				if (!timeout) {
					MessageTemplate fn = MessageTemplate.and(MessageTemplate
							.MatchPerformative(ACLMessage.INFORM),
							MessageTemplate.MatchConversationId("SUPPLY"));
					ACLMessage msg = receive(fn);
					if (msg != null) {
						String[] content2 = msg.getSender().toString()
								.split(" ");
						int i = content2[3].indexOf('@');
						for (int j = 0; j < i; j++) {
							System.out.print(content2[3].charAt(j));
						}
						String[] content1 = msg.getContent()
								.split(" ");
						System.out.println(": supply " + content1[0] +" (load:"+content1[1]+" gen:"+content1[2]+")");
						rectable2[sup] = new RecTable(msg.getSender(),
								Integer.parseInt(content1[0]));
						sup++;
					} else {
					}
				}

			}

		});

		addBehaviour(new CyclicBehaviour(this) { // GET MSG FROM TIMER
			public void action() {
				ACLMessage msg;
				MessageTemplate mt = MessageTemplate.and(
						MessageTemplate.MatchPerformative(ACLMessage.INFORM),
						MessageTemplate.MatchConversationId("START"));
				msg = receive(mt);
				if (msg != null) {
					final String[] content = msg.getContent().split(" ");
					System.out.println("------[Time:" + content[1]
							+ ":00  Utility price:" + content[0]
							+ "]-----------------------------------------");
					myAgent.addBehaviour(new WakerBehaviour(myAgent, 3500) { // Match
																				// START
						protected void handleElapsedTimeout() {
							timeout = true;
							System.out.printf("requst: %d supply: %d\n", req,
									sup);
							int j = req;
							for (int i = 0; i < j; i++) { // OPTIMIZE THE
															// ELECTRICITY AND
															// SEND NSG BACK TO
															// MIA
								ACLMessage reply = new ACLMessage(
										ACLMessage.INFORM);
								reply.setConversationId("REPPLY");
								reply.addReceiver(rectable1[i].getName());
								if (sup == 0) {
									reply.setContent("Utility "
											+ Integer.toString(rectable1[i]
													.getContent()) + " $"
											+ content[0]);
								} else {
									reply.setContent(Match.findseller(table,
											rectable1[i], rectable2, sup,
											content[0]));
								}
								send(reply);
								String[] content2 = rectable1[i].getName()
										.toString().split(" ");
								int x = content2[3].indexOf('@');
								for (int y = 0; y < x; y++) {
									System.out.print(content2[3].charAt(y));
								}
								System.out.println(": " + reply.getContent());
								rectable1[i].reset();
							}
							j = sup;
							for (int i = 0; i < j; i++) { // SEND THE MSG BACK
															// TO MIA
								ACLMessage reply2 = new ACLMessage(
										ACLMessage.INFORM);
								reply2.setConversationId("REPPLY2");
								reply2.addReceiver(rectable2[i].getName());
								reply2.setContent("Sell all ($"+table[Match.findman(rectable2[i].getName(), table)].getpfs()+")");
								send(reply2);
								String[] content2 = rectable2[i].getName()
										.toString().split(" ");
								int x = content2[3].indexOf('@');
								for (int y = 0; y < x; y++) {
									System.out.print(content2[3].charAt(y));
								}
								System.out.println(": " + reply2.getContent());
								rectable2[i].reset();

							}
							req = 0;
							sup = 0;
							timeout = false;
						}
					});
				}
				mt = MessageTemplate.and(
						MessageTemplate.MatchPerformative(ACLMessage.INFORM),
						MessageTemplate.MatchConversationId("ISLAND"));
				msg = myAgent.receive(mt);
				if(msg!= null){
					System.out.println("----------------island-------------------");
				}
			}
		});
	}
}
