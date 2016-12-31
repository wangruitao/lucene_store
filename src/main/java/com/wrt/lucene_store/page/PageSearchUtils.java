package com.wrt.lucene_store.page;

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class PageSearchUtils {


	/**
	 * 
	 * @param field 查询字段
	 * @param content 查询内容
	 */
	public void search(String field, String content) {
		DirectoryReader reader = PageIndexUtils.getDirectoryReader();
		IndexSearcher search = new IndexSearcher(reader);
		QueryParser parser = new QueryParser(field, new StandardAnalyzer());
		try {
			Query query = parser.parse(content);
			TopDocs tops = search.search(query, 500);
			ScoreDoc[] scores = tops.scoreDocs;
			System.out.println("查询总数：" + tops.totalHits);
			int num = 1;
			for(ScoreDoc scd : scores) {
				Document doc = search.doc(scd.doc);
				System.out.println("name: " + doc.get("name") + "##### size: " + doc.get("size") + "KB ###### path: " + doc.get("path"));
				if((num%5) == 0) {
					num = 0;
					System.out.println("-------------------------------------------------");
				}
				num ++;
			}
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param field 查询字段
	 * @param content 查询内容
	 * @param pageNum 第几页，1为首页
	 * @param pageSize 每页显示几行
	 */
	public void searchPage(String field, String content, Integer pageNum, Integer pageSize) {
		DirectoryReader reader = PageIndexUtils.getDirectoryReader();
		IndexSearcher search = new IndexSearcher(reader);
		QueryParser parser = new QueryParser(field, new StandardAnalyzer());
		try {
			Query query = parser.parse(content);
			int total = pageNum * pageSize;
			TopDocs tops = search.search(query, total);
			System.out.println("查询总数：" + tops.totalHits);
			if(total <= tops.totalHits) {
				ScoreDoc[] scores = tops.scoreDocs;
				int start = (pageNum-1) * pageSize;
				for(int i=start; i<scores.length; i++) {
					Document doc = search.doc(scores[i].doc);
					System.out.println("name: " + doc.get("name") + "##### size: " + doc.get("size") + "KB ###### path: " + doc.get("path"));
				}
			}
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param field 查询字段
	 * @param content 查询内容
	 * @param pageNum 第几页，1为首页
	 * @param pageSize 每页显示几行
	 */
	public void seachAfter(String field, String content, Integer pageNum, Integer pageSize) {
		DirectoryReader reader = PageIndexUtils.getDirectoryReader();
		IndexSearcher search = new IndexSearcher(reader);
		QueryParser parser = new QueryParser(field, new StandardAnalyzer());
		try {
			Query query = parser.parse(content);
			int total = pageNum * pageSize;
			int start = (pageNum-1) * pageSize;
			TopDocs tops = null;
			ScoreDoc after = null;
			if(start != 0) {
				tops = search.search(query, start);
				after = tops.scoreDocs[start-1];
			}
			TopDocs afterTops = null;
			if(start == 0) {
				afterTops = search.searchAfter(null, query, pageSize);
			} else {
				if(start <= tops.totalHits) {
					if(total >= tops.totalHits) {
						pageSize = tops.totalHits - start;
					}
					afterTops = search.searchAfter(after, query, pageSize);
				}
			}
			System.out.println("查询总数：" + afterTops.totalHits);
			ScoreDoc[] afterScore = afterTops.scoreDocs;
			for(ScoreDoc score : afterScore) {
				Document doc = search.doc(score.doc);
				System.out.println("name: " + doc.get("name") + "##### size: " + doc.get("size") + "KB ###### path: " + doc.get("path"));
			}
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
	}
}
