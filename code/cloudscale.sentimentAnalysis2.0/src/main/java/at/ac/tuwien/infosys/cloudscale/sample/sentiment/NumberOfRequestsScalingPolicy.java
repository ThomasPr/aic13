package at.ac.tuwien.infosys.cloudscale.sample.sentiment;

import java.util.HashMap;

import at.ac.tuwien.infosys.cloudscale.monitoring.EventCorrelationEngine;
import at.ac.tuwien.infosys.cloudscale.monitoring.MonitoringMetric;
import at.ac.tuwien.infosys.cloudscale.policy.IScalingPolicy;
import at.ac.tuwien.infosys.cloudscale.vm.ClientCloudObject;
import at.ac.tuwien.infosys.cloudscale.vm.IHost;
import at.ac.tuwien.infosys.cloudscale.vm.IHostPool;

public class NumberOfRequestsScalingPolicy implements IScalingPolicy {

	@Override
	public IHost selectHost(ClientCloudObject newCloudObject, IHostPool hostPool) {
		
		IHost selected = null;
		int minRequests = Integer.MAX_VALUE;
		
		for(IHost host : hostPool.getHosts()) {
			int numberOfRequestsForHost = getNumberOfRequestsForHost(host);
			if(numberOfRequestsForHost < minRequests) {
				selected = host;
				minRequests = numberOfRequestsForHost;
			}
		}
		
		//TODO scale up
		if(selected == null) {
			selected = hostPool.startNewHost();
			register(selected);
			System.out.println("scaling up to " + hostPool.getHostsCount());
		}
		
		if(minRequests >= 10) {
			register(hostPool.startNewHost());
			System.out.println("scaling up to " + hostPool.getHostsCount());
		}
		
		
		System.out.println(String.format("select host %s (%d)", selected.getId().toString(), minRequests));
		
		return selected;
	}

	
	@Override
	public boolean scaleDown(IHost host, IHostPool hostPool) {

		if(!host.isOnline()) 
			return false;
		
		if(host.getCloudObjectsCount() > 0) //TODO really necessary?
			return false;
		
		if(hostPool.getHostsCount() <= 1)
			return false;
		
		if(getNumberOfRequestsForHost(host) >= 1) 
			return false;
		
		return false;
	}
	
	private Integer getNumberOfRequestsForHost(IHost host) {
		Object value = EventCorrelationEngine.getInstance().getMetricsDatabase().getLastValue("NumberOfRequestsMetric_"+host.getId().toString());
		System.out.println(value);
		if(value == null)
			return -1;
		
		if (((HashMap) value).get("req") == null)
			return 0;
			
		return (Integer) ((HashMap) value).get("req");
	}
	
	private void register(IHost host) {
		String id = host.getId().toString();
		MonitoringMetric metric = new MonitoringMetric();
		metric.setName("NumberOfRequestsMetric_" + id);
//		metric.setEpl(String.format("select * as req from NumberOfRequestEvent(hostId=\"%s\").win:length(1)", id));
		metric.setEpl(String.format("select numberOfRequest as req from NumberOfRequestEvent(hostId=\"%s\").win:length(1)", id));
		EventCorrelationEngine.getInstance().registerMetric(metric);
	}
}
