package common;

import java.util.ArrayList;

public class BugReportMetaField {
	
	int bugID;
	String reporter;
	String domain;
	String product;
	String component;
	String productVer;
	String hardware;
	String openDate;
	String modifiedDate;
	String status;
	String priority;
	String sever;
	String assignee;
	String finalAssignee;
	String qa;
	String mileStone;
	String whiteboard;
	String os;
	ArrayList<String> ccList = new ArrayList<String>();
	


	public void setBugReportMetaField(int bugID, String reporter, String domain, String product, String component,
			String productVer, String hardware, String openDate, String modifiedDate, String status, String priority,
			String sever, String assignee, String os, String finalAssignee) {
		this.bugID = bugID;
		this.reporter = reporter;
		this.domain = domain;
		this.product = product;
		this.component = component;
		this.productVer = productVer;
		this.hardware = hardware;
		this.openDate = openDate;
		this.modifiedDate = modifiedDate;
		this.status = status;
		this.priority = priority;
		this.sever = sever;
		this.assignee = assignee;
		this.os = os;
		this.finalAssignee = finalAssignee;
	}
	public String getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public String getMileStone() {
		return mileStone;
	}
	public void setMileStone(String mileStone) {
		this.mileStone = mileStone;
	}

	public String getWhiteboard() {
		return whiteboard;
	}
	public void setWhiteboard(String whiteboard) {
		this.whiteboard = whiteboard;
	}
	public String getOs() {
		return os;
	}
	public void setOs(String os) {
		this.os = os;
	}
	public void addCC(String cc){
		ccList.add(cc);
	}
	public ArrayList<String> getCcList() {
		return ccList;
	}
	public void setCcList(ArrayList<String> ccList) {
		this.ccList = ccList;
	}
	public String getQa() {
		return qa;
	}
	public void setQa(String qa) {
		this.qa = qa;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getAssignee() {
		return assignee;
	}
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public int getBugID() {
		return bugID;
	}
	public void setBugID(int bugID) {
		this.bugID = bugID;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public String getComponent() {
		return component;
	}
	public void setComponent(String component) {
		this.component = component;
	}
	public String getProductVer() {
		return productVer;
	}
	public void setProductVer(String productVer) {
		this.productVer = productVer;
	}
	public String getReporter() {
		return reporter;
	}
	public void setReporter(String reporter) {
		this.reporter = reporter;
	}
	public String getOpenDate() {
		return openDate;
	}
	public void setOpenDate(String openDate) {
		this.openDate = openDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSever() {
		return sever;
	}
	public void setSever(String sever) {
		this.sever = sever;
	}

	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getHardware() {
		return hardware;
	}
	public void setHardware(String hardware) {
		this.hardware = hardware;
	}
	public String getFinalAssignee() {
		return finalAssignee;
	}
	public void setFinalAssignee(String finalAssignee) {
		this.finalAssignee = finalAssignee;
	}
	@Override
	public String toString() {
		return "BugReportMetaField [bugID=" + bugID + ", reporter=" + reporter + ", domain=" + domain + ", product="
				+ product + ", component=" + component + ", productVer=" + productVer + ", hardware=" + hardware
				+ ", openDate=" + openDate + ", modifiedDate=" + modifiedDate + ", status=" + status + ", priority="
				+ priority + ", sever=" + sever + ", assignee=" + assignee + ", qa=" + qa + ", mileStone=" + mileStone
				+ ", whiteboard=" + whiteboard + ", ccList=" + ccList + ", os=" + os + ", getModifiedDate()="
				+ getModifiedDate() + ", getMileStone()=" + getMileStone() + ", getWhiteboard()=" + getWhiteboard()
				+ ", getOs()=" + getOs() + ", getCcList()=" + getCcList() + ", getQa()=" + getQa() + ", getDomain()="
				+ getDomain() + ", getAssignee()=" + getAssignee() + ", getBugID()=" + getBugID() + ", getProduct()="
				+ getProduct() + ", getComponent()=" + getComponent() + ", getProductVer()=" + getProductVer()
				+ ", getReporter()=" + getReporter() + ", getOpenDate()=" + getOpenDate() + ", getStatus()="
				+ getStatus() + ", getSever()=" + getSever() + ", getPriority()=" + getPriority() + ", getHardware()="
				+ getHardware() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
	
	
}
