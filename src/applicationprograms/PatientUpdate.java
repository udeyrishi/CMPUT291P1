package applicationprograms;

import java.util.Calendar;
import java.util.Date;
import java.sql.*;

import common.*;

/**
 * PatientUpdate is one of the four main modules of this
 * program. It is responsible for updating or creating new
 * patients.
 *
 */
public class PatientUpdate extends ApplicationProgram {

    private int patient_health_care_no;
    private String patient_name;
    private String patient_address;
    private Date patient_birthday;
    private String patient_phonenumber;
    private String[] patient_tests_not_allowed;
    
    /**
     * Constructor.
     * @param connection is a Connection object connecting
     * to the remote database.
     * @param io is a UIO object for interacting with user.
     */
    public PatientUpdate(Connection connection, UIO io) {
        super(connection, io);
    }
        
    /**
     * Primary method responsible for running a loop asking
     * for patient information and updating the database.
     */
    @Override
    public void run() throws SQLException {
        // Main portion of the PatientUpdate application.
        // Loops until the user enters '-1'.
        
        while (true) {
            printWelcomeMessage();
            patient_health_care_no = ioproc.getInputInteger("");
            if (patient_health_care_no == -1) break;
            if (inDatabase(patient_health_care_no)) runUpdateSequence();
            else createNewPatient();
            System.out.println("The patient has been updated successfully!");
        }
    }
    
    
    /**
     * Sequence of commands required to update a patient 
     * that already exists in the database.
     * @throws SQLException
     */
    private void runUpdateSequence() throws SQLException {    
        printPatientFoundMessage();
        obtainPatientName();
        obtainExtraPatientInformation();
        updatePatient();
    }
    
    /**
     * Ask the user for the name of the patient.
     */
    private void obtainPatientName() {
        patient_name = ioproc.getInputString("\nPlease enter the patient's new name. Enter 'skip' to skip this prompt. ");
    }
    
    /**
     * Ask the user for patient information.
     */
    private void obtainExtraPatientInformation() {
        patient_address = ioproc.getInputString("Please enter the patient's new address. Enter 'skip' to skip this prompt. ");
        patient_birthday = ioproc.getInputDate("Please enter the patient's new birthday (YYYY-MM-DD). Enter '1111-11-11' to skip this prompt. ");
        patient_phonenumber = ioproc.getInputString("Please enter the patient's new 10-digit phone number. Enter 'skip' to skip this prompt. ");
        patient_tests_not_allowed = ioproc.getInputString("Please enter a comma separated list of Test IDs that the patient is not allowed to take. Enter 'skip' to skip this prompt. ").split(",");
    }
    
    /**
     * Update the patient table and not_allowed 
     * table based on the patient information.
     * @throws SQLException
     */
    private void updatePatient() throws SQLException {
        if (!isSkip(patient_name)) { updateTable("patient", "name", patient_name); }
        if (!isSkip(patient_address)) { updateTable("patient", "address", patient_address); }
        if (!isSkip(patient_birthday)) { updateTable("patient", "birth_day", ioproc.getTestDateInSQLDateStringForm(patient_birthday)); }
        if (!isSkip(patient_phonenumber)) { updateTable("patient", "phone", patient_phonenumber); }
        if (!isSkip(patient_tests_not_allowed)) { 
            for (String test_id : patient_tests_not_allowed) {
                insertToTable("not_allowed", "health_care_no, type_id", Integer.toString(patient_health_care_no) + ", " + test_id.trim());
            }; 
        }
    }
    
    /**
     * Create a new patient entry in the database 
     * based on the health care number and the given 
     * patient name. Then, obtain the rest of the information 
     * relating to the patient and update the database.
     * @throws SQLException
     */
    private void createNewPatient() throws SQLException {
        printNewPatientMessage();
        String choice = ioproc.getInputString("Enter 'yes' if so; otherwise, enter any other characters. ");
        if (choice.equals("yes")) {
            patient_name = ioproc.getInputString("Please enter the full name of the new patient. ");
            insertToTable("patient", "health_care_no, name", Integer.toString(patient_health_care_no) + ", " + "'" + patient_name + "'");
        }
        obtainExtraPatientInformation();
        updatePatient();
    }
    
    /**
     * Checks if a given String is equal to the skip value.
     * @param s
     * @return
     */
    private boolean isSkip(String s) {
        if (s.equals("skip")) { return true; }
        else return false;
    }
    
    /**
     * Checks if a given String array is equal to the skip value.
     * @param s
     * @return
     */
    private boolean isSkip(String[] s) {
        if (s[0].equals("skip")) { return true; }
        else return false;
    }
    
    /**
     * Checks if a given Date is equal to the skip value.
     * @param d
     * @return
     */
    private boolean isSkip(Date d) {
        Calendar date = Calendar.getInstance();
        date.setTime(d);
        if ((date.get(Calendar.YEAR) == 1111) && ((date.get(Calendar.MONTH)+1) == 11) && (date.get(Calendar.DATE) == 11)) {
            return true;
        }
        else return false;
    }
    
    /**
     * Generic fill-in-the-blank SQL command for updating a table.
     * @param table
     * @param field
     * @param value
     * @throws SQLException
     */
    private void updateTable(String table, String field, String value) throws SQLException {  
        String update = "UPDATE " + table + " "
                      + "SET " + field + " = " + "'" + value + "' "
                      + "WHERE health_care_no = " + patient_health_care_no;
        connection.createStatement().executeUpdate(update);
    }
    
    /**
     * Generic fill-in-the-blank SQL command for inserting into a table.
     * @param table
     * @param fields
     * @param values
     * @throws SQLException
     */
    private void insertToTable(String table, String fields, String values) throws SQLException {
        String insertion = "INSERT INTO " + table + " "
                         + "(" + fields + ") "
                         + "VALUES " + "(" + values + ")";
        connection.createStatement().executeUpdate(insertion);
    }
     
    /**
     * Checks if a health care number already exists in the database.
     * @param health_care_no
     * @return
     * @throws SQLException
     */
    private boolean inDatabase(int health_care_no) throws SQLException {
        Statement stmt = connection.createStatement();
        String query = "SELECT * FROM patient WHERE health_care_no = " + health_care_no;
        ResultSet result = stmt.executeQuery(query);
        return result.next();
    }
    
    /**
     * Prints message indicating that the given health care 
     * number exists in the database. Gives the 
     * instructions for updating a patient.
     */
    private void printPatientFoundMessage() {
        System.out.println("\nA patient with health care number " + patient_health_care_no + " exists in the database.");
        System.out.println("Please enter the updated patient information as per the prompts.");
        System.out.println("Skipping a prompt will retain the existing patient information for that category.");
    }
    
    /**
     * Prints message if a new patient needs to be created.
     * Prompts user to verify that a new user needs to be created.
     */
    private void printNewPatientMessage() {
        System.out.println("This health care number was not found in the database.");
        System.out.println("Would you like to create a new entry in the database using this health care number?");
    }
    
    /**
     * Print welcome message.
     */
    private void printWelcomeMessage() {
        System.out.println("\nWelcome to the Patient Information Update application.");
        System.out.println("To begin, enter the health care number of the patient you would like to update.");
        System.out.println("If the health care number is not found in the database, a new entry will be made for you.");
        System.out.println("Enter '-1' to leave this application.");
    }
}
