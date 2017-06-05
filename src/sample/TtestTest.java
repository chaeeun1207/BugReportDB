package sample;

import java.text.DecimalFormat;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.inference.TTest;

// http://commons.apache.org/proper/commons-math/javadocs/api-3.4/org/apache/commons/math3/stat/inference/TTest.html

public class TtestTest {

	public static void main(String[] args) throws Exception{	
		
		double[] data1 = {0.0, 0.1, 0.2, 0.3, 0.0, 0.5, 0.6, 0.7, 0.1, 0.0, 0.0, 0.0};
		double[] data2 = {0.5, 0.8, 0.2, 0.3, 0.0, 0.5, 0.6, 0.7, 0.1, 0.0, 0.0, 0.9};
		
		double tStatistic;
		
		TTest ttest = new TTest();
		tStatistic = ttest.pairedTTest(data1, data2);

		String dispPatterm = "0.###";
		DecimalFormat form = new DecimalFormat(dispPatterm);
		
		System.out.print("p-value : " +form.format(tStatistic));
		System.out.print("if p-value < 0.05, two dataset is no different");
		
		
	}
}
