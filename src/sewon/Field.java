
package sewon;

public class Field {
		final String name;
	    int os;
	    int priority;
	    int hardware;
	    int version;
	    int component;
	    int severity;
	    int assignee;
	    int product;
	    int status;
	    int total;
	    
	    public Field (String name,int os, int priority, int hardware, int version, int coponent, int severity, int assignee, int product, int total){
		    this.name=name;
	    	this.os=os;
		    this.priority=priority;
		    this.hardware=hardware;
		    this.version=version;
		    this.component=component;
		    this.severity=severity;
		    this.assignee= assignee;
		    this.product=product;
		    this.status=status;
		    this.total=total;
	    }
	    
		static public class Refield {
			String name;
			int n0;
			int n1;
			int n2;
			int n3;
			int n4;
			int total;
			
			public Refield(String name, int n0, int n1, int n2, int n3, int n4,int total){
				this.name=name;
				this.n0=n0;
				this.n1=n1;
				this.n2=n2;
				this.n3=n3;
				this.n4=n4;
				this.total=total;
			}
		    public void updatezero(){
		    	n0=total-(n1+n2+n3+n4);
		    }
		}
	    public String fieldname(){
	           return name;
	    }

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
