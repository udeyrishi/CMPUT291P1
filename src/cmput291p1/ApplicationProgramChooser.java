package cmput291p1;

import java.sql.*;
import java.util.*;

import common.*;
import prescription.*;
import medicaltest.*;
/*
import patientupdate.*;
import searchengine.*;
*/

public class ApplicationProgramChooser {
	private Connection connection;
	
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
	
	public ApplicationProgramChooser() { }
	
	private PossibleChoices getUserInput() {
        String user_input = "";
        Scanner input_stream = new Scanner(System.in);
        String error_message = "Improper input. Please input an integer between 0 to 4, inclusive.";
        PossibleChoices user_choice;
        while (true) {
        	user_input = input_stream.next().trim();
        	user_choice = PossibleChoices.fromLetter(user_input);
        	if (user_choice == null)
        		System.out.println(error_message);
        	else
        		break;
        }
        
        input_stream.close();
        return user_choice;
    }
	
	public static void initiateWelcomeScreen() {
        // Print the welcome screen.
        
        System.out.println("Welcome to the Health Care Application System!\n");
        System.out.println("Please enter '0' to initiate the Prescription program.");
        System.out.println("Please enter '1' to initiate the Medical Test program.");
        System.out.println("Please enter '2' to initiate the Patient Information Update program.");
        System.out.println("Please enter '3' to initiate the Search Engine program.");
        System.out.println("Please enter '4' to exit the program.");
    }
	
	public ApplicationProgram getApplicationProgram() throws IllegalArgumentException, SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (connection == null)
	        connection = (new ConnectionManager()).getConnection();
		
		initiateWelcomeScreen();
		
		PossibleChoices user_choice = getUserInput();
        switch (user_choice) {
        	case PRESCRIPTION:
        		return new Prescription(connection);
        		
        	case MEDICAL_TEST:
        		return new MedicalTest(connection);
        		/*
        	case PATIENT_UPDATE:
        		return new patient_update(connection);
        	case SEARCH_ENGINE:
        		return new search_engine(connection);
        		*/
        	case QUIT:
        		return null;
        	default:
        		System.out.println("Invalid choice.");
        		return null;
        }
    }
}
