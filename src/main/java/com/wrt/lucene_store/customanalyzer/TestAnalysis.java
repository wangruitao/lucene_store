package com.wrt.lucene_store.customanalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.junit.Test;

import com.wrt.lucene_store.tokeninfo.TokenInfoUtils;

public class TestAnalysis {

	@Test
	public void testDisplayToken() {
		Analyzer sda = new StandardAnalyzer();
		Analyzer spa = new SimpleAnalyzer();
		Analyzer wa = new WhitespaceAnalyzer();
		Analyzer sta = new StopAnalyzer();
		
		TokenInfoUtils au = new TokenInfoUtils();
		String str = "then at the heart of the most lax, alert, and most low awareness, and left it godsend failed.";
		str = "是中国人啊";
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
		
		TokenInfoUtils au = new TokenInfoUtils();
		au.displayAllToken(str, sda);
	}
}
