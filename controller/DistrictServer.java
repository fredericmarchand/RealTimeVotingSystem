package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import networking.WSocket;
import model.District;
import model.Province;

public class DistrictServer {
	
	private District district;
	final WSocket server;
	
	public DistrictServer(String name, Province province, int port) {
		district = new District (name, province);
		server = new WSocket(port);
	}
	
	public District getDistrict() {
		return district;
	}
	
	public static void main(String[] args) {
		String districtName = args[0];
		int port = Integer.parseInt(args[1]);
		
		DistrictServer server = new DistrictServer(districtName, Province.Ontario, port);
		
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
