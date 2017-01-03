package com.wrt.lucene_store;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.junit.Test;

import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.analysis.SimpleAnalyzer;
import com.wrt.lucene_store.chinese.ChineseAnalyzerUtils;
import com.wrt.lucene_store.customsynonym.MyAnalyzer;

public class TestChineseAnalyzer {

	/**
	 * 自定义字典路径
	 */
	@Test
	public void testMMsegAnalyzer() {
		
		ChineseAnalyzerUtils au = new ChineseAnalyzerUtils();
		Analyzer cjk = new SimpleAnalyzer(Dictionary.getInstance("H:\\lucenetemp\\data"));
		String str = "我来自中国内蒙古赤峰市松山区";
		au.displayToken(str, cjk);
	}
	
	/**
	 * 自定义同义词
	 */
	@Test
	public void testCustomSynonymAnalyzer() {
		ChineseAnalyzerUtils au = new ChineseAnalyzerUtils();
		MyAnalyzer ma = new MyAnalyzer();
		String str = "我来自中国内蒙古赤峰市";
		au.displayToken(str, ma);
	}
	
	/**
	 * CJKAnalyzer Lucene contrib中附带的二元分词
	 */
	@Test
	public void testCJKAnalyzer() {
		ChineseAnalyzerUtils au = new ChineseAnalyzerUtils();
		CJKAnalyzer cjk = new CJKAnalyzer();
		String str = "我来自中国内蒙古赤峰市";
		au.displayAllToken(str, cjk);
	}
	
	/**
	 * lucene 自带中文分词
	 */
	@Test
	public void testStandardAnalyzer() {
		ChineseAnalyzerUtils au = new ChineseAnalyzerUtils();
		StandardAnalyzer sa = new StandardAnalyzer();
		String str = "我来自中国内蒙古赤峰市";
		au.displayAllToken(str, sa);
	}
}
