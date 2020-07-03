package com.app.driver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.app.service.ScyllaService;

public class App {

	public static void main(String[] args) throws IOException {

		// **Everything is hard coded in this example. Refer to ScyllaService class
		// **Only query parameters are passed through here.
		// **ContactPoints ={"172.18.0.2", "172.18.0.3", "172.18.0.4" }
		// **ContactPoints are set in ScyllaService class.
		// **Contact points is the list of one or more node address.
		// **keyspace = catalog (database name)
		// **table = superheroes (Scylla table)

		// ########################################################################################
		// Start
		ScyllaService service = new ScyllaService();

		// ########################################################################################
		// Creating the schema
		service.createSchema();

		// #########################################################################################
		// Inserting data into the schema created.

		ArrayList<String> first_names = new ArrayList<String>(Arrays.asList("Peter", "Steve", "Bruce"));
		ArrayList<String> last_names = new ArrayList<String>(Arrays.asList("Parker", "Rogers", "Wayne"));
		ArrayList<String> superhero_names = new ArrayList<String>(
				Arrays.asList("Spiderman", "Captain America", "Batman"));
		String image_location = "/home/sb/Pictures/Sample_Images/";
		File file; // image of the character

		for (int i = 0; i < 3; i++) {

			// image file of the character is like "firstname_lastname" in my case.
			file = new File(image_location + first_names.get(i) + "_" + last_names.get(i) + ".jpg");
			service.insertData(first_names.get(i), last_names.get(i), superhero_names.get(i), file);

		}

		// #######################################################################################
		// Select Query
		service.select();

		// #######################################################################################
		// Delete Query
		// service.delete("Steve", "Rogers");

		// #######################################################################################
		// Fetch character image to a location
		String output_location = "/home/sb/Pictures/Sample_outputs/";
		service.getPicture("Bruce", "Wayne", output_location);

		// #######################################################################################
		// Clearing up the resources
		service.end();

	}

}
