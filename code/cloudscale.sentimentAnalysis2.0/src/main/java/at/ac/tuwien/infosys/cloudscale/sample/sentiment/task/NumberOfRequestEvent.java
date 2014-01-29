package at.ac.tuwien.infosys.cloudscale.sample.sentiment.task;

import at.ac.tuwien.infosys.cloudscale.messaging.objects.monitoring.Event;

public class NumberOfRequestEvent extends Event {

	private int numberOfRequest;
	private String hostId;

	public NumberOfRequestEvent(int numberOfRequest, String hostId) {
		this.numberOfRequest = numberOfRequest;
		this.hostId = hostId;
	}

	public int getNumberOfRequest() {
		return numberOfRequest;
	}

	public void setNumberOfRequest(int numberOfRequest) {
		this.numberOfRequest = numberOfRequest;
	}

	public String getHostId() {
		return hostId;
	}

	public void setHostId(String hostId) {
		this.hostId = hostId;
	}
}
