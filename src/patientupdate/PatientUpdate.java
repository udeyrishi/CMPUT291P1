package patientupdate;

import java.util.Calendar;
import java.util.Date;
import java.sql.*;

import common.*;

public class PatientUpdate extends ApplicationProgram {

    private int patient_health_care_no;
    private String patient_name;
    private String patient_address;
    private Date patient_birthday;
    private String patient_phonenumber;
    private String[] patient_tests_not_allowed;
    
    public PatientUpdate(Connection connection, UIO io) {
        super(connection, io);
    }
        
    @Override
    public void run() throws SQLException {
        // Main portion of the PatientUpdate application.
        // Loops until the user enters '-1'.
        
        while (true) {
            printWelcomeMessage();
            patient_health_care_no = ioproc.getInputInteger("");
            if (patient_health_care_no == -1) { break; }
            if (inDatabase(patient_health_care_no)) { runUpdateSequence(); }
            else { createNewPatient(); }
        }
    }
    
    public void runUpdateSequence() throws SQLException {
        // Sequence of commands required to update a patient that already exists in the database.
        
        printPatientFoundMessage();
        obtainPatientName();
        obtainExtraPatientInformation();
        updatePatient();
    }
    
    public void obtainPatientName() {
        // Ask the user for the name of the patient.
        
        patient_name = ioproc.getInputString("Please enter the patient's new name. Enter 'skip' to skip this prompt. ");
    }
    
    public void obtainExtraPatientInformation() {
        // Ask the user for patient information.
        
        patient_address = ioproc.getInputString("Please enter the patient's new address. Enter 'skip' to skip this prompt. ");
        patient_birthday = ioproc.getInputDate("Please enter the patient's new birthday. Enter '1111-11-11' to skip this prompt. ");
        patient_phonenumber = ioproc.getInputString("Please enter the patient's new 10-digit phone number. Enter 'skip' to skip this prompt. ");
        patient_tests_not_allowed = ioproc.getInputString("Please enter a comma separated list of Test IDs that the patient is not allowed to take. Enter 'skip' to skip this prompt").split(",");
    }
    
    public void updatePatient() throws SQLException {
        // Update the patient table and not_allowed table based on the patient information.
        
        if (!isSkip(patient_name)) { updateTable("patient", "name", patient_name); }
        if (!isSkip(patient_address)) { updateTable("patient", "address", patient_address); }
        if (!isSkip(patient_birthday)) { updateTable("patient", "birth_day", ioproc.getTestDateInSQLDateStringForm(patient_birthday)); }
        if (!isSkip(patient_phonenumber)) { updateTable("patient", "phone", patient_phonenumber); }
        if (!isSkip(patient_tests_not_allowed)) { 
            for (String test_id : patient_tests_not_allowed) {
                insertToTable("not_allowed", "health_care_no, type_id", Integer.toString(patient_health_care_no) + ", " + test_id);
            }; 
        }
    }
    
    public void createNewPatient() throws SQLException {
        // Create a new patient entry in the database based on the health care number and the given patient name.
        // Then, obtain the rest of the information relating to the patient and update the database.
        
        printNewPatientMessage();
        String choice = ioproc.getInputString("Enter 'yes' if so; otherwise, enter any other characters. ");
        if (choice.equals("yes")) {
            patient_name = ioproc.getInputString("Please enter the full name of the new patient. ");
            insertToTable("patient", "health_care_no, name", Integer.toString(patient_health_care_no) + ", " + "'" + patient_name + "'");
        }
        obtainExtraPatientInformation();
        updatePatient();
    }
    
    public boolean isSkip(String s) {
     // Checks if a given String is equal to the skip value.
        
        if (s.equals("skip")) { return true; }
        else return false;
    }
    
    public boolean isSkip(String[] s) {
        // Checks if a given String array is equal to the skip value.
        
        if (s[0].equals("skip")) { return true; }
        else return false;
    }
    
    public boolean isSkip(Date d) {
        // Checks if a given Date is equal to the skip value.
        
        Calendar date = Calendar.getInstance();
        date.setTime(d);
        if ((date.get(Calendar.YEAR) == 1111) && (date.get(Calendar.MONTH) == 11) && (date.get(Calendar.DATE) == 11)) {
            return true;
        }
        else return false;
    }
    
        public void updateTable(String table, String field, String value) throws SQLException {
        // Generic fill-in-the-blank SQL command for updating a table.
            
        String update = "UPDATE " + table + " "
                      + "SET " + field + " = " + "'" + value + "'"
                      + "WHERE health_care_no = " + patient_health_care_no;
        connection.createStatement().executeUpdate(update);
    }
    
    public void insertToTable(String table, String fields, String values) throws SQLException {
        // Generic fill-in-the-blank SQL command for inserting into a table.
        
        String insertion = "INSERT INTO " + table + " "
                         + "(" + fields + ") "
                         + "VALUES " + "(" + values + ")";
        connection.createStatement().executeUpdate(insertion);
    }
        
    public boolean inDatabase(int health_care_no) throws SQLException {
        // Checks if a health care number already exists in the database.
        
        Statement stmt = connection.createStatement();
        String query = "SELECT * FROM patient WHERE health_care_no = " + health_care_no;
        ResultSet result = stmt.executeQuery(query);
        if (result.first()) {return true;}
        else return false;
    }
    
    public void printPatientFoundMessage() {
        // Message indicating that the given health care number exists in the database.
        // Gives the instructions for updating a patient.
        
        System.out.println("A patient with health care number " + patient_health_care_no + " exists in the database.");
        System.out.println("Please enter the updated patient information as per the prompts.");
        System.out.println("Skipping a prompt will retain the existing patient information for that category.");
    }
    
    public void printNewPatientMessage() {
        System.out.println("This health care number was not found in the database.");
        System.out.println("Would you like to create a new entry in the database using this health care number?");
    }
    
    public void printWelcomeMessage() {
        // Welcome screen.        
        
        System.out.println("\nWelcome to the Patient Information Update application.");
        System.out.println("To begin, enter the health care number of the patient you would like to update.");
        System.out.println("If the health care number is not found in the database, a new entry will be made for you.");
        System.out.println("Enter '-1' to leave this application.\n");
    }
}
