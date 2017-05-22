package common;

public class CUEZILLA {

	String domain;
	String project;
	int bugID;
	int itemization;
	int actionKeyword;
	int resultKeyword;
	int stepKeyword;
	int buildKeyword;
	int uiKeyword;
	double keywordScore;
	int codeExample;
	int patch;
	int stackTrace;
	int screenShot;
	public int getBugID() {
		return bugID;
	}
	public void setBugID(int bugID) {
		this.bugID = bugID;
	}
	public int getItemization() {
		return itemization;
	}
	public void setItemization(int itemization) {
		this.itemization = itemization;
	}
	public int getActionKeyword() {
		return actionKeyword;
	}
	public void setActionKeyword(int actionKeyword) {
		this.actionKeyword = actionKeyword;
	}
	public int getResultKeyword() {
		return resultKeyword;
	}
	public void setResultKeyword(int resultKeyword) {
		this.resultKeyword = resultKeyword;
	}
	public int getStepKeyword() {
		return stepKeyword;
	}
	public void setStepKeyword(int stepKeyword) {
		this.stepKeyword = stepKeyword;
	}
	public int getBuildKeyword() {
		return buildKeyword;
	}
	public void setBuildKeyword(int buildKeyword) {
		this.buildKeyword = buildKeyword;
	}
	public int getUiKeyword() {
		return uiKeyword;
	}
	public void setUiKeyword(int uiKeyword) {
		this.uiKeyword = uiKeyword;
	}
	public double getKeywordScore() {
		return keywordScore;
	}
	public void setKeywordScore(double keywordScore) {
		this.keywordScore = keywordScore;
	}
	public int getCodeExample() {
		return codeExample;
	}
	public void setCodeExample(int codeExample) {
		this.codeExample = codeExample;
	}
	public int getPatch() {
		return patch;
	}
	public void setPatch(int patch) {
		this.patch = patch;
	}
	public int getStackTrace() {
		return stackTrace;
	}
	public void setStackTrace(int stackTrace) {
		this.stackTrace = stackTrace;
	}
	public int getScreenShot() {
		return screenShot;
	}
	public void setScreenShot(int screenShot) {
		this.screenShot = screenShot;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	@Override
	public String toString() {
		return "CUEZILLA [bugID=" + bugID + ", itemization=" + itemization + ", actionKeyword=" + actionKeyword
				+ ", resultKeyword=" + resultKeyword + ", stepKeyword=" + stepKeyword + ", buildKeyword=" + buildKeyword
				+ ", uiKeyword=" + uiKeyword + ", keywordScore=" + keywordScore + ", codeExample=" + codeExample
				+ ", patch=" + patch + ", stackTrace=" + stackTrace + ", screenShot=" + screenShot + "]";
	}
	
	
	
	
	

}
