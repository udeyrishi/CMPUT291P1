package common;

import java.sql.*;
import java.util.Scanner;
import java.io.Console;

public class Connect {

    public static String get_user_input() {
        // Returns a string containing a line of user input. The characters are hidden by default as they are typed in the console.
        // If the characters cannot be hidden (it fails in Eclipse), they will be shown in the console.
        
        try {
            Console cons = System.console();
            return new String(cons.readPassword());
        }
        catch (NullPointerException npe) {
            Scanner stream = new Scanner(System.in);
            return stream.next();
        }
    }
    
    
    public static String get_username() {
        // Returns a String containing a SQL-PLUS username, as inputed by the user.

        System.out.println("Please enter your SQL Plus username.");
        return get_user_input();
    }
    
    
    public static String get_password() {
        // Returns a String containing a SQL-PLUS password, as inputed by the user.

        System.out.println("Please enter your SQL Plus password.");
        return get_user_input();
    }
    
    
    public static Connection get_connection() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        String username = get_username();
        String password = get_password();
        
        String m_driverName = "oracle.jdbc.driver.OracleDriver";
        Class<?> drvClass = Class.forName(m_driverName);
        DriverManager.registerDriver((Driver)drvClass.newInstance());        
        
        String url = "dbc:oracle:thin:@gwynne.cs.ualberta.ca:1521:CRS";
        Connection con = DriverManager.getConnection(url, username, password);
        return con;
    }
}
