package at.ac.tuwien.aic.group4.cloudcomputing.wicket;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.stereotype.Component;

import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.settings.BootstrapSettings;

@Component
public class Application extends WebApplication {

	@Override
	public Class<? extends Page> getHomePage() {
		return TaskListPage.class;
	}
	
	@Override
	protected void init() {
		super.init();
		
		mountPage("/", TaskListPage.class);
		mountPage("/login", LoginPage.class);
		mountPage("/newTask", NewTaskPage.class);
		mountPage("/stats", StatsPage.class);
		
		getComponentInstantiationListeners().add(new SpringComponentInjector(this));
		Bootstrap.install(Application.get(), new BootstrapSettings());
	}
	
	@Override
	public Session newSession(Request request, Response response) {
		return new Session(request);
	}
}
