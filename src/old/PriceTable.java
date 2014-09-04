package old;


import jade.core.AID;

public class PriceTable {
	private AID name;
	private int pfs;
	private int pfn;
	public PriceTable(AID a,int b,int c){
		name = a;
		pfs = b;
		pfn = c;
	}
	public AID getName(){
		return name;
	}
	public int getpfs(){
		return pfs;
	}
	public int getpfn(){
		return pfn;
	}

}