package mlp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import util.PreProcessor;

public class TextInfoExtraction {
	static String domain = "birt";
	static String project = "birt";
	
	public static void main(String[] args) throws Exception {
		
		// 1. Build Connection for Domain & Project
		Connection conn = DriverManager.getConnection("jdbc:h2:./DB/"+domain+"/"+project,"sa","");
		System.out.println("-------- CONNECT WITH "+domain+" "+project+" DB ----------");;
		
		// 2. Extracting Bug Report Text Field & Preprocessing
		Statement q = conn.createStatement();
		ResultSet rs = q.executeQuery("SELECT * FROM BUG_REPORT");		
		// ref: bugTextMap = Key: BugId , Value: Term List
		HashMap<Integer, String[]> bugTextMap = new HashMap<Integer, String[]>();
		// ref: total term Set
		HashSet<String> termSet = new HashSet<String>(); 
		PreProcessor pp = new PreProcessor(); 
		while(rs.next()){
			int bugID = rs.getInt("BUG_ID");
			String summary = pp.stemContentNatural(pp.splitNatureLanguage(pp.stemContentSource(pp.splitSourceCode(rs.getString("SUMMARY")))));			
			String description = pp.stemContentNatural(pp.splitNatureLanguage(pp.stemContentSource(pp.splitSourceCode(rs.getString("DESCRIPTION")))));
			String[] textField = pp.stemContentSource(pp.splitNatureLanguage(summary + " "+description)).split(" ");
			for(int i = 0 ; i<textField.length; i++)
				termSet.add(textField[i]);
			bugTextMap.put(bugID, textField);
		}
		
		// 3. Total Term Transformation from termSet based on HashSet to bugTermArray based on ArrayList
		ArrayList<String> bugTermArray = new ArrayList<String>(termSet);
		System.out.println(bugTermArray.size());
		// 4 Write TextField based on just term frequency (= Term Count)

		BufferedWriter bw = new BufferedWriter(new FileWriter("./data/"+project+"-termList.csv"));
		bw.write("termID,Term\n");
		for(int i = 0; i<bugTermArray.size(); i++){
			bw.write(i+","+bugTermArray.get(i)+"\n");
		}		
		bw.close();
		// 5. Count term frequency and Write it
		bw = new BufferedWriter(new FileWriter("./data/"+project+"-texfField.csv"));
		bw.write("bugID,");
		for(int i = 0; i<bugTermArray.size()-1; i++){
			bw.write(i+",");
		}
		bw.write((bugTermArray.size()-1)+"\n");
		
		Iterator<Integer> iter = bugTextMap.keySet().iterator();
		while(iter.hasNext()){
			int bugID = iter.next();
			String[] textField = bugTextMap.get(bugID);
			ArrayList<String> termList = new ArrayList<String>();
			Collections.addAll(termList, textField);
			HashMap<String, Integer> termCntMap = countTerms(termList);
			
			bw.write(bugID+",");
			for(int i = 0 ; i<bugTermArray.size()-1; i++){
				String term = bugTermArray.get(i);
				if(termCntMap.containsKey(term)){
					bw.write(termCntMap.get(term)+",");					
				}else{
					bw.write(0+",");
				}
			}
			String term = bugTermArray.get(bugTermArray.size()-1);
			if(termCntMap.containsKey(term)){
				bw.write(termCntMap.get(term)+"\n");					
			}else{
				bw.write(0+"\n");
			}
				
		}
		bw.close();		
	}

	private static HashMap<String, Integer> countTerms(ArrayList<String> termList) {
		HashMap<String, Integer> result = new HashMap<String, Integer>();
		
		for(int i = 0 ; i < termList.size(); i++){
			String term = termList.get(i);
			if(result.containsKey(term)){
				result.replace(term, result.get(term)+1);
			}else{
				result.put(term, 1);
			}
		}		
		
		return result;		
	}

}
