package at.ac.tuwien.aic.group4.cloudcomputing.importer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import static at.ac.tuwien.aic.group4.cloudcomputing.cloudscale.Util.*;

public class Database {

	private Connection connection;

	public Database() {
		connect();
	}

	public void connect() {
		try {
			connection = DriverManager.getConnection(getDbUrl(), getDbUsername(), getDbPassword());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void disconnect() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void insertTweet(String tweet, Date created_at) {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO tweet (text, date) VALUES (?, ?)");
			preparedStatement.setString(1, tweet);
			preparedStatement.setTimestamp(2, new Timestamp(created_at.getTime()));
			preparedStatement.execute();
		} catch (SQLException e) { }
	}
}
