package common;

public class History {

	int bugID;
	String date;	
	String field;
	String prev;
	String post;
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getBugID() {
		return bugID;
	}
	public void setBugID(int bugID) {
		this.bugID = bugID;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getPrev() {
		return prev;
	}
	public void setPrev(String prev) {
		this.prev = prev;
	}
	public String getPost() {
		return post;
	}
	public void setPost(String post) {
		this.post = post;
	}
	@Override
	public String toString() {
		return "History [bugID=" + bugID + ", date=" + date + ", field=" + field + ", prev=" + prev + ", post=" + post
				+ "]";
	}
	
	
	
	
}
