package HomeOptimization;
import java.util.ArrayList;
import java.util.HashMap;

public class MonitorData {

	String defaultDataList[] = {"CONSUMPTION","GENERATION","TEMPERATURE_INDOOR","TEMPERATURE_OUTDOOR","LUMINANCE_INDOOR","LUMINANCE_OUTDOOR","PIR"};
	
	HashMap<String,ArrayList<String>> monitorData = null;
	
	public MonitorData()
	{
		monitorData = new HashMap<String,ArrayList<String>>();
		for(int i = 0;i<defaultDataList.length;i++)
		{
			ArrayList<String> data = new ArrayList<String>();
			monitorData.put(defaultDataList[i],data);
		}
		/*Object[] key = monitorData.keySet().toArray();
		for(int i = 0;i<key.length;i++)
		{
			System.out.println("Key : " + (String)key[i]);
		}*/
	}
	
	public void AddNewMonitorData(String dataName)
	{
		
	}
	
	public boolean PutData(String dataName,String dataValue)
	{
		//String dataName = data.split(":")[0];
		//double dataValue = Double.parseDouble(data.split(":")[1]);
		
		if(monitorData.containsKey(dataName))
		{
			monitorData.get(dataName).add(dataValue);
			//System.out.println(monitorData.get(dataName) + dataName+ " putValue : " + dataValue);
			return true;
		}
		else
			return false;
	}
	
	public String GetLatestData(String dataName)
	{
		String returnValue = null;
		
		if(monitorData.containsKey(dataName))
		{
			ArrayList<String> data = monitorData.get(dataName);
			if(data.size() > 0)
			{
				returnValue = data.get(data.size()-1);
				//System.out.println(data +"  "+ dataName+ " returnValue : " + returnValue);
			}
		}
		return returnValue;
		
	}
}
