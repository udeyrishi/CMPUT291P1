package common;


import java.io.InputStream;
import java.util.Date;

public class UIO extends SimpleUIO {

	public UIO(InputStream in) {
		super(in);
	}
	
	public Integer getInputInteger(String message) {
		while (true) {
			try {
				return super.getInputInteger(message);
			}
			catch (NumberFormatException e) {
				System.out.println("Invalid input. Try again.");
			}
		}
	}
	
	public Date getInputDate(String message) {
		while (true) {
			try {
				return super.getInputDate(message);
			}
			catch (IllegalArgumentException e) {
				System.out.println("Invalid input. Try again.");
			}
		}
	}

}
