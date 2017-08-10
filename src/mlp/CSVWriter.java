package mlp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CSVWriter {
	static String domain = "birt";
	static String project = "birt";
	
	public static void main(String[] args) throws Exception {
		
		// 1. Build Connection for Domain & Project
		Connection conn = DriverManager.getConnection("jdbc:h2:./DB/"+domain+"/"+project,"sa","");
		System.out.println("-------- CONNECT WITH "+domain+" "+project+" DB ----------");;
		
		// 2. Build File Writer for CSV File & Write Field Name
		// (Modify Sequence of Field Name for mapping Meka Data Files (.arff) )
		BufferedWriter bw = new BufferedWriter(new FileWriter("./data/"+project+"-changing.csv"));
		// Below fields are predicted by MEKA
		// # of predicted Fields : 8
		bw.write("CHG_PROJECT,CHG_COMPONENT,CHG_VERSION,CHG_HW,CHG_OS,CHG_PRIORITY,CHG_SEVERITY,CHG_ASSIGNEE,");
		bw.write("BUG_ID,BUG_REPORTER,PROJECT,COMPONENT,VERSION,HW,OS,PRIORITY,SEVERITY,ASSIGNEE\n");
		
		// 3. Read Bug Report Information (Meta Field & History)
		// (It will be modified by Sewon Data set such as BEASS and etc..)
		Statement q = conn.createStatement();

		System.out.println(domain+" - "+project+ " CSV WRITER START");
		//3.1 Read All Data from Meta_Field Table
		ResultSet rs = q.executeQuery("SELECT * FROM META_FIELD");
		int num = 0;
		boolean errorOlderBug = false;
		while(rs.next()){			
			//3.1.1 Read Metafield information
			String metaField = rs.getInt("BUG_ID")+","+rs.getString("BUG_REPORTER").replace(",","_")
					+","+rs.getString("PROJECT").replace(",","_")+","+rs.getString("COMPONENT").replace(",","_")+","
					+rs.getString("VERSION").replace(",","_")+","+rs.getString("HW").replace(",","_")+","
					+rs.getString("OS").replace(",","_")+","+rs.getString("PRIORITY").replace(",","_")
					+","+rs.getString("SEVERITY").replace(",","_")+","+rs.getString("ASSIGNEE").replace(",","_")+"\n";
			metaField = metaField.replace("\\.", "").replace("-", "").replace("_", "").replace(" ", "");
			String status = rs.getString("STATUS");
			if(!status.contains("fix")){
				System.out.println(rs.getInt("bug_id")+" didn't be fixed");
				continue;
			}
			
			
			//3.1.2 Declare Change Flag (0 or 1)	
			int CHG_PROJECT = 0;
			int CHG_COMPONENT = 0;
			int CHG_VERSION  = 0;
			int	CHG_HW = 0;
			int CHG_OS = 0;
			int CHG_PRIORITY = 0;
			int CHG_SEVERITY = 0;
			int CHG_ASSIGNEE = 0;
			
			//3.1.3 Get bug id from each data
			int bugID = rs.getInt("bug_id");
			num++;
			
			Statement q2 = conn.createStatement();
			
			//3.1.4 Read Bug History of each bug_id from history
			ResultSet rs2 = q2.executeQuery("SELECT * FROM HISTORY WHERE BUG_ID = "+bugID+" order by date desc");
			boolean beforeAssigned = false;
			while(rs2.next()){
				String field = rs2.getString("FIELD").toLowerCase();
				String prev = rs2.getString("PREV").toLowerCase().replace("-", "").replace("_", "").replace(" ", "").replace(",", "");
				String post = rs2.getString("POST").toLowerCase().replace("-", "").replace("_", "").replace(" ", "").replace(",", "");
//				System.out.println(metaField);
				// A. Check Each Field Changes
				// (Just Check 8 Fields because we consider just changes before first developer assignment)
				switch(field){
					case "status" : 
						if(post.toLowerCase().contains("assign")){
							beforeAssigned = true;
						}
						status = prev;
						
					break;	// project = product
					case "product" : 
						if(beforeAssigned)						
							CHG_PROJECT = 1; 
						String[] data = metaField.split(",");
						metaField = "";
						for(int i = 0 ; i<data.length; i++){
							if(i != 2 && i != data.length-1)
								metaField = metaField+data[i]+",";
							else if(i == data.length-1)
								metaField = metaField+data[i].replace("\n", "")+"\n";
							else
								metaField = metaField+prev.replace(",", "")+",";
						}						
						break;	// project = product
					case "component" :
						if(beforeAssigned)		
							CHG_COMPONENT = 1; 
						data = metaField.split(",");
						metaField = "";
						for(int i = 0 ; i<data.length; i++){
							if(i != 3 && i != data.length-1)
								metaField = metaField+data[i]+",";
							else if(i == data.length-1){
								metaField = metaField+data[i].replace("\n", "")+"\n";
							}
							else{
								metaField = metaField+prev.replace(",", "")+",";								
							}
						}	
						break;
					case "version" : 
						if(beforeAssigned)		
							CHG_VERSION  = 1; 
						data = metaField.split(",");
						metaField = "";
						for(int i = 0 ; i<data.length; i++){
							if(i != 4 && i != data.length-1)
								metaField = metaField+data[i]+",";
							else if(i == data.length-1)
								metaField = metaField+data[i].replace("\n", "")+"\n";
							else{
								if(prev.contains("unspeci"))
									prev = "uns";
								prev = prev.replaceAll(".0", "");
								metaField = metaField+prev.replace(",", "")+",";
							}
						}				
						break;
					case "hardware" : 
						if(beforeAssigned)		
							CHG_HW = 1; 
						data = metaField.split(",");
						metaField = "";
						for(int i = 0 ; i<data.length; i++){
							if(i != 5 && i != data.length-1)
								metaField = metaField+data[i]+",";
							else if(i == data.length-1)
								metaField = metaField+data[i].replace("\n", "")+"\n";
							else
								metaField = metaField+prev.replace(",", "")+",";
						}		
						break;
					case "os" : 
						if(beforeAssigned)		
							CHG_OS = 1; 
						data = metaField.split(",");
						metaField = "";
						for(int i = 0 ; i<data.length; i++){
							if(i != 6 && i != data.length-1)
								metaField = metaField+data[i]+",";
							else if(i == data.length-1)
								metaField = metaField+data[i].replace("\n", "")+"\n";
							else
								metaField = metaField+prev.replace(",", "")+",";
						}		
						break;
					case "priority" : 
						if(beforeAssigned)		
							CHG_PRIORITY = 1; 
						data = metaField.split(",");
						metaField = "";
						for(int i = 0 ; i<data.length; i++){
							if(i != 7 && i != data.length-1)
								metaField = metaField+data[i]+",";
							else if(i == data.length-1)
								metaField = metaField+data[i].replace("\n", "")+"\n";
							else
								metaField = metaField+prev.replace(",", "")+",";
						}					
						break;
					case "severity" :
						if(beforeAssigned)		
							CHG_SEVERITY = 1; 
						data = metaField.split(",");
						metaField = "";
						for(int i = 0 ; i<data.length; i++){
							if(i != 8 && i != data.length-1)
								metaField = metaField+data[i]+",";
							else if(i == data.length-1)
								metaField = metaField+data[i].replace("\n", "")+"\n";
							else
								metaField = metaField+prev.replace(",", "")+",";
						}			
						break;
					case "assignee" : 
						if(beforeAssigned)	
							CHG_ASSIGNEE = 1; 
						// although status is no changed to "Assigned",
						// assignee is assigned directly.
						// we consider that this case is errors.
//						if(beforeAssigned) 
//							errorOlderBug = true;
						data = metaField.split(",");
						metaField = "";
						for(int i = 0 ; i<data.length; i++){
							if(i != data.length-1)
								metaField = metaField+data[i]+",";
							else
								metaField = metaField+prev.replace(",", "").replace(".", "").replace("\\.", "").replace("\n", "")+"\n";
						}		
						break;						
				}
			}
			
			if(!errorOlderBug){
				System.out.println(domain+" - "+project+ " WRITE: " + bugID+" "+status);
//				System.out.println(CHG_PROJECT+","+CHG_COMPONENT+","+CHG_VERSION+","+CHG_HW+","+CHG_OS+","+CHG_PRIORITY+","+CHG_SEVERITY+","+CHG_ASSIGNEE+",");
				System.out.println(metaField);
			//B. Write Changes & MetaField
				bw.write(CHG_PROJECT+","+CHG_COMPONENT+","+CHG_VERSION+","+CHG_HW+","+CHG_OS+","+CHG_PRIORITY+","+CHG_SEVERITY+","+CHG_ASSIGNEE+",");
				bw.write(metaField);
			}
			
		}
		
		System.out.println(domain+" - "+project+ " CSV WRITER FINISH : # of Bugs = " +num);				
		
		bw.close();
	}
}
