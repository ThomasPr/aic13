package at.ac.tuwien.aic.group4.cloudcomputing.wicket;

import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;

public class Session extends WebSession {

	private String username;
	
	public Session(Request request) {
		super(request);
	}
	
	public static Session get() {
		return (Session)WebSession.get();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
