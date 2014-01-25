package com.congressionalphotodirectory.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "committeemember")
public class CommitteeMember {

	private String side;
	private int rank;
	private String title;
	private Legislator legislator;
	
	public String getSide() {
		return side;
	}
	public void setSide(String side) {
		this.side = side;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Legislator getLegislator() {
		return legislator;
	}
	public void setLegislator(Legislator legislator) {
		this.legislator = legislator;
	}
	
}
