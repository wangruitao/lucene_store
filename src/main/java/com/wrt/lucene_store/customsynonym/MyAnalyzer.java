package com.wrt.lucene_store.customsynonym;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;

import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.MaxWordSeg;
import com.chenlb.mmseg4j.analysis.MMSegTokenizer;

public final class MyAnalyzer extends Analyzer {

	private Dictionary dic;
	
	public MyAnalyzer() {
		dic = Dictionary.getInstance("H:/lucenetemp/data");
	}
	
	public MyAnalyzer(Dictionary dic) {
		this.dic = dic;
	}
	
	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		Tokenizer tokenizer = new MMSegTokenizer(new MaxWordSeg(dic));
		return new TokenStreamComponents(tokenizer, new MyTokenFilter(tokenizer));
	}
	
}
