package at.ac.tuwien.aic.group4.cloudcomputing.wicket;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import at.ac.tuwien.aic.group4.cloudcomputing.dao.CustomerDAO;
import at.ac.tuwien.aic.group4.cloudcomputing.model.Customer;
import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;

public class LoginPage extends WebPage {

	@SpringBean
	private CustomerDAO customerDAO;
	
	private TextField<String> usernameTextField;
	
	public LoginPage() {
		
		add(new NotificationPanel("feedback"));
		
		Form form = new Form("form") {
			@Override
			public void onSubmit() {
				String username = usernameTextField.getModelObject();
				Customer customer = customerDAO.findByName(username);
				
				if(customer == null) {
					customer = new Customer();
					customer.setName(username);
					customerDAO.saveOrUpdate(customer);
				}
				
				Session.get().setUsername(customer.getName());
				setResponsePage(TaskListPage.class);
			}
		};
		
		usernameTextField = new TextField<String>("searchString", Model.of(""));
		usernameTextField.setRequired(true);
		form.add(usernameTextField);
		
		add(form);
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
	    super.renderHead(response);
	    Bootstrap.renderHead(response);
	}
		
}
