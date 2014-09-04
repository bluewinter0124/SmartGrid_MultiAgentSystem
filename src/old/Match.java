package old;


import jade.core.AID;

public class Match {
	private static String seller;
	private static int need;

	static String findseller(PriceTable[] list, RecTable req, RecTable sup[],
			int len, String ms) {
		String[] content2;
		int x;
		seller = "Buy ";
		need = req.getContent();
		for (int j = 0; j < len; j++) {
			if (sup[j].getContent() == 0) {
				continue;
			}
			if (underbudget(req.getName(), sup[j].getName(), list)) {
				content2 = sup[j].getName().toString().split(" ");
				x = content2[3].indexOf('@');
				if (need > sup[j].getContent()) { // seller = number price
					need = need - sup[j].getContent();
					seller = seller
							+ Integer.toString(sup[j].getContent())
							+ " ("
							+ content2[3].subSequence(0, x)
							+ " $"
							+ Integer.toString(list[findman(sup[j].getName(),
									list)].getpfs()) + "), ";
					sup[j].setContent(0);
				} else if (need <= sup[j].getContent()) {
					sup[j].setContent(sup[j].getContent() - need);
					seller = seller
							+ Integer.toString(need)
							+ " ("
							+ content2[3].subSequence(0, x)
							+ " $"
							+ Integer.toString(list[findman(sup[j].getName(),
									list)].getpfs()) + ")";

					need = 0;
					break;
				}
			} else
				continue;
		}
		if (need > 0) {			
				seller = seller + Integer.toString(need) + " (Utility " +" $"
						+ ms+")";
		}
		return seller;
	}

	 static int findman(AID a, PriceTable[] list) {
		int i;
		for (i = 0; i < list.length; i++) {
			if (a.equals(list[i].getName())) {
				break;
			}
		}
		return i;
	}

	private static boolean underbudget(AID a, AID b, PriceTable[] list) {
		int i = 0, j = 0;
		if (list[findman(a, list)].getpfn() >= list[findman(b, list)].getpfs()) {
			return true;
		} else
			return false;
	}
}