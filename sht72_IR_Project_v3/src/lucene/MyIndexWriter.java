package lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * The class for indexing a document.
 * The index() method receives those parameters from a MyDocumentForLucene instance and add them to a lucene doc,
 * and then add that doc to indexwriter.
 * @author Shuning Tong
 */

public class MyIndexWriter {
	
	protected File dir;
	private Directory directory;
	private IndexWriter ixwriter;
	private FieldType contentFieldType;
	private FieldType storedIndexFieldType;
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
	private static final FieldType STRING_FIELD_TYPE_INDEXED = new FieldType();
	static {
		
		STRING_FIELD_TYPE_INDEXED.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
		STRING_FIELD_TYPE_INDEXED.setStored(true);
		STRING_FIELD_TYPE_INDEXED.setStoreTermVectors(true);
		STRING_FIELD_TYPE_INDEXED.setStoreTermVectorOffsets(true);
	}
	
	private static final FieldType STRING_FIELD_TYPE_INDEXED_STORED = new FieldType();
	static{
	STRING_FIELD_TYPE_INDEXED_STORED.setIndexOptions(IndexOptions.DOCS);
	STRING_FIELD_TYPE_INDEXED_STORED.setStored(true);
	STRING_FIELD_TYPE_INDEXED_STORED.setStoreTermVectors(true);
	STRING_FIELD_TYPE_INDEXED_STORED.setStoreTermVectorOffsets(true);
	}
	
	
	public MyIndexWriter() throws IOException {
		directory = FSDirectory.open(Paths.get(lucene.Path.Index0404Dir));
		Map<String,Analyzer> analyzerPerField = new HashMap<>();
		analyzerPerField.put("SKILL", new WhitespaceAnalyzer());
		analyzerPerField.put("ADDRESS", new WhitespaceAnalyzer());
		PerFieldAnalyzerWrapper aWrapper = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerPerField);
		IndexWriterConfig indexConfig=new IndexWriterConfig(aWrapper);
		indexConfig.setMaxBufferedDocs(10000);

		ixwriter = new IndexWriter(directory, indexConfig);
		
	}

	public void index( String jobname, String url, String companyName, int postDate, String postDateToStore, String address, String skill, LinkedList<Integer> experience, String content) throws IOException {
		Document doc = new Document();
		// JOBNAME, URL, COMPANYNAME and ADDRESS are all stored fields
		doc.add(new StoredField("JOBNAME", jobname));
		doc.add(new StoredField("URL", url));
		doc.add(new StoredField("COMPANYNAME", companyName));
		// POSTDATE should be a int field so that we can sort
		doc.add(new IntField("POSTDATE", postDate, INT_FIELD_TYPE_STORED_SORTED));
		doc.add(new StoredField("POSTDATETOSTORE", postDateToStore));
		doc.add(new Field("ADDRESS", address, STRING_FIELD_TYPE_INDEXED_STORED));
		doc.add(new Field("SKILL", skill, STRING_FIELD_TYPE_INDEXED_STORED));
		for (Integer i: experience){
			doc.add(new IntField("EXPERIENCE", i, Field.Store.NO));
		}
		doc.add(new Field("CONTENT", content, STRING_FIELD_TYPE_INDEXED));
		//default method to add with the analyzer we defined previously
		ixwriter.addDocument(doc);
	}
	
	public void close() throws IOException {
		ixwriter.close();
		directory.close();
	}
	
}
