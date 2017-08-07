package assigner;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import common.BugReportMetaField;
import util.PreProcessor;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.output.prediction.AbstractOutput;
import weka.classifiers.evaluation.output.prediction.CSV;
import weka.classifiers.functions.SMO;
import weka.core.Debug.Random;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class MLBasedAssigner {
	
	//Platform = UI, SWT (Component)
	static String[] projects = {"birt/birt", "eclipse/jdt","eclipse/platform/ui", "eclipse/platform/swt","tools/aspectj"};  
	static HashMap<String, Integer> numMap = new HashMap<String, Integer>();
	static Connection dbConn = null;
	static HashMap<Integer, String> bugMFMap = new HashMap<Integer, String>(); 
	static HashMap<Integer, String> bugFinalAssigneeMap = new HashMap<Integer, String>();
	public static void main(String[] args) throws Exception{
		
		for(int i = 4 ; i<5; i++){
//		for(int i = 3 ; i<projects.length; i++){
			String project = projects[i];
			dbConn = bugDBConnection(project);
			writeFile(dbConn,10000,project);
			addTermInfo(dbConn,project);
			dbConn.close();
			
			String path = "./data/"+project.split("/")[1]+"-total.csv";
			if(project.split("/").length>2)
				path = "./data/"+project.split("/")[2]+"-total.csv";
			DataSource source = new DataSource(path);
			
			Instances data = source.getDataSet();
			data.setClassIndex(data.numAttributes()-1);
			String[] options = new String[2];
			SMO classifier = new SMO();
			
			StringBuffer forPredictionsPrinting = new StringBuffer();
			AbstractOutput plainText = new CSV();
			plainText.setBuffer(forPredictionsPrinting);
			options[0] = "-p"; options[1]="1";
			plainText.setOptions(options);
			plainText.setOutputDistribution(true);	
			
/*			int trainSize = (int) Math.round(data.numInstances() * 70 / 100);
rmsep 			int testSize = data.numInstances() - trainSize;
			Instances train = new Instances(data, 0, trainSize);
			Instances test = new Instances(data, trainSize, testSize);
			classifier.buildClassifier(train);
			Evaluation eval = new Evaluation(train);	
			eval.evaluateModel(classifier,test,plainText);*/
			
			Evaluation eval = new Evaluation(data);				
			eval.crossValidateModel(classifier, data, 10, new Random(1), plainText); 
			System.out.println(eval.toSummaryString());
			String[] classDetail = eval.toClassDetailsString().split("\n");
			HashMap<String,String> assignList = new HashMap<String,String>();
			int num = 1;
			for(int j = 3 ; j<classDetail.length; j++){
				String[] classData = classDetail[j].replace("  ", "").split(" ");
				if(classData.length>3){
					assignList.put(String.valueOf(num),classData[3]);
					System.out.println(num+":"+classData[3]);
					num++;
				}
				
			}
			
			System.out.println(forPredictionsPrinting.toString());
			
			double top1Num = 0;
			double top3Num = 0;
			double top5Num = 0;
			double top10Num = 0;
			double otherNum = 0;
			
			String[] lines = forPredictionsPrinting.toString().split("\n");
			for(int j = 1; j<lines.length;j++){
				String tempStr = lines[j].replace("+", "");
				String[] strList = tempStr.split(",");
				ArrayList<String> othersList = new ArrayList<String>();
				for(int k = 4 ; k<strList.length-1; k++)
					othersList.add(strList[k]);
//				System.out.println(tempStr);
				String[] probData = tempStr.split(",");
				HashMap<String,Double> probList = new HashMap<String,Double>();
				num = 1;
				for(int l = 4; l<4+assignList.size()-1; l++){
					probList.put(String.valueOf(num), Double.parseDouble(probData[l].replace("*", "")));
					num++;
				}
				String actual = tempStr.split(",")[1];			
				String top1 = tempStr.split(",")[2];
				
				String id = tempStr.split(",")[strList.length-1];
				if(id.equals("?")) continue;

				String top2 = "";
				String top3 = "";
				String top4 = "";
				String top5 = "";
				String top6 = "";
				String top7 = "";
				String top8 = "";
				String top9 = "";
				String top10 = "";
				
				Iterator<String> it = sortByValue(probList).iterator();
				num = 1;
				while(it.hasNext()){
					String keyNum = it.next();
					switch(num){
					case 1: top1 = assignList.get(keyNum); if(actual.split(":")[1].equals(assignList.get(keyNum))) {top1Num++;top3Num++; top5Num++;top10Num++; System.out.println("1");}break;
					case 2: top2 = assignList.get(keyNum); if(actual.split(":")[1].equals(assignList.get(keyNum))) {top3Num++; top5Num++;top10Num++;System.out.println("2");}break;
					case 3: top3 = assignList.get(keyNum); if(actual.split(":")[1].equals(assignList.get(keyNum))) {top3Num++; top5Num++;top10Num++;System.out.println("3");}break;
					case 4: top4 = assignList.get(keyNum); if(actual.split(":")[1].equals(assignList.get(keyNum))) {top5Num++; top10Num++;System.out.println("4");}break;
					case 5: top5 = assignList.get(keyNum); if(actual.split(":")[1].equals(assignList.get(keyNum))) {top5Num++; top10Num++;System.out.println("5");}break;
					case 6: top6 = assignList.get(keyNum); if(actual.split(":")[1].equals(assignList.get(keyNum))) {top10Num++; System.out.println("6");}break;
					case 7: top7 = assignList.get(keyNum); if(actual.split(":")[1].equals(assignList.get(keyNum))) {top10Num++; System.out.println("7");}break;
					case 8: top8 = assignList.get(keyNum); if(actual.split(":")[1].equals(assignList.get(keyNum))) {top10Num++; System.out.println("8");}break;
					case 9: top9 = assignList.get(keyNum); if(actual.split(":")[1].equals(assignList.get(keyNum))) {top10Num++;  System.out.println("9");}break;
					case 10: top10 = assignList.get(keyNum); if(actual.split(":")[1].equals(assignList.get(keyNum))) {top10Num++; System.out.println("10");}break;
//					default: otherNum++; System.out.println("0");break;
					}
					
					num++;
				}
//				System.out.println(top1Num+" "+top3Num+" "+top5Num+" "+top10Num+" "+otherNum+" "+probList.size());
//				System.out.println(top1+" "+top2+" "+top3+" "+top4+" "+top5+" "+top6+" "+top7+" "+top8+" "+top9+" "+top10);
//				System.out.println(id+"\t\t\t"+actual+" "+predict+" ");
				
			}
		}
		
	}
	
	private static void addTermInfo(Connection dbConn, String projects) throws IOException, SQLException {
		PreProcessor pp = new PreProcessor(); 
		String project = projects.split("/")[1];
		if(projects.split("/").length > 2)
			project = projects.split("/")[2];
		
		int i = 0;
		HashSet<String> terms = new HashSet<String>();
		HashMap<Integer, String[]> bugContentMap = new HashMap<Integer, String[]>();
		Iterator iter = bugMFMap.keySet().iterator();
		while(iter.hasNext()){					
			int bugID = (int) iter.next();
			String sql = "select * from bug_Report where bug_id = "+bugID;
			Statement q = dbConn.createStatement();
			ResultSet rs = q.executeQuery(sql);
			HashMap<String, Integer> termCntMap = new HashMap<String, Integer>(); 
			if(rs.next()){
				String content = rs.getString("SUMMARY");
				content = content + " "+rs.getString("DESCRIPTION");
				String[] termOfContent = pp.splitNatureLanguageEx(pp.stemContentNatural(pp.splitNatureLanguage(content)));
				bugContentMap.put(bugID,termOfContent);
				for(int j = 0 ; j<termOfContent.length; j++){
//					System.out.println(termOfContent[j]);;
					if(termCntMap.containsKey(termOfContent[j])){
						termCntMap.replace(termOfContent[j], termCntMap.get(termOfContent[j])+1);
					}else
						termCntMap.put(termOfContent[j], 1);
				}
				
				Iterator<String> it = sortByValue(termCntMap).iterator();
				int num = 0;
				while(it.hasNext()){
					if(num >=10 ) break;
					String term = it.next();
					terms.add(term);
//					System.out.println(term+" "+termCntMap.get(term));
					num++;
				}
			}
		}
			
		
		BufferedWriter bw = new BufferedWriter(new FileWriter("./data/"+project+"-Terms.csv"));
		bw.write("bugID,");		
		
		ArrayList<String> uniqueTerm = new ArrayList<String>();
		uniqueTerm.addAll(terms);
		for(i = 0 ; i<uniqueTerm.size(); i++){			
			bw.write(uniqueTerm.get(i)+",");
		}
		bw.write("finalAssignee\n");
		System.out.println(uniqueTerm.size()+" "+terms.size());
		
		HashMap<Integer, String> bugContentMapForCombine = new HashMap<Integer, String>(); 
		
		Iterator it = bugContentMap.keySet().iterator();
		while(it.hasNext()){
			int bugID =  (int) it.next();
			bw.write(bugID+",");
			String[] content = bugContentMap.get(bugID);
			String termCntString = "";
			for(i = 0 ; i<uniqueTerm.size(); i++){	
				String term = uniqueTerm.get(i);
				int termCnt = 0;
				for(int j = 0 ; j<content.length; j++){
					if(content[j].equals(term)){
						termCnt++; break;
					}
				}
				termCntString = termCntString+termCnt+",";
				bw.write(termCnt+",");
			}
			bugContentMapForCombine.put(bugID, termCntString);
			
			bw.write(bugFinalAssigneeMap.get(bugID));
			bw.write("\n");
		}
		
		bw.close();
		System.out.println(project+" TERM FILE FINISH");
			
		bw = new BufferedWriter(new FileWriter("./data/"+project+"-total.csv"));
		bw.write("bugID,mf-openDate,mf-reporter,mf-project,mf-component,mf-version,mf-hw,mf-priority,mf-severity,mf-os,mf-assignee,");
		for(i = 0 ; i<uniqueTerm.size(); i++){			
			bw.write(uniqueTerm.get(i)+",");
		}
		bw.write("mf-finalAssignee\n");
		
		TreeMap<Integer, String> tm = new TreeMap<Integer,String>(bugMFMap);
		iter = tm.keySet().iterator();
		while(iter.hasNext()){
			int bugID = (int) iter.next();
			bw.write(bugID+","+bugMFMap.get(bugID)+bugContentMapForCombine.get(bugID)+bugFinalAssigneeMap.get(bugID)+"\n");
		}
		bw.close();		
		System.out.println(project+" TOTAL FILE FINISH");
		
/*
		BufferedWriter bw2 = new BufferedWriter(new FileWriter("./data/"+project+"-AttMap.csv"));
		String[] header = {"bugID","mf-openDate","mf-reporter","mf-project","mf-component","mf-version","mf-hw","mf-priority","mf-severity","mf-os","mf-assignee"};
		for(i = 0 ; i<header.length; i++)
			bw2.write(i+1+":"+header[i]+"\n");
		for(i = 0 ; i<uniqueTerm.size(); i++)
			bw2.write(i+12+":"+uniqueTerm.get(i)+"\n");
		bw2.close();*/
	}
	
	
	static class ValueComparator implements Comparator<String> {
	    Map<String, Double> base;

	    public ValueComparator(Map<String, Double> base) {
	        this.base = base;
	    }
	    
	    @Override
	    public int compare(String a, String b) {
//	        if(base.get(a) <= base.get(b)) {
	      if(base.get(a) >= base.get(b)) { // 내림차순
	            return -1;
	        } else {
	            return 1;
	        }
	    }
	}
	
	
	public static double tf(List<String> doc, String term) {
	    double result = 0;
	    for (String word : doc) {
	       if (term.equalsIgnoreCase(word))
	              result++;
	       }
	    return result / doc.size();
	}
	
	public static double idf(List<List<String>> docs, String term) {
	    double n = 0;
	    for (List<String> doc : docs) {
	        for (String word : doc) {
	            if (term.equalsIgnoreCase(word)) {
	                n++;
	                break;
	            }
	        }
	    }
	    return Math.log(docs.size() / n);
	}
	
	
	
	public static double getTfIdf(List<String> doc, List<List<String>> docs, String term) {
	    return tf(doc, term) * idf(docs, term);
	}

	static void writeFile(Connection dbConn, int num, String projects) throws IOException, SQLException{		
		String project = projects.split("/")[1];
		if(projects.split("/").length > 2)
			project = projects.split("/")[2];
		
		BufferedWriter bw = new BufferedWriter(new FileWriter("./data/"+project+"-metafield.csv"));
		bw.write("bugID,mf-openDate,mf-reporter,mf-project,mf-component,mf-version,mf-hw,mf-priority,mf-severity,mf-os,mf-assignee,mf-finalAssignee\n");
		
		String sql = "select rownum as no, * from (SELECT  * FROM META_FIELD where status like '%fixed%'"
				+ "and assignee not like '%inbox%' and assignee not like '"+project+"')";
		
		if(projects.split("/").length > 2)
			sql = "select rownum as no, * from (SELECT  * FROM META_FIELD where status like '%fixed%'"
					+ "and assignee not like '%inbox%' and assignee not like '%"+project+"%'"
							+ "and assignee not like '%"+projects.split("/")[1]+"%' and component like '%"+project+"%')";
		
		
		Statement q = dbConn.createStatement();
		ArrayList<BugReportMetaField> metaFieldList = new ArrayList<BugReportMetaField>(); 
		ResultSet rs = q.executeQuery(sql);
		int i = 0;
		while(rs.next()){
			BugReportMetaField bugMF = new BugReportMetaField();
			bugMF.setBugReportMetaField(rs.getInt("BUG_ID"), rs.getString("BUG_REPORTER"), rs.getString("DOMAIN"), rs.getString("PROJECT"), rs.getString("COMPONENT"),rs.getString("VERSION"),
					rs.getString("HW"), rs.getString("OPEN_DATE"), rs.getString("MODIFIED_DATE"), rs.getString("STATUS"), rs.getString("PRIORITY"),
					rs.getString("SEVERITY"), rs.getString("ASSIGNEE"), rs.getString("OS"), rs.getString("ASSIGNEE"));
			int bugID = rs.getInt("BUG_ID");
//			System.out.println(bugMF);
			Statement q2 = dbConn.createStatement();
			ResultSet rs2 = q2.executeQuery("SELECT * FROM HISTORY WHERE BUG_ID = " +bugID +" order by date desc");
			while(rs2.next()){
				String field = rs2.getString("FIELD");
				switch(field){
					case "product" : bugMF.setProduct(rs2.getString("PREV")); break;
					case "component" : bugMF.setComponent(rs2.getString("PREV")); break;
					case "version" : bugMF.setProductVer(rs2.getString("PREV")); break;
					case "hardware" : bugMF.setHardware(rs2.getString("PREV")); break;
					case "priority" : bugMF.setPriority(rs2.getString("PREV")); break;
					case "severity" : bugMF.setSever(rs2.getString("PREV")); break;
					case "assignee" : bugMF.setAssignee(rs2.getString("PREV")); break;
					case "os" : bugMF.setOs(rs2.getString("PREV")); break;
					case "resolution" : bugMF.setStatus(bugMF.getStatus().split(":")[0]+":"+rs2.getString("PREV")); break;
					case "status": bugMF.setStatus(rs2.getString("PREV")+":"+bugMF.getStatus().split(":")[1]); break;				
				}
			}
			
			String assignee = bugMF.getAssignee();
			q2 = dbConn.createStatement();
			rs2 = q2.executeQuery("SELECT * FROM NAME_MAP where FULL_NAME like '%"+assignee+"%' or abb_name like '%"+assignee+"%'");
			if(rs2.next()){
				bugMF.setAssignee(rs2.getString("FULL_NAME"));
			}
			metaFieldList.add(bugMF);
			bw.write(bugMF.getBugID()+","+bugMF.getOpenDate()+","+bugMF.getReporter()+","+bugMF.getProduct()+","+bugMF.getComponent()+","+bugMF.getProductVer()
					+","+bugMF.getHardware()+","+bugMF.getPriority()+","+bugMF.getSever()+","+bugMF.getOs()+","+bugMF.getAssignee()+","+bugMF.getFinalAssignee()+"\n");
			String mfString = 
			bugMFMap.put(bugID, bugMF.getOpenDate()+","+bugMF.getReporter()+","+bugMF.getProduct()+","+bugMF.getComponent()+","+bugMF.getProductVer()
			+","+bugMF.getHardware()+","+bugMF.getPriority()+","+bugMF.getSever()+","+bugMF.getOs()+","+bugMF.getAssignee()+",");
			bugFinalAssigneeMap.put(bugID, bugMF.getFinalAssignee());
//			System.out.println(bugMF);
			i++;
			System.out.println(i+" "+bugMF.getBugID()+" "+project+" FINISHIED TO WRITE FILES "+bugMF.getAssignee()+" / "+bugMF.getFinalAssignee());
		}			
		
		bw.close();
	}
	
	
	static Connection bugDBConnection(String projects) throws Exception
	{
		Class.forName("org.h2.Driver");
		if(projects.split("/").length>2)
			projects = projects.split("/")[0]+"/"+projects.split("/")[1];

		Connection conn = DriverManager.getConnection("jdbc:h2:./DB/"+projects,"sa","");
		System.out.println("-------- CONNECT WITH "+projects+" DB ----------");;

		return conn;
	}
	
	public static List sortByValue(final Map map){
        List<String> list = new ArrayList();
        list.addAll(map.keySet());
         
        Collections.sort(list,new Comparator(){             

            public int compare(Object o1,Object o2){
                Object v1 = map.get(o1);
                Object v2 = map.get(o2);                 

                return ((Comparable) v1).compareTo(v2);
            }

        });

        Collections.reverse(list); // 주석시 오름차순

        return list;

    }
}
