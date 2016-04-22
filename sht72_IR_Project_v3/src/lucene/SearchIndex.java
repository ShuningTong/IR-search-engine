package lucene;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TextFragment;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class SearchIndex {

	private Directory directory;
	private DirectoryReader ireader;
	private IndexSearcher isearcher;
	private PerFieldAnalyzerWrapper aWrapper;

	public SearchIndex(){
		try {
			this.directory = FSDirectory.open(Paths.get(lucene.Path.Index0404Dir));
			this.ireader = DirectoryReader.open(directory);
			this.isearcher = new IndexSearcher(ireader);
			Map<String,Analyzer> analyzerPerField = new HashMap<>();
			analyzerPerField.put("SKILL", new WhitespaceAnalyzer());
			analyzerPerField.put("ADDRESS", new WhitespaceAnalyzer());
			this.aWrapper = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerPerField);
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


		BooleanQuery bq;
		try {
			bq = si.prepareQuery(skill, experience, address, content);
			if (sort == 1){
				si.searchResult(bq);
			}else if (sort == 2){
				si.searchResultSortByPostDate(bq);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		sc.close();
		si.closeReader();

		//long endTime=System.currentTimeMillis();
		//System.out.println("load index & retrieve running time: "+(endTime-startTime)/60000.0+" min");

	}

	public BooleanQuery prepareQuery(String skill, int experience, String address, String content) throws Exception {
		
		
		QueryParser skillParser = new QueryParser("SKILL", this.aWrapper);
		Query skillQuery = skillParser.parse(skill);
		//BoostQuery skillBoostQuery = new BoostQuery(skillQuery, 2.0f);
		
		Query experienceQuery = NumericRangeQuery.newIntRange("EXPERIENCE", experience, experience, true, true);
		
		QueryParser addressParser = new QueryParser("ADDRESS", this.aWrapper);
		Query addressQuery = addressParser.parse(address);
		//BoostQuery addressBoostQuery = new BoostQuery(addressQuery, 10.0f);
		
		BooleanQuery.Builder complexQueryBuilder = new BooleanQuery.Builder();
		complexQueryBuilder.add(skillQuery, BooleanClause.Occur.MUST);
		complexQueryBuilder.add(experienceQuery, BooleanClause.Occur.SHOULD);
		complexQueryBuilder.add(addressQuery, BooleanClause.Occur.MUST);

		if (content != null && !content.equals("")){
			QueryParser contentParser = new QueryParser("CONTENT", this.aWrapper);
			Query contentQuery = contentParser.parse(content);
			complexQueryBuilder.add(contentQuery, BooleanClause.Occur.SHOULD);
		}

		BooleanQuery complexQuery = complexQueryBuilder.build();	
		return complexQuery;

	}

	public void searchResult(BooleanQuery bq){

		TopDocs matches;
		try {
			matches = isearcher.search(bq, ireader.maxDoc());
			// TopDocs.totalHits returns the number of matching documents
			int totalHits = matches.totalHits;
			System.out.println("Found " + totalHits + " documents!");
			int count = 1;
			
			// highlighter
			SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter();
	        Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(bq));

			// TopDocs.scoreDocs attribute is an array containing the requested number of top matches.
			for(ScoreDoc scoredoc: matches.scoreDocs){
				int id = scoredoc.doc;
				Document doc = isearcher.doc(id);
				System.out.print(count + ". ");
				System.out.println(doc.get("JOBNAME"));
				System.out.println(doc.get("SKILL"));
				System.out.println(doc.get("URL"));
				System.out.print(doc.get("COMPANYNAME"));
				System.out.print("  " + doc.get("ADDRESS"));
				System.out.println("  " + doc.get("POSTDATETOSTORE"));
				/*Explanation explanation = isearcher.explain(bq, scoredoc.doc);
				System.out.println(explanation.toString());
				System.out.println("----------");*/
				String text = doc.get("CONTENT");
	            TokenStream tokenStream = TokenSources.getTermVectorTokenStreamOrNull("CONTENT", ireader.getTermVectors(id), highlighter.getMaxDocCharsToAnalyze() - 1);
	            TextFragment[] frag = highlighter.getBestTextFragments(tokenStream, text, false, 4);
	            for (int j = 0; j < frag.length; j++) {
	                if ((frag[j] != null) && (frag[j].getScore() > 0)) {
	                    System.out.println((frag[j].toString()));
	                }
	            }
	            
				count++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidTokenOffsetsException e2){
			e2.printStackTrace();
		}
	}

	public void searchResultSortByPostDate(BooleanQuery bq){
		TopDocs matches;
		try {
			matches = isearcher.search(bq, ireader.maxDoc(), new Sort(new SortField("POSTDATE", SortField.Type.INT, true)));
			int totalHits = matches.totalHits;
			System.out.println("Found " + totalHits + " documents!");
			int count = 1;
			
			SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter();
	        Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(bq));
	        
			for(ScoreDoc scoredoc: matches.scoreDocs){
				int id = scoredoc.doc;
				Document doc = isearcher.doc(id);
				System.out.print(count + ". ");
				System.out.println(doc.get("JOBNAME"));
				System.out.println(doc.get("SKILL"));
				System.out.println(doc.get("URL"));
				System.out.print(doc.get("COMPANYNAME"));
				System.out.print("  " + doc.get("ADDRESS"));
				System.out.println("  " + doc.get("POSTDATETOSTORE"));
				String text = doc.get("CONTENT");
	            TokenStream tokenStream = TokenSources.getTermVectorTokenStreamOrNull("CONTENT", ireader.getTermVectors(id), highlighter.getMaxDocCharsToAnalyze() - 1);
	            TextFragment[] frag = highlighter.getBestTextFragments(tokenStream, text, false, 4);
	            for (int j = 0; j < frag.length; j++) {
	                if ((frag[j] != null) && (frag[j].getScore() > 0)) {
	                    System.out.println((frag[j].toString()));
	                }
	            }
	            
				count++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidTokenOffsetsException e) {
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
