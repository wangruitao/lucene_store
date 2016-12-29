package com.wrt.lucene_store;

import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseTokenizer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.util.CharArraySet;

/**
 * 自定义StopAnalyzer
 * @author wrt
 *
 */
public class MyStopAnalyzer extends Analyzer {

	private Set stops;
	
	public MyStopAnalyzer(String[] sws) {
		stops = StopFilter.makeStopSet(sws, true);
	}
	
	@SuppressWarnings("unchecked")
	public MyStopAnalyzer() {
		stops = new HashSet<String>();
		stops.addAll(StopAnalyzer.ENGLISH_STOP_WORDS_SET);
	}

	/**
	 * 组合模式
	 * TokenStream 是 Tokenizer和 Filter的组合
	 */
	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		 Tokenizer source = new LowerCaseTokenizer();
		 return new TokenStreamComponents(source, new StopFilter(source, new CharArraySet(stops, true)));
	}
	
}
