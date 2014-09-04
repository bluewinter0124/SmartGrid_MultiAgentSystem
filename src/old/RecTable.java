package old;


import jade.core.AID;

public class RecTable {
		private AID name;
		private int content;
		public RecTable(AID a,int b){
			name = a;
			content =b;
		}
		public AID getName(){
			return name;
		}
		public int getContent(){
			return content;
		}
		public void setContent(int a){
			content = a;
		}
		public void reset(){
			name = null;
			content = 0;
		}
}
