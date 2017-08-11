package sewon;

import java.sql.ResultSet;
import java.sql.Statement;


public class AboutQ1 {

	void print_res(Statement q, String key, Field field ) throws Exception{
	
		String fieldname;
		int count;
		
		System.out.println("================="+key+"Q1 success======================");
		/*
		try
		{
				ResultSet rs1 = q.executeQuery("SELECT field, count(distinct bug_id) as count FROM HISTORY WHERE field !='status' and field !='resolution' group by field ");			
				while(rs1.next()){
					fieldname=rs1.getString("field");
					count=rs1.getInt("count");
					BugDBConnection.find_field(fieldname,field,count);
				}	
				ResultSet rs2 = q.executeQuery("SELECT field, count(distinct bug_id) as count FROM HISTORY WHERE field='status' and prev!='new' and post!='resolved' and post!='closed'");			
				while(rs2.next()){
					count=rs2.getInt("count");
					field.status=count;
				}				
		} 논문따라잡기*/
		try
		{
				ResultSet rs1 = q.executeQuery("SELECT field, count(distinct bug_id) as count FROM BEASS WHERE field !='status' and field !='resolution' group by field");			
				//ResultSet rs1 = q.executeQuery("SELECT field, count(distinct bug_id) as count FROM (SELECT * FROM BEASS as b1 where PARSEDATETIME(date,'yyyy-MM-dd hh:mm:ss')  < all (select PARSEDATETIME(b2.date,'yyyy-MM-dd hh:mm:ss') from beass as b2 where b2.post='assigned' and b1.bug_id=b2.bug_id) order by date asc) WHERE field !='status' and field !='resolution' group by field");			
				while(rs1.next()){
					fieldname=rs1.getString("field");
					count=rs1.getInt("count");
					BugDBConnection.find_field(fieldname,field,count);
				}	
		}
		catch(Exception e1)
		{	
			//System.out.println("Error!");
			e1.printStackTrace();
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("success Q1");
	}

}
