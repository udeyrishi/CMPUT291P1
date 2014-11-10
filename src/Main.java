import java.io.IOException;
import java.util.Scanner;
import java.util.Arrays;
import java.sql.*;

public class Main {
    
    /**
     * The welcome program. Selects and opens application programs based on user input. 
     */
    
    public static void initiate_welcome_screen() {
        // Print the welcome screen.
        
        System.out.println("Welcome to the Health Care Application System!\n");
        System.out.println("Please enter '1' to initiate the Prescription program.");
        System.out.println("Please enter '2' to initiate the Medical Test program.");
        System.out.println("Please enter '3' to initiate the Patient Information Update program.");
        System.out.println("Please enter '4' to initiate the Search Engine program.");
        System.out.println("Please enter 'exit' to exit the program.");
    }
    
    public static String get_user_input() {
        // Get input from the user. Only integers between 1-4 will be accepted (for the 4 modes). 
        // Entering 'exit' will quit the program.
        
        String user_input = "";
        Scanner input_stream = new Scanner(System.in);
        String[] acceptable_inputs = {"1", "2", "3", "4", "exit"};
        String error_message = "Improper input. Please input an integer between 1 to 4, inclusive, or the word 'exit'.";

        user_input = input_stream.next();
 
        while (!Arrays.asList(acceptable_inputs).contains(user_input)) {
            System.out.println(error_message);
            user_input = get_user_input();
        }
        
        return user_input;
    }
    
    
    
    
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        // Main program. Opens an application based on user selection. Opens the 'main' method of the selected application.
        
        String user_choice = "";
        String[] dummy = {"arbitrary", "argument", "for", "main"};
        Boolean exit_flag = false;
        
        // Fetch the connection object from common.Connect.
        Connection connection = new common.Connect().get_connection();
        
        while (true) {
            initiate_welcome_screen();
            user_choice = get_user_input();
            
            switch(user_choice) {
                //case "1": new prescription.Presrciption().main(dummy); break;
                //case "2": new medicaltest.MedicalTest().main(dummy); break;
                //case "3": patientupdate.PatientUpdate pu = new patientupdate.PatientUpdate(connection); 
                //          pu.main(dummy); break;
                //case "4": new searchengine.SearchEngine.main(dummy); break;
                case "exit": exit_flag = true; break;
            }
            
            user_choice = null;
            
            if (exit_flag) {
                System.in.close();
                connection.close();
                System.out.println("\nThe program will now exit.\n");
                break;
            }
        }      
    }
}
