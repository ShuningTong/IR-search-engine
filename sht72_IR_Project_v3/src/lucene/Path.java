package lucene;


public class Path {
	public static String DataWebDir="data/docset.trecweb";//address of docset.trectext
	public static String DataTextDir="data/docset.trectext";//address of docset.trectext
	
	public static String ResultHM1="data/result.";
	//there is "." at the end because we want to use new FileWriter(Path.ResultHM1 + dataType) to indicate the extension of the result file
	
	public static String StopwordDir="data/stopword.txt";//address of stopwords.txt
	//public static String TestDir = "data/test.txt";
	//public static String TestWebDir = "data/testWeb.txt";
	public static String TestCSVDir="data/test.csv";
	public static String CSVDir="data/data.csv";
	public static String CSV2Dir="data/data2.csv";
	public static String CSV0404Dir = "data/data0404.csv";
	
	//public static String TestResultCSV="data/test_result.txt";
	//public static String ResultCSV="data/result.txt";
	public static String ResultOfCSVCollection="data/result0404/result0404.txt";
	public static String ResultForLucene = "data/result0404/result0404_forLucene.txt";
	//public static String Result0404Check2CSV = "data/result0404check2.txt";
	
	public static String IndexDir="data//index//";
	public static String Index2Dir="data//index2//";
	public static String Index0404Dir = "data/index0404/";
	

}
