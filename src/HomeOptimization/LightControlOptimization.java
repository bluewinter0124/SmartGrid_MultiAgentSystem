package HomeOptimization;
import java.io.*;
import java.util.*;
import java.lang.*;

public class LightControlOptimization{
	public MonitorData monitorData;
	public int ROW = 4;
	public int COL = 3;
	public int TRADITIONAL = 1;
	public String lightvalue_Filename = "Lightvalue.txt";
	public String pirvalue_Filename = "PIRvalue.txt";
	public int[][] person = new int[ROW][COL];
	public int[][] person_new = new int[ROW][COL];
	public int[] light1 = new int[ROW-TRADITIONAL];
	public int[] light2 = new int[TRADITIONAL];
	public int[] light3 = new int[ROW];
	public int[] light4 = new int[ROW];
	public int[] weight1 = new int[ROW-TRADITIONAL];
	public int[] weight2 = new int[TRADITIONAL];
	public int[] weight3 = new int[ROW];
	public int[] weight4 = new int[ROW];
	
	public LightControlOptimization(MonitorData monitorData)
	{
		this.monitorData = monitorData;
		Initial();
	}
	
	//Initial person map
	public void person_Init(){
		int i, j;
		for(i = 0; i < ROW; i++){
			for(j = 0; j < COL; j++){
				person[i][j] = 0;		
			}		
		}
	}

	//Initial setting of lights in column of 1 exclude traditional light
	public void light1_Init(){
		int i;
		for(i = 0; i < ROW-TRADITIONAL; i++){
			light1[i] = 0;
			weight1[i] = 0;
		}
	}

	//Initial setting of traditional light
	public void light2_Init(){
		light2[TRADITIONAL-1] = 0;
		weight2[TRADITIONAL-1] = 0;
	}

	//Initial setting of lights in column of 3
	public void light3_Init(){
		int i;
		for(i = 0; i < ROW; i++){
			light3[i] = 0;
			weight3[i] = 0;
		}
	}

	//Initial setting of lights in column of 4
	public void light4_Init(){
		int i;
		for(i = 0; i < ROW; i++){
			light4[i] = 0;
			weight4[i] = 0;
		}
	}

	//Print person map
	public void person_Map(){
		int i, j;
		System.out.println("-- Person Map --");
		for(i = 0; i < ROW; i++){
			for(j = 0; j < COL; j++){
				System.out.print(person[i][j] + "\t");	
			}
			System.out.println();
		}
	}

	//Print light map
	public void light_Map(){
		int i;
		System.out.println("-- Light Map --");
		for(i = 0; i < ROW; i++){
			if(i == ROW-1){
				System.out.print(light2[TRADITIONAL-1] + "\t");
			}else{
				System.out.print(light1[i] + "\t");
			}
			System.out.print(light3[i] + "\t");
			System.out.println(light4[i] + "\t");
		}
	}

	//Get light control value
	public String get_Lightmap(){
		String control_Command = "";
		int i, j;
		for(j = 0; j < COL; j++){
			for(i = 0; i < ROW; i++){
				if(j == 0 && i != ROW-1)
					control_Command = control_Command + light1[i] + ",";
				else if(j == 0 && i == ROW-1)
					control_Command = control_Command + light2[TRADITIONAL-1] + ",";
				else if(j == 1)
					control_Command = control_Command + light3[i] + ",";
				else if(j == 2 && i != ROW-1)
					control_Command = control_Command + light4[i] + ",";
				else if(j == 2 && i == ROW-1)
					control_Command = control_Command + light4[i] + "\n";
			}
		}
		return control_Command;
	}

	//Print weight map
	public void weight_Map(){
		int i;
		System.out.println("-- Weight Map --");
		for(i = 0; i < ROW; i++){
			if(i == ROW-1){
				System.out.print(weight2[TRADITIONAL-1] + "\t");		
			}else{
				System.out.print(weight1[i]+ "\t");
			}
			System.out.print(weight3[i]+ "\t");
			System.out.println(weight4[i]+ "\t");
		}
	}

	//Random person is in or not in map
	public void random_Personmap() throws IOException{
		FileWriter fw = new FileWriter(pirvalue_Filename);
		int i, j;
		Random ran = new Random();
		for(i = 0; i < ROW; i++){
			for(j = 0; j < COL; j++){
				fw.write(ran.nextInt(2) + "\t");
			}
			fw.write("\n");
		}
		fw.flush();
		fw.close();
	}

	//Get value(0/1) of PIR sensor
	public void input_PIRvalue(String input_Command){
		String[] tokens = input_Command.split("_");
		int i, j, token_count = 0;
		for(j = 0; j < COL; j++){
			for(i = 0; i < ROW; i++){
				person_new[i][j] = Integer.valueOf(tokens[token_count]);
				token_count++;
			}
		}
	}

	//Calculate weight value of each light
	public void weight_Value(){
		int i, j;
		for(i = 0; i < ROW; i++){
			for(j = 0; j < COL; j++){
				if(person_new[i][j]-person[i][j] == 1){
					//0 -> 1
					if(i == 0 && j == 0){
						weight1[i]+=15;	//self
						weight3[i]+=1;	//right
						weight1[i+1]+=3;//down
					}else if(i == 1 && j == 0){
						weight1[i]+=15;	//self
						weight1[i-1]+=3;//up
						weight3[i]+=1;	//right
						weight1[i+1]+=3;//down
					}else if(i == 2 && j == 0){
						weight1[i]+=15;	//self
						weight1[i-1]+=3;//up
						weight3[i]+=1;	//right
						weight2[0]+=1;	//down
					}else if(i == 3 && j == 0){
						weight2[0]+=3;	//self
						weight1[i-1]+=3;//up
						weight3[i]+=1;	//right
					}else if(i == 0 && j == 1){
						weight3[i]+=1;	//self
						weight1[i]+=3;	//left
						weight4[i]+=1;	//right
						weight3[i+1]+=1;//down
					}else if(i == 1 && j == 1){
						weight3[i]+=1;	//self
						weight1[i]+=3;	//left
						weight3[i-1]+=1;//up
						weight4[i]+=1;	//right
						weight3[i+1]+=1;//down
					}else if(i == 2 && j == 1){
						weight3[i]+=1;  //self
						weight1[i]+=3;  //left
						weight3[i-1]+=1;//up
						weight4[i]+=1;  //right
						weight3[i+1]+=1;//down
					}else if(i == 3 && j == 1){
						weight3[i]+=1;  //self
						weight2[0]+=1;  //left
						weight3[i-1]+=1;//up
						weight4[i]+=1;  //right
					}else if(i == 0 && j == 2){
						weight4[i]+=1;	//self
						weight3[i]+=1;	//left
						weight4[i+1]+=1;//down
					}else if(i == 1 && j == 2){
						weight4[i]+=1;	//self
						weight3[i]+=1;	//left
						weight4[i-1]+=1;//up
						weight4[i+1]+=1;//down
					}else if(i == 2 && j == 2){
						weight4[i]+=1;  //self
						weight3[i]+=1;  //left
						weight4[i-1]+=1;//up
						weight4[i+1]+=1;//down
					}else if(i == 3 && j == 2){
						weight4[i]+=1;	//self
						weight3[i]+=1;	//left
						weight4[i-1]+=1;//up
					}
				}else if(person_new[i][j]-person[i][j] == -1){
					//1 -> 0
					if(i == 0 && j == 0){
						weight1[i] = weight1[i] - 15;	//self
						weight3[i] = weight3[i] - 1;	//right
						weight1[i+1] = weight1[i+1] - 3;//down
					}else if(i == 1 && j == 0){
						weight1[i] = weight1[i] - 15;	//self
						weight1[i-1] = weight1[i-1] - 3;//up
						weight3[i] = weight3[i] - 1;	//right
						weight1[i+1] = weight1[i+1] - 3;//down
					}else if(i == 2 && j == 0){
						weight1[i] = weight1[i] - 15;	//self
						weight1[i-1] = weight1[i-1] - 3;//up
						weight3[i] = weight3[i] - 1;	//right
						weight2[0] = weight2[0] - 1;	//down
					}else if(i == 3 && j == 0){
						weight2[0] = weight2[0] - 3;	//self
						weight1[i-1] = weight1[i-1] - 3;//up
						weight3[i] = weight3[i] - 1;	//right
					}else if(i == 0 && j == 1){
						weight3[i] = weight3[i] - 1;	//self
						weight1[i] = weight1[i] - 3;	//left
						weight4[i] = weight4[i] - 1;	//right
						weight3[i+1] = weight3[i+1] - 1;//down
					}else if(i == 1 && j == 1){
						weight3[i] = weight3[i] - 1;	//self
						weight1[i] = weight1[i] - 3;	//left
						weight3[i-1] = weight3[i-1] - 1;//up
						weight4[i] = weight4[i] - 1;	//right
						weight3[i+1] = weight3[i+1] - 1;//down
					}else if(i == 2 && j == 1){
						weight3[i] = weight3[i] - 1;  	//self
						weight1[i] = weight1[i] - 3;  	//left
						weight3[i-1] = weight3[i-1] - 1;//up
						weight4[i] = weight4[i] - 1;  	//right
						weight3[i+1] = weight3[i+1] - 1;//down
					}else if(i == 3 && j == 1){
						weight3[i] = weight3[i] - 1;  	//self
						weight2[0] = weight2[0] - 1;  	//left
						weight3[i-1] = weight3[i-1] - 1;//up
						weight4[i] = weight4[i] - 1;  	//right
					}else if(i == 0 && j == 2){
						weight4[i] = weight4[i] - 1;	//self
						weight3[i] = weight3[i] - 1;	//left
						weight4[i+1] = weight4[i+1] - 1;//down
					}else if(i == 1 && j == 2){
						weight4[i] = weight4[i] - 1;	//self
						weight3[i] = weight3[i] - 1;	//left
						weight4[i-1] = weight4[i-1] - 1;//up
						weight4[i+1] = weight4[i+1] - 1;//down
					}else if(i == 2 && j == 2){
						weight4[i] = weight4[i] - 1;  	//self
						weight3[i] = weight3[i] - 1;  	//left
						weight4[i-1] = weight4[i-1] - 1;//up
						weight4[i+1] = weight4[i+1] - 1;//down
					}else if(i == 3 && j == 2){
						weight4[i] = weight4[i] - 1;	//self
						weight3[i] = weight3[i] - 1;	//left
						weight4[i-1] = weight4[i-1] - 1;//up
					}
				}
				person[i][j] = person_new[i][j];
			}
		}
	}

	//Get light control value
	public void light_Control(){
		int i;
		for(i = 0; i < ROW; i++){
			if(i != ROW-1){
				if(weight1[i] >= 15)
					light1[i] = 15;
				else
					light1[i] = weight1[i];
			}
			if(weight3[i] >= 1)
				light3[i] = 1;
			else
				light3[i] = 0;
			if(weight4[i] >= 1)
				light4[i] = 1;
			else
				light4[i] = 0;
		}
		if(weight2[0] >= 3)
			light2[0] = 3;
		else
			light2[0] = weight2[0];
	}

	//Clear Screen
	public void clear_Screen(){
		try{
			Thread.sleep(1000);
			System.out.print("\033[0;0H\033[2J");
		}catch(Exception e){
			System.out.println("Exception caught");
		}
	}	
	
	public String Initial(){
		String lightControlvalue;
		//clear_Screen();
		//Initial person-map and light-map
		person_Init();
		light1_Init();
		light2_Init();
		light3_Init();
		light4_Init();
		//Print initial map
		person_Map();
		light_Map();
		lightControlvalue = get_Lightmap();
		return lightControlvalue;
	}

	public String Optimize(){
		String input_PIR = monitorData.GetLatestData("PIR");
		String lightControlvalue;
		//clear_Screen();
		input_PIRvalue(input_PIR);
		weight_Value();
		light_Control();
		person_Map();
		light_Map();
		lightControlvalue = get_Lightmap();
		return lightControlvalue;
	}

	/*public static void main(String[] argv){
		System.out.println(Initial());
		System.out.println(Optimize("0_1_0_0_0_0_0_0_0_0_0_0"));
	}*/
}
