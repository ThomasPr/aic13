package at.ac.tuwien.aic.group4.cloudcomputing.wicket.pages;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import at.ac.tuwien.aic.group4.cloudcomputing.dao.TaskDAO;
import at.ac.tuwien.aic.group4.cloudcomputing.model.Task;
import classifier.ClassifierBuilder;
import classifier.IClassifier;
import classifier.WeightedMajority;
import classifier.WekaClassifier;

public class ClassificationTask extends WebPage {

	@SpringBean
	private TaskDAO taskDAO;
	
	double numberOfTweets = 0;
	double sumOfClassification = 0;
	
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	public ClassificationTask(PageParameters params) {
		Task task = taskDAO.findById(params.get("id").toString());
		if(task == null) return;
		
		classifyTweets(task.getSearchPattern().toLowerCase(), task.getSearchStart(), task.getSearchEnd());
		
		if(numberOfTweets == 0) task.setResult(-1.0);;
		task.setResult(sumOfClassification / numberOfTweets);
		
		task.setNumberOfTweets((long) numberOfTweets);
		task.setFinished(true);
		taskDAO.merge(task);
	}
	
	private void classifyTweets(String search, Date start, Date end) {
		try {
			WeightedMajority classifier = prepareClassifier();
			
			for(File tweetFile : getTweetFiles(start, end))
				classifyTweets(tweetFile, search, classifier);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	
	private void classifyTweets(File tweetfile, String search, WeightedMajority classifier) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new BufferedInputStream(new FileInputStream(tweetfile)))));

			String line;
			while((line = reader.readLine()) != null)
				if(line.contains(search))
					classifyTweet(line, classifier);

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void classifyTweet(String tweet, WeightedMajority classifier) {
		try {
			String polarity = classifier.weightedClassify(tweet).getPolarity();
			sumOfClassification += Integer.parseInt(polarity);
			numberOfTweets++;
		} catch (Exception e) { }

	}

	private List<File> getTweetFiles(Date start, Date end) {
		if(start.after(end)) {
			Date tmp = start;
			start = end;
			end = tmp;
		}
		
		List<File> tweetFiles = new ArrayList<File>();
		
		Calendar c = Calendar.getInstance(); 
		c.setTime(start);
		while(! c.getTime().after(end)) {
			File tweetFile = new File("tweets/" + dateFormat.format(c.getTime()) + ".txt.gz");
			if(tweetFile.exists()) tweetFiles.add(tweetFile);
			c.add(Calendar.DATE, 1);
		}
		
		return tweetFiles;
	}
}
