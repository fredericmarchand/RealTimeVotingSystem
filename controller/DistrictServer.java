package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import model.District;

public class DistrictServer {
	
	private District district;
	
	public DistrictServer(String name) {
		district = new District (name);
	}
	
	public District getDistrict() {
		return district;
	}
	
	public static void main(String[] args) {

		DistrictServer server = new DistrictServer("Ottawa South");
		
		// Connect to database
		try {

			// direct java to the sqlite-jdbc driver jar code
			// load the sqlite-JDBC driver using the current class loader
			Class.forName("org.sqlite.JDBC");

			// create connection to a database in the project home directory.
			// if the database does not exist one will be created in the home
			// directory
			Connection database = DriverManager.getConnection("jdbc:sqlite:" + server.getDistrict().getName());
			
			

			database.close(); //close connection to database

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
