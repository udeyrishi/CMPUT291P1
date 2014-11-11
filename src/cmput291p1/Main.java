package cmput291p1;
import java.sql.*;

public class Main {
    
    /**
     * The welcome program. Selects and opens application programs based on user input. 
     */
    public static void main(String[] args) {
    	ApplicationProgramChooser APC = new ApplicationProgramChooser();
    	try {
	    	while(true)
	    		APC.getApplicationProgram().run();
    	}
    	
		catch (IllegalArgumentException e1) {
			System.out.println(e1.getMessage());
			System.out.println("Debug information: ");
			e1.printStackTrace();
		}
    	
    	catch (NullPointerException e1) {
    		// User chose exit option
    		System.out.println("Now exiting...");
    	} 
    	
    	catch (SQLException e) {
			System.out.println(e.getMessage());
		}
    }
}
