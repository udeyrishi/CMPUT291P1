package common;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

public class UIO {
	
	public UIO() {
	}
	
	public String getInputString(String message) {
		System.out.print(message);
		Scanner input = new Scanner(System.in);
		String output = input.next();
		input.close();
		return output;
	}
	
	public Integer getInputInteger(String message) {
		System.out.print(message);
		Scanner input = new Scanner(System.in);
		Integer output = input.nextInt();
		input.close();
		return output;
	}
	
	public Date getInputDate(String message) {
		System.out.print(message);
		Scanner input = new Scanner(System.in);
		Date output = getDate(input.next());
		input.close();
		return output;
	}
	
	
	
	private Date getDate(String date_string) throws IllegalArgumentException {
		String[] split_date = date_string.split("\\-");
		Calendar date = Calendar.getInstance();
		date.clear();
		try {
			date.set(Calendar.YEAR, Integer.valueOf(split_date[0]));
			date.set(Calendar.MONTH, Integer.valueOf(split_date[1]));
			date.set(Calendar.DATE, Integer.valueOf(split_date[2]));
			return date.getTime();
		} catch (NumberFormatException nfe) {
			throw new IllegalArgumentException("Please enter dates as yyyy-mm-dd");
		} catch (ArrayIndexOutOfBoundsException idxe) {
			throw new IllegalArgumentException("Please enter dates as yyyy-mm-dd");
		}
	}
	
	/* Inspired by Picasso and Prescription.java */
	public String getTestDateInSQLDateStringForm(Date date) {
		return (new SimpleDateFormat("dd-MMM-YYYY")).format(date);
	}
}
