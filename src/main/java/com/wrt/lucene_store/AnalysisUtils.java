package com.wrt.lucene_store;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

public class AnalysisUtils {

	public void displayToken(String str, Analyzer a) {
		TokenStream tokenStream = a.tokenStream("content", new StringReader(str));
		try {
			CharTermAttribute cta = tokenStream.addAttribute(CharTermAttribute.class);
			tokenStream.reset();
			while(tokenStream.incrementToken()) {
				System.out.print("[" + cta + "]");
			}
			System.out.println();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public void displayAllToken(String str, Analyzer a) {
		TokenStream tokenStream = a.tokenStream("content", new StringReader(str));
		try {
			CharTermAttribute cta = tokenStream.addAttribute(CharTermAttribute.class);
			OffsetAttribute oa = tokenStream.addAttribute(OffsetAttribute.class);
			PositionIncrementAttribute pa = tokenStream.addAttribute(PositionIncrementAttribute.class);
			TypeAttribute ta = tokenStream.addAttribute(TypeAttribute.class);
			tokenStream.reset();
			while(tokenStream.incrementToken()) {
				System.out.println("[Char]: " + cta + " [Offset]: start-" + oa.startOffset() + " end-" + oa.endOffset() + 
						" [Position]: " + pa.getPositionIncrement() + " [Type]: " + ta.type());
			}
			System.out.println();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
