package com.wrt.lucene_store;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class FileIndexUtils {

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
	
	public DirectoryReader getDirectoryReader() {
		return reader;
	}
	
	public void copyFiles() {
		File files = new File(path);
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
	
	public void create() {
		
	}
	
}
