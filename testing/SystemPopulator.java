package testing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import model.Address;
import model.Candidate;
import model.Party;
import model.Person;
import model.Province;
import model.Voter;

public class SystemPopulator {

	public static void populateVotersAndCandidates(String inputFile,
			ArrayList<Person> voters, ArrayList<Person> candidates) {

		BufferedReader br = null;
		String line;
		String firstName, lastName;
		String street, city, postalCode;
		int sin;

		Province province;
		Address address;
		Person newPerson;

		try {

			br = new BufferedReader(new FileReader(inputFile));
			while ((line = br.readLine()) != null) {
				String[] data = line.split("%");

				firstName = data[1];
				lastName = data[2];
				street = data[3];
				city = data[4];
				postalCode = data[6].replace(" ", "");
				sin = Integer.valueOf(data[7]);

				province = Province.getProvinceFromName(data[5]);
				address = new Address(street, city, province, postalCode);
				newPerson = null;

				if (data[0].equals("c")) {
					newPerson = new Candidate(firstName, lastName, address, sin);
					candidates.add(newPerson);
				} else if (data[0].equals("v")) {
					newPerson = new Voter(firstName, lastName, address, sin);
				}

				if (newPerson != null)
					voters.add(newPerson);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void populateParties(String inputFile,
			ArrayList<Party> parties) {
		BufferedReader br = null;
		String line;

		try {

			br = new BufferedReader(new FileReader(inputFile));

			while ((line = br.readLine()) != null) {
				String[] data = line.split("%");

				if (data[0].equals("p")) {
					Party newParty = new Party(data[1], null);
					parties.add(newParty);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String args[]) {
		try {
			final String FILE_1 = args[0];
			final String FILE_2 = args[1];
			final String OUTPUT_FILE = args[2];

			BufferedWriter out = null;

			try {
				out = new BufferedWriter(new FileWriter(OUTPUT_FILE));
				out.write("Populating the system with Voters and Candidates...");
				out.newLine();
				out.newLine();

				ArrayList<Person> voters = new ArrayList<Person>();
				ArrayList<Person> candidates = new ArrayList<Person>();
				SystemPopulator.populateVotersAndCandidates(FILE_1, voters,
						candidates);

				out.write("Total Voters: " + voters.size());
				out.newLine();
				out.newLine();

				ArrayList<Party> parties = new ArrayList<Party>();
				SystemPopulator.populateParties(FILE_2, parties);

				out.write("Total Parties: " + parties.size());
				out.newLine();
				out.newLine();

				out.write("Candidates:");
				out.newLine();
				for (int i = 0; i < candidates.size(); ++i) {
					out.write(candidates.get(i).toString());
					out.newLine();
				}
				out.newLine();

				out.write("Done Populating.");
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			System.out
					.println("Usage: SystemPopulator <votersFile> <partiesFile> <outputFile>");
		}
	}
}