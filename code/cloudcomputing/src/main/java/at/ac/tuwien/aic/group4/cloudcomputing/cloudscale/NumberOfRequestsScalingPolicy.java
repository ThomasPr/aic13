package at.ac.tuwien.aic.group4.cloudcomputing.cloudscale;

import at.ac.tuwien.infosys.cloudscale.monitoring.EventCorrelationEngine;
import at.ac.tuwien.infosys.cloudscale.monitoring.MonitoringMetric;
import at.ac.tuwien.infosys.cloudscale.policy.IScalingPolicy;
import at.ac.tuwien.infosys.cloudscale.vm.ClientCloudObject;
import at.ac.tuwien.infosys.cloudscale.vm.IHost;
import at.ac.tuwien.infosys.cloudscale.vm.IHostPool;

public class NumberOfRequestsScalingPolicy implements IScalingPolicy {

	private static Object lock = new Object();
	
	@Override
	public IHost selectHost(ClientCloudObject newCloudObject, IHostPool hostPool) {

		IHost selected = null;
		double averageLoad = 0.0;
		
		synchronized (lock) {
			
			if(hostPool.getHostsCount() < 1)
				return startNewHost(hostPool);
			
			selected = findSuitableHost(hostPool);
			averageLoad = Util.calculateAverageLoad(hostPool);
		}
		
		if(averageLoad > 1.5 && selected.getCloudObjectsCount() >= 2 ) 
			return startNewHost(hostPool);
		else
			return selected;
	}
	
	private IHost findSuitableHost(IHostPool hostPool) {
		IHost selected = hostPool.getHosts().iterator().next();
		
		for(IHost host : hostPool.getHosts())
			if(host.getCloudObjectsCount() < selected.getCloudObjectsCount())
				selected = host;

		return selected;
	}
	
	private IHost startNewHost(IHostPool hostPool) {
		IHost host = hostPool.startNewHost();
		registerHost(host);
		return host;
	}
	
	private void registerHost(IHost host) {
		String id = host.getId().toString();
		MonitoringMetric metric = new MonitoringMetric();
		metric.setName("load5_" + id);
		metric.setEpl(String.format("select load5 as load5 from LoadEvent(hostId=\"%s\").win:length(1)", id));
		EventCorrelationEngine.getInstance().registerMetric(metric);
	}

	
	@Override
	public boolean scaleDown(IHost host, IHostPool hostPool) {
		synchronized (lock) {
			
			if(!host.isOnline()) 
				return false;
			
			if(host.getCloudObjectsCount() > 0)
				return false;
			
			if(hostPool.getHostsCount() <= 1)
				return false;

			return Util.calculateAverageLoad(hostPool) < 0.5;
		}	
	}
}
