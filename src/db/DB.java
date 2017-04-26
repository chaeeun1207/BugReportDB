package db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import common.BugReport;
import common.EvaluatedMetrics;
import common.ForXML;
import common.Property;

public class DB {
	private Connection conn = null;
	
	DB() throws Exception
	{
		Class.forName("org.h2.Driver");
		conn = DriverManager.getConnection("jdbc:h2:./DB/"+Property.getInstance().getTargetResolution()+"/"+A_Main.project,"sa","");
		System.out.println("-------- CONNECT WITH "+Property.getInstance().getTargetResolution()+" "+A_Main.project+" DB ----------");;
		
		if(conn!=null) createTable();
	}		
	
	public Connection getConn()
	{
		return conn;
	}

	private void createTable() throws Exception
	{
		Statement q = conn.createStatement();
		try
		{
			q.execute("Create Table Initial_BUG_REPORT("
					+ "BUG_ID int PRIMARY KEY,"
					+ "BUG_REPORTER VARCHAR(255),"
					+ "PRD_NAME varchar(255),"
					+ "COMP_NAME varchar(255),"
					+ "PRD_VER varchar(50),"
					+ "BUG_HW varchar(128),"					
					+ "BUG_OPEN_DATE DATETIME,"
					+ "BUG_STATUS VARCHAR(255),"
					+ "BUG_PRIOR VARCHAR(128),"
					+ "BUG_SEVER VARCHAR(255),"
					+ "BUG_SUM VARCHAR(255),"
					+ "BUG_DES VARCHAR(99999),"
					
					+ "EVAL_ITEM INT,"
					+ "EVAL_KEYWORD_ACTION INT,"
					+ "EVAL_KEYWORD_RESULT INT,"
					+ "EVAL_KEYWORD_STEP INT,"
					+ "EVAL_KEYWORD_BUILD INT,"
					+ "EVAL_KEYWORD_UI INT,"
					+ "EVAL_KEYWORD_SCORE DOUBLE,"
					+ "EVAL_CODE INT,"
					+ "EVAL_PATCH INT,"
					+ "EVAL_STRACT INT,"
					
					+ "EVAL_READ_INCAID DOUBLE,"
					+ "EVAL_READ_ARI DOUBLE,"
					+ "EVAL_READ_LIAU DOUBLE,"
					+ "EVAL_READ_FLESH DOUBLE,"
					+ "EVAL_READ_FOG DOUBLE,"
					+ "EVAL_READ_SMOG DOUBLE,"
					
					+ "BUG_ASSIGNEE varchar(255));");
			
			System.out.println("---Initial BUG REPORT TABLE CREATED...");
		}catch(Exception e)
		{
			System.out.println("---Initial BUG REPORT TABLE CREATION ERROR...");
		}
		
		try
		{
			q.execute("Create Table REPORTER_TOSSING("
					+ "BUG_ID INT,"
					+ "FIRST_ASSIGNEE VARCHAR(255),"
					+ "TOSSED_ASSIGNEE VARCHAR(255),"
					+ "TOSSING_DATE varchar(255));");
			
			System.out.println("---Initial REPORTER_TOSSING TABLE CREATED...");
		}catch(Exception e)
		{
			System.out.println("---Initial REPORTER_TOSSING TABLE CREATION ERROR...");
		}
	}
	
	public void dropTable() throws Exception
	{
		Statement q = conn.createStatement();
		q.execute("DELETE FROM INITIAL_BUG_REPORT;");
		System.out.println("---DELETE INITIAL BUG REPORT TABLE...");
		q.execute("DELETE FROM REPORTER_TOSSING;");
		System.out.println("---DELETE INITIAL REPORTER_TOSSING TABLE...");
	}
			
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
	}
}
