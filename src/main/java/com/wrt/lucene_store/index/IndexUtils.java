package com.wrt.lucene_store.index;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class IndexUtils {

	private String[] ids = {"1", "2", "3", "4"};
	private String[] emails = {"33@qq.com", "44@qq.com", "11@wrt.com", "32@wrtc.com" };
	private String[] fromNames = {"mack", "irukck", "mack", "joik"};
	private String[] contents ={"from 11_name@qq.com email, content is 11XX",
			"quick brown fox",
			"jumps over lazy, broun dog",
			"jumps over extremely very lazy broxn dog"};
//			"from 22_name@qq.com email, content is 11YY",
//			"from 33_name@qq.com email, content is 33CC",
//			"from 44_name@qq.com email, content is 44NN"};
	
	private int[] nums = {1,2,3,4};
	private Date[] dates = null;
	
	private static Directory dic = null;
	private static DirectoryReader reader = null;
	
	static {
		try {
			dic = FSDirectory.open(new File("H:/lucenetemp/index02").toPath());
			reader = DirectoryReader.open(dic);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public DirectoryReader getDirectoryReader() {
		return reader;
	}
	
	public IndexUtils() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
		dates = new Date[ids.length];
		try {
			dates[0] = sdf.parse("2016-12-22");
			dates[1] = sdf.parse("2016-11-21");
			dates[2] = sdf.parse("2015-06-09");
			dates[3] = sdf.parse("2014-07-13");
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
	}
	
	public static DirectoryReader getReader() {
		try {
			if (reader == null) {
				reader = DirectoryReader.open(dic);
			} else {
				DirectoryReader tr = DirectoryReader.openIfChanged(reader);
				if(tr != null) {
					reader.close();
					reader = tr;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return reader;
	}
	
	public static IndexSearcher getIndexSearcher() {
		return new IndexSearcher(getReader());
	}

	public void create() {
		IndexWriter writer = null;
		try {
			writer = new IndexWriter(dic, new IndexWriterConfig(new StandardAnalyzer()));
			writer.deleteAll();
			Document doc = null;
			for(int i=0; i<ids.length; i++) {
				doc = new Document();
				Field content = new TextField("content", contents[i], Store.NO);
				doc.add(new StringField("id", ids[i], Store.YES));
				doc.add(new StringField("email", emails[i], Store.YES));
				doc.add(new StringField("fromName", fromNames[i], Store.YES));
				doc.add(new IntField("num", nums[i], Store.YES));
				doc.add(new LongField("date", dates[i].getTime(), Store.YES));
				doc.add(content);
				//在content上加权值
				if(emails[i].substring(emails[i].indexOf("@") + 1).contains("wrt")) {
					content.setBoost(2.0f);
				} else if(emails[i].substring(emails[i].indexOf("@") + 1).contains("wrtc")) {
					content.setBoost(1.5f);
				} else {
					content.setBoost(0.5f);
				}
				writer.addDocument(doc);
			}
			writer.commit();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void delete() {
		IndexWriter writer = null;
		try {
			writer = new IndexWriter(dic, new IndexWriterConfig(new StandardAnalyzer()));
			writer.deleteDocuments(new Term("id", "1"));
			writer.commit();
			//回滚
//			writer.rollback();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void forceDelete() {
		IndexWriter writer = null;
		try {
			writer = new IndexWriter(dic, new IndexWriterConfig(new StandardAnalyzer()));
			writer.forceMergeDeletes();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void update() {
		IndexWriter writer = null;
		try {
			writer = new IndexWriter(dic,  new IndexWriterConfig(new StandardAnalyzer()) );
			Document doc = new Document();
			doc.add(new StringField("id", "11", Field.Store.YES));
			doc.add(new StringField("email", emails[0], Field.Store.YES));
			doc.add(new StringField("fromName", fromNames[0], Field.Store.YES));
			doc.add(new TextField("content", contents[0], Field.Store.NO));
			writer.updateDocument(new Term("id", "1" ), doc); 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
