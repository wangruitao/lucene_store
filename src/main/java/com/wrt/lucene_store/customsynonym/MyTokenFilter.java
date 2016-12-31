package com.wrt.lucene_store.customsynonym;

import java.io.IOException;
import java.util.Stack;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

public class MyTokenFilter extends TokenFilter {

	private CharTermAttribute ca;
	private PositionIncrementAttribute  pia;
	private Stack<String> sameStack;
	private State state;
	
	
	protected MyTokenFilter(TokenStream input) {
		super(input);
		ca = input.addAttribute(CharTermAttribute.class);
		pia = input.addAttribute(PositionIncrementAttribute.class);
	}
	
	@Override
	public boolean incrementToken() throws IOException {
		if(!sameStack.isEmpty()) {
			//将元素出栈,并获取同义词  
			String str = sameStack.pop();
			//还原状态 
			this.restoreState(state);
			//先清空再添加 
			ca.setEmpty();
			ca.append(str);
			//设置位置为0,表示同义词  
			pia.setPositionIncrement(0);
			return true;
		}
		if(!input.incrementToken()) {
			return false;
		}
		 //如词汇中有同义词,捕获当前状态  
		if(getSameWord(ca.toString())) {
			state = this.captureState();
		}
		return true;  
	}
	
	public boolean getSameWord(String word) {
		sameStack.clear();
		if(word.equals("我")) {
			sameStack.push("俺");
			sameStack.push("人家");
			sameStack.push("本人");
		}
		if(word.equals("中国")) {
			sameStack.push("中华");
			sameStack.push("华夏");
		}
		return !sameStack.isEmpty();
	}

}
