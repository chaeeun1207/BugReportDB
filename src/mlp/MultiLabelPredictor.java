package mlp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.ResultSet;

import meka.classifiers.multilabel.CC;
import meka.classifiers.multilabel.Evaluation;
import meka.core.MLUtils;
import meka.core.Result;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;

public class MultiLabelPredictor{
	
	/* For Heap Space: Window > Preference > General > Show Heap Status Checking
	 *  Right Click > Run As > Run Configuration > Arguments > "-xmx3g"
	 *  */
	
	/* REF: https://github.com/Waikato/meka/blob/master/src/main/java/meka/classifiers/multilabel/Evaluation.java */
	
	
	static String fileName = "birt-changing";
	static String classNum = "8"; // 1~classNum	

	public static void main(String[] args) throws Exception{
		
		// 1. load CSV file
		CSVLoader loader = new CSVLoader();
		loader.setSource(new File("./data/"+fileName+".csv"));
		Instances data = loader.getDataSet();
		data.setClassIndex(data.numAttributes()-1);
		
	    // 2. save ARFF for WEKA
		ArffSaver saver = new ArffSaver();
		saver.setInstances(data);
		saver.setFile(new File("./data/"+fileName+".arff"));
		saver.writeBatch();
		DataSource source = new DataSource("./data/"+fileName+".arff");
		data = source.getDataSet();
		
		// 2.1 Modify Class Type From Numeric To Nominal for avoiding heap space error
		NumericToNominal convert= new NumericToNominal();
        String[] options= new String[2];
        options[0]="-R";
        options[1]="1-"+classNum;  //range of variables to make numeric

        convert.setOptions(options);
        convert.setInputFormat(data);

        data = Filter.useFilter(data, convert);
        
	    // 3. modify ARFF for MEKA
		BufferedWriter bw = new BufferedWriter(new FileWriter("./data/"+fileName+".arff"));
		String[] dataSet = data.toString().split("\n");
		bw.write("@relation '"+fileName+": -C "+classNum+"'\n");
		for(int i = 1 ;  i < dataSet.length; i++)
			bw.write(dataSet[i]+"\n"); 
		bw.close();

		// 4. Load ARFF for MEKA
		data = DataSource.read("./data/"+fileName+".arff"); 
		MLUtils.prepareData(data);
		
		// 5. Evaluating with 10-fold Cross validation on BIRT Data Set
		int numFolds = 10; 
		System.out.println("Cross-validate CC classifier using " + numFolds + " folds"); 
		CC classifier = new CC();
		
		// If we specify the bottom classifier, Default is J48 which can't use Numeric Classes
		// However Random Forest occurs "OutOfMemoryError"
//		classifier.setClassifier(new RandomForest()); 
		// further configuration of classifier 
		String top = "PCut1"; 
		Result result = Evaluation.cvModel(classifier, data, numFolds, top); 
		System.out.println(result);
		System.out.println(Result.getResultAsString(result));
		
		// 6. Evaluating with divided train & test data set
		System.out.println("Split Evaluation CC classifier using 80%"); 
		int dataNum = data.size();
		int trainSize = (int)(dataNum*0.8);
		int testSize = dataNum - trainSize;
		
		// 6.1 Split Training and Testing Data Set
		Instances trainData = new Instances(data, 0, trainSize);
		Instances testData = new Instances(data, (int)(dataNum*0.8), testSize);
		result = Evaluation.evaluateModel(classifier, trainData, testData);
		System.out.println(result);
		System.out.println(Result.getResultAsString(result));
		
		// 6.2 Print Result with BugID
		String[] resultArray = Result.getResultAsString(result).split("\n");
		// Start index "1" because Result Array's first line is Title such as PREDICTION (N = 4482.0)...
		// End index "length-1" because Result Array's final line is a line such as "================"
		for(int i = 1 ; i<resultArray.length-1; i++){
			String bugID = testData.get(i-1).toString().split(",")[8];
			System.out.println(bugID+"\t"+resultArray[i]);
		}
				
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
