package sewon;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class FileWriterExample {
	static public HashMap<String, Connection> connMap = new HashMap<String, Connection>();;
	static public ArrayList<String> errorList = new ArrayList<String>();
	
	FileWriterExample() throws Exception
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
	
	public static void main(String[] args) throws Exception {
		
		// Load Overall DB Connection
		new FileWriterExample();
		
		// Load Key Set from ConnMap (ex: domain-project)
		Iterator<String> iter = connMap.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			String domain = key.split("-")[0];
			String project = key.split("-")[1];
			Connection conn = connMap.get(domain+"-"+project);
			
			//1. Check Whether or not Directory is exist 
			File dir = new File("./temp/"+domain+"/");
			if(!dir.exists())
				dir.mkdir();
			
			//2. Build File Writer for CSV File and Write Field Name
			// (CHG = Change) (project = product)
			// If divide line, add "\n" on last point
			BufferedWriter bw = new BufferedWriter(new FileWriter("./temp/"+domain+"/"+project+".csv"));
			bw.write("BUG_ID,STATUS,BUG_REPORTER,PROJECT,COMPONENT,VERSION,HW,OS,PRIORITY,SEVERITY,ASSIGNEE,");
			bw.write("CHG_STATUS,CHG_PROJECT,CHG_COMPONENT,CHG_VERSION,CHG_HW,CHG_OS,CHG_PRIORITY,CHG_SEVERITY,CHG_ASSIGNEE\n");
			
			//3. Read Bug Report Information (Metafield & History)
			try
			{
				Statement q = conn.createStatement();
				
				//3.1 Read All Data from Meta_Field Table
				ResultSet rs = q.executeQuery("SELECT * FROM META_FIELD");			
				while(rs.next()){
					System.out.println(domain+" - "+project+ " CSV WRITER START");
					
					//3.1.1 Write Metafield information on csv file
					bw.write(rs.getInt("BUG_ID")+","+rs.getString("STATUS")+","+rs.getString("BUG_REPORTER")+","+rs.getString("PROJECT")+","+rs.getString("COMPONENT")+","
							+rs.getString("VERSION")+","+rs.getString("HW")+","+rs.getString("OS")+","+rs.getString("PRIORITY")+","+rs.getString("SEVERITY")+","+rs.getString("ASSIGNEE")+",");
					
					//3.1.2 Declare Change Flag (0 or 1)			
					int CHG_STATUS = 0;
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
					
					Statement q2 = conn.createStatement();
					
					//3.1.4 Read Bug History of each bug_id from history
					ResultSet rs2 = q2.executeQuery("SELECT FIELD FROM HISTORY WHERE BUG_ID = "+bugID+" group by field");					
					while(rs2.next()){
						String field = rs2.getString("FIELD").toLowerCase();
						// A. Check Each Field Changes
						//************************** Problem: Don't consider REOPEN status, just check status is changed or not. (After Modifying)
						switch(field){
							case "status": CHG_STATUS = 1; break;
							case "product" : CHG_PROJECT = 1; break;	// project = product
							case "component" : CHG_COMPONENT = 1; break;
							case "version" : CHG_VERSION  = 1; break;
							case "hardware" : CHG_HW = 1; break;
							case "os" : CHG_OS = 1; break;
							case "priority" : CHG_PRIORITY = 1; break;
							case "severity" : CHG_SEVERITY = 1; break;
							case "assignee" : CHG_ASSIGNEE = 1; break;						
						}
					}
					
					//B. Write Changes
					bw.write(CHG_STATUS+","+CHG_PROJECT+","+CHG_COMPONENT+","+CHG_VERSION+","+CHG_HW+","+CHG_OS+","+CHG_PRIORITY+","+CHG_SEVERITY+","+CHG_ASSIGNEE+"\n");
					
					
					System.out.println(domain+" - "+project+ " CSV WRITER FINISH");
				}
				
			}
			catch(Exception e1)
			{		
				e1.printStackTrace();
			}
			
			bw.close();
			conn.close();	
		}		
		
	}

}
