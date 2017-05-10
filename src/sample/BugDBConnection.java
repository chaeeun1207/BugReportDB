package sample;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class BugDBConnection {
	static public HashMap<String, Connection> connMap = new HashMap<String, Connection>();;
	static public ArrayList<String> errorList = new ArrayList<String>();
	
	BugDBConnection() throws Exception
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
		new BugDBConnection();
		
		Connection cheConnection = connMap.get("science-ice");
				
		try
		{
			int bugid= 483617;
			Statement q = cheConnection.createStatement();
			ResultSet rs = q.executeQuery("SELECT * FROM history where bug_id = "+bugid+" order by bug_id, date");			
			while(rs.next()){
				System.out.println(rs.getInt("BUG_ID")+" = "+rs.getString("DATE")+ " : "+rs.getString("FIELD")+" = "+rs.getString("prev")+" -> "+rs.getString("post"));
				if(rs.getString("field").toLowerCase().equals("assignee")){
					Statement q2 = cheConnection.createStatement();
					ResultSet rs2 = q2.executeQuery("SELECT * FROM meta_field where bug_id = "+bugid);			
					while(rs2.next()){
						System.out.println(rs2.getInt("BUG_ID")+" = "+rs2.getString("assignee"));
						
					}
				}
				
			}
			
		}
		catch(Exception e1)
		{		
			e1.printStackTrace();
		}
		
		try
		{
			Statement q = cheConnection.createStatement();
			ResultSet rs = q.executeQuery("SELECT * FROM history WHERE PARSEDATETIME(date,'yyyy-MM-dd hh:mm:ss') < DATE '2007-02-21'  order by date asc");			
			while(rs.next()){
				System.out.println(rs.getInt("BUG_ID")+" = "+rs.getString("DATE")+ " : "+rs.getString("FIELD")+" = "+rs.getString("prev")+" -> "+rs.getString("post"));
			}
			
		}
		catch(Exception e1)
		{		
			e1.printStackTrace();
		}
		
		System.out.println("===================================================================================================================");
		
		String targetDate = "2007-02-21";
		
		try
		{
			Statement q = cheConnection.createStatement();
			ResultSet rs = q.executeQuery("SELECT * FROM history WHERE PARSEDATETIME(date,'yyyy-MM-dd hh:mm:ss') < DATE '"+targetDate+"'  order by date asc");			
			while(rs.next()){
				System.out.println(rs.getInt("BUG_ID")+" = "+rs.getString("DATE")+ " : "+rs.getString("FIELD")+" = "+rs.getString("prev")+" -> "+rs.getString("post"));
			}
			
		}
		catch(Exception e1)
		{		
			e1.printStackTrace();
		}
	}

}
