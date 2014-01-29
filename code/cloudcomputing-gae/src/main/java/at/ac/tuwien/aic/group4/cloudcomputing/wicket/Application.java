package at.ac.tuwien.aic.group4.cloudcomputing.wicket;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.stereotype.Component;

import at.ac.tuwien.aic.group4.cloudcomputing.wicket.pages.ClassificationTask;
import at.ac.tuwien.aic.group4.cloudcomputing.wicket.pages.LoginPage;
import at.ac.tuwien.aic.group4.cloudcomputing.wicket.pages.NewTaskPage;
import at.ac.tuwien.aic.group4.cloudcomputing.wicket.pages.TaskListPage;
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
		mountPage("/classification", ClassificationTask.class);
		
		getComponentInstantiationListeners().add(new SpringComponentInjector(this));
		Bootstrap.install(Application.get(), new BootstrapSettings());
	}
}
