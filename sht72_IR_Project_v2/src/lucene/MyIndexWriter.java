package lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.LinkedList;

/**
 * Class for creating index
 * -- INFSCI 2140: Information Storage and Retrieval Spring 2016
 */

public class MyIndexWriter {
	
	protected File dir;
	private Directory directory;
	private IndexWriter ixwriter;
	private FieldType type;
	private static final FieldType INT_FIELD_TYPE_STORED_SORTED = new FieldType();
	static {
	    INT_FIELD_TYPE_STORED_SORTED.setTokenized(true);
	    INT_FIELD_TYPE_STORED_SORTED.setOmitNorms(true);
	    INT_FIELD_TYPE_STORED_SORTED.setIndexOptions(IndexOptions.DOCS);
	    INT_FIELD_TYPE_STORED_SORTED.setNumericType(FieldType.NumericType.INT);
	    INT_FIELD_TYPE_STORED_SORTED.setStored(true);
	    INT_FIELD_TYPE_STORED_SORTED.setDocValuesType(DocValuesType.NUMERIC);
	    INT_FIELD_TYPE_STORED_SORTED.freeze();
	}
	
	public MyIndexWriter() throws IOException {
		directory = FSDirectory.open(Paths.get(lucene.Path.Index0404Dir));  
		IndexWriterConfig indexConfig=new IndexWriterConfig(new StandardAnalyzer());
		indexConfig.setMaxBufferedDocs(10000);
		
		//IndexWriter always has Directory and IndexWriterConfig two parameters
		ixwriter = new IndexWriter(directory, indexConfig);
		type = new FieldType();
		//Only documents and term frequencies are indexed
		type.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
		type.setStored(false);
		type.setStoreTermVectors(true);
		//you can choose separately whether these details are also stored in your term vectors 
		//by passing these constants as the fourth argument to the Field constructor:
	}
	
	/**
	 * This method build index for each document.
	 * NOTE THAT: in your implementation of the index, you should transform your string docnos into non-negative integer docids !!!
	 * In MyIndexReader, you should be able to request the integer docid for docnos.
	 * 
	 * @param docno 
	 * @param content
	 * @throws IOException
	 */
	public void index( String jobname, String url, String companyName, int postDate, String address, String skill, LinkedList<Integer> experience, String content) throws IOException {
		// you should implement this method to build index for each document
		Document doc = new Document();
		// each document should typically contain one or more stored fields which uniquely identify it.
		doc.add(new StoredField("JOBNAME", jobname));
		doc.add(new StoredField("URL", url));
		doc.add(new StoredField("COMPANYNAME", companyName));
		
		doc.add(new IntField("POSTDATE", postDate, INT_FIELD_TYPE_STORED_SORTED));
		
		doc.add(new StoredField("ADDRESS", address));
		
		doc.add(new Field("SKILL", skill, type));
		for (Integer i: experience){
			doc.add(new IntField("EXPERIENCE", i, Field.Store.NO));
		}
		doc.add(new Field("CONTENT", content, type));
		
		//default one to add with the analyzer we defined previously
		ixwriter.addDocument(doc);
	}
	
	/**
	 * Close the index writer, and you should output all the buffered content (if any).
	 * @throws IOException
	 */
	public void close() throws IOException {
		// you should implement this method if necessary
		ixwriter.close();
		directory.close();
	}
	
}
