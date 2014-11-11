import java.io.IOException;
import java.util.*;
import java.sql.*;

import common.ConnectionManager;

public class Main {
    
    /**
     * The welcome program. Selects and opens application programs based on user input. 
     */
    
    public static void initiate_welcome_screen() {
        // Print the welcome screen.
        
        System.out.println("Welcome to the Health Care Application System!\n");
        System.out.println("Please enter '0' to initiate the Prescription program.");
        System.out.println("Please enter '1' to initiate the Medical Test program.");
        System.out.println("Please enter '2' to initiate the Patient Information Update program.");
        System.out.println("Please enter '3' to initiate the Search Engine program.");
        System.out.println("Please enter '4' to exit the program.");
    }
    
    public static String get_user_input() {
        // Get input from the user. Only integers between 1-4 will be accepted (for the 4 modes). 
        // Entering 'q' will quit the program.
        
        String user_input = "";
        Scanner input_stream = new Scanner(System.in);
        ArrayList<String> acceptable_inputs = new ArrayList<String>(5);
        acceptable_inputs.add("0"); 
        acceptable_inputs.add("1"); 
        acceptable_inputs.add("2"); 
        acceptable_inputs.add("3"); 
        acceptable_inputs.add("4");
        
        String error_message = "Improper input. Please input an integer between 0 to 4, inclusive.";

        while (true) {
        	user_input = input_stream.next();
        	if (acceptable_inputs.contains(user_input))
        		break;
        	else
        		System.out.println(error_message);
        }
        input_stream.close();
        return user_input;
    }
    
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        // Main program. Opens an application based on user selection. Opens the 'main' method of the selected application.
        
        String user_choice = "";
        //String[] dummy = {"arbitrary", "argument", "for", "main"};
        Boolean exit_flag = false;
        
        // Fetch the connection object from common.Connect.
        ConnectionManager con = new ConnectionManager();
        Connection connection;
        try {
        	connection = con.get_connection();
        }
        catch (SQLException e) {
        	// Failue to connect;
        	System.out.println("Connection to server timed-out. Now exiting...");
        	return;
        }
        
        while (true) {
            initiate_welcome_screen();
            user_choice = get_user_input();
            exit_flag = true;
            /*
            switch(user_choice) {
                //case "1": new prescription.Presrciption().main(dummy); break;
                //case "2": new medicaltest.MedicalTest().main(dummy); break;
                //case "3": patientupdate.PatientUpdate pu = new patientupdate.PatientUpdate(connection); 
                //          pu.main(dummy); break;
                //case "4": new searchengine.SearchEngine.main(dummy); break;
                case "exit": exit_flag = true; break;
            }
            */
            user_choice = null;
            
            if (exit_flag) {
                //System.in.close();
                connection.close();
                System.out.println("\nThe program will now exit.\n");
                break;
            }
        }      
    }
}
