package com.wrt.lucene_store;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class LuceneService {

	private String[] ids = {"1", "2", "3", "4"};
	private String[] emails = {"11@qq.com", "22@qq.com", "33@qq.com", "44@qq.com" };
	private String[] fromNames = {"11_name", "22_name", "33_name", "44_name"};
	private String[] contents ={"from 11_name@qq.com email, content is 11XX",
			"from 22_name@qq.com email, content is 22YY",
			"from 33_name@qq.com email, content is 33CC",
			"from 44_name@qq.com email, content is 44NN"};
	
	private Directory dic = null;
	
	public LuceneService() {
		try {
			this.dic = FSDirectory.open(new File("D:/lucene temp/index02").toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void create() {
		IndexWriter writer = null;
		try {
			writer = new IndexWriter(dic, new IndexWriterConfig(new StandardAnalyzer()));
			Document doc = null;
			for(int i=0; i<ids.length; i++) {
				doc = new Document();
				doc.add(new StringField("id", ids[i], Store.YES));
				doc.add(new StringField("email", emails[i], Store.YES));
				doc.add(new StringField("fromName", fromNames[i], Store.YES));
				doc.add(new TextField("content", contents[i], Store.NO));
				writer.addDocument(doc);
			}
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
	
	public void search() {
		DirectoryReader reader = null;
		try {
			reader = DirectoryReader.open(dic);
			IndexSearcher search = new IndexSearcher(reader);
			QueryParser parse = new QueryParser("content", new StandardAnalyzer());
			Query query = parse.parse("11XX");
			TopDocs tops = search.search(query, 10);
			ScoreDoc[] score = tops.scoreDocs;
			Document doc = null;
			for(ScoreDoc sd : score) {
				doc = search.doc(sd.doc);
				System.out.println("id: " + doc.get("id") + " email:" + doc.get("email") + " content:" + doc.get("content"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void query() {
		DirectoryReader reader = null;
		try {
			reader = DirectoryReader.open(dic);
			System.out.println("numDocs: " + reader.numDocs());
			System.out.println("maxDoc: " + reader.maxDoc());
			System.out.println("numDeletedDocs: " + reader.numDeletedDocs());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(reader != null) {
				try {
					reader.close();
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
