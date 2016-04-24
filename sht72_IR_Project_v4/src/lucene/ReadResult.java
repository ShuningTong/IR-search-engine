package lucene;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import lucene.Path;

/**
 * This class reads from result of CSVCollection.java, and prepare next document as a MyDocumentForLucene instance.
 * @author Shuning Tong
 */
public class ReadResult {
	// pre-defined skill set
	private String[] skillSet = {"java", "python", "c", "c++", "c#", "sql", "php", "html", "css", "javascript"};
	private FileInputStream fis;
	private BufferedReader br;
	// the error counter for experience requirements not being one of "not specified", "more than" or "between"
	private int experienceNumError;
	// the error counter for address not being in the format like "Philadelphia, PA"
	private int addressFormatError;
	// the error counter for post date not being in the format like "3/15/16"
	private int postDateFormatError;
	// the error counter for company name being "null"
	private int companyNameNullError;
	
	/**
	 * Constructor, prepare to read from result of CSVCollection.java.
	 */
	public ReadResult(){
		try {
			fis = new FileInputStream(Path.ResultOfCSVCollection);
	        br = new BufferedReader(new InputStreamReader(fis));   
	        experienceNumError = 0;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @return next document as a MyDocumentForLucene instance
	 * @throws IOException
	 */
	public MyDocumentForLucene nextDocument(){
		String jobname = "";
		String url = "";
		String companyNameCrude = "";
		String addressSpecific = "";
		String postDateNoFormat = "";
		String content = "";
		
		try {
			jobname = br.readLine();
			// when no more documents, close file reader
			if(jobname == null) {
				br.close();
				fis.close();
				return null;
			}
			
			url =br.readLine();
			companyNameCrude = br.readLine();
			addressSpecific = br.readLine();
			postDateNoFormat = br.readLine();
			content = br.readLine();
			br.readLine();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String companyName = "";
		if(!companyNameCrude.equalsIgnoreCase("null")){
			/*String[] companyNameArray = companyNameCrude.split("\\W+");
			StringBuilder companyNameBuilder = new StringBuilder();
			for (String companyNameWord: companyNameArray){
				companyNameBuilder.append(companyNameWord + " ");
			}
			companyName = companyNameBuilder.toString();*/
			companyName = companyNameCrude;
		}else{
			companyName = "Unknown";
			companyNameNullError++;
		}
		
		String address = "";
		// most of address format is like "Philadelphia, PA"
		// so we split address using "," to get the state abbreviations
		String[] addressArray = addressSpecific.split(",");
		if(addressArray.length == 2){
			address = addressArray[1].trim().toUpperCase();
		}else {
			addressFormatError++;
		}

		String month = null;
		String day = null;
		String year = null;
		// most of post date format is like "3/15/16"
		// so we split post date using this regex to get month, day and year
		String[] postDateArray = postDateNoFormat.split("\\W+");
		if(postDateArray.length == 3){
			if(isNumeric(postDateArray[0]) && isNumeric(postDateArray[1]) && isNumeric(postDateArray[2])){
				month = String.format("%02d", Integer.parseInt(postDateArray[0]));
				day = String.format("%02d", Integer.parseInt(postDateArray[1]));
				year = String.format("%02d", Integer.parseInt(postDateArray[2]));
			}else{
				postDateFormatError++;
			}
		}else {
			postDateFormatError++;
		}
		int postDate = 0;
		String postDateToStore = postDateNoFormat;
		// we convert 3/15/16 to 160315, so that we can sort by post date
		if(month != null && day != null && year != null){
			postDate = Integer.parseInt(year + month + day);
		}
		
		// check each word in content, if it is a skill word, add it to skillBuilder
		// in the process of checking skill word, we also check the start of job snapshot
		String skill = "";
		StringBuilder skillBuilder = new StringBuilder();
		boolean[] skillSetBoolean = new boolean[skillSet.length];
		int snapshotIndex = 0;
		String[] contentArray = content.split("[^a-z0-9A-Z_#+]+");
		// rewrite this paragraph, iterate skillSet, once any of them appear in content, we add that into skillArray, 
		// and do not compare it anymore.
		for (int i = 0; i < contentArray.length; i++){
			for (int j = 0; j < skillSet.length; j++){
				if (!skillSetBoolean[j] && contentArray[i].equalsIgnoreCase(skillSet[j])){
					skillSetBoolean[j] = true;
					skillBuilder.append(skillSet[j] + " ");
				}		
			}
			if(contentArray[i].equalsIgnoreCase("Snapshot")){
				snapshotIndex = i;
			}
		}
		skill = skillBuilder.toString();
		//System.out.println(skill);

		LinkedList<String> experienceList = new LinkedList<>();
		LinkedList<Integer> experienceNum = new LinkedList<>();
		LinkedList<Integer> experience = new LinkedList<>();
		boolean startExperience= false;
		// we start from snapshotIndex to search for "Experience", 
		// once we hit that word, we start adding words into experienceList, at most 4 in total.
		for (int i = snapshotIndex + 1; i < contentArray.length; i++ ){
			if (startExperience){
				experienceList.add(contentArray[i]);
			}
			if(experienceList.size() == 4){
				break;
			}
			if (contentArray[i].equalsIgnoreCase("Experience")){
				startExperience = true;
			}
		}
		// we iterate through experienceList to search for numbers
		for (int i = 0; i < experienceList.size(); i++){
			if(isNumeric(experienceList.get(i))){
				int num = Integer.parseInt(experienceList.get(i));
				experienceNum.add(num);
			}
		}
		
		// if there is no number, it's "not specified"
		// we choose 15 as the highest possible experience requirements
		if(experienceNum.size() == 0){
			for(int i = 0; i < 15; i++){
				experience.add(i);
			}
		// if there is one number, it's "more than" centain years
		}else if(experienceNum.size() == 1){
			int start = experienceNum.get(0);
			for (int i = start; i < 15; i++){
				experience.add(i);
			}
		// if there is two numbers, it's "between x and y years"
		}else if(experienceNum.size() == 2){
			int start = experienceNum.get(0);
			int end = experienceNum.get(1);
			for (int i = start; i <= end; i++){
				experience.add(i);
			}
		}else{
			experienceNumError++;
		}

		MyDocumentForLucene doc = new MyDocumentForLucene(jobname, url, companyName, postDate, postDateToStore, address, skill, experience, content);
		return doc;
	}
	
	public static boolean isNumeric(String str){
		try{
	      int i = Integer.parseInt(str);
	    }catch(NumberFormatException nfe){
	      return false;
	    }
	    return true;
	}
	
	public void closeReader(){
		try {
			this.br.close();
			this.fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		try {
			FileWriter wr = new FileWriter(Path.ResultForLucene);
			ReadResult rr = new ReadResult();
			MyDocumentForLucene mdfl = rr.nextDocument();
			while(mdfl != null){
				wr.append(mdfl.toString() + "\n");
				mdfl = rr.nextDocument();
			}
			System.out.println(rr.experienceNumError);
			System.out.println(rr.addressFormatError);
			System.out.println(rr.postDateFormatError);
			// 99 documents have a company name "null"
			System.out.println(rr.companyNameNullError);

			rr.closeReader();
			wr.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
