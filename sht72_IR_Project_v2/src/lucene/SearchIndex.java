package lucene;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class SearchIndex {
	
	private Directory directory;
	private DirectoryReader ireader;
	private IndexSearcher isearcher;
	
	public SearchIndex(){
		try {
			this.directory = FSDirectory.open(Paths.get(lucene.Path.Index0404Dir));
			this.ireader = DirectoryReader.open(directory);
		    this.isearcher = new IndexSearcher(ireader);
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}

	public static void main(String[] args) {
		//long startTime=System.currentTimeMillis();
		SearchIndex si = new SearchIndex();
		Scanner sc = new Scanner(System.in);
			// we will use a dropdown menu or form to let user to choose from what we have already defined
			System.out.println("What skills do you have (e.g. java, python, c, sql, html, php, javascript):");
			String skill = sc.nextLine();
			
			// we will let user to type in a single int from 0 to 14
			System.out.println("How many years of experience do you have (e.g. 0, 1, 2):");
			int experience = sc.nextInt();
			
			// we will use a dropdown menu tp let user choose from these states
			String address = sc.nextLine();
			System.out.println("Where do you want to work (e.g. PA, CA, WA, NY):");
			address = sc.nextLine();
			
			// we will let user to type in some words
			System.out.println("Other additional info that you want to search on?");
			String content = sc.nextLine();
			
			System.out.println("Do you want to sort by 1. relevance or 2. post date (enter 1 or 2):");
			int sort = sc.nextInt();

			if(skill != null && address != null && content != null){
				try {
					BooleanQuery bq = si.prepareQuery(skill, experience, address, content);
					if (sort == 1){
						si.searchResult(bq);
					}else if (sort == 2){
						si.searchResultSortByPostDate(bq);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		sc.close();
		si.closeReader();

		//long endTime=System.currentTimeMillis();
		//System.out.println("load index & retrieve running time: "+(endTime-startTime)/60000.0+" min");

	}
	
	public BooleanQuery prepareQuery(String skill, int experience, String address, String content) throws Exception {
		QueryParser skillParser = new QueryParser("SKILL", new StandardAnalyzer());
		Query skillQuery = skillParser.parse(skill);
		
		Query experienceQuery = NumericRangeQuery.newIntRange("EXPERIENCE", experience, experience, true, true);
		
		Term t = new Term("ADDRESS", address);
		TermQuery addressQuery = new TermQuery(t);
		
		QueryParser contentParser = new QueryParser("CONTENT", new StandardAnalyzer());
		Query contentQuery = contentParser.parse(content);
		
		BooleanQuery.Builder complexQueryBuilder = new BooleanQuery.Builder();
		complexQueryBuilder.add(skillQuery, BooleanClause.Occur.MUST);
		complexQueryBuilder.add(experienceQuery, BooleanClause.Occur.MUST);
		complexQueryBuilder.add(addressQuery, BooleanClause.Occur.SHOULD);
		complexQueryBuilder.add(contentQuery, BooleanClause.Occur.SHOULD);
		BooleanQuery complexQuery = complexQueryBuilder.build();
		
		return complexQuery;
	
	}
	
	public void searchResult(BooleanQuery bq){

		TopDocs matches;
		try {
			matches = isearcher.search(bq, 20);
			int totalHits = matches.totalHits;
			
			System.out.println("Found " + totalHits + " documents!");
			int count = 1;
			for(ScoreDoc scoredoc: matches.scoreDocs){
				Document doc = isearcher.doc(scoredoc.doc);
				System.out.print(count + ". ");
				System.out.println(doc.get("JOBNAME"));
				System.out.println(doc.get("URL"));
				System.out.print(doc.get("COMPANYNAME"));
				System.out.print("  " + doc.get("ADDRESS"));
				System.out.println("  " + doc.get("POSTDATE"));
				count++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void searchResultSortByPostDate(BooleanQuery bq){
		TopDocs matches;
		try {
			matches = isearcher.search(bq, 20, new Sort(new SortField("POSTDATE", SortField.Type.INT, true)));
			int totalHits = matches.totalHits;
			
			System.out.println("Found " + totalHits + " documents!");
			int count = 1;
			for(ScoreDoc scoredoc: matches.scoreDocs){
				Document doc = isearcher.doc(scoredoc.doc);
				System.out.print(count + ". ");
				System.out.println(doc.get("JOBNAME"));
				System.out.println(doc.get("URL"));
				System.out.print(doc.get("COMPANYNAME"));
				System.out.print("  " + doc.get("ADDRESS"));
				System.out.println("  " + doc.get("POSTDATE"));
				count++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void closeReader(){
		try {
			ireader.close();
			directory.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

}
