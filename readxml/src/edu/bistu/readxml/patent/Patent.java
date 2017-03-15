package edu.bistu.readxml.patent;


public class Patent {
	
	String title;
	String kind;
	String pubNum;
	String absContent;
	String claimText;
	
	public Patent(){
		title = "";
		kind = "";
		pubNum = "";
		absContent = "";
		claimText = "";
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}
	public String getPubNum() {
		return pubNum;
	}
	public void setPubNum(String pubNum) {
		this.pubNum = pubNum;
	}
	public String getAbsContent() {
		return absContent;
	}
	public void setAbsContent(String absContent) {
		this.absContent = absContent;
	}
	public String getClaimText() {
		return claimText;
	}
	public void setClaimText(String claimText) {
		this.claimText = claimText;
	}
	
	public void appendKind(String kind){
		this.kind += kind;
	}
	
	public void appendPubNum(String pubNum){
		this.pubNum += pubNum;
	}
	
	public void appendClaimText(String claimText){
		this.claimText += claimText;
	}
}
