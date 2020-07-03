package com.app.service;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import com.app.datasource.DataSource;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class ScyllaService {

	// Connection parameters
	String[] contactPoints = { "172.18.0.2", "172.18.0.3", "172.18.0.4" };

	// Creating the connection
	DataSource datasource = new DataSource(contactPoints, "catalog");
	Session session = datasource.getSession();

	// Creating the keyspace and the table if not exists.
	public void createSchema() {

		session.execute("CREATE KEYSPACE IF NOT EXISTS catalog "
				+ "WITH replication = {'class': 'NetworkTopologyStrategy', 'DC1': 3}");
		session.execute(
				"CREATE TABLE IF NOT EXISTS catalog.superheroes(first_name text, last_name text, superhero_name text, picture blob, PRIMARY KEY(first_name,last_name))");

	}

	// insertQuery
	public void insertData(String first_name, String last_name, String superhero_name, File picture)
			throws IOException {

		ByteBuffer buffer = readAll(picture);
		PreparedStatement insert = session.prepare(
				"INSERT INTO catalog.superheroes (first_name,last_name,superhero_name,picture) VALUES (?,?,?,?)");
		session.execute(insert.bind(first_name, last_name, superhero_name, buffer));
		System.out.println("\nData inserted:");
		System.out.println(
				"First_Name : " + first_name + ",Last_Name : " + last_name + ",Superhero_Name : " + superhero_name);
	}

	// select/fetch query
	public void select() {

		System.out.println("\n\n Displaying Results");
		ResultSet results = session.execute("select * from catalog.superheroes");

		for (Row row : results) {

			String first_name = row.getString("first_name");
			String last_name = row.getString("last_name");
			String superhero_name = row.getString("superhero_name");
			System.out.println("\n" + "First_Name : " + first_name + ",Last_Name : " + last_name + ",Superhero_Name : "
					+ superhero_name);

		}
	}

	// get imageQuery
	public void getPicture(String first_name, String last_name,String output_location) throws IOException {

		File tmpFile = new File(output_location + first_name + "_" + last_name + ".jpg");
		System.out.printf("\nWriting retrieved buffer to %s%n ", tmpFile.getAbsoluteFile());

		PreparedStatement get = session
				.prepare("SELECT picture FROM catalog.superheroes WHERE first_name = ? AND last_name = ?");
		Row row = session.execute(get.bind(first_name, last_name)).one();
		writeAll(row.getBytes("picture"), tmpFile);
        System.out.println("Data written successfully !!!");
	}

	// delete query
	public void delete(String first_name, String last_name) {

		PreparedStatement delete = session
				.prepare("DELETE FROM catalog.superheroes WHERE first_name = ? and last_name = ?");
		session.execute(delete.bind(first_name, last_name));
		System.out.print("\n\nDeleted " + first_name + "......");

	}

	// intermediate function which reads the image into a buffer for storage.
	// Used in insertQuery
	public ByteBuffer readAll(File file) throws IOException {
		FileInputStream inputStream = null;
		boolean threw = false;
		try {
			inputStream = new FileInputStream(file);
			FileChannel channel = inputStream.getChannel();
			ByteBuffer buffer = ByteBuffer.allocate((int) channel.size());
			channel.read(buffer);
			buffer.flip();
			return buffer;
		} catch (IOException e) {
			threw = true;
			throw e;
		} finally {
			close(inputStream, threw);
		}
	}

	// intermediate function which reads the image data from the Scylla table
	// and writes to a location in the local machine.
	// Used in getPicture()
	public void writeAll(ByteBuffer buffer, File file) throws IOException {
		FileOutputStream outputStream = null;
		boolean threw = false;
		try {
			outputStream = new FileOutputStream(file);
			FileChannel channel = outputStream.getChannel();
			channel.write(buffer);
		} catch (IOException e) {
			threw = true;
			throw e;
		} finally {
			close(outputStream, threw);
		}
	}

	// intermediate function to close the streams used for
	// reading and writing the image data.
	// Used in readAll() and writeAll() methods.
	public void close(Closeable inputStream, boolean threw) throws IOException {
		if (inputStream != null)
			try {
				inputStream.close();
			} catch (IOException e) {
				if (!threw)
					throw e; // else preserve original exception
			}
	}

	// Closing the connection
	public void end() {
		datasource.closeConnection();
	}

}
