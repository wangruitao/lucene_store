package com.wrt.lucene_store.index;

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RegexpQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.util.BytesRef;

public class SearchUtils {

	private DirectoryReader reader = null;

	/**
	 * JavaCC就是一个非常流行的用Java写的解析器生成器。
	 * 
	 * Before choosing to use the provided Query Parser, please consider the
	 * following: 意思就是提醒我们在选择使用QueryParser之前请先仔细考虑下面3个问题。
	 * 
	 * 1.QueryParser是为用户输入文本而设计的而不是你应用程序生成的文本而设计的，什么意思？
	 * 意思就是你要考虑最恶劣的情况，因为用户输入的文本是无法预知的，你不能试图去规范用户输入什么样格式的查询字符串，
	 * 如果你正在准备这么做，请你还是去使用Query api 构建你的Query实现类吧。
	 * 
	 * 2.没有分词的域请直接使用Query
	 * API来构建你的Query实现类，因为QueryParser会使用分词器对用户输入的文本进行分词得到N个Term,然后再根据匹配的，这点你必须清楚。
	 * 
	 * 3.第3点里提示你在设计查询表单时，对应普通的文本框可以直接使用QueryParser,但像日期范围啊搜索关键字啊下拉框里选定某个值或多个值进行限定值时，
	 * 请使用Query  API去做。
	 * 
	 * Term： Term直接用一个单词表示，如“hello” ,多个Term用空格分割，如“hello java”,
	 * 
	 * Field: 可以添加上域，域和Term字符串用冒号隔开，如 title:"The Right Way"，查询多个域用or或者and连接，
	 * 
	 * 如 title:"The Right Way" AND text:go
	 * 
	 * Term字符串你还可以使用通配符进行模糊匹配，如title: ja*a title:ja?a title:ja*等等
	 * 
	 * 你还可以使用~字符开启FuzzyQuery,如title:roam~ or title:roam~0.8
	 * 相似度阀值取值范围是0-1之间，默认值是0.5，
	 * 
	 * QueryParser语法表达式还支持开启PhraseQuery短语查询，如title: "jakarta apache"~10
	 * 
	 * 表示查询title域中包含jakarta和apache字符且jakarta在apache前面且jakarta与apache之间间隔距离在10个间距之内(即<=10)。
	 * 
	 * 当然也支持范围查询，title:[java to php],age[18 to 28]
	 * 
	 * 你也可以单独为某个Term设置权重，如title:java^4,默认权重都为1.
	 * 
	 * Boolean
	 * Operators即boolean操作符即or和and,用来链接多个Term的，如果两个Term仅仅用空格隔开，则默认为or链接的，如title:java^5
	 * and content:lucen*
	 * 
	 * 当然还有+ -字符，表示必须符合和必须不符合即排除的意思，如 +jakarta
	 * lucene,但注意只有一个Term的时候，不能用NOT,比如NOT "jakarta apache"是不合法的。
	 * 
	 * 而这样就可以， "jakarta apache" -"Apache Lucene"表示必须包含 jakarta
	 * apache,但不能包含Apache Lucene.
	 * 
	 * 当or and条件很复杂时，需要限制优先级时可以用()小括号对Term条件进行分组，如 (jakarta OR apache) AND
	 * website
	 * 
	 * 当对某个域的限定值有多个可以用or/and进行链接，也可以用()写在一起，如 title:(+return +"pink
	 * panther")，当然你也可以用and拆成title:return and title:"pink panther"
	 * 
	 * Lucene中需要进行转义的特殊字符包括 ：
	 * 
	 * + - && || ! ( ) { } [ ] ^ " ~ * ? : \
	 * 
	 * QueryParser使用示例如下：
	 * 
	 * QueryParser parser = new QueryParser(fieldName, new IKAnalyzer()); 
	 * Query query = parser.parse(queryString); queryString即为上面解释的那些queryParser查询表达式。
	 * 
	 * 但QueryParser并不能完全代替Query API，它并不能实现所有Query实现类的功能，比如它不支持SpanQuery.
	 * 
	 * 上面说的都是在单个域中查询，当然要在多个域中查询你可以使用or/and进行拼接，如果要在多个域中进行查询，
	 * 你除了用or/and拼接以外，多了另一种选择，它就是MultiFieldQueryParser.我想Google大家都用过，
	 * Google的搜索界面就为我们提供了一个搜索输入框，用户只需要输入搜索关键字即可，
	 * 而不用关心我输入的搜索关键字接下来会在哪些域(Field)里去查找，可能底层我们的索引会建立title,content,category等各种域，
	 * 会依次从这几个域中去匹配是否有符合用户输入的查询关键字，但这些都用户都是透明的，用户也没必要去了解这些，
	 * MultiFieldQueryParser就是用来解决这种多域查询问题的。
	 * 
	 * public MultiFieldQueryParser(String[] fields, Analyzer analyzer, Map<String,Float> boosts) 
	 * { 
	 * 		this(fields, analyzer); this.boosts = boosts;
	 * }
	 * 
	 * 这是MultiFieldQueryParser的构造函数，首先fields毫无疑问就是提供一个域名称数组即你需要在哪些域中进行查询，
	 * analyzer即分词器对象，用户输入的搜索关键字我们需要对其分词，为什么要分词？因为用户输入的搜索关键字可能是一句话，
	 * 比如：我女朋友要跟我分手，我该怎么办，分词后可能得到的只有两个关键字就是女朋友和分手，其他都是停用词被剔除了。
	 * 最后一个boosts参数是一个map对象，是用来设置每个域的权重加权值的，map的key就是域名称，value就是加权值。
	 * boosts参数可以不传入，你传入一个null也行，不传入即表示不进行特殊加权，则默认权重加权值都是1.
	 */
	public void searchQueryParser() {
		try {
			IndexSearcher search = IndexUtils.getIndexSearcher();
			QueryParser parse = new QueryParser("content", new StandardAnalyzer());
			Query query = parse.parse("content");
			query = parse.parse("email:11*");
			TopDocs tops = search.search(query, 20);
			ScoreDoc[] score = tops.scoreDocs;
			int totalHits = tops.totalHits;
			System.out.println("查询总数： " + totalHits);
			Document doc = null;
			for (ScoreDoc sd : score) {
				doc = search.doc(sd.doc);
				System.out.println("id: " + doc.get("id") + " email:" + doc.get("email") + " content:"
						+ doc.get("content") + " num:" + doc.get("num") + " date:" + doc.get("date"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void query() {
		reader = IndexUtils.getReader();
		System.out.println("numDocs: " + reader.numDocs());
		System.out.println("maxDoc: " + reader.maxDoc());
		System.out.println("numDeletedDocs: " + reader.numDeletedDocs());
	}

	public void searchTermQuery() {
		IndexSearcher search = IndexUtils.getIndexSearcher();
		TermQuery query = new TermQuery(new Term("content", "from"));
		try {
			TopDocs tops = search.search(query, 10);
			ScoreDoc[] scoreDocs = tops.scoreDocs;
			Document doc = null;
			for (ScoreDoc sd : scoreDocs) {
				doc = search.doc(sd.doc);
				System.out.println("id: " + doc.get("id") + " email:" + doc.get("email") + " content:"
						+ doc.get("content") + " num:" + doc.get("num") + " date:" + doc.get("date"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void searchTermRangeQuery() {
		IndexSearcher search = IndexUtils.getIndexSearcher();
		TermRangeQuery query = TermRangeQuery.newStringRange("fromNames", "1", "3", false, true);
		try {
			TopDocs tops = search.search(query, 10);
			ScoreDoc[] scoreDocs = tops.scoreDocs;
			int totalHits = tops.totalHits;
			System.out.println("查询总数： " + totalHits);
			Document doc = null;
			for (ScoreDoc sd : scoreDocs) {
				doc = search.doc(sd.doc);
				System.out.println("id: " + doc.get("id") + " email:" + doc.get("email") + " content:"
						+ doc.get("content") + " num:" + doc.get("num") + " date:" + doc.get("date"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void searchPrefixQuery() {
		IndexSearcher search = IndexUtils.getIndexSearcher();
		PrefixQuery query = new PrefixQuery(new Term("email", "3"));
		try {
			TopDocs tops = search.search(query, 10);
			ScoreDoc[] scoreDocs = tops.scoreDocs;
			int totalHits = tops.totalHits;
			System.out.println("查询总数： " + totalHits);
			Document doc = null;
			for (ScoreDoc sd : scoreDocs) {
				doc = search.doc(sd.doc);
				System.out.println("id: " + doc.get("id") + " email:" + doc.get("email") + " content:"
						+ doc.get("content") + " num:" + doc.get("num") + " date:" + doc.get("date"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void searchWildcardQuery() {
		IndexSearcher search = IndexUtils.getIndexSearcher();
		WildcardQuery query = new WildcardQuery(new Term("email", "4*"));
		try {
			TopDocs tops = search.search(query, 10);
			ScoreDoc[] scoreDocs = tops.scoreDocs;
			int totalHits = tops.totalHits;
			System.out.println("查询总数： " + totalHits);
			Document doc = null;
			for (ScoreDoc sd : scoreDocs) {
				doc = search.doc(sd.doc);
				System.out.println("id: " + doc.get("id") + " email:" + doc.get("email") + " content:"
						+ doc.get("content") + " num:" + doc.get("num") + " date:" + doc.get("date"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void searchFuzzyQuery() {
		IndexSearcher search = IndexUtils.getIndexSearcher();
		FuzzyQuery query = new FuzzyQuery(new Term("fromName", "jack"), 2);
		try {
			TopDocs tops = search.search(query, 10);
			ScoreDoc[] scoreDocs = tops.scoreDocs;
			int totalHits = tops.totalHits;
			System.out.println("查询总数： " + totalHits);
			Document doc = null;
			for (ScoreDoc sd : scoreDocs) {
				doc = search.doc(sd.doc);
				System.out.println("id: " + doc.get("id") + " email:" + doc.get("email") + " content:"
						+ doc.get("content") + " num:" + doc.get("num") + " date:" + doc.get("date"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void searchRegexpQuery() {
		IndexSearcher search = IndexUtils.getIndexSearcher();
		RegexpQuery query = new RegexpQuery(new Term("content", "11.*"), 2);
		try {
			TopDocs tops = search.search(query, 10);
			ScoreDoc[] scoreDocs = tops.scoreDocs;
			int totalHits = tops.totalHits;
			System.out.println("查询总数： " + totalHits);
			Document doc = null;
			for (ScoreDoc sd : scoreDocs) {
				doc = search.doc(sd.doc);
				System.out.println("id: " + doc.get("id") + " email:" + doc.get("email") + " content:"
						+ doc.get("content") + " num:" + doc.get("num") + " date:" + doc.get("date"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void searchNumericRangeQuery() {
		IndexSearcher search = IndexUtils.getIndexSearcher();
		Query query = NumericRangeQuery.("num", new BytesRef(1), new BytesRef(3), true, false);
		try {
			TopDocs tops = search.search(query, 10);
			ScoreDoc[] scoreDocs = tops.scoreDocs;
			int totalHits = tops.totalHits;
			System.out.println("查询总数： " + totalHits);
			Document doc = null;
			for (ScoreDoc sd : scoreDocs) {
				doc = search.doc(sd.doc);
				System.out.println("id: " + doc.get("id") + " email:" + doc.get("email") + " content:"
						+ doc.get("content") + " num:" + doc.get("num") + " date:" + doc.get("date"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void searchBooleanQuery() {
		IndexSearcher search = IndexUtils.getIndexSearcher();
		BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
		try {
			Query query = new WildcardQuery(new Term("email", "33*"));
			Query query1 = new PrefixQuery(new Term("fromName", "iru"));
			booleanQuery.add(query, Occur.MUST).add(query1, Occur.MUST_NOT);
			TopDocs tops = search.search(booleanQuery.build(), 10);
			ScoreDoc[] scoreDocs = tops.scoreDocs;
			int totalHits = tops.totalHits;
			System.out.println("查询总数： " + totalHits);
			Document doc = null;
			for (ScoreDoc sd : scoreDocs) {
				doc = search.doc(sd.doc);
				System.out.println("id: " + doc.get("id") + " --email:" + doc.get("email") + " --fromName:"
						+ doc.get("fromName") + " --content:" + doc.get("content") + " --num:" + doc.get("num")
						+ " --date:" + doc.get("date"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * PhraseQuery和SpanQuery，那我就随带扯一扯这两个Query的区别吧，我估计这是很多初学Lucene者比较困惑的问题，
	 * 两个Query都能根据多个Term进行查询，但PhraseQuery只能按照查询短语在文档中出现的顺序进行匹配，而不能颠倒过来匹配，
	 * 比如你查询quick lazy,而索引中出现的是xxxxxxxxlazy qucikxxxxxxx,那PhraseQuery就没法匹配到了，
	 * 这时候你就只能使用SpanQuery了，SpanQuery的inorder参数允许你设置是否按照查询短语在文档中出现的顺序进行匹配，
	 * 以及是否允许有重叠，什么叫是否允许重叠？举个例子说明，假如域的值是这样的：“ jumps over extremely very lazy
	 * broxn dog ”, 而你的查询短语是“dog
	 * over”,因为索引中dog在over后面，而你提供的查询短语中dog却在over前面，这与它在索引文档中出现的顺序是颠倒的，
	 * 这时候你就不能使用PhraseQuery,PhraseQuery只能按出现顺序匹配，这种颠倒顺序匹配无法用PhraseQuery实现，
	 * 把SpanQuery的inOrder设为false,就可以无视顺序了，即只要你能按slop规定的步数内匹配到dog over或者 over
	 * dog都算匹配成功。 而如果inOrder设为true,意思就是你只能在规定步数内匹配到dog over,而匹配到over
	 * dog不算，并且匹配过程中不能有重叠。什么叫重叠？ 要得到dog
	 * over，那只能把over往右移动6步，可是它跨过了dog了，即dog重叠了，意思就是你只能在两者之间移动，不能跨越两者的边界进行匹配。
	 * 我解释的不知道你们能看的明白不？注意两者的slop都是最多需要移动几步的意思即在规定步数内达到你想要的情况。
	 * 
	 * 备注：不知道slop的步数，可以通过luke看content分词情况
	 */
	public void searchPhraseQuery() {
		IndexSearcher search = IndexUtils.getIndexSearcher();
		try {
			PhraseQuery.Builder builder = new PhraseQuery.Builder();
			builder.add(new Term("content", "email"));
			builder.add(new Term("content", "11_name"));
			builder.setSlop(3);
			TopDocs tops = search.search(builder.build(), 10);
			ScoreDoc[] scoreDocs = tops.scoreDocs;
			int totalHits = tops.totalHits;
			System.out.println("查询总数： " + totalHits);
			Document doc = null;
			for (ScoreDoc sd : scoreDocs) {
				doc = search.doc(sd.doc);
				System.out.println("id: " + doc.get("id") + " --email:" + doc.get("email") + " --fromName:"
						+ doc.get("fromName") + " --content:" + doc.get("content") + " --num:" + doc.get("num")
						+ " --date:" + doc.get("date"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void searchPage(String fieldName, String searchContent, Integer pageNumber, Integer pageSize) {
		try {
			IndexSearcher search = IndexUtils.getIndexSearcher();
			QueryParser parse = new QueryParser(fieldName, new StandardAnalyzer());
			Query query = parse.parse(searchContent);
			int totalNumber = pageNumber * pageSize;
			TopDocs tops = search.search(query, totalNumber);
			ScoreDoc[] score = tops.scoreDocs;
			int totalHits = tops.totalHits;
			System.out.println("查询总数： " + totalHits);
			Document doc = null;
			for (int i=(pageNumber-1)*pageSize; i<totalHits; i++) {
				doc = search.doc(score[i].doc);
				System.out.println("id: " + doc.get("id") + " email:" + doc.get("email") + " content:"
						+ doc.get("content") + " num:" + doc.get("num") + " date:" + doc.get("date"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
