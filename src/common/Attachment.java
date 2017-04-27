package common;

public class Attachment {
	int bugID;
	String attacher;
	public String getAttacher() {
		return attacher;
	}
	public void setAttacher(String attacher) {
		this.attacher = attacher;
	}
	String date;
	int attachID;
	String type;
	public int getBugID() {
		return bugID;
	}
	public void setBugID(int bugID) {
		this.bugID = bugID;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getAttachID() {
		return attachID;
	}
	public void setAttachID(int attachID) {
		this.attachID = attachID;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "Attachment [bugID=" + bugID + ", date=" + date + ", attachID=" + attachID + ", type=" + type + "]";
	}
	
	

}
