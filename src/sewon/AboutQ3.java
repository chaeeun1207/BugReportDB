
package sewon;

import java.sql.ResultSet;
import java.sql.Statement;

public class AboutQ3 {

	void print_res(Statement q, String key, Field f1, Field f2, Field f3, Field f4 )throws Exception{
		System.out.println("================="+key+"Q3 success======================");
		try
		{
			ResultSet rs1 = q.executeQuery("SELECT field, recount, count(recount) FROM (SELECT field, case when re_count<4 then re_count else '4' end as recount FROM (SELECT bug_id, field, count(field) as re_count FROM BEASS group by bug_id, field)) group by field,recount order by field,recount");
			//ResultSet rs1 = q.executeQuery("SELECT field, recount, count(recount) FROM (SELECT field, case when re_count<4 then re_count else '4' end as recount FROM (SELECT bug_id, field, count(field) as re_count FROM (SELECT * FROM BEASS as b1 where PARSEDATETIME(date,'yyyy-MM-dd hh:mm:ss')  < all (select PARSEDATETIME(b2.date,'yyyy-MM-dd hh:mm:ss') from beass as b2 where b2.post='assigned' and b1.bug_id=b2.bug_id) order by date asc) group by bug_id, field)) group by field,recount order by field,recount");
			
			while(rs1.next()){
				String fieldname=rs1.getString("field");
				String recount=rs1.getString("recount");
				int count=rs1.getInt("count(recount)");
				switch(recount){
					case "1": BugDBConnection.find_field(fieldname,f1,count); break;
					case "2": BugDBConnection.find_field(fieldname,f2,count); break;
					case "3": BugDBConnection.find_field(fieldname,f3,count); break;
					case "4": BugDBConnection.find_field(fieldname,f4,count); break;
				}
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
		System.out.println("success Q3");
	}

}
