package at.ac.tuwien.aic.group4.cloudcomputing.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.json.DataObjectFactory;

public class Importer {

	private static String TWEETS_FILE = System.getProperty("user.home") + "/tweets.txt";
	private static final int POOL_SIZE = 8;
	
	private static BufferedReader reader;
	
	public static void main(String[] args) {
		try {
			reader = new BufferedReader(new FileReader(TWEETS_FILE));
	
			List<Thread> threadList = new ArrayList<Thread>(POOL_SIZE);
			for(int i = 0; i<POOL_SIZE; i++) {
				Thread t = importTask();
				t.start();
				threadList.add(t);
			}
			
			for(Thread t : threadList)
				t.join();
			
			reader.close();
			
		} catch(FileNotFoundException e) {
			String path = new File(TWEETS_FILE).getAbsoluteFile().toString();
			System.err.println("Expect the extracted tweets from TUWEL in '" + path + "'! ");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static Thread importTask() {
		return new Thread() {
			@Override
			public void run() {
				try {
					Database database = new Database();
					String line;
					
					while((line = reader.readLine()) != null) {
						if(line.startsWith("{")) {
							try {
								Status twitterStatus = DataObjectFactory.createStatus(line);
								database.insertTweet(twitterStatus.getText(), twitterStatus.getCreatedAt());
							} catch (TwitterException e) { }
						}
					}
					database.disconnect();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
	}
}
