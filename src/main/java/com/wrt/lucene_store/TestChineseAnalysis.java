package com.wrt.lucene_store;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.junit.Test;

import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.analysis.SimpleAnalyzer;
import com.wrt.lucene_store.chinese.ChineseAnalyzerUtils;
import com.wrt.lucene_store.customsynonym.MyAnalyzer;

public class TestChineseAnalysis {

	@Test
	public void testDisplayToken() {
		
		ChineseAnalyzerUtils au = new ChineseAnalyzerUtils();
		Analyzer cjk = new SimpleAnalyzer(Dictionary.getInstance("H:\\lucenetemp\\data"));
		String str = "我来自中国内蒙古赤峰市松山区";
		au.displayToken(str, cjk);
	}
	
	@Test
	public void testCustomSynonym() {
		ChineseAnalyzerUtils au = new ChineseAnalyzerUtils();
		MyAnalyzer ma = new MyAnalyzer();
		String str = "我来自中国内蒙古赤峰市";
		au.displayToken(str, ma);
	}
	
	@Test
	public void testDisplayAllToken() {
		
		ChineseAnalyzerUtils au = new ChineseAnalyzerUtils();
		CJKAnalyzer cjk = new CJKAnalyzer();
		String str = "我来自中国内蒙古赤峰市";
		au.displayAllToken(str, cjk);
	}
	
	@Test
	public void testIndex() {
		
		ChineseAnalyzerUtils au = new ChineseAnalyzerUtils();
		String str = "我来自中国内蒙古赤峰市";
		au.index(str);
	}
	
}
