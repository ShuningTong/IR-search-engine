package lucene;

import java.util.LinkedList;

public class BuildIndex {
	
	public static void main(String[] args) throws Exception{
		long startTime=System.currentTimeMillis();
		WriteIndex();
		long endTime=System.currentTimeMillis();
		System.out.println("index construction running time: "+(endTime-startTime)/60000.0+" min"); 
		
	}

	public static void WriteIndex() throws Exception {
		ReadResult corpus = new ReadResult();
		MyIndexWriter output=new MyIndexWriter();
		MyDocumentForLucene doc = null;
		int count=0;
		while ((doc = corpus.nextDocument()) != null) {
			String jobname = doc.getJobname();
			String url = doc.getUrl();
			String companyName = doc.getCompanyName();
			int postDate = doc.getPostDate();
			String address = doc.getAddress();
			String skill = doc.getSkill();
			LinkedList<Integer> experience = doc.getExperience();
			String content = doc.getContent();
			output.index(jobname, url, companyName, postDate, address, skill, experience, content); 
			count++;
			if(count%10==0)
				System.out.println("finish "+count+" docs");
		}
		System.out.println("totaly document count:  "+count);
		output.close();
	}

}
