package com.wrt.lucene_store.sortandfilter;

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TopFieldDocs;

import com.wrt.lucene_store.page.PageIndexUtils;

public class SortAndFilterUtils {

	private DirectoryReader reader;
	
	public void sortSearch() {
		reader = PageIndexUtils.getDirectoryReader();
		IndexSearcher search = new IndexSearcher(reader);
		QueryParser parser = new QueryParser("name", new StandardAnalyzer());
		try {
			Query query = parser.parse("[a* TO e*] AND suffix:ini");
//			TopFieldDocs docs = search.search(query, 10, Sort.INDEXORDER);
			SortField sfield = new SortField("size", Type.INT, true);
			Sort sort = new Sort(sfield);
			TopFieldDocs docs = search.search(query, 450, sort);
			ScoreDoc[] sds = docs.scoreDocs;
			System.out.println("查询总数： " + docs.totalHits);
			Document doc = null;
			for (ScoreDoc sd : sds) {
				doc = search.doc(sd.doc);
				System.out.println("docId:" + sd.doc + " name: " + doc.get("name") + " suffix:" + doc.get("suffix") + " size:" + doc.get("filesize") + " content:"
						+ doc.get("content") + " path:" + doc.get("path") + " filedate:" + doc.get("filedate"));
			}
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void fSearch() {
		reader = PageIndexUtils.getDirectoryReader();
		IndexSearcher search = new IndexSearcher(reader);
		QueryParser parser = new QueryParser("name", new StandardAnalyzer());
		try {
			Query query = parser.parse("[a* TO e*] AND suffix:ini");
//			TopFieldDocs docs = search.search(query, 10, Sort.INDEXORDER);
			SortField sfield = new SortField("size", Type.INT, true);
			Sort sort = new Sort(sfield);
			TopFieldDocs docs = search.search(query, 450, sort);
			ScoreDoc[] sds = docs.scoreDocs;
			System.out.println("查询总数： " + docs.totalHits);
			Document doc = null;
			for (ScoreDoc sd : sds) {
				doc = search.doc(sd.doc);
				System.out.println("docId:" + sd.doc + " name: " + doc.get("name") + " suffix:" + doc.get("suffix") + " size:" + doc.get("filesize") + " content:"
						+ doc.get("content") + " path:" + doc.get("path") + " filedate:" + doc.get("filedate"));
			}
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
	}
}
