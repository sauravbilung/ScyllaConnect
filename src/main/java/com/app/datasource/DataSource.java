package com.app.datasource;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

// Class which creates the connection to Scylla Cluster
public class DataSource {

	String[] contactPoints; // List of node addresses to which client drivers tries to connect
	String keyspace; // Database whose tables have similar configurations.
	Cluster cluster; // Cluster library provides connection to a cluster.
	Session session; // Session library provides the connection to a specific keyspace.

	public DataSource(String[] contactPoints, String keyspace) {
		super();
		this.contactPoints = contactPoints;
		this.keyspace = keyspace;
		createConnection();
	}

	public void createConnection() {
		cluster = Cluster.builder().addContactPoints(contactPoints).build();
		session = cluster.connect(keyspace);
	}

	public Session getSession() {
		return session;
	}	

	public void closeConnection() {
		session.close();
		cluster.close();
	}

}
