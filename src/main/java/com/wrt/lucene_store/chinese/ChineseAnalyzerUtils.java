package com.wrt.lucene_store.chinese;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class ChineseAnalyzerUtils {

	private static final String PATH = "H:/lucenetemp/index04";
	private static Directory dir;
	private static IndexWriter writer;
	private static DirectoryReader reader = null;

	static {
		Path path = Paths.get(PATH);
		try {
			dir = FSDirectory.open(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static DirectoryReader getReader() {
		if(reader == null) {
			try {
				reader = DirectoryReader.open(dir);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} 
		return reader;
	}
	
	public void index(String str) {
		Document doc = new Document();
		doc.add(new TextField("content", new StringReader(str)));
		try {
			writer.deleteAll();
			writer.addDocument(doc);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void search(String searchName, Analyzer mmsa) {
		try {
			if(reader == null) {
				reader = DirectoryReader.open(dir);
			} else {
				DirectoryReader tr = DirectoryReader.openIfChanged(reader);
				if(tr != null) {
					reader.close();
					reader = tr;
				}
			}
			IndexSearcher search = new IndexSearcher(reader);
			QueryParser parser = new QueryParser("content", mmsa);
			Query query = parser.parse(searchName);
			TopDocs tds = search.search(query, 10);
			ScoreDoc[] sda = tds.scoreDocs;
			for(ScoreDoc score : sda) {
				Document doc = search.doc(score.doc);
				System.out.println(doc.get("content"));
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void displayToken(String str, Analyzer mma) {
		TokenStream stream = mma.tokenStream("content", new StringReader(str));
		CharTermAttribute cta = stream.addAttribute(CharTermAttribute.class);
		try {
			stream.reset();
			while(stream.incrementToken()) {
				System.out.print("[" + cta + "]");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void displayAllToken(String str, Analyzer mma) {
		TokenStream stream = mma.tokenStream("content", new StringReader(str));
		CharTermAttribute cta = stream.addAttribute(CharTermAttribute.class);
		OffsetAttribute off = stream.addAttribute(OffsetAttribute.class);
		PositionIncrementAttribute posIncrAtt = stream.addAttribute(PositionIncrementAttribute.class);
		TypeAttribute typeAtt = stream.addAttribute(TypeAttribute.class);
		try {
			stream.reset();
			while(stream.incrementToken()) {
				System.out.println("[Char]: " + cta + " [Offset]: start-" + off.startOffset() + " end-" + off.endOffset() + 
						" [Position]: " + posIncrAtt.getPositionIncrement() + " [Type]: " + typeAtt.type());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
