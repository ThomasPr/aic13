package at.ac.tuwien.aic.group4.cloudcomputing.cloudscale;

import javax.jms.JMSException;

import at.ac.tuwien.infosys.cloudscale.annotations.CloudObject;
import at.ac.tuwien.infosys.cloudscale.annotations.DestructCloudObject;
import at.ac.tuwien.infosys.cloudscale.annotations.EventSink;
import at.ac.tuwien.infosys.cloudscale.monitoring.IEventSink;
import at.ac.tuwien.infosys.cloudscale.server.CloudScaleServerRunner;

@CloudObject
public class SleepTask extends Thread {

	private static Integer numberOfRequests = 0;
	
	@EventSink
	private IEventSink events;
	
	private int sleepTime;
	
	public SleepTask(int time) {
		this.sleepTime = time;
	}
	
	@Override
	@DestructCloudObject
	public void run() {
		requestStarted();
		sleep(sleepTime);
		requestFinished();
	}	
	
	public void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// May happen
		}
	}
	

	private void requestStarted() {
		synchronized (numberOfRequests) {
			numberOfRequests++;
			numberOfRequestsChangedEvent();
		}
	}

	private synchronized void requestFinished() {
		synchronized (numberOfRequests) {
			numberOfRequests--;
			numberOfRequestsChangedEvent();
		}
	}
	
	private void numberOfRequestsChangedEvent() {
		try {
			String hostId = CloudScaleServerRunner.getInstance().getId().toString(); 
			events.trigger(new NumberOfRequestEvent(numberOfRequests, hostId));
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}


}
