package lucene;

import java.util.LinkedList;

/**
 *  INFSCI 2140: Information Storage and Retrieval Spring 2016
 */
public class MyDocumentForLucene{
	private String jobname;
	private String url;
	private String companyName;
	private int postDate;
	private String address;
	private String skill;
	private LinkedList<Integer> experience;
	private String content;

	
	public MyDocumentForLucene(String jobname, String url, String companyName, int postDate, String address, String skill, LinkedList<Integer> experience, String content){
		this.jobname = jobname;
		this.url = url;
		this.companyName = companyName;
		this.postDate = postDate;
		this.address = address;
		this.skill = skill;
		this.experience = experience;
		this.content = content;
	}


	public String getJobname() {
		return jobname;
	}


	public String getUrl() {
		return url;
	}


	public String getCompanyName() {
		return companyName;
	}


	public int getPostDate() {
		return postDate;
	}


	public String getAddress() {
		return address;
	}


	public String getSkill() {
		return skill;
	}


	public LinkedList<Integer> getExperience() {
		return experience;
	}


	public String getContent() {
		return content;
	}
	
	@Override
	public String toString(){
		String s = this.jobname + "\n" 
	+ this.url + "\n" 
				+ this.companyName + "\n" 
	+ this.postDate + "\n" 
				+ this.address + "\n" 
	+ this.skill + "\n" 
				+ this.experience + "\n" 
	+ this.content + "\n";
		return s;
	}
	
	
	
}
