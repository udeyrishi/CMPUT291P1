package common;


import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

public class UIO {
	Scanner input;
	
	public UIO(InputStream in) {
		this.input = new Scanner(in);
	}
	
	public void cleanUp() {
		if (input != null)
			input.close();
	}
	
	public String getInputString(String message) {
		System.out.print(message);
		String output = input.nextLine();
		return output.trim();
	}
	
	public Integer getInputInteger(String message) {
		while (true) {
			String integer = getInputString(message);
			try {
				return Integer.parseInt(integer);
			}
			catch (NumberFormatException e) {
				System.out.println("Invalid input. Try again.");
			}
		}
	}
	
	public Date getInputDate(String message) {
		while (true) {
			String date = getInputString(message);
			try {
				return getDate(date);
			}
			catch (IllegalArgumentException e) {
				System.out.println("Invalid input. Try again.");
			}
		}
	}
	
	private Date getDate(String date_string) throws IllegalArgumentException {
		String[] split_date = date_string.split("\\-");
		Calendar date = Calendar.getInstance();
		date.clear();
		try {
			date.set(Calendar.YEAR, Integer.valueOf(split_date[0]));
			date.set(Calendar.MONTH, Integer.valueOf(split_date[1])-1);
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
