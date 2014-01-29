package at.ac.tuwien.aic.group4.cloudcomputing.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import at.ac.tuwien.aic.group4.cloudcomputing.model.Tweet;

@Repository
public class TweetDAO extends GenericDAO<Tweet> {
	
	private static final String searchQuery = "FROM tweet WHERE text ILIKE :text AND DATE(date) BETWEEN DATE(:start) and DATE(:end)";
	
	public TweetDAO() {
		super(Tweet.class);
	}
	
	public List<Tweet> findTweets(String text, Date start, Date end, long firstResult, long maxResults) {
//		return getCurrentSession()
//				.createSQLQuery("SELECT * " + searchQuery + " LIMIT :limit OFFSET :offset")
//				.addEntity(Tweet.class)
//				.setParameter("text", "%" + text + "%")
//				.setDate("start", start)
//				.setDate("end", end)
//				.setParameter("limit", maxResults)
//				.setParameter("offset", firstResult)
//				.list();
		return new ArrayList<Tweet>();
	}
	
	public long countTweets(String text, Date start, Date end) {
//		return ((BigInteger) getCurrentSession()
//				.createSQLQuery("SELECT COUNT(*) " + searchQuery)
//				.setParameter("text", "%" + text + "%")
//				.setDate("start", start)
//				.setDate("end", end)
//				.uniqueResult())
//				.longValue();
		return 0;
	}
}
