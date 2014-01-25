package com.congressionalphotodirectory.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;

import org.apache.commons.lang3.StringUtils;

import com.congressionalphotodirectory.data.Committee.Committees;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "legislator")
public class Legislator {

	//Fields
	private boolean in_office;
	private String party;
	private String gender;
	private String state;
	private String state_name;
	private String district;
	private String title;
	private String chamber;
	private String senate_class;
	private String state_rank;
	private String birthday;
	private String term_start;
	private String term_end;
	
	//Identifiers
	private String bioguide_id;
	private String thomas_id;
	private String govtrack_id;
	private String votesmart_id;
	private String crp_id;
	private String lis_id;
	private List<String> fec_ids;
	
	//Names
	private String first_name;
	private String nickname;
	private String last_name;
	private String middle_name;
	private String name_suffix;
	
	//Contact
	private String phone;
	private String website;
	private String office;
	private String contact_form;
	private String fax;
	
	//Social
	private String twitter_id;
	private String youtube_id;
	private String facebook_id;
	
	//Terms
	private List<Term> terms;
	
	//Custom fields
	private Committees committees;
	private String photoUrl;

	public boolean isIn_office() {
		return in_office;
	}

	public void setIn_office(boolean in_office) {
		this.in_office = in_office;
	}

	public String getParty() {
		return party;
	}

	public void setParty(String party) {
		this.party = party;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getState_name() {
		return state_name;
	}

	public void setState_name(String state_name) {
		this.state_name = state_name;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getChamber() {
		return chamber;
	}

	public void setChamber(String chamber) {
		this.chamber = chamber;
	}

	public String getSenate_class() {
		return senate_class;
	}

	public void setSenate_class(String senate_class) {
		this.senate_class = senate_class;
	}

	public String getState_rank() {
		return state_rank;
	}

	public void setState_rank(String state_rank) {
		this.state_rank = state_rank;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getTerm_start() {
		return term_start;
	}

	public void setTerm_start(String term_start) {
		this.term_start = term_start;
	}

	public String getTerm_end() {
		return term_end;
	}

	public void setTerm_end(String term_end) {
		this.term_end = term_end;
	}

	public String getBioguide_id() {
		return bioguide_id;
	}

	public void setBioguide_id(String bioguide_id) {
		this.bioguide_id = bioguide_id;
	}

	public String getThomas_id() {
		return thomas_id;
	}

	public void setThomas_id(String thomas_id) {
		this.thomas_id = thomas_id;
	}

	public String getGovtrack_id() {
		return govtrack_id;
	}

	public void setGovtrack_id(String govtrack_id) {
		this.govtrack_id = govtrack_id;
	}

	public String getVotesmart_id() {
		return votesmart_id;
	}

	public void setVotesmart_id(String votesmart_id) {
		this.votesmart_id = votesmart_id;
	}

	public String getCrp_id() {
		return crp_id;
	}

	public void setCrp_id(String crp_id) {
		this.crp_id = crp_id;
	}

	public String getLis_id() {
		return lis_id;
	}

	public void setLis_id(String lis_id) {
		this.lis_id = lis_id;
	}

	public List<String> getFec_ids() {
		return fec_ids;
	}

	public void setFec_ids(List<String> fec_ids) {
		this.fec_ids = fec_ids;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getNickname() {
		
		//Manually set some nicknames
		if ( StringUtils.isEmpty( nickname ) ) {
			if ( this.getFirst_name().equalsIgnoreCase( "Christopher" ) ) {
				nickname = "Chris";
			} else if ( this.getFirst_name().equalsIgnoreCase( "Bernard" ) ) {
				nickname = "Bernie";
			} else if ( this.getFirst_name().equalsIgnoreCase( "Charles" ) ) {
				nickname = "Chuck";
			} else if ( this.getFirst_name().equalsIgnoreCase( "Timothy" ) ) {
				nickname = "Tim";
			} else if ( this.getFirst_name().equalsIgnoreCase( "Michael" ) ) {
				nickname = "Mike";
			} else if ( this.getFirst_name().equalsIgnoreCase( "Thomas" ) ) {
				nickname = "Tom";
			} else if ( this.getFirst_name().equalsIgnoreCase( "Daniel" ) ) {
				nickname = "Dan";
			} else if ( this.getFirst_name().equalsIgnoreCase( "Benjamin" ) ) {
				nickname = "Ben";
			} else if ( this.getFirst_name().equalsIgnoreCase( "Matthew" ) ) {
				nickname = "Matt";
			} else if ( this.getFirst_name().equalsIgnoreCase( "Richard" ) ) {
				nickname = "Dick";
			} else {
				nickname = "";
			}
		}
		
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getMiddle_name() {
		return middle_name;
	}

	public void setMiddle_name(String middle_name) {
		this.middle_name = middle_name;
	}

	public String getName_suffix() {
		return name_suffix;
	}

	public void setName_suffix(String name_suffix) {
		this.name_suffix = name_suffix;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getOffice() {
		return office;
	}

	public void setOffice(String office) {
		this.office = office;
	}

	public String getContact_form() {
		return contact_form;
	}

	public void setContact_form(String contact_form) {
		this.contact_form = contact_form;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getTwitter_id() {
		return twitter_id;
	}

	public void setTwitter_id(String twitter_id) {
		this.twitter_id = twitter_id;
	}

	public String getYoutube_id() {
		return youtube_id;
	}

	public void setYoutube_id(String youtube_id) {
		this.youtube_id = youtube_id;
	}

	public String getFacebook_id() {
		return facebook_id;
	}

	public void setFacebook_id(String facebook_id) {
		this.facebook_id = facebook_id;
	}

	public List<Term> getTerms() {
		return terms;
	}

	public void setTerms(List<Term> terms) {
		this.terms = terms;
	}
	
	public boolean isSenator() {
		return this.getChamber().equals("senate");
	}
	
	public String getClassDistrict() {
		if ( this.isSenator() )
			return this.getSenate_class();
		else
			return this.getDistrict();
	}
	
	public String getImgFileName() {
		String fileName = this.getChamber() + "_" + this.getState() + "_" + this.getClassDistrict() + "_" + this.getParty();
		fileName = fileName.replace(" ", "_");
		fileName = fileName + ".jpg";
		return fileName;
	}
	
	public Committees getCommittees() {
		return committees;
	}

	public void setCommittees(Committees committees) {
		this.committees = committees;
	}
	
	public void addCommittee( Committee committee ) {
		if ( committees == null ) {
			committees = new Committees();
		}
		committees.addCommittee(committee);
	}
	
	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "legislators")
	/**
	 * Convenience class for JAXB
	 *
	 */
	public static class Legislators {
	 
	    @XmlElement(name = "legislator", type = Legislator.class)
	    private List<Legislator> legislators;
	 
	    public Legislators() {
	    	legislators = new ArrayList<Legislator>();
	    }
	 
	    public Legislators(List<Legislator> legislators) {
	        this.legislators = legislators;
	    }
	 
	    public List<Legislator> getLegislators() {
	        return legislators;
	    }
	 
	    public void setLegislators(List<Legislator> legislators) {
	        this.legislators = legislators;
	    }
	    
	    public void addLegislator( Legislator legislator ) {
	    	this.legislators.add(legislator);
	    }
	}
}
