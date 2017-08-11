package sewon;

import java.sql.ResultSet;
import java.sql.Statement;

public class AboutQ4 {
	void print_res(Statement q, String key, Field avg, Field min, Field max) throws Exception{
		System.out.println("================="+key+"Q4 success======================");
		try
		{
			ResultSet rs1 = q.executeQuery("SELECT field, avg(time), min(time),max(time) FROM (SELECT field, PARSEDATETIME(date,'yyyy-MM-dd hh:mm:ss')-PARSEDATETIME(open_date,'yyyy-MM-dd hh:mm:ss') as time FROM (SELECT h.bug_id, open_date, date, field FROM BEASS as h, META_FIELD as m WHERE h.bug_id=m.bug_id)  group by field, time) group by field");
			//ResultSet rs1 = q.executeQuery("SELECT field, avg(time), min(time),max(time) FROM (SELECT field, PARSEDATETIME(date,'yyyy-MM-dd hh:mm:ss')-PARSEDATETIME(open_date,'yyyy-MM-dd hh:mm:ss') as time FROM (SELECT h.bug_id, open_date, date, field FROM (SELECT * FROM BEASS as b1 where PARSEDATETIME(date,'yyyy-MM-dd hh:mm:ss')  < all (select PARSEDATETIME(b2.date,'yyyy-MM-dd hh:mm:ss') from beass as b2 where b2.post='assigned' and b1.bug_id=b2.bug_id) order by date asc) as h, META_FIELD as m WHERE h.bug_id=m.bug_id)  group by field, time) group by field");
			
			while(rs1.next()){
				String fieldname=rs1.getString("field");
				int average=rs1.getInt("avg(time)");
				int minimum=rs1.getInt("min(time)");
				int maximum=rs1.getInt("max(time)");
				BugDBConnection.find_field(fieldname,avg,average);
				BugDBConnection.find_field(fieldname,min,minimum);
				BugDBConnection.find_field(fieldname,max,maximum);
			}	
		}
		catch(Exception e1)
		{	
			e1.printStackTrace();
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("success Q4");
	}

}
