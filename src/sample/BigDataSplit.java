package sample;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BigDataSplit {
	
	static String[] data = {"eclipse-platform", "eclipse-jdt","birt-birt"};
	
	public static void main(String[] args){		
		 
		for(int i = 0 ; i<data.length; i++){
			try {
				Class.forName("org.h2.Driver");

				Connection conn = DriverManager.getConnection("jdbc:h2:./DB/"+data[i].split("-")[0]+"/"+data[i].split("-")[1],"sa","");
				
				
				Statement q = conn.createStatement();
				System.out.println(q.execute("INSERT INTO BEASS(BUG_ID,DATE,FIELD,PREV,POST) SELECT BUG_ID,DATE,FIELD,PREV,POST FROM history as h1 "
						+ "WHERE  PARSEDATETIME(date,'yyyy-MM-dd hh:mm:ss')  < all (select PARSEDATETIME(h2.date,'yyyy-MM-dd hh:mm:ss') from history "
						+ "as h2 where h2.field='assignee' and h1.bug_id=h2.bug_id) order by date asc"));
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
							
		
		}
	}

}
