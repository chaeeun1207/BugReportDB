package highscool;

import java.util.Random;

import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.evaluation.output.prediction.AbstractOutput;
import weka.classifiers.evaluation.output.prediction.CSV;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class WekaTest {


	public static void main(String[] args) throws Exception{
		
		// 1. Load Data Set
		DataSource source = new DataSource("./data/iris.csv");
		Instances data = source.getDataSet();
		data.setClassIndex(data.numAttributes()-1);
		String[] options = new String[2];
		
		// 2. Set Machine Learning Algorighm (Classifier)
		RandomForest classifier = new RandomForest();
		// 3. Evaluation
		Evaluation eval = new Evaluation(data);		
		StringBuffer forPredictionsPrinting = new StringBuffer();
		AbstractOutput plainText = new CSV();
		plainText.setBuffer(forPredictionsPrinting);
		options[0] = "-p"; options[1]="1";
		plainText.setOptions(options);
//		plainText.setOutputDistribution(true);
		
		// 4. Evaluating with 10-fold cross validation
		eval.crossValidateModel(classifier, data, 10, new Random(1), plainText);
		
		// 5. Print Prediction Summary
		System.out.println(forPredictionsPrinting.toString());
		
		
		// 6. Print Each Prediction Results
		String[] lines = forPredictionsPrinting.toString().split("\n");
		for(int i = 1; i<lines.length;i++){
			String tempStr = lines[i].replace("+", "");
			String actual = tempStr.split(",")[1];			
			String predict = tempStr.split(",")[2];
			String id = tempStr.split(",")[5];
			if(id.equals("?")) continue;
			String firstProb = tempStr.split(",")[3];
			String secondProb = tempStr.split(",")[4];
			System.out.println(id+"\t"+actual+" "+predict+" ");
			System.out.println(id+"\t"+firstProb+"\t"+secondProb);
			
		}
		
	}


}
