package at.ac.tuwien.aic.group4.cloudcomputing.cloudscale;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import at.ac.tuwien.infosys.cloudscale.monitoring.EventCorrelationEngine;
import at.ac.tuwien.infosys.cloudscale.vm.IHost;
import at.ac.tuwien.infosys.cloudscale.vm.IHostPool;

public class Util {

	private static Properties dbProperties;
	
	public static double getLoad(IHost host) {
		if(host == null || host.getId() == null)
			return 0.0;
		
		Object value = EventCorrelationEngine.getInstance().getMetricsDatabase().getLastValue("load5_" + host.getId().toString());
		if(value == null)
			return 0.0;
		
		Double result = (Double) ((HashMap) value).get("load5");
		if (result == null)
			return 0.0;
			
		return result;
	}
	
	public static synchronized void loadDbProperties() {
		if(dbProperties != null)
			return;
					
		try {
			dbProperties = new Properties();
			dbProperties.load(Util.class.getClassLoader().getResourceAsStream("database.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static String getDbUrl() {
        loadDbProperties();
        return dbProperties.getProperty("db.url");
	}
	
	public static String getDbUsername() {
        loadDbProperties();
        return dbProperties.getProperty("db.username");
	}
	
	public static String getDbPassword() {
        loadDbProperties();
        return dbProperties.getProperty("db.password");

	}

	public static double calculateAverageLoad(IHostPool hostPool) {
		double averageLoad = 0.0;
		for(IHost host : hostPool.getHosts())
			averageLoad += (getLoad(host) / hostPool.getHostsCount());
		
		return averageLoad;
	}
	
	
}
