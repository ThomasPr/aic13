package at.ac.tuwien.aic.group4.cloudcomputing.importer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.json.DataObjectFactory;

public class Importer {

	private static final String TWEETS_FILE = System.getProperty("user.home") + "/tweets.txt"; 
	
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static Map<String, Writer> writers = new HashMap<String, Writer>();
	
	public static void main(String[] args) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(TWEETS_FILE));
			
			String line;
			while((line = reader.readLine()) != null) {
				if(line.startsWith("{")) {
					try {
						Status twitterStatus = DataObjectFactory.createStatus(line);
						String text = twitterStatus.getText().replaceAll("\n", " ").toLowerCase().trim();
						String date = dateFormat.format(twitterStatus.getCreatedAt());
						getWriterForDate(date).write(text + "\n");
					} catch (TwitterException e) { }
				}
			}
			
			reader.close();
			for(Writer writer : writers.values())
				writer.close();
			
		} catch(FileNotFoundException e) {
			String path = new File(TWEETS_FILE).getAbsoluteFile().toString();
			System.err.println("Expect the extracted tweets from TUWEL in '" + path + "'! ");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static Writer getWriterForDate(String date) throws IOException {
		if(writers.containsKey(date))
			return writers.get(date);
		
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream("../../src/main/webapp/tweets/" + date + ".txt.gz"))));
		writers.put(date, writer);
		return writer;
	}
}
