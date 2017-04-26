package db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import common.BugReport;
import common.BugReportMetaField;
import common.Comment;
import common.History;

public class BugReportReader {

	static String[] filePath = {"E:\\eclipse_bugreport\\1-50000\\","E:\\eclipse_bugreport\\50001-100000\\","E:\\eclipse_bugreport\\100001-150000\\",
			"E:\\eclipse_bugreport\\150001-200000\\","E:\\eclipse_bugreport\\200001-250000\\","E:\\eclipse_bugreport\\250001-300000\\",
			"E:\\eclipse_bugreport\\300001-350000\\","E:\\eclipse_bugreport\\350001-400000\\","E:\\eclipse_bugreport\\400001-450000\\",
			"E:\\eclipse_bugreport\\450001-500000\\","E:\\eclipse_bugreport\\500001-600000\\"};


	public static void main(String[] args) throws IOException {
		
		BufferedReader br = new BufferedReader(new FileReader("./data/domain.csv"));
		
		String str;
		
		HashMap<String, String> domainMap = new HashMap<String, String>(); 
		while((str = br.readLine())!= null){
			String domain = str.split(",")[0].toLowerCase();
			String project= str.split(",")[1].replace("?", "").toLowerCase();
		
			domainMap.put(project, domain);
		}
		
		// 50,000 Bug Report Analysis
		File directory = new File(filePath[0]);
		
		ArrayList<BugReport> bugReportList = new ArrayList<BugReport>();
		ArrayList<BugReportMetaField> metaFieldList = new ArrayList<BugReportMetaField>();		
		File[] files = directory.listFiles();
		
		for(int i = 0 ; i<files.length; i++){
		//for(int i = 0 ; i<22; i++){
			System.out.print(files[i].getName()+"\t");
			BugReport bugReport = new BugReport();			
			BugReportMetaField metaField = new BugReportMetaField();			
			ArrayList<History> historyList = new ArrayList<History>();			
			ArrayList<Comment> commentList = new ArrayList<Comment>();
			
			bugReport.setBugID(Integer.parseInt(files[i].getName().split("\\.")[0]));
			metaField.setBugID(Integer.parseInt(files[i].getName().split("\\.")[0]));
			int desc = 0;
			int hist = 0;
			int bugID = Integer.parseInt(files[i].getName().split("\\.")[0]);
			boolean fail = false;
			String recentHistoryDate = "";
			
			br = new BufferedReader(new FileReader(files[i]));
			while((str=br.readLine()) != null){			
				if(str.contains("Bug #"+bugID+" does not exist.")){
					fail = true;
					break;
				}
				
				//1. Read Meta Field Data
				if(str.contains("<th>Summary:</th>")){
					bugReport.setSummary(br.readLine().replace("<td colspan=\"3\">", "").replace("</td>", ""));
				}
				if(str.contains("<th>Product:</th>")){
					br.readLine();
					String product = br.readLine().replace("<td>", "").replace("</td>", "");
					product = product.substring(product.indexOf("]")+2).toLowerCase();
					metaField.setProduct(product);
					metaField.setDomain(domainMap.get(metaField.getProduct().replace("?", "")));
				}
				if(str.contains("<th class=\"rightcell\">Reporter:</th>")){
					String reporter = br.readLine();
					if(reporter.contains("&lt;"))
						reporter = reporter.substring(0, reporter.indexOf("&lt;")).replace("-", "").replace("_","");
					else
						reporter = reporter.replace("-", "").replace("_","");
					metaField.setReporter(reporter.replace("<td>", "").replace("</td>", "").replace("@", "").replace(" ", "").replace("\\.", "").toLowerCase());
				}
				if(str.contains("<th>Component:</th>")){
					metaField.setComponent(br.readLine().split("</td>")[0].replace("<td>", "").replace(" ", "").toLowerCase());
					String assignee = br.readLine();
					if(assignee.contains("&lt;"))
						assignee = assignee.substring(0, assignee.indexOf("&lt;")).replace("@", "").replace("-", "").replace("_","");
					else
						assignee = assignee.replace("@", "").replace("-", "").replace("_","");
					metaField.setAssignee(assignee.replace("<td>", "").replace("</td>", "").toLowerCase().replace(" ", "").replace("\\.", ""));
				}
				if(str.contains("<th>Status:</th>")){
					metaField.setStatus((br.readLine().replace("<td>", "")+":"+br.readLine()).replace(" ", "").toLowerCase());
				}
				if(str.contains("<th class=\"rightcell\">QA Contact:</th>")){
					metaField.setQa(br.readLine().replace("<td>", "").replace("</td>", "").replace(" ", "").toLowerCase());
				}
				if(str.contains("<th>Severity:</th>")){
					String severity = br.readLine();
					severity = severity.substring(severity.indexOf(">")+1);
					metaField.setSever(severity);
				}
				if(str.contains("<th>Priority:</th>")){
					String priority = br.readLine();
					priority = priority.substring(priority.indexOf(">")+1);
					metaField.setPriority(priority);
				}
				if(str.contains("<th class=\"rightcell\">CC:</th>")){
					String ccs = br.readLine();
					if(ccs.contains(",")){
						ccs = ccs.substring(ccs.indexOf(">")+1);
						ArrayList<String> ccList = new ArrayList<String>();
						Collections.addAll(ccList, ccs.split(","));
						metaField.setCcList((ArrayList<String>) ccList.clone());
					}
				}
				if(str.contains("<th>Version:</th>")){
					String version = br.readLine();
					version = version.substring(version.indexOf(">")+1).substring(0, version.indexOf("<")-1);
					metaField.setProductVer(version);
				}
				if(str.contains("<th>Target Milestone:</th>")){
					String milestone = br.readLine();
					milestone = milestone.substring(milestone.indexOf(">")+1).substring(0, milestone.indexOf("<")-1);
					metaField.setMileStone(milestone.toLowerCase());
				}
				if(str.contains("<th>Hardware:</th>")){
					String hardware = br.readLine();
					hardware = hardware.substring(hardware.indexOf(">")+1).substring(0, hardware.indexOf("<")-1);					
					metaField.setHardware(hardware.toLowerCase());
				}
				if(str.contains("<th>OS:</th>")){
					String os = br.readLine();
					os = os.substring(os.indexOf(">")+1).substring(0, os.indexOf("<")-1);	
					metaField.setOs(os.toLowerCase());
				}
				if(str.contains("<th>Whiteboard:</th>")){
					metaField.setWhiteboard(br.readLine().replace("<td colspan=\"3\">","").replace("</td>", "").replace(" ", ""));
				}
				

				//2. Read Description Data
				if(str.contains("<pre class=\"bz_comment_text\">") && desc == 0){
					bugReport.setDescription(str.substring(str.indexOf("\">")).replace(">", ""));
					desc = 1;					
				}
				if(desc == 1){
					String description = bugReport.getDescription()+"\n"+str;
					if(str.contains("</pre>")){
						desc = 2;
						description = description.replace("</pre>", "");
					}
					
					bugReport.setDescription(description);
				}
				
				
				//3. Read Comment Data
				if(desc == 2 && str.contains("<a href=\"https://bugs.eclipse.org/bugs/show_bug.cgi?id="+bugID)){
					Comment comment = new Comment();
					String num = str;
					num = num.substring(num.indexOf("Comment"), num.indexOf("</")).replace("Comment ", "");
					comment.setNum(Integer.parseInt(num));
					while((str=br.readLine())!=null){
						if(str.contains("<span class=\"bz_comment_user\">")){
							String data = br.readLine();
							data = data.replace("<span class=\"vcard\"><span class=\"fn\">", "");
							data = data.substring(0, data.indexOf("</"));
							comment.setCommenter(data.replace(" ", "").replace("-", "").replace("_", "").replace("@", "").replace("\\.", "").toLowerCase());
							break;
						}
					}
					while((str=br.readLine())!=null){
						if(str.contains("<span class=\"bz_comment_time\">")){					
							String data = br.readLine();
							comment.setDate(data.replace(" EST", "").replace(" EDT", "").replace("  ", ""));
							break;
						}
					}
					String text = "";
					int commentIter = 0;
					while((str=br.readLine())!=null){				

						if(commentIter == 1 && str.contains("</pre>")){								
							text = text+" "+str.replace("</pre>", "");
							comment.setDescription(text);break;
						}
						if(commentIter == 1)
							text = text + " "+str+"\n";	
						if(str.contains("<pre class=\"bz_comment_text\">")){
							if(str.contains("*** <a class=\"bz_bug_link")){
								break;
							}
							if(str.contains("</pre>")){
								comment.setDescription(str.replace("<pre class=\"bz_comment_text\">", "").replace("</pre>", ""));
								commentIter = 1;
								break;
							}
							text = str.replace("<pre class=\"bz_comment_text\">", "")+"\n";
							commentIter = 1;
						}	
					}
					if(commentIter > 0)
						commentList.add(comment);
				}				
				
				//4. Read History Data 
				if(str.contains("<table border cellpadding=\"4\">") && hist == 0){
					hist = 1;
				}			
				if(hist == 1 && str.contains("<td rowspan=") && str.contains("valign=\"top\">") && (str.contains("EDT") || str.contains("EST"))){
					recentHistoryDate = str;
					recentHistoryDate = recentHistoryDate.substring(recentHistoryDate.indexOf("\">")+2).replace(" EDT","").replace("EST", "");		
				}				
				
				if(hist == 1 && str.toLowerCase().replace(" ", "").equals("status")){
					History history = new History();
					history.setBugID(bugID);
					history.setField("status");
					history.setDate(recentHistoryDate);
					String data = br.readLine();
					history.setPrev(data.replace("</td><td>", "").replace(" ", "").toLowerCase());
					data = br.readLine();
					history.setPost(data.replace("</td><td>", "").replace(" ", "").toLowerCase());
					historyList.add(history);
				}
				
				if(hist == 1 && str.toLowerCase().replace(" ", "").equals("resolution")){
					History history = new History();
					history.setBugID(bugID);
					history.setField("resolution");
					history.setDate(recentHistoryDate);
					String data = br.readLine();
					history.setPrev(data.replace("</td><td>", "").replace(" ", "").toLowerCase());
					data = br.readLine();
					history.setPost(data.replace("</td><td>", "").replace(" ", "").toLowerCase());
					historyList.add(history);
				}
				
				if(hist == 1 && str.toLowerCase().replace(" ", "").equals("product")){
					History history = new History();
					history.setBugID(bugID);
					history.setField("product");
					history.setDate(recentHistoryDate);
					String data = br.readLine();
					history.setPrev(data.replace("</td><td>", "").replace(" ", "").toLowerCase());
					data = br.readLine();
					history.setPost(data.replace("</td><td>", "").replace(" ", "").toLowerCase());
					historyList.add(history);
				}
				
				if(hist == 1 && str.toLowerCase().replace(" ", "").equals("component")){
					History history = new History();
					history.setBugID(bugID);
					history.setField("component");
					history.setDate(recentHistoryDate);
					String data = br.readLine();
					history.setPrev(data.replace("</td><td>", "").replace(" ", "").toLowerCase());
					data = br.readLine();
					history.setPost(data.replace("</td><td>", "").replace(" ", "").toLowerCase());
					historyList.add(history);
				}
				

				if(hist == 1 && str.toLowerCase().replace(" ", "").equals("priority")){
					History history = new History();
					history.setBugID(bugID);
					history.setField("priority");
					history.setDate(recentHistoryDate);
					String data = br.readLine();
					history.setPrev(data.replace("</td><td>", "").replace(" ", "").toLowerCase());
					data = br.readLine();
					history.setPost(data.replace("</td><td>", "").replace(" ", "").toLowerCase());
					historyList.add(history);
				}
				

				if(hist == 1 && str.toLowerCase().replace(" ", "").equals("severity")){
					History history = new History();
					history.setBugID(bugID);
					history.setField("severity");
					history.setDate(recentHistoryDate);
					String data = br.readLine();
					history.setPrev(data.replace("</td><td>", "").replace(" ", "").toLowerCase());
					data = br.readLine();
					history.setPost(data.replace("</td><td>", "").replace(" ", "").toLowerCase());
					historyList.add(history);
				}
				
				if(hist == 1 && str.toLowerCase().replace(" ", "").equals("os")){
					History history = new History();
					history.setBugID(bugID);
					history.setField("os");
					history.setDate(recentHistoryDate);
					String data = br.readLine();
					history.setPrev(data.replace("</td><td>", "").replace(" ", "").toLowerCase());
					data = br.readLine();
					history.setPost(data.replace("</td><td>", "").replace(" ", "").toLowerCase());
					historyList.add(history);
				}
				
				if(hist == 1 && str.toLowerCase().replace(" ", "").equals("version")){
					History history = new History();
					history.setBugID(bugID);
					history.setField("version");
					history.setDate(recentHistoryDate);
					String data = br.readLine();
					history.setPrev(data.replace("</td><td>", "").replace(" ", "").toLowerCase());
					data = br.readLine();
					history.setPost(data.replace("</td><td>", "").replace(" ", "").toLowerCase());
					historyList.add(history);
				}
				

				if(hist == 1 && str.toLowerCase().replace(" ", "").equals("hardware")){
					History history = new History();
					history.setBugID(bugID);
					history.setField("hardware");
					history.setDate(recentHistoryDate);
					String data = br.readLine();
					history.setPrev(data.replace("</td><td>", "").replace(" ", "").toLowerCase());
					data = br.readLine();
					history.setPost(data.replace("</td><td>", "").replace(" ", "").toLowerCase());
					historyList.add(history);
				}
				
				if(hist == 1 && str.toLowerCase().replace(" ", "").equals("assignee")){
					History history = new History();
					history.setBugID(bugID);
					history.setField("assignee");
					history.setDate(recentHistoryDate);
					String data = br.readLine();
					data = data.replace(" ", "").replace("-", "").replace("_", "").toLowerCase();
					history.setPrev(data.replace("</td><td>", "").replace(" ", ""));
					data = br.readLine();
					history.setPost(data.replace("</td><td>", "").replace(" ", ""));
					historyList.add(history);
				}
			}				
			if(!fail){
				bugReport.setCommentList(commentList);
				bugReport.setHistoryList(historyList);
				bugReportList.add(bugReport);				
				metaFieldList.add(metaField);
				System.out.println((i+1.0)/files.length+ " "+commentList.size()+" "+historyList.size()+ " "+metaFieldList.size()+" "+bugReportList.size());
			}else
				System.out.println();
		}
	}
}
