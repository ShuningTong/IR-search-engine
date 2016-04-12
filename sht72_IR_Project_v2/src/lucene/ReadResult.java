package lucene;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import lucene.Path;

public class ReadResult {
	private String[] skillSet = {"java", "python", "c", "sql", "html", "php", "javascript"};
	private BufferedReader br;
	private FileInputStream fis;
	private int experienceNumError;
	private int addressFormatError;
	private int postDateFormatError;
	private int companyNameNullError;
	
	public ReadResult() throws IOException {
		fis = new FileInputStream(Path.Result0404CSV);
        br = new BufferedReader(new InputStreamReader(fis));   
        experienceNumError = 0;
	}
	
	public MyDocumentForLucene nextDocument() throws IOException {
		String jobname=br.readLine();
		if(jobname==null) {
			br.close();
			fis.close();
			return null;
		}
		
		String url =br.readLine();
		String companyNameCrude = br.readLine();
		String addressSpecific = br.readLine();
		String address = "";
		String postDateNoFormat = br.readLine();
		String content = br.readLine();
		br.readLine();
		StringBuilder skillBuilder = new StringBuilder();
		String skill = "";
		LinkedList<String> experienceList = new LinkedList<>();
		LinkedList<Integer> experienceNum = new LinkedList<>();
		LinkedList<Integer> experience = new LinkedList<>();
		int snapshotIndex = 0;
		
		String companyName = "";
		if(!companyNameCrude.equalsIgnoreCase("null")){
			String[] companyNameArray = companyNameCrude.split("\\W+");
			StringBuilder companyNameBuilder = new StringBuilder();
			for (String companyNameWord: companyNameArray){
				companyNameBuilder.append(companyNameWord + " ");
			}
			companyName = companyNameBuilder.toString();
			System.out.println(companyName);
		}else{
			companyNameNullError++;
		}
		
		
		String[] addressArray = addressSpecific.split(",");
		if(addressArray.length == 2){
			address = addressArray[1].trim().toUpperCase();
		}else {
			addressFormatError++;
		}
		
		String month = null;
		String day = null;
		String year = null;
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
		if(month != null && day != null && year != null){
			postDate = Integer.parseInt(year + month + day);
		}
		
		String[] contentArray = content.split("\\W+");
		for (int i = 0; i < contentArray.length; i++){
			for (String skillItem: skillSet){
				if (contentArray[i].equalsIgnoreCase(skillItem)){
					skillBuilder.append(skillItem + " ");
				}		
			}
			if(contentArray[i].equalsIgnoreCase("Snapshot")){
				snapshotIndex = i;
			}
		}
		boolean startExperience= false;
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
		
		for (int i = 0; i < experienceList.size(); i++){
			if(isNumeric(experienceList.get(i))){
				int num = Integer.parseInt(experienceList.get(i));
				experienceNum.add(num);
			}
		}
		if(experienceNum.size() == 0){
			for(int i = 0; i < 15; i++){
				experience.add(i);
			}
		}else if(experienceNum.size() == 1){
			int start = experienceNum.get(0);
			for (int i = start; i < 15; i++){
				experience.add(i);
			}
		}else if(experienceNum.size() == 2){
			int start = experienceNum.get(0);
			int end = experienceNum.get(1);
			for (int i = start; i <= end; i++){
				experience.add(i);
			}
		}else{
			experienceNumError++;
		}
		
		
		// check which one is numeric and retrieve that 
		// if no, record 0 - 20
		// check key word is more than / to 
		// record corresponding values
		
		// change postDate to LocalDate type and get its long value
		// change address to WA etc.
		
		skill = skillBuilder.toString();
		
		MyDocumentForLucene doc = new MyDocumentForLucene(jobname, url, companyName, postDate, address, skill, experience, content);
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
			FileWriter wr = new FileWriter(Path.Result0404CheckCSV);
			ReadResult rr = new ReadResult();
			MyDocumentForLucene mdfl = rr.nextDocument();
			while(mdfl != null){
				wr.append(mdfl.toString() + "\n");
				mdfl = rr.nextDocument();
			}
			System.out.println(rr.experienceNumError);
			System.out.println(rr.addressFormatError);
			System.out.println(rr.postDateFormatError);
			System.out.println(rr.companyNameNullError);

			rr.closeReader();
			wr.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
