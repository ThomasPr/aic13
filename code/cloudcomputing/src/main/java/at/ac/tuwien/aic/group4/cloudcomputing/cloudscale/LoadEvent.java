package at.ac.tuwien.aic.group4.cloudcomputing.cloudscale;

import at.ac.tuwien.infosys.cloudscale.messaging.objects.monitoring.Event;

public class LoadEvent extends Event {

	private static final long serialVersionUID = 1L;
	
	private String hostId;
	private Double load1;
	private Double load5;
	private Double load15;
	
	public LoadEvent() { }

	public LoadEvent(String hostId, double[] load) {
		this.hostId = hostId;
		this.load1 = load[0];
		this.load5 = load[1];
		this.load15 = load[2];
	}

	public String getHostId() {
		return hostId;
	}

	public void setHostId(String hostId) {
		this.hostId = hostId;
	}

	public Double getLoad1() {
		return load1;
	}

	public void setLoad1(Double load1) {
		this.load1 = load1;
	}

	public Double getLoad5() {
		return load5;
	}

	public void setLoad5(Double load5) {
		this.load5 = load5;
	}

	public Double getLoad15() {
		return load15;
	}

	public void setLoad15(Double load15) {
		this.load15 = load15;
	}
}
