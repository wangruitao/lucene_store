package com.wrt.lucene_store.page;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class PageIndexUtils {

	private static Directory dic = null;
	private static DirectoryReader reader = null;
	private String path = "H:/lucenetemp/file";
	
	static {
		try {
			dic = FSDirectory.open(new File("H:/lucenetemp/index03").toPath());
			reader = DirectoryReader.open(dic);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static DirectoryReader getDirectoryReader() {
		return reader;
	}
	
	public void copyFiles() {
		File files = createDirectory(path);
		if(files.isDirectory()) {
			File[] flist = files.listFiles();
			for(int i=1; i<10; i++) {
				int num = 0;
				for(File file : flist) {
					String fileName = file.getName();
					String[] fileA = fileName.split("\\.");
					try {
						FileUtils.copyFile(file, new File(path + "/" + fileA[0] + "_" + i*num + "." + fileA[1]));
					} catch (IOException e) {
						e.printStackTrace();
					}
					num ++;
				}
			}
		}
	}
	
	public File createDirectory(String path) {
		File file = new File(path);
		if(!file.exists()) {
			if(!file.mkdirs()) {
				return null;
			}
		} 
		return file;
	}
	
	public void create() {
		IndexWriter writer = null;
		try {
			writer = new IndexWriter(dic, new IndexWriterConfig(new StandardAnalyzer()));
			File files = new File(path);
			if(files.isDirectory()) {
				File[] flist = files.listFiles();
				List<Document> list = new ArrayList<Document>();
				Document doc = null;
				for(File file : flist) {
					doc = new Document();
					doc.add(new StringField("name", file.getName(), Store.YES));
					doc.add(new IntField("size", (int)(file.length()/1024), Store.YES));
					doc.add(new StringField("path", file.getAbsolutePath(), Store.YES));
					doc.add(new TextField("content", FileUtils.readFileToString(file, Charset.forName("UTF-8")), Store.NO));
					list.add(doc);
				}
				if(list != null) {
					writer.addDocuments(list);
				}
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
	
}
