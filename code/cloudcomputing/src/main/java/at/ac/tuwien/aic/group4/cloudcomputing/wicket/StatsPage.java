package at.ac.tuwien.aic.group4.cloudcomputing.wicket;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;

import at.ac.tuwien.aic.group4.cloudcomputing.cloudscale.Util;
import at.ac.tuwien.aic.group4.cloudcomputing.dao.TaskDAO;
import at.ac.tuwien.aic.group4.cloudcomputing.model.Task;
import at.ac.tuwien.infosys.cloudscale.management.CloudManager;
import at.ac.tuwien.infosys.cloudscale.vm.IVirtualHost;

import com.googlecode.wickedcharts.highcharts.options.Axis;
import com.googlecode.wickedcharts.highcharts.options.AxisType;
import com.googlecode.wickedcharts.highcharts.options.ChartOptions;
import com.googlecode.wickedcharts.highcharts.options.ExportingOptions;
import com.googlecode.wickedcharts.highcharts.options.Legend;
import com.googlecode.wickedcharts.highcharts.options.Marker;
import com.googlecode.wickedcharts.highcharts.options.Options;
import com.googlecode.wickedcharts.highcharts.options.SeriesType;
import com.googlecode.wickedcharts.highcharts.options.Title;
import com.googlecode.wickedcharts.highcharts.options.livedata.LiveDataSeries;
import com.googlecode.wickedcharts.highcharts.options.livedata.LiveDataUpdateEvent;
import com.googlecode.wickedcharts.highcharts.options.series.Point;
import com.googlecode.wickedcharts.wicket6.highcharts.Chart;

import de.agilecoders.wicket.core.Bootstrap;

public class StatsPage extends WebPage {
	
	private ListView<IVirtualHost> hostView;
	private ListView<Task> taskView;
	
	@SpringBean
	private TaskDAO taskDao;
	
	public StatsPage() {

		add(new Chart("objectsChart", new StatsOptions(new StatFunction() {
			@Override
			public Number getValue() {
				int number = 0;
				for(IVirtualHost host : CloudManager.getInstance().getHosts())
					number += host.getCloudObjectsCount();
				return number;
			}
		})));

		add(new Chart("hostsChart", new StatsOptions(new StatFunction() {
			@Override
			public Number getValue() {
				return CloudManager.getInstance().getHosts().size();
			}
		})));

		add(new Chart("loadChart", new StatsOptions(new StatFunction() {
			@Override
			public Number getValue() {
				return Util.calculateAverageLoad(CloudManager.getInstance().getHostPool());
			}
		})));

		
		WebMarkupContainer updateContainer = new WebMarkupContainer("ajaxUpdate");
		updateContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(10)));
		
		IModel<List<IVirtualHost>> poolModel = new LoadableDetachableModel<List<IVirtualHost>>() {
	        protected List<IVirtualHost> load() {
	            return new ArrayList<IVirtualHost>(CloudManager.getInstance().getHosts());
	        }
		};
		List<IVirtualHost> pool = new ArrayList<IVirtualHost>(CloudManager.getInstance().getHosts());
		hostView = new ListView<IVirtualHost>("hostRow", poolModel) {
			protected void populateItem(ListItem<IVirtualHost> item) {
				IVirtualHost host = item.getModelObject();
				item.add(new Label("host", host.getId() == null ? "starting ..." : host.getId().toString()));
				item.add(new Label("number", host.getCloudObjectsCount()));
				item.add(new Label("load", Util.getLoad(host)));
			}
		};
		updateContainer.add(hostView);
		
		IModel<List<Task>> taskModel = new LoadableDetachableModel<List<Task>>() {
	        protected List<Task> load() {
	        	return taskDao.findRunningTasks();
	        }
		};
		taskView = new ListView<Task>("taskRow", taskModel) {
			protected void populateItem(ListItem<Task> item) {
				Task task = item.getModelObject();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				item.add(new Label("search", task.getSearchPattern()));
				item.add(new Label("period", df.format(task.getSearchStart()) + " - " + df.format(task.getSearchEnd())));
				item.add(new Label("tweets", task.getNumberOfTweets()));
			}
		};
		updateContainer.add(taskView);
		
		add(updateContainer);
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		Bootstrap.renderHead(response);
	}
	
	
	private class StatsOptions extends Options {
		
		private StatFunction statFunction;

		public StatsOptions(StatFunction function) {
			super();
			this.statFunction = function;
			
			setChartOptions(new ChartOptions().setType(SeriesType.SPLINE));
			setTitle(new Title("").setEnabled(false));
			setxAxis(new Axis().setType(AxisType.DATETIME));
			setyAxis(new Axis().setTitle(new Title("").setEnabled(false)));
			setLegend(new Legend(false));
			setExporting(new ExportingOptions().setEnabled(false));
			addSeries(createSeries());
		}

		private LiveDataSeries createSeries() {
			LiveDataSeries series = new LiveDataSeries(this, 10000) {
				@Override
				public Point update(final LiveDataUpdateEvent event) {
					return new Point()
						.setX(new Date().getTime())
						.setY(statFunction.getValue())
						.setMarker(new Marker().setEnabled(false));
				}
			};
			series.addJavaScriptParameter("currentTime", "new Date()");

	    	List<Point> points = new ArrayList<Point>();
	    	for(int i=0; i<30; i++) 
	    		points.add(new Point()
	    			.setX(System.currentTimeMillis())
	    			.setY(statFunction.getValue())
	    			.setMarker(new Marker().setEnabled(false)));
	    	series.setData(points);
	    	
			return series;
		}
	}
	
	public interface StatFunction {
		Number getValue();
	}
}
