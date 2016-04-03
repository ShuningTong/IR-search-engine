package v1;

import java.util.Scanner;

public class SearchIndex {

	public static void main(String[] args) {
		//long startTime=System.currentTimeMillis();
		Scanner sc = new Scanner(System.in);
		System.out.println("Please type in a word that you want to search (e.g. java, python, c):");
		String token = sc.nextLine();
		
		try {
			ReadIndex(token);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//long endTime=System.currentTimeMillis();
		//System.out.println("load index & retrieve running time: "+(endTime-startTime)/60000.0+" min");

	}
	
	public static void ReadIndex(String token) throws Exception {
		MyIndexReader ixreader=new MyIndexReader();
		int df = ixreader.DocFreq(token);
		long ctf = ixreader.CollectionFreq(token);
		System.out.println(" >> the token \""+token+"\" appeared in "+df+" documents and "+ctf+" times in total");
		if(df>0){
			int[][] posting = ixreader.getPostingList(token);
			for(int ix=0;ix<posting.length;ix++){
				int docid = posting[ix][0];
				int freq = posting[ix][1];
				String jobname = ixreader.getJobname(docid);
				String url = ixreader.getURL(docid);
				System.out.println(jobname + "\n" + url + "\n" + docid + "   " + freq);
			}
		}
		ixreader.close();

	}

}
