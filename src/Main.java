import java.util.Scanner;

public class Main {
    
    /**
     * The welcome program. Selects and opens application programs based on user input. 
     */
    
    public static void initiate_welcome_screen() {
        // Print the welcome screen.
        
        System.out.println("Welcome to the Health Care Application System!\n");
        System.out.println("Please enter 1 to initiate the Prescription program.");
        System.out.println("Please enter 2 to initiate the Medical Test program.");
        System.out.println("Please enter 3 to initiate the Patient Information Update program.");
        System.out.println("Please enter 4 to initiate the Search Engine program.");
    }
    
    public static int get_user_input() {
        // Get input from the user. Only integers between 1-4 will be accepted (for the 4 modes).
        
        int program_choice = 0;
        Scanner user_input = new Scanner(System.in);
        String error_message = "Improper input. Please input an integer between 1 to 4, inclusive.";
        
        try {
            program_choice = user_input.nextInt();
        }
        catch (java.util.InputMismatchException ime) {
            System.out.println(error_message);
            program_choice = get_user_input();
        }
                
        while ((program_choice < 1) || (program_choice > 4)) {
            System.out.println(error_message);
            program_choice = get_user_input();
        }
        
        user_input.close();
        return program_choice;
    }
    
    
    public static void main(String[] args) {
        // Main program. Opens an application based on user selection. Opens the 'main' method of the selected application.
        
        int user_choice = 0;
        
        while (true) {
            initiate_welcome_screen();
            user_choice = get_user_input();
            String[] dummy = {"arbitrary", "argument", "for", "main"};
            
            switch(user_choice) {
                case 1: new prescription.Presrciption().main(dummy); break;
                case 2: new medicaltest.MedicalTest().main(dummy); break;
                case 3: new patientupdate.PatientUpdate.main(dummy); break;
                case 4: new searchengine.SearchEngine.main(dummy); break;
            }
            
            user_choice = 0;
        }
    }
}
