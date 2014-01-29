package at.ac.tuwien.aic.group4.cloudcomputing.wicket;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;

import at.ac.tuwien.aic.group4.cloudcomputing.dao.TaskDAO;
import at.ac.tuwien.aic.group4.cloudcomputing.model.Task;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;

public class TaskListPage extends BasePage {

	@SpringBean
	private TaskDAO taskDAO;
	
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	public TaskListPage() {
		
		add(new NotificationPanel("feedback"));
		
		add(new BookmarkablePageLink<NewTaskPage>("newTaskLink", NewTaskPage.class));
		
		WebMarkupContainer table = new WebMarkupContainer("table");
		table.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(10)));
		add(table);
		
		IModel<List<Task>> tableModel = new LoadableDetachableModel<List<Task>>() {
	        protected List<Task> load() {
	            return taskDAO.findTasksForUsername(Session.get().getUsername());
	        }
		};
		ListView<Task> listview = new ListView<Task>("row", tableModel) {
		    protected void populateItem(ListItem<Task> item) {
		    	Task task = item.getModelObject();
		    	item.add(new Label("searchQuery", task.getSearchPattern()));
		    	item.add(new Label("period", dateFormat.format(task.getSearchStart()) + " - " + dateFormat.format(task.getSearchEnd())));
		    	item.add(new Label("tweets", task.getNumberOfTweets()));
		    	
		    	String result = task.getFinished() ? String.format("%.2f", task.getResult()) : "still running ...";
		    	item.add(new Label("result", result));
		    }
		};
		table.add(listview);
	}
}
