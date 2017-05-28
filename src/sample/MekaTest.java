package sample;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import meka.classifiers.multilabel.BR;
import meka.classifiers.multilabel.Evaluation;
import meka.core.MLUtils;
import meka.core.Result;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;

public class MekaTest {
	static String fileName = "iris";
	static String classNum = "1";
	
	/** Class Num Example
		@relation ¡¯Example_Dataset: -C 3¡¯
		@attribute category {A,B,C,NEG}  	// <== Class
		@attribute label {0,1}				// <== Class
		@attribute rank {1,2,3}				// <== Class
		@attribute X1 {0,1}
		@attribute X2 {0,1}
		@attribute X3 numeric
		@attribute X4 numeric
	 */


	public static void main(String[] args) throws Exception{
		
		// load CSV file
		CSVLoader loader = new CSVLoader();
		loader.setSource(new File("./data/iris.csv"));
		Instances data = loader.getDataSet();
		data.setClassIndex(data.numAttributes()-1);
		
	    // save ARFF for WEKA
		ArffSaver saver = new ArffSaver();
		saver.setInstances(data);
		saver.setFile(new File("./data/iris.arff"));
		saver.writeBatch();
		DataSource source = new DataSource("./data/iris.arff");
		data = source.getDataSet();
		
	    // modify ARFF for MEKA
		BufferedWriter bw = new BufferedWriter(new FileWriter("./data/iris.arff"));
		String[] dataSet = data.toString().split("\n");
		bw.write("@relation '"+fileName+": -C "+classNum+"'\n");
		for(int i = 1 ;  i < dataSet.length; i++)
			bw.write(dataSet[i]+"\n");
		bw.close();

		// Load ARFF for MEKA
		data = DataSource.read("./data/iris.arff"); 
		MLUtils.prepareData(data);
		
		int numFolds = 10; 
		System.out.println("Cross-validate BR classifier using " + numFolds + " folds"); 
		BR classifier = new BR(); 
//		classifier.setClassifier(new RandomForest());
		// further configuration of classifier 
		String top = "PCut1"; 
		Result result = Evaluation.cvModel(classifier, data, numFolds, top); 
				System.out.println(result); 
/*
		
		
		String[] options = new String[2];
		RandomForest classifier = new RandomForest();
		Evaluation eval = new Evaluation(data);		
		StringBuffer forPredictionsPrinting = new StringBuffer();
		AbstractOutput plainText = new CSV();
		plainText.setBuffer(forPredictionsPrinting);
		options[0] = "-p"; options[1]="1";
		plainText.setOptions(options);
		plainText.setOutputDistribution(true);		
		eval.crossValidateModel(classifier, data, 10, new Random(1), plainText);
		System.out.println(forPredictionsPrinting.toString());
		
		String[] lines = forPredictionsPrinting.toString().split("\n");
		for(int i = 1; i<lines.length;i++){
			String tempStr = lines[i].replace("+", "");
			String actual = tempStr.split(",")[1];			
			String predict = tempStr.split(",")[2];
			String id = tempStr.split(",")[7];
			if(id.equals("?")) continue;
			String firstProb = tempStr.split(",")[4];
			String secondProb = tempStr.split(",")[5];
			String thirdProb = tempStr.split(",")[6];
			System.out.println(id+"\t\t\t"+actual+" "+predict+" ");
			System.out.println(id+"\t\t\t"+firstProb+"\t"+secondProb+"\t"+thirdProb);
			
		}
		*/
	}


}
