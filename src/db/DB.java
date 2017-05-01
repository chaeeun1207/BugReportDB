package db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import common.Attachment;
import common.BugReport;
import common.BugReportMetaField;
import common.Comment;
import common.History;

public class DB {
	static public HashMap<String, Connection> connMap = new HashMap<String, Connection>();;
	static public ArrayList<String> errorList = new ArrayList<String>();
	
	DB(HashMap<String, String> domainMap) throws Exception
	{
		Class.forName("org.h2.Driver");
		Iterator iter = domainMap.keySet().iterator();
		while(iter.hasNext()){
			String project = (String) iter.next();
			String domain = domainMap.get(project);
			Connection conn = DriverManager.getConnection("jdbc:h2:./DB/"+domain+"/"+project,"sa","");
			System.out.println("-------- CONNECT WITH "+domain+" "+project+" DB ----------");;
			connMap.put(domain+"-"+project, conn);
		}
	}		

	DB(HashMap<String, String> domainMap,boolean del) throws Exception
	{
		if(del == true){
			Class.forName("org.h2.Driver");
			Iterator iter = domainMap.keySet().iterator();
			while(iter.hasNext()){
				String project = (String) iter.next();
				String domain = domainMap.get(project);
				Connection conn = DriverManager.getConnection("jdbc:h2:./DB/"+domain+"/"+project,"sa","");
				System.out.println("-------- CONNECT WITH "+domain+" "+project+" DB ----------");;
				connMap.put(domain+"-"+project, conn);
				dropTable(domain,project);			
				if(conn!=null) createTable(domain,project);
			}
		}
		if(del == false){
			Class.forName("org.h2.Driver");
			Iterator iter = domainMap.keySet().iterator();
			while(iter.hasNext()){
				String project = (String) iter.next();
				String domain = domainMap.get(project);
				Connection conn = DriverManager.getConnection("jdbc:h2:./DB/"+domain+"/"+project,"sa","");
				System.out.println("-------- CONNECT WITH "+domain+" "+project+" DB ----------");;
				connMap.put(domain+"-"+project, conn);				
				cleanTable(domain,project);			
			}
		}
	}		
	/*
	public Connection getConn(String domain, String project)
	{
		return connMap.get(domain+"-"+project);
	}*/

	public DB(HashMap<String, String> domainMap, int i) throws Exception {
		Class.forName("org.h2.Driver");
		Iterator iter = domainMap.keySet().iterator();
		while(iter.hasNext()){
			String project = (String) iter.next();
			String domain = domainMap.get(project);
			Connection conn = DriverManager.getConnection("jdbc:h2:./DB/"+domain+"/"+project,"sa","");
			System.out.println("-------- CONNECT WITH "+domain+" "+project+" DB ----------");;
			connMap.put(domain+"-"+project, conn);
			cleanAttachTable(domain,project);		

			try
			{
				Statement q = connMap.get(domain+"-"+project).createStatement();
				q.execute("Create Table ATTACHMENT("
						+ "BUG_ID int,"
						+ "ATTACHER VARCHAR(128),"
						+ "DATE VARCHAR(128),"
						+ "ATTACH_ID int primary key,"
						+ "TYPE varchar(128));");
				
				System.out.println("---COMMENT TABLE ATTACHMENT CREATED...");
			}catch(Exception e)
			{
				System.err.println("---COMMENT TABLE ATTACHMENT CREATION ERROR...");
			}
			
		}
	}

	private void cleanAttachTable(String domain, String project) throws SQLException {
		try{
			Statement q = connMap.get(domain+"-"+project).createStatement();
			q.execute("DROP TABLE ATTACHMENT;");
			System.out.println("---DROP ATTACHMENT TABLE...");
		}catch(Exception e){
			System.err.println("ATTACHTMENT DROP ERROR");
		}
		
	}

	private void cleanTable(String domain, String project) throws SQLException {
		Statement q = connMap.get(domain+"-"+project).createStatement();
		q.execute("DELETE FROM BUG_REPORT;");
		System.out.println("---DELETE BUG_REPORT TABLE...");
		q.execute("DELETE FROM META_FIELD;");
		System.out.println("---DELETE META_FIELD TABLE...");
		q.execute("DELETE FROM  HISTORY;");
		System.out.println("---DELETE HISTORY TABLE...");
		q.execute("DELETE FROM COMMENT;");
		System.out.println("---DELETE COMMENT TABLE...");
		
	}

	private void createTable(String domain, String project) throws Exception
	{
		Statement q = connMap.get(domain+"-"+project).createStatement();
		try
		{
			q.execute("Create Table BUG_REPORT("
					+ "BUG_ID int PRIMARY KEY,"
					+ "SUMMARY VARCHAR(512),"
					+ "DESCRIPTION VARCHAR(99999));");
			
			System.out.println("--- BUG REPORT TABLE CREATED...");
		}catch(Exception e)
		{
			System.err.println("---BUG REPORT TABLE CREATION ERROR...");
		}
		try
		{
			q.execute("Create Table META_FIELD("
					+ "BUG_ID int PRIMARY KEY,"
					+ "STATUS VARCHAR(128),"
					+ "OPEN_DATE varchar(128),"			
					+ "MODIFIED_DATE varchar(128),"
					+ "BUG_REPORTER VARCHAR(255),"
					+ "DOMAIN varchar(64),"
					+ "PROJECT varchar(64),"
					+ "COMPONENT varchar(128),"
					+ "VERSION varchar(64),"
					+ "HW varchar(64),"
					+ "OS varchar(64),"						
					+ "PRIORITY VARCHAR(64),"
					+ "SEVERITY VARCHAR(64),"					
					+ "ASSIGNEE varchar(255));");
			
			System.out.println("---META_FIELD TABLE CREATED...");
		}catch(Exception e)
		{
			System.err.println("---META_FIELD TABLE CREATION ERROR...");
		}
		
		try
		{
			q.execute("Create Table HISTORY("
					+ "BUG_ID int,"
					+ "DATE varchar(128),"				
					+ "FIELD VARCHAR(128),"
					+ "PREV VARCHAR(128),"					
					+ "POST varchar(128));");
			
			System.out.println("---HISTORY TABLE CREATED...");
		}catch(Exception e)
		{
			System.err.println("---HISTORY TABLE CREATION ERROR...");
		}
		

		try
		{
			q.execute("Create Table COMMENT("
					+ "BUG_ID int,"
					+ "NUM int,"
					+ "REPORTER VARCHAR(255),"
					+ "DATE varchar(128),"
					+ "TEXT VARCHAR(99999));");
			
			System.out.println("---COMMENT TABLE COMMENT CREATED...");
		}catch(Exception e)
		{
			System.err.println("---COMMENT TABLE COMMENT CREATION ERROR...");
		}
		
		try
		{
			q.execute("Create Table ATTACHMENT("
					+ "BUG_ID int,"
					+ "ATTACHER VARCHAR(128),"
					+ "DATE VARCHAR(128),"
					+ "ATTACH_ID int primary key,"
					+ "TYPE varchar(128));");
			
			System.out.println("---COMMENT TABLE ATTACHMENT CREATED...");
		}catch(Exception e)
		{
			System.err.println("---COMMENT TABLE ATTACHMENT CREATION ERROR...");
		}
		
		
		
		
	}
	
	
	private void dropTable(String domain, String project) throws Exception
	{
		try{
			Statement q = connMap.get(domain+"-"+project).createStatement();
			q.execute("DROP TABLE BUG_REPORT;");
			System.out.println("---DROP BUG_REPORT TABLE...");
			q.execute("DROP TABLE META_FIELD;");
			System.out.println("---DROP META_FIELD TABLE...");
			q.execute("DROP TABLE  HISTORY;");
			System.out.println("---DROP HISTORY TABLE...");
			q.execute("DROP TABLE COMMENT;");
			System.out.println("---DROP COMMENT TABLE...");
			q.execute("DROP TABLE ATTACHMENT;");
			System.out.println("---DROP ATTACHMENT TABLE...");
		}catch(Exception e){
			System.err.println("DROP TABLE ERROR "+domain+"-"+project);
		}
		
	}
	

	public void insertBugReport(BugReport b, BugReportMetaField mf) throws Exception
	{
		String key = mf.getDomain()+"-"+mf.getProduct();
		ArrayList<Comment> cl = b.getCommentList();
		ArrayList<History> hl = b.getHistoryList();
		try
		{
			Statement q = connMap.get(key).createStatement();
			q.execute("INSERT INTO BUG_REPORT VALUES ("+ b.getBugID() + ",'"+b.getSummary().replace("'", "")+"','"+b.getDescription().replace("'", "")+"');");
			q.execute("INSERT INTO META_FIELD VALUES ("+ mf.getBugID()+",'"+mf.getStatus()+"','"+mf.getOpenDate()+"','"+mf.getModifiedDate()
				+"','"+mf.getReporter()+"','"+mf.getDomain()+"','"+mf.getProduct()+"','"+mf.getComponent()+"','"+mf.getProductVer()+"','"+mf.getHardware()
				+"','"+mf.getOs()+"','"+mf.getPriority()+"','"+mf.getSever()+"','"+mf.getAssignee()+"');");
			for(int i = 0 ; i<cl.size(); i++)
				q.execute("INSERT INTO COMMENT VALUES ("+b.getBugID()+","+cl.get(i).getNum()
						+",'"+cl.get(i).getCommenter()+"','"+cl.get(i).getDate()+"','"+cl.get(i).getDescription().replace("'", "")+"');");
			for(int i = 0 ; i<hl.size(); i++)
				q.execute("INSERT INTO HISTORY VALUES ("+b.getBugID()+",'"+hl.get(i).getDate()
						+"','"+hl.get(i).getField()+"','"+hl.get(i).getPrev()+"','"+hl.get(i).getPost()+"');");
			
		}
		catch(Exception e1)
		{
			errorList.add(b.getBugID()+" "+e1.getMessage());
			System.err.println(e1);
		}
	}	
	
	public void close(HashMap domainMap) throws SQLException{
		Iterator iter =  domainMap.keySet().iterator();
		while(iter.hasNext()){
			String key = (String)iter.next();
			String domain = (String) domainMap.get(key);
			connMap.get(domain+"-"+key).close();
			
		}
		System.out.println(errorList);
	}

	public void insertAttachment(Attachment att, String key) {
		try
		{
			Statement q = connMap.get(key.split("-",2)[1]).createStatement();
			System.out.println(q.execute("INSERT INTO ATTACHMENT VALUES ("+ att.getBugID() + ",'"+att.getAttacher().replace("'", "")+"','"+att.getDate()+"',"
				+att.getAttachID()+",'"+att.getType()+"');"));			
			
		}
		catch(Exception e1)
		{
			errorList.add(att.getBugID()+" "+e1.getMessage());
			System.err.println(e1);
		}
	}
	

	
		/*	
	public void insertInitBugReport(BugReport b, EvaluatedMetrics e) throws Exception
	{
		try
		{
		Statement q = conn.createStatement();
		q.execute("INSERT INTO Initial_BUG_REPORT VALUES ("+ b.getBugID() + ",'"+b.getReporter()+"','"+b.getProduct()+"','"+b.getComponent()+"','"+b.getProductVer()+"','"
					+b.getHardware()+"','"+b.getOpenDate()+"','"+b.getStatus()+"','"+b.getPriority()+"','"+b.getSever()+"','"+b.getSummary()+"','"+b.getDescription()+"',"
					+e.getItem()+","+e.getKeywordAction()+","+e.getKeywordResult()+","+e.getKeywordStep()+","+e.getKeywordBuild()+","+e.getKeywordUI()+","+e.getKeywordScore()+","
					+e.getCode()+","+e.getPatch()+","+e.getPatch()+","+e.getReadIncaid()+","+e.getReadARI()+","+e.getReadLiau()+","+e.getReadFlesh()+","+e.getReadFog()+","
					+e.getReadSmog()+",'"+b.getAssignee()+"');");
		}
		catch(Exception e1)
		{
			System.err.println(e1);
		}
	}	
	public void inserReporterTossing(int bugID, String firstAssignee, String tossedAssignee, String tossingDate) throws Exception
	{
		try
		{
		Statement q = conn.createStatement();
		q.execute("INSERT INTO REPORTER_TOSSING VALUES ("+ bugID+",'"+firstAssignee+"','"+tossedAssignee+"','"+tossingDate+"');");
		}
		catch(Exception e1)
		{
			System.err.println(e1);
		}
	}	

	public void exit() throws Exception
	{
		if(conn!=null)
		{
			conn.close();
			System.out.println("---CONNECTION CLOSED...");
		}
	}*/
}
