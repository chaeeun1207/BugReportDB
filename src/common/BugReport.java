package common;

import java.util.ArrayList;

public class BugReport {

	int bugID;
	String summary;
	String description;
	ArrayList<Comment> commentList = new ArrayList<Comment>();
	ArrayList<History> historyList = new ArrayList<History>();
	

	public void addHistory (History history){
		historyList.add(history);
	}
	public ArrayList<History> getHistoryList() {
		return historyList;
	}
	public void setHistoryList(ArrayList<History> historyList) {
		this.historyList = historyList;
	}
	public int getBugID() {
		return bugID;
	}
	public void setBugID(int bugID) {
		this.bugID = bugID;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String descrliption) {
		String desc = descrliption.replace("'", ".");
		if(desc.length()>99999)
			desc = desc.substring(0,99999);
		this.description = desc;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary.replace("'", ".");;
	}
	public void addComment (Comment comment){
		commentList.add(comment);
	}
	public ArrayList<Comment> getCommentList() {
		return commentList;
	}
	public void setCommentList(ArrayList<Comment> commentList) {
		this.commentList = commentList;
	}
	@Override
	public String toString() {
		return "BugReport [ summary=" + summary + ", description="+ description +" " +commentList.size() + "]";
	}
	
	
}
