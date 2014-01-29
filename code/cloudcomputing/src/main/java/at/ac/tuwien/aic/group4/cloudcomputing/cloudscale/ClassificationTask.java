package at.ac.tuwien.aic.group4.cloudcomputing.cloudscale;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.jms.JMSException;

import at.ac.tuwien.infosys.cloudscale.annotations.ByValueParameter;
import at.ac.tuwien.infosys.cloudscale.annotations.CloudObject;
import at.ac.tuwien.infosys.cloudscale.annotations.DestructCloudObject;
import at.ac.tuwien.infosys.cloudscale.annotations.FileDependency;
import at.ac.tuwien.infosys.cloudscale.annotations.FileDependency.FileAccess;
import at.ac.tuwien.infosys.cloudscale.monitoring.DefaultEventSink;
import at.ac.tuwien.infosys.cloudscale.monitoring.IEventSink;
import at.ac.tuwien.infosys.cloudscale.server.CloudScaleServerRunner;
import classifier.ClassifierBuilder;
import classifier.IClassifier;
import classifier.WeightedMajority;
import classifier.WekaClassifier;

@CloudObject
@FileDependency(dependencyProvider = WEKAFileProvider.class, accessType = FileAccess.ReadOnly)
public class ClassificationTask {

	private String dbUrl;
	private String dbUsername;
	private String dbPassword;
	
	double numberOfTweets = 0;
	double sumOfClassification = 0;
	private Connection dbConnection;
	
	private static MonitoringThread monitoringThread;
	private static Object lock = new Object();

	public ClassificationTask(
			@ByValueParameter String dbUrl, 
			@ByValueParameter String dbUsername, 
			@ByValueParameter String dbPassword)  {
		
		this.dbUrl = dbUrl;
		this.dbUsername = dbUsername;
		this.dbPassword = dbPassword;
		
		startLoadMonitoring();
	}

	@DestructCloudObject
	public double classifyTweets(
			@ByValueParameter String searchQuery, 
			@ByValueParameter Date startDate, 
			@ByValueParameter Date endDate) throws Exception {
		
		WeightedMajority classifier = prepareClassifier();
		
		openDbConnection();
		ResultSet resultSet = findTweets(searchQuery, startDate, endDate);
		if(resultSet == null) return 0.0;
		
		while(resultSet.next()) {
			String tweet = resultSet.getString("text");
			try {
				String polarity = classifier.weightedClassify(tweet).getPolarity();
				sumOfClassification += Integer.parseInt(polarity);
				numberOfTweets++;
			} catch (Exception e) { }
		}

		closeDbConnection();
		
		if(numberOfTweets == 0) return -1;
		return sumOfClassification / numberOfTweets;
	}
	
	private WeightedMajority prepareClassifier() throws Exception {
		List<IClassifier> classifiers = new LinkedList<IClassifier>();
		ClassifierBuilder cb = new ClassifierBuilder();
		WekaClassifier wc1 = cb.retrieveClassifier("weka.classifiers.bayes.NaiveBayes");
		WekaClassifier wc2 = cb.retrieveClassifier("weka.classifiers.trees.J48");
		WekaClassifier wc3 = cb.retrieveClassifier("weka.classifiers.functions.VotedPerceptron");
		classifiers.add(wc1);
		classifiers.add(wc2);
		classifiers.add(wc3);
		return new WeightedMajority(classifiers);
	}
	
	private void openDbConnection() throws SQLException {
		dbConnection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
	}
	
	private void closeDbConnection() throws SQLException {
		if(dbConnection != null)
			dbConnection.close();
	}
	
	private ResultSet findTweets(String searchQuery, Date startDate, Date endDate) throws SQLException {
		PreparedStatement preparedStatement;
		preparedStatement = dbConnection.prepareStatement("SELECT text FROM tweet WHERE text ILIKE ? AND DATE(date) BETWEEN DATE(?) AND DATE(?)");
		preparedStatement.setString(1, "%" + searchQuery + "%");
		preparedStatement.setDate(2, new java.sql.Date(startDate.getTime()));
		preparedStatement.setDate(3, new java.sql.Date(endDate.getTime()));
		preparedStatement.setFetchSize(1000);
		return preparedStatement.executeQuery();
	}

	
	private void startLoadMonitoring() {
		synchronized (lock) {
			if(monitoringThread == null) {
				monitoringThread = new MonitoringThread();
				monitoringThread.start();
			}
		}
	}
	
	
	private class MonitoringThread extends Thread {
		
		private IEventSink eventSink;
		
		public MonitoringThread() {
			this.eventSink = new DefaultEventSink();
		}
		
		@Override
		public void run() {
			try {
				while(true) {
					try {
						CloudScaleServerRunner runner = CloudScaleServerRunner.getInstance();
						if(runner != null)
							eventSink.trigger(new LoadEvent(runner.getId().toString(), getLoads()));
						Thread.sleep(10000);
					} catch (JMSException e) {
						e.printStackTrace();
					}
				}
			}
			catch(InterruptedException e) { }
		}
		
		private double[] getLoads() {
			File file = new File("/proc/loadavg");
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(file));
				String[] loads = reader.readLine().split(" ");
				double[] result = new double[3];
				for(int i=0; i<result.length; i++)
					result[i] = Double.parseDouble(loads[i]);
				return result;
			} catch (IOException | NumberFormatException e) {
				return new double[]{0.0, 0.0, 0.0};
			}
			finally {
				try {
					if(reader != null)
						reader.close();
				} catch (IOException e) { }
			}
		}
	}
}
