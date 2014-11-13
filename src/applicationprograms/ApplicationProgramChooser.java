package applicationprograms;

import java.sql.*;

import common.*;

/**
 * Class that manages user interaction to pick an ApplicationProgram.
 *
 */
public class ApplicationProgramChooser {
	private Connection connection;
	private UIO io;
	
	/**
	 * An enum that maps all the possible user choices to a string value. 
	 * This string value is the one that user should input.
	 */
	private enum PossibleChoices {
		PRESCRIPTION("0"), MEDICAL_TEST("1"), PATIENT_UPDATE("2"), SEARCH_ENGINE("3"), QUIT("4");
		
		private String letter;
		private PossibleChoices(String letter) {
			this.letter = letter;
		}
		
		/**
		 * Maps a string to its equivalent PossibleChoices object.
		 * @param letter The string to be mapped.
		 * @return The PossibleChoices object that corresponds to the letter. Null if 
		 * no mapping exists.
		 */
		public static PossibleChoices fromLetter(String letter) {
	        for (PossibleChoices s : values()) {
	            if (s.letter.equals(letter)) 
	            	return s;
	        }
	        
	        return null;
	    }
	};
	
	/**
	 * Constructor.
	 */
	public ApplicationProgramChooser() {
		io = new UIO(System.in);
	}
	
	/**
	 * Prompts for user input, and returns the user's choice as a PossibleChoices object.
	 * @return The PossibleChoices object referring to the user's selection.
	 */
	private PossibleChoices getUserInput() {
        String error_message = "Improper input. Please input an integer between 0 to 4, inclusive.";
        PossibleChoices user_choice;
        
        while (true) {
        	user_choice = PossibleChoices.fromLetter(io.getInputString(""));
        	if (user_choice == null)
        		System.out.println(error_message);
        	else
        		break;
        }
        
        return user_choice;
    }
	
	/**
	 * Prints the welcome screen.
	 */
	private void initiateWelcomeScreen() {
        // Print the welcome screen.
        
        System.out.println("\nWelcome to the Health Care Application System!");
        System.out.println("Any change to the database will only be committed upon successful exit of the program.\n");
        System.out.println("Please enter '0' to initiate the Prescription program.");
        System.out.println("Please enter '1' to initiate the Medical Test program.");
        System.out.println("Please enter '2' to initiate the Patient Information Update program.");
        System.out.println("Please enter '3' to initiate the Search Engine program.");
        System.out.println("Please enter '4' to exit the program.");
    }
	
	/**
	 * Gets the user input, and returns an ApplicationProgram object based on the
	 * application program that the user wants to run.
	 * @return The ApplicationProgram object based on user input.
	 * @throws SQLException Thrown if the ConnectionManager encounters an error
	 * while connecting with the Oracle server.
	 */
	public ApplicationProgram getApplicationProgram() throws SQLException {
		if (connection == null)
	        connection = (new ConnectionManager(io)).getConnection();
		
		initiateWelcomeScreen();
		
		PossibleChoices user_choice = getUserInput();
        switch (user_choice) {
        	case PRESCRIPTION:
        		return new Prescription(connection, io);
        		
        	case MEDICAL_TEST:
        		return new MedicalTest(connection, io);
        		
        	case PATIENT_UPDATE:
        		return new PatientUpdate(connection, io);
        		
        	case SEARCH_ENGINE:
        		return new SearchEngine(connection, io);
        		
        	case QUIT:
        		// Cleanup io, commit the transactions, and close the connection.
        		commitChangesAndClose();
        		return null;
        	default:
        		// Error occurred. Cleanup io, rollback transactions, and close the connection. 
        		System.out.println("Invalid choice.");
        		abandonChangesAndClose();
        		return null;
        }
    }

	/**
	 * Commits all the transactions.
	 * @throws SQLException Thrown if commit fails.
	 */
	public void commitChanges() throws SQLException {
		if (connection == null)
			return;
		connection.commit();
	}
	
	/**
	 * Abandons all the transactions.
	 * @throws SQLException Thrown if rollback fails.
	 */
	public void abandonChanges() throws SQLException {
		if (connection == null)
			return;
		connection.rollback();
	}
	
	/**
	 * Closes the connection.
	 * @throws SQLException Thrown if closing fails.
	 */
	public void closeConnection() throws SQLException {
		if (connection == null)
			return;
		connection.close();
	}
	
	/**
	 * Cleans up the UIO resources, commits the transactions, and closes the
	 * connection.
	 * @throws SQLException Thrown if committing, or closing the connection fails.
	 */
	public void commitChangesAndClose() throws SQLException {
		io.cleanUp();
		commitChanges();
		closeConnection();
	}
	
	/**
	 * Cleans up the UIO resources, abandons the transactions, and closes
	 * the connection.
	 * @throws SQLException Thrown if rolling back the changes, or closing the connection
	 * fails.
	 */
	public void abandonChangesAndClose() throws SQLException {
		io.cleanUp();
		abandonChanges();
		closeConnection();
	}
}
