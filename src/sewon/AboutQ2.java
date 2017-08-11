package sewon;

import java.sql.ResultSet;
import java.sql.Statement;

public class AboutQ2 {

	void print_res(Statement q, String key, Field.Refield field ) throws Exception{
		System.out.println("================="+key+"Q2 success======================");
		try
		{
			ResultSet rs1 = q.executeQuery("SELECT refieldnum, count(bug_id) as count FROM ( SELECT bug_id, case when fieldnum<4 then fieldnum else '4' end as refieldnum  FROM (SELECT bug_id, count(distinct field) as fieldnum FROM BEASS WHERE field !='resolution' AND prev!='new' AND post!='resolved' AND post!='closed' group by bug_id)) group by refieldnum");			
			//ResultSet rs1 = q.executeQuery("SELECT refieldnum, count(bug_id) as count FROM ( SELECT bug_id, case when fieldnum<4 then fieldnum else '4' end as refieldnum  FROM (SELECT bug_id, count(distinct field) as fieldnum FROM (SELECT * FROM BEASS as b1 where PARSEDATETIME(date,'yyyy-MM-dd hh:mm:ss')  < all (select PARSEDATETIME(b2.date,'yyyy-MM-dd hh:mm:ss') from beass as b2 where b2.post='assigned' and b1.bug_id=b2.bug_id) order by date asc) WHERE field !='resolution' AND prev!='new' AND post!='resolved' AND post!='closed' group by bug_id)) group by refieldnum");			
			
			while(rs1.next()){
				String refieldnum=rs1.getString("refieldnum");
				int count=rs1.getInt("count");
				switch(refieldnum){
					case "1":
						field.n1=count; break;
					case "2":
						field.n2=count; break;
					case "3":
						field.n3=count; break;
					case "4":
						field.n4=count; break;
				}
			}
			field.updatezero();
		}
		catch(Exception e1)
		{	
			//System.out.println("Error!");
			e1.printStackTrace();
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("success Q2");

	}

}
