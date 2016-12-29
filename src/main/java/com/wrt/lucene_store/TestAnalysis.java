package com.wrt.lucene_store;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.junit.Test;

public class TestAnalysis {

	@Test
	public void testDisplayToken() {
		Analyzer sda = new StandardAnalyzer();
		Analyzer spa = new SimpleAnalyzer();
		Analyzer wa = new WhitespaceAnalyzer();
		Analyzer sta = new StopAnalyzer();
		
		AnalysisUtils au = new AnalysisUtils();
		String str = "then at the heart of the most lax, alert, and most low awareness, and left it godsend failed.";
		au.displayToken(str, sda);
		System.out.println("*********************************************");
		au.displayToken(str, spa);
		System.out.println("*********************************************");
		au.displayToken(str, wa);
		System.out.println("*********************************************");
		au.displayToken(str, sta);
	}
	
	@Test
	public void testStopAnalyzer() {

		String str = "then at the heart of the most lax, alert, and most low awareness, and left it godsend failed.";
//		String[] atr = {};
		String[] atr = {"lax", "low"};
		Analyzer sda = new MyStopAnalyzer(atr);
		
		AnalysisUtils au = new AnalysisUtils();
		au.displayAllToken(str, sda);
	}
}
