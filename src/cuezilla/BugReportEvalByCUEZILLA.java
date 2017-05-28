package cuezilla;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.CUEZILLA;
import db.DB;
import util.Splitter;
import util.Stem;

public class BugReportEvalByCUEZILLA {
	static String project = "aspectj"; 
	static String brUrl = "https://bugs.eclipse.org/bugs/show_bug.cgi?id=";	
	static String attachUrl = "https://bugs.eclipse.org/bugs/attachment.cgi?id=";

	static public HashMap<String, Connection> connMap = new HashMap<String, Connection>();;
	static public ArrayList<String> errorList = new ArrayList<String>();
	
	public BugReportEvalByCUEZILLA() throws Exception
	{
		Class.forName("org.h2.Driver");
		BufferedReader br = new BufferedReader(new FileReader("./data/domain.csv"));
		String str;
		while((str=br.readLine())!= null){
			String[] line = str.split(",");
			System.out.println(line[0] +" "+line[1].replace("?", ""));
			String domain = line[0];
			String project = line[1].replace("?", "");

			Connection conn = DriverManager.getConnection("jdbc:h2:./DB/"+domain+"/"+project,"sa","");
			System.out.println("-------- CONNECT WITH "+domain+" "+project+" DB ----------");;

			connMap.put(domain.toLowerCase()+"-"+project.toLowerCase(), conn);
			
		}
	}
	
	
	public static void extract() throws Exception{
		new BugReportEvalByCUEZILLA();
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Iterator<String> iter = connMap.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			String domain = key.split("-")[0];
			String project = key.split("-")[1];
			
			System.out.println(key);
			
			Connection conn = connMap.get(key);
			Statement q = conn.createStatement();
			ResultSet rs = q.executeQuery("SELECT * FROM BUG_REPORT A, META_FIELD B WHERE A.BUG_ID = B.BUG_ID");
//			System.out.print(rs);
			
			String stackTrace ="";
		    String tracePattern = "(([a-zA-Z0-9_\\-$]*\\.)*[a-zA-Z_<][a-zA-Z0-9_\\-$>]*" +
		        		"[a-zA-Z_<(][a-zA-Z0-9_\\-$>);/\\[]*" +
		        		"\\(([a-zA-Z_][a-zA-Z0-9_\\-]*\\.java:[0-9]*|[a-zA-Z_][a-zA-Z0-9_\\-]*\\.java\\((?i)inlined compiled code\\)|[a-zA-Z_][a-zA-Z0-9_\\-]*\\.java\\((?i)compiled code\\)|(?i)native method|(?i)unknown source)\\))";
		        
		    Pattern r = Pattern.compile(tracePattern);
		    
			while(rs.next()){				
				int bugID = rs.getInt("BUG_ID");
				
				Statement q22 = conn.createStatement();
				ResultSet rs22 = q22.executeQuery("SELECT * FROM CUEZILLA WHERE BUG_ID = "+bugID);
				if(rs22.next()) continue;
				
				String description  = rs.getString("DESCRIPTION");
				String dateString = rs.getString("OPEN_DATE");
				Date reportDate = format.parse(dateString);
				
				String reporter = rs.getString("BUG_REPORTER");

				System.out.println(domain+" "+project+" "+bugID+" "+dateString);
				
				int itemScore = 0;
				int codeScore = 0;
				int sTraceScore = 0;
				int patchScore = 0;
				
				//1. Patch Extraction
				int start = -1;
				int end = 0;
				//String forPatch = description.replace("\n", ".");
				String[] itemizedDesc = description.split("\n");
				for(int j = 0; j<itemizedDesc.length; j++){
					String item = itemizedDesc[j];				
					if(start == -1 && item.toLowerCase().contains("index:")){
						if(j+2<itemizedDesc.length && itemizedDesc[j+2].toLowerCase().contains("==="))
							start = j;
					}else if(start == -1 && item.toLowerCase().contains("rcs")){
						if(j+1<itemizedDesc.length && itemizedDesc[j+1].toLowerCase().contains("file:"))
							start = j;
					}else if(start == -1 && item.toLowerCase().contains("---")){
						if(j+1<itemizedDesc.length && itemizedDesc[j+1].toLowerCase().contains("+++"))
							start = j;
					}else if(start == -1 && item.toLowerCase().contains("diff")){
						if(j+1<itemizedDesc.length && itemizedDesc[j+1].toLowerCase().contains("-"))
							start = j;
					}
					
					if(start > -1 && item.equals("+}")){
						end = j;
					}else if(start > -1){
						if(j+1<itemizedDesc.length && item.contains("+") && itemizedDesc[j+1].contains(";")){
							end = j;
						}
					}else if(start > -1 && item.equals("-}")){
						end = j;
					}else if(start > -1){
						if(j+1<itemizedDesc.length && item.contains("-") && itemizedDesc[j+1].contains(";"))
								end = j;
					}
					//System.out.println(bug.getID()+" "+j+" "+start+" "+end +" " +item);
				}
				if(end > 0)
					patchScore = 1;
				description = "";
				for(int j = 0; j < start+1; j++){
					description = description + "\n"+itemizedDesc[j];
				}
				description = description + "\n";
				for(int j = end; j < itemizedDesc.length; j++){
					description = description + "\n"+itemizedDesc[j];
				}
//				System.out.println(domain+" "+project+" "+bugID+" "+description);
				
				//2. Stack Trace Extraction			
				
			    Matcher m = r.matcher(description);		    
		        while (m.find()) {
		        	String group = m.group();
		        	stackTrace =  stackTrace+"\n"+group;
		        	description = description.replace(group, "");
		        	sTraceScore = 1;
		        }        
		        if(sTraceScore == 1){
//		        	System.out.println(description);
		        	description.replace("at", "");
		        }
		        
		        //3. Code Example Extraction
		        List<String> split = readValidJsonStrings(description);
		        if(split.size() > 0){
		        	codeScore = 1;
		        	//System.out.println(split);
		        	for(int j = 0; j<split.size(); j++)
		        		description = description.replace(split.get(j), "");
		        }
		        if(description.contains("public void class main(String[] args)")){
		        	codeScore = 1;
		        	description = description.replace("public static void main(String[] args)","");
		        	//System.err.println("public static void main(String[] args)");
		        }
		        
//		        System.out.println(description);
		        
		        //4. itemization
				itemizedDesc = description.split("\n");
				int itemNum = 1;
				for(int j = 0 ; j<itemizedDesc.length; j++){
					String item = itemizedDesc[j];
					item = item.replaceAll(" ", "");
					item = item.replaceAll("\t", "");
					item = item.replaceAll("\"", "");
					item = item.trim();
					if(item.isEmpty())
						continue;
//					System.out.println(item+"\t\t\t"+item.substring(0,1)+"\t\t\t!!!!!!!!!!!!!!");
					if(item.substring(0,1).contains("-")){
						if(j+1<itemizedDesc.length){
//							System.out.println("\t\t\t\t\t\t\t"+item);
							if(itemizedDesc[j+1].length()>0 && item.substring(0, 1).contains("-")){
								itemScore++;//break;
							}
						}
					}
					if(item.substring(0,1).contains("+")){
						if(j+1<itemizedDesc.length)
							if(itemizedDesc[j+1].length()>0 && item.substring(0, 1).contains("+")){
								itemScore++;//break;
							}
					}
					if(item.substring(0,1).contains("*")){
						if(j+1<itemizedDesc.length)
							if(itemizedDesc[j+1].length()>0 &&item.substring(0, 1).contains("*")){
								itemScore++;//break;
							}
					}
					if(itemNum < 3 && item.substring(0,1).contains(String.valueOf(itemNum))){
						itemNum++;
					}
					if(itemNum < 6 && item.substring(0,1).contains(String.valueOf(itemNum/2))){
						itemNum++;
					}
					if(itemNum < 9 && item.substring(0,1).contains(String.valueOf(itemNum/3))){
						itemNum++;
					}
					if(itemNum >3){
						itemScore = 1; break;
					}				
					if(item.split(" -").length>2){
						itemScore=1; break;
					}
					if(item.split("\t-").length>2){
						itemScore=1; break;
					}
					
					if(itemScore >= 2){
						itemScore = 1; break;
					}
				}       

		        

				int keyActionScore = 0;
				int keyResultScore = 0;
				int keyStepScore = 0;
				int keyBuildScore = 0;
				int keyUIScore = 0;
				String[] actionKeywords = {"add","open","update","find","use","set","select","show","remove","create","load","get","save","run","install","click","hang","try",
						"change","display","appear","move"};
				String[] resultKeywords = {"observed","expected", "error","missing","crash","freeze","boot","exception","fail"};
				String[] stepKeywords = {"steps","repro"};
				String[] buildKeywords = {"build"};
				String[] uiKeywords = {"toolbar","dialog","menu","view","page","editor","color","tab"};
				
		        //5. keyword Extraction
				String preProcessedDescription = Stem.stem(Splitter.splitNatureLanguageEx(description));
				String actionDescription = Stem.stem(actionKeywords);
				String resultDescription = Stem.stem(resultKeywords);
				String stepDescription = Stem.stem(stepKeywords);
				String buildDescription = Stem.stem(buildKeywords);
				String uiDescription = Stem.stem(uiKeywords);
				String[] descriptionWords = preProcessedDescription.split(" ");
				actionKeywords = actionDescription.split(" ");			
				resultKeywords = resultDescription.split(" ");
				stepKeywords = stepDescription.split(" ");
				buildKeywords = buildDescription.split(" ");
				uiKeywords = uiDescription.split(" ");
				
				for(int j = 0; j<descriptionWords.length; j++){
					if(keyActionScore == 0){
						for(int k = 0; k<actionKeywords.length; k++){
							if(descriptionWords[j].toLowerCase().equals(actionKeywords[k].toLowerCase())){
								keyActionScore = 1;
								break;
							}						
						}
					}
					if(keyResultScore == 0){
						for(int k = 0; k<resultKeywords.length; k++){
							if(descriptionWords[j].toLowerCase().equals(resultKeywords[k].toLowerCase())){
								keyResultScore = 1;
								break;
							}						
						}
					}
					if(keyStepScore == 0){
						for(int k = 0; k<stepKeywords.length; k++){
							if(descriptionWords[j].toLowerCase().equals(stepKeywords[k].toLowerCase())){
								keyStepScore = 1;
								break;
							}						
						}
					}
					if(keyBuildScore == 0){
						for(int k = 0; k<buildKeywords.length; k++){
							if(descriptionWords[j].toLowerCase().equals(buildKeywords[k].toLowerCase())){
								keyBuildScore = 1;
								break;
							}						
						}
					}
					if(keyUIScore == 0){
						for(int k = 0; k<uiKeywords.length; k++){
							if(descriptionWords[j].toLowerCase().equals(uiKeywords[k].toLowerCase())){
								keyUIScore = 1;
								break;
							}						
						}
					}
				}
				
				double keywordScore = (keyActionScore+keyResultScore+keyStepScore+keyBuildScore+keyUIScore*1.0)/5.0;
				    
		        
		        int screenShotScore = 0;			
		        //6. Attachment Analysis (patch, screenshot)
		        
		        
		        Statement q2 = conn.createStatement();
				ResultSet rs2 = q2.executeQuery("SELECT * FROM ATTACHMENT WHERE BUG_ID ="+bugID);
				
				while(rs2.next()){
					String attachDateString = rs2.getString("DATE").trim();
					attachDateString = attachDateString+":00";
					Date attachDate = format.parse(attachDateString);
					String attacher = rs2.getString("ATTACHER");
					
					long diff = (attachDate.getTime()-reportDate.getTime())/1000;
					
					if(diff > 600){
						System.out.println(diff+" sec after");
						continue;
					}
					
					if(!attacher.toLowerCase().contains(reporter.toLowerCase()) && !reporter.toLowerCase().contains(attacher.toLowerCase())){
						System.out.println("diff Reporter" + reporter + " "+attacher);
						continue;
					}
						
					
					
					String type = rs2.getString("TYPE");
					if(patchScore == 0 && type.toLowerCase().contains("patch"))
						patchScore = 1;
					if(sTraceScore == 0 && type.toLowerCase().contains("stack"))
						sTraceScore = 1;
					if(codeScore == 0 && type.toLowerCase().contains("code"))
						codeScore = 1;
					if(screenShotScore == 0 && type.toLowerCase().contains("image"))
						screenShotScore = 1;
					if(patchScore == 1 && sTraceScore == 1 && codeScore == 1 && screenShotScore == 1)
						break;
				}
				
				
				DB db = new DB(domain, project);
				
				CUEZILLA c = new CUEZILLA();
				c.setActionKeyword(keyActionScore);
				c.setBugID(bugID);
				c.setBuildKeyword(keyBuildScore);
				c.setCodeExample(codeScore);
				c.setDomain(domain);
				c.setItemization(itemScore);
				c.setKeywordScore(keywordScore);
				c.setPatch(patchScore);
				c.setProject(project);
				c.setResultKeyword(keyResultScore);
				c.setScreenShot(screenShotScore);
				c.setStackTrace(sTraceScore);
				c.setStepKeyword(keyStepScore);
				c.setUiKeyword(keyUIScore);
				
				
				db.insertCuezilla(c);
				
				System.out.println(c);
				
			}
			
		}
	}
	
	
	public static List<String> readValidJsonStrings(String allText) {   
	    List<String> jsonList = new ArrayList<String>();
	    int[] endsAt = new int[1];
	    endsAt[0] = 0;
	    int num = 0;
	    while(num >= 0) {
	    	try{
		        int startsAt = allText.indexOf("{", endsAt[0]);
//		        System.out.println(startsAt+" "+endsAt[0]);
		        num = startsAt-endsAt[0];
		        if(startsAt < endsAt[0]){
		        	System.err.println(startsAt+" "+endsAt[0]);
		        	break;
		        }
		        if (startsAt == -1) {
		            break;
		        }
		        
		        String aJson = parseJson(allText, startsAt, endsAt);
		        if(aJson.equals(null))
		        	break;
		        jsonList.add(aJson);
	    	}catch(Exception e ){
	    		return jsonList;
	    	}
	    }
		return jsonList;
	}

	private static String parseJson(String str, int startsAt, int[] endsAt) {

	    Stack<Integer> opStack = new Stack<Integer>();
	    int i = startsAt + 1;
	    while (i < str.length()) {

	        if (str.charAt(i) == '}') {
	            if (opStack.isEmpty()) {
	                endsAt[0] = i + 1;
	                return str.substring(startsAt, i + 1);
	            } else {
	                opStack.pop();
	            }
	        }else if (str.charAt(i) == '{') {
	            opStack.push(i);
	        }

	        i++;
	    }
	    return null;
	}
	
}
