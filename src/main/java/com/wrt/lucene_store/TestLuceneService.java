package com.wrt.lucene_store;

import org.junit.Test;

public class TestLuceneService {

	private LuceneService ls;
	
	
	public TestLuceneService() {
		this.ls = new LuceneService();
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
	
}
