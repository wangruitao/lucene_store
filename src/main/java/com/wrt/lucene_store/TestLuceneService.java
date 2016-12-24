package com.wrt.lucene_store;

import org.junit.Test;

public class TestLuceneService {

	private IndexUtils ls;
	
	
	public TestLuceneService() {
		this.ls = new IndexUtils();
	}

	@Test
	public void testCreate() {
		ls.create();
	}
	
	@Test
	public void testSearch() {
		ls.search();
	}
	
	@Test
	public void testQuery() {
		ls.query();
	}
	
	@Test
	public void testDelete() {
		ls.delete();
	}
	
	@Test
	public void testForceDelete() {
		ls.forceDelete();
	}
	
	@Test
	public void testUpdate() {
		ls.update();
	}
	
	@Test
	public void testDirectoryReader() {
		for(int i=0; i<10; i++) {
			ls.search();
			System.out.println("########################################");
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Test
	public void testSearchTermQuery() {
		ls.searchTermQuery();
	}
	
	@Test
	public void testSearchTermRangeQuery() {
		ls.searchTermRangeQuery();
	}
	
	@Test
	public void testSearchPrefixQuery() {
		ls.searchPrefixQuery();
	}
	
	@Test
	public void testSearchWildcardQuery() {
		ls.searchWildcardQuery();
	}
	
	@Test
	public void testSearchFuzzyQuery() {
		ls.searchFuzzyQuery();
	}
}
