package applicationprograms;

import java.sql.*;

import common.*;

public class ApplicationProgramChooser {
	private Connection connection;
	private UIO io;
	
	private enum PossibleChoices {
		PRESCRIPTION("0"), MEDICAL_TEST("1"), PATIENT_UPDATE("2"), SEARCH_ENGINE("3"), QUIT("4");
		
		private String letter;
		private PossibleChoices(String letter) {
			this.letter = letter;
		}
		
		public static PossibleChoices fromLetter(String letter) {
	        for (PossibleChoices s : values()) {
	            if (s.letter.equals(letter)) 
	            	return s;
	        }
	        
	        return null;
	    }
	};
	
	public ApplicationProgramChooser() {
		io = new UIO(System.in);
	}
	
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
	
	private void initiateWelcomeScreen() {
        // Print the welcome screen.
        
        System.out.println("Welcome to the Health Care Application System!\n");
        System.out.println("Please enter '0' to initiate the Prescription program.");
        System.out.println("Please enter '1' to initiate the Medical Test program.");
        System.out.println("Please enter '2' to initiate the Patient Information Update program.");
        System.out.println("Please enter '3' to initiate the Search Engine program.");
        System.out.println("Please enter '4' to exit the program.");
    }
	
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
        		io.cleanUp(); // Ask the UIO object to close its scanners
        		return null;
        	default:
        		System.out.println("Invalid choice.");
        		return null;
        }
    }
}
