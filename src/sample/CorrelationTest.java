package sample;

import java.text.DecimalFormat;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

public class CorrelationTest {

	public static void main(String[] args) throws Exception{	
		
		double[] data1 = {0.0, 0.1, 0.2, 0.3, 0.0, 0.5, 0.6, 0.7, 0.1, 0.0, 0.0, 0.0};
		double[] data2 = {0.5, 0.8, 0.2, 0.3, 0.0, 0.5, 0.6, 0.7, 0.1, 0.0, 0.0, 0.9};
		
		String dispPatterm = "0.###";
		DecimalFormat form = new DecimalFormat(dispPatterm);
		
		System.out.print("CORRELATION : " +form.format(new PearsonsCorrelation().correlation(data1, data2)));
		
	}
}
