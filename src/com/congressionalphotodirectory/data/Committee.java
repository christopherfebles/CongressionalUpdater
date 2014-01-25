package com.congressionalphotodirectory.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "committee")
public class Committee {
	
	private String name;
	private String committee_id;
	private String chamber;
	private String url;
	private String office;
	private String phone;
	private boolean subcommittee;
	private String parent_committee_id;
	
	private	Committee parent_committee;
	private List<Committee> subcommittees;
	private List<CommitteeMember> members;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCommittee_id() {
		return committee_id;
	}
	public void setCommittee_id(String committee_id) {
		this.committee_id = committee_id;
	}
	public String getChamber() {
		return chamber;
	}
	public void setChamber(String chamber) {
		this.chamber = chamber;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getOffice() {
		return office;
	}
	public void setOffice(String office) {
		this.office = office;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public boolean isSubcommittee() {
		return subcommittee;
	}
	public void setSubcommittee(boolean subcommittee) {
		this.subcommittee = subcommittee;
	}
	public String getParent_committee_id() {
		return parent_committee_id;
	}
	public void setParent_committee_id(String parent_committee_id) {
		this.parent_committee_id = parent_committee_id;
	}
	public Committee getParent_committee() {
		return parent_committee;
	}
	public void setParent_committee(Committee parent_committee) {
		this.parent_committee = parent_committee;
	}
	public List<Committee> getSubcommittees() {
		return subcommittees;
	}
	public void setSubcommittees(List<Committee> subcommittees) {
		this.subcommittees = subcommittees;
	}
	public List<CommitteeMember> getMembers() {
		return members;
	}
	public void setMembers(List<CommitteeMember> members) {
		this.members = members;
	}
	
	@Override
	public boolean equals( Object o ) {
		Committee other = (Committee)o;
		return this.getCommittee_id().equalsIgnoreCase(other.getCommittee_id());
	}
	
	@Override
	public int hashCode() {
		return this.getCommittee_id().hashCode();
	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "committees")
	/**
	 * Convenience class for JAXB
	 *
	 */
	public static class Committees {
	 
	    @XmlElement(name = "committee", type = Committee.class)
	    private List<Committee> committees;
	 
	    public Committees() {
	    	committees = new ArrayList<Committee>();
	    }
	 
	    public Committees(List<Committee> committees) {
	        this.committees = committees;
	    }
	 
	    public List<Committee> getCommittees() {
	        return committees;
	    }
	 
	    public void setCommittees(List<Committee> committees) {
	        this.committees = committees;
	    }
	    
	    public void addCommittee( Committee commitee ) {
	    	committees.add(commitee);
	    }
	}
	
}
