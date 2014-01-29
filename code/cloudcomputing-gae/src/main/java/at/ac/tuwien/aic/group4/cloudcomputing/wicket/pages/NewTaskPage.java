package at.ac.tuwien.aic.group4.cloudcomputing.wicket.pages;

import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;

import at.ac.tuwien.aic.group4.cloudcomputing.dao.CustomerDAO;
import at.ac.tuwien.aic.group4.cloudcomputing.dao.TaskDAO;
import at.ac.tuwien.aic.group4.cloudcomputing.dao.TweetDAO;
import at.ac.tuwien.aic.group4.cloudcomputing.model.Task;

import com.google.appengine.api.backends.BackendServiceFactory;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions.Builder;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextFieldConfig;

public class NewTaskPage extends BasePage {

	@SpringBean
	private CustomerDAO customerDAO;
	
	@SpringBean
	private TaskDAO taskDAO;
	
	@SpringBean
	private TweetDAO tweetDAO;
	
	private TextField<String> searchTextField;
	private DateTextField startDateTextField;
	private DateTextField endDateTextField;
	
	public NewTaskPage() {
		
		add(new NotificationPanel("feedback"));
		
		Form form = new Form("form") {
			@Override
			public void onSubmit() {
				String searchQuery = searchTextField.getModelObject();
				Date startDate = startDateTextField.getModelObject();
				Date endDate = endDateTextField.getModelObject();
				
				Task task = new Task();
				task.setSearchPattern(searchQuery);
				task.setSearchStart(startDate);
				task.setSearchEnd(endDate);
				task.setFinished(false);
				task.setUser(customerDAO.findByName((String) getSession().getAttribute("username")));
				taskDAO.persist(task);
				
				getSession().success("Tweet analysis for '" + task.getSearchPattern() + "' started.");
				setResponsePage(TaskListPage.class);
				
				Queue queue = QueueFactory.getDefaultQueue();
			    queue.add(Builder
			    		.withUrl("/classification")
			    		.param("id", KeyFactory.keyToString(task.getId()))
			    		.header("Host", BackendServiceFactory.getBackendService().getBackendAddress("worker"))
			    		.method(Method.GET));
			}
		};
		
		DateTextFieldConfig dtfc = new DateTextFieldConfig()
			.autoClose(true)
			.withFormat("yyyy-MM-dd");
		
		searchTextField = new TextField<String>("searchString", Model.of(""));//
		searchTextField.setRequired(true);
		searchTextField.add(StringValidator.minimumLength(3));
		form.add(searchTextField);
		
		Date startDate = new GregorianCalendar(2011, 06, 29).getTime();
		startDateTextField = (new DateTextField("startDate", new Model<Date>(startDate), dtfc));
		startDateTextField.setRequired(true);
		form.add(startDateTextField);
		
		Date endDate = new GregorianCalendar(2011, 07, 01).getTime();
		endDateTextField = new DateTextField("endDate", new Model<Date>(endDate), dtfc);
		endDateTextField.setRequired(true);
		form.add(endDateTextField);
		
		add(form);
	}
}
