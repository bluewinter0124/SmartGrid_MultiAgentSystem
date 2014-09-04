package HomeOptimization;

public class AirConditionerOptimization {
	
	MonitorData monitorData;
	double appropriateTemperature = 24;
	double acceptableTemperature = 27;
	double tolerableBurden = 5; 
	double burdenStep = 2;

	public AirConditionerOptimization(MonitorData monitorData)
	{
		this.monitorData = monitorData;
	}
	
	public boolean setAppropriateTemperature(double temperature)
	{
		if(temperature >= 16 && temperature<= 31)
		{
			appropriateTemperature = temperature;
			if(temperature > acceptableTemperature)
				acceptableTemperature = temperature;
			
			return true;
		}
		else
			return false;
		
	}
	
	public boolean setAcceptableTemperature(double temperature)
	{
		if(temperature >= 16 && temperature<= 31)
		{
			acceptableTemperature = temperature;
			if(temperature < appropriateTemperature)
				appropriateTemperature = temperature;
			
			return true;
		}
		else
			return false;	
	}
	
	public boolean setTolerableBurden(double tolerance)
	{
		if(tolerance >= 0)
		{
			tolerableBurden = tolerance;
			return true;
		}
		else
			return false;	
	}
	
	public double Optimize()//(MonitorData monitorData)
	{
		//Get TEMPERATURE_INDOOR and TEMPERATURE_OUTDOOR data from MonitorData
		if(monitorData.GetLatestData("TEMPERATURE_OUTDOOR") == null || monitorData.GetLatestData("TEMPERATURE_INDOOR") == null)
		{
			System.out.println("-Incorrect Temperature Data, Stop Air Conditioner Optimization");
			return -1;
		}
		double outdoorTemp = Double.parseDouble(monitorData.GetLatestData("TEMPERATURE_OUTDOOR"));
		double indoorTemp = Double.parseDouble(monitorData.GetLatestData("TEMPERATURE_INDOOR"));
		
		//Shut down 
		if(outdoorTemp <= appropriateTemperature) 
			return 0;
		
		double tempDiff = outdoorTemp - indoorTemp; // Temperature difference between indoor and outdoor
		double CurrentBurden = tempDiff / burdenStep; // Calculate current burden
		double airConditionerTemp = appropriateTemperature;
		if(CurrentBurden >= tolerableBurden) // Current burden bigger than to tolerable
		{
			airConditionerTemp = acceptableTemperature;
		}
		else
		{
			airConditionerTemp += ((CurrentBurden / tolerableBurden) * (acceptableTemperature - appropriateTemperature));
		}
		
		airConditionerTemp = Math.round(airConditionerTemp);
		System.out.println("In:" + indoorTemp + "," + " OUT:" + outdoorTemp + " > " +airConditionerTemp);
		
		return airConditionerTemp;
	}
}
