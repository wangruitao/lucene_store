package com.wrt.lucene_store;

import org.junit.Test;

public class TestLuceneService {

	private IndexUtils ius;
	private SearchUtils sus;
	private FileIndexUtils fius;
	
	
	public TestLuceneService() {
		this.ius = new IndexUtils();
		this.sus = new SearchUtils();
		this.fius = new FileIndexUtils();
	}

	@Test
	public void testCreate() {
		ius.create();
	}
	
	@Test
	public void testSearchsearchQueryParser() {
		sus.searchQueryParser();
	}
	
	@Test
	public void testQuery() {
		sus.query();
	}
	
	@Test
	public void testDelete() {
		ius.delete();
	}
	
	@Test
	public void testForceDelete() {
		ius.forceDelete();
	}
	
	@Test
	public void testUpdate() {
		ius.update();
	}
	
	@Test
	public void testDirectoryReader() {
		for(int i=0; i<10; i++) {
			sus.searchQueryParser();
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
		sus.searchTermQuery();
	}
	
	@Test
	public void testSearchTermRangeQuery() {
		sus.searchTermRangeQuery();
	}
	
	@Test
	public void testSearchPrefixQuery() {
		sus.searchPrefixQuery();
	}
	
	@Test
	public void testSearchWildcardQuery() {
		sus.searchWildcardQuery();
	}
	
	@Test
	public void testSearchFuzzyQuery() {
		sus.searchFuzzyQuery();
	}
	
	@Test
	public void testSearchRegexpQuery() {
		sus.searchRegexpQuery();
	}
	
	@Test
	public void testSearchNumericRangeQuery() {
		sus.searchNumericRangeQuery();
	}
	
	@Test
	public void testSearchBooleanQuery() {
		sus.searchBooleanQuery();
	}
	
	@Test
	public void testSearchPhraseQuery() {
		sus.searchPhraseQuery();
	}
	
	@Test
	public void testCopyFiles() {
		fius.copyFiles();
	}
}
