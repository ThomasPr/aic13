package at.ac.tuwien.aic.group4.cloudcomputing.wicket.pages;

import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;

import de.agilecoders.wicket.core.Bootstrap;

public class BasePage extends WebPage {

	public BasePage() {
		if (getUsername() == null)
			throw new RestartResponseAtInterceptPageException(LoginPage.class);
		
		add(new Label("username", getUsername()));
		add(new Link("logout") {
			public void onClick() {
				getSession().invalidate();
				setResponsePage(TaskListPage.class);
			}
		});
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
	    super.renderHead(response);
	    Bootstrap.renderHead(response);
	}
	
	protected String getUsername() {
		return (String) getSession().getAttribute("username");
	}
}
