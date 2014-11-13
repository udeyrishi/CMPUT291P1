package cmput291p1;
import java.sql.*;

import applicationprograms.ApplicationProgramChooser;

/**
 * The main application class.
 */
public class Main {
    
    /**
     * The main Java method. Uses other classes to prompt for user input, pick
     * appropriate application program, and run it, until unless the user asks to
     * quit.
     * @param args Irrelevant for this program
     */
    public static void main(String[] args) {
    	ApplicationProgramChooser APC = new ApplicationProgramChooser();
    	try {
	    	while(true) {
	    		APC.getApplicationProgram().run();
	    		APC.commitChanges();
	    	}
	    	
    	}
    	
		catch (IllegalArgumentException e1) {
			System.out.println(e1.getMessage());
			System.out.println("Abandoning changes...");
			System.out.println("Debug information: ");
			e1.printStackTrace();
			try {
				APC.abandonChangesAndClose();
			} catch (SQLException e) {
				// Nothing can be done here...
				System.out.println("abandonChangesAndClose failed.");
			}
		}
    	
    	catch (NullPointerException e1) {
    		// User chose exit option
    		System.out.println("Now exiting...");
    	} 
    	
    	catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("Abandoning changes...");
			try {
				APC.abandonChangesAndClose();
			} catch (SQLException e1) {
				// Nothing can be done here...
				System.out.println("abandonChangesAndClose failed.");
			}
		}
    }
}
