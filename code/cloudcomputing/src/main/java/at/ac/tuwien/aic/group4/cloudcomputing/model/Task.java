package at.ac.tuwien.aic.group4.cloudcomputing.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Task implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	private Customer user;
	
	private String searchPattern;
	private Date searchStart;
	private Date searchEnd;
	
	private Long numberOfTweets;
	private Double result;
	
	private Boolean finished;

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Customer getUser() {
		return user;
	}

	public void setUser(Customer user) {
		this.user = user;
	}

	public String getSearchPattern() {
		return searchPattern;
	}

	public void setSearchPattern(String searchPattern) {
		this.searchPattern = searchPattern;
	}

	public Date getSearchStart() {
		return searchStart;
	}

	public void setSearchStart(Date searchStart) {
		this.searchStart = searchStart;
	}

	public Date getSearchEnd() {
		return searchEnd;
	}

	public void setSearchEnd(Date searchEnd) {
		this.searchEnd = searchEnd;
	}

	public Long getNumberOfTweets() {
		return numberOfTweets;
	}

	public void setNumberOfTweets(Long numberOfTweets) {
		this.numberOfTweets = numberOfTweets;
	}

	public Double getResult() {
		return result;
	}

	public void setResult(Double result) {
		this.result = result;
	}

	public Boolean getFinished() {
		return finished;
	}

	public void setFinished(Boolean finished) {
		this.finished = finished;
	}
}
