package sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class BugDBConnection {
	static public HashMap<String, Connection> connMap = new HashMap<String, Connection>();;
	static public ArrayList<String> errorList = new ArrayList<String>();
	
	BugDBConnection() throws Exception
	{
		Class.forName("org.h2.Driver");
		Connection conn = DriverManager.getConnection("jdbc:h2:./DB/birt/birt","sa","");
		System.out.println("-------- CONNECT WITH BIRT BIRT DB ----------");;
		connMap.put("birt-birt", conn);
	}
	
	public static void main(String[] args) throws Exception {		
		new BugDBConnection();
		
		try
		{
			Statement q = connMap.get("birt-birt").createStatement();
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
			Statement q = connMap.get("birt-birt").createStatement();
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
