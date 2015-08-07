package pers.textClassifier.LinShudu;
import java.io.*;


/*

Porter stemmer in Java. The original paper is in

    Porter, 1980, An algorithm for suffix stripping, Program, Vol. 14,
    no. 3, pp 130-137,

See also http://www.tartarus.org/~martin/PorterStemmer

History:

Release 1

Bug 1 (reported by Gonzalo Parra 16/10/99) fixed as marked below.
The words 'aed', 'eed', 'oed' leave k at 'a' for step 3, and b[k-1]
is then out outside the bounds of b.

Release 2

Similarly,

Bug 2 (reported by Steve Dyrdahl 22/2/00) fixed as marked below.
'ion' by itself leaves j = -1 in the test for 'ion' in step 5, and
b[j] is then outside the bounds of b.

Release 3

Considerably revised 4/9/00 in the light of many helpful suggestions
from Brian Goetz of Quiotix Corporation (brian@quiotix.com).

Release 4

*/


/**波特词干算法 
 * 位于分类 自然语言处理
 * 在英语中，一个单词常常是另一个单词的“变种”，如：happy=>happiness，这里happy叫做happiness的词干（stem）。
 * 在信息检索系统中，我们常常做的一件事，就是在Term规范化过程中，提取词干（stemming），即除去英文单词分词变换形式的结尾。
 * 应用最为广泛的、中等复杂程度的、基于后缀剥离的词干提取算法是波特词干算法，也叫波特词干器（Porter Stemmer）。
 * 详见官方网站。比较热门的检索系统包括Lucene、Whoosh等中的词干过滤器就是采用的波特词干算法。
 * 
 * 马丁.波特博士（Dr. Martin Porter）于1979年，在英国剑桥大学，计算机实验室，发明了波特词干算法。
 * 波特词干算法当时是作为一个大型IR项目的一部分被提出的。它的原始论文为：
 * C.J. van Rijsbergen, S.E. Robertson and M.F. Porter, 1980. 
 * New models in probabilistic information retrieval. 
 * London: British Library. 
 * (British Library Research and Development Report, no. 5587).
 * 最初的波特词干提取算法是使用BCPL语言编写的。
 * 作者在其网站上公布了各种语言的实现版本，其中C语言的版本是作者编写的最权威的版本。
 * 波特词干器适用于涉及到提取词干的IR研究工作，其实验结果是可重复的，波特词干器的输出结果是确定性的，不是随机的。
 * （还有基于随机的高级词干提取算法，虽然会更准确，但同时也更加复杂）。
 */

/**
 * Stemmer, implementing the Porter Stemming Algorithm
 *
 * The Stemmer class transforms a word into its root form. The input word can be
 * provided a character at time (by calling add()), or at once by calling one of
 * the various stem(something) methods.
 * 在实际处理中，需要分六步走。首先，我们先定义一个Stemmer类。
 */

class Stemmer {
	private char[] b;
	private int i, /* offset into b *//* b中的元素位置（偏移量） */
			i_end, /* offset to end of stemmed word *//* 要抽取词干单词的结束位置 */
			j, k;
	private static final int INC = 50; /* 随着b的大小增加数组要增长的长度（防止溢出） */

	
	/* unit of size whereby b is increased */
	public Stemmer() {
		b = new char[INC];
		i = 0;
		i_end = 0;
	}/*这里，b是一个数组，用来存待词干提取的单词（以char的形式）。这里的变量k会随着词干抽取而变化。*/

	
	/**
	 * Add a character to the word being stemmed. When you are finished adding
	 * characters, you can call stem(void) to stem the word.
	 * 接着，我们要添加单词来进行处理：增加一个字符到要存放待处理的单词的数组。
	 * 添加完字符时， 可以调用stem(void)方法来进行抽取词干的工作。
	 */
	public void add(char ch) {
		if (i == b.length) {
			char[] new_b = new char[i + INC];
			for (int c = 0; c < i; c++)
				new_b[c] = b[c];
			b = new_b;
		}
		b[i++] = ch;
	}

	
	/**
	 * Adds wLen characters to the word being stemmed contained in a portion of
	 * a char[] array. This is like repeated calls of add(char ch), but faster.
	 * 增加wLen长度的字符数组到存放待处理的单词的数组b。
	 */
	public void add(char[] w, int wLen) {
		if (i + wLen >= b.length) {
			char[] new_b = new char[i + wLen + INC];
			for (int c = 0; c < i; c++)
				new_b[c] = b[c];
			b = new_b;
		}
		for (int c = 0; c < wLen; c++)
			b[i++] = w[c];
	}

	
	/**
	 * After a word has been stemmed, it can be retrieved by toString(), or a
	 * reference to the internal buffer can be retrieved by getResultBuffer and
	 * getResultLength (which is generally more efficient.)
	 */
	public String toString() {
		return new String(b, 0, i_end);
	}

	/**
	 * Returns the length of the word resulting from the stemming process.
	 */
	public int getResultLength() {
		return i_end;
	}

	/**
	 * Returns a reference to a character buffer containing the results of the
	 * stemming process. You also need to consult getResultLength() to determine
	 * the length of the result.
	 */
	public char[] getResultBuffer() {
		return b;
	}

	/**
	 * 接下来，是一系列工具函数。
	 * cons(i)：参数i：int型；返回值bool型。当i为辅音时，返回真；否则为假。 
	 * cons(i) is true <=> b[i] is a consonant.
	 * cons(i) 为真 <=> b[i] 是一个辅音 
	 */
	private final boolean cons(int i) {
		switch (b[i]) {
		case 'a':
		case 'e':
		case 'i':
		case 'o':
		case 'u':
			return false;
		case 'y': //y开头，为辅；否则看i-1位，如果i-1位为辅，y为元，反之亦然。
			return (i == 0) ? true : !cons(i - 1);
		default:
			return true;
		}
	}

	/**
	 * m() measures the number of consonant sequences between 0 and j. if c is a
	 * consonant sequence and v a vowel sequence, and <..> indicates arbitrary
	 * presence,
	 * <c><v> 			gives 0 
	 * <c>vc<v> 		gives 1 
	 * <c>vcvc<v> 		gives 2 
	 * <c>vcvcvc<v> 	gives 3
	 * ....
	 * 
	 * m()：返回值：int型。表示单词b介于0和j之间辅音序列的个度。现假设c代表辅音序列，而v代表元音序列。
	 * <..>表示任意存在。于是有如下定义； 
	 * <c><v>          	结果为 0 
	 * <c>vc<v>       	结果为 1 
	 * <c>vcvc<v>    	结果为 2 
	 * <c>vcvcvc<v> 	结果为 3 
	 * .... 
	 */
	private final int m() {
		int n = 0;//辅音序列的个数，初始化
		int i = 0;//偏移量
		while (true) {
			if (i > j)
				return n;//如果超出最大偏移量，直接返回n
			if (!cons(i))
				break;//如果是元音，中断
			i++;//辅音移一位，直到元音的位置
		}
		i++;//移完辅音，从元音的第一个字符开始
		while (true) {//循环计算vc的个数
			while (true) {//循环判断v
				if (i > j)
					return n;
				if (cons(i))
					break;//出现辅音则终止循环
				i++;
			}
			i++;
			n++;
			while (true) {//循环判断c
				if (i > j)
					return n;
				if (!cons(i))
					break;
				i++;
			}
			i++;
		}
	}

	/** 
	* vowelinstem() is true <=> 0,...j contains a vowel 
	* vowelinstem() 为真 <=> 0,...j 包含一个元音   
	* vowelinstem()：返回值：bool型。从名字就可以看得出来，表示单词b介于0到i之间是否存在元音。 
	*/
	private final boolean vowelinstem() {
		int i;
		for (i = 0; i <= j; i++)
			if (!cons(i))
				return true;
		return false;
	}

	/**
	* doublec(j) is true <=> j,(j-1) contain a double consonant. 
	* doublec(j) 为真 <=> j,(j-1) 包含两个一样的辅音  
	* doublec(j)：参数j：int型；返回值bool型。这个函数用来表示在j和j-1位置上的两个字符是否是相同的辅音。 
	*/
	private final boolean doublec(int j) {
		if (j < 1)
			return false;
		if (b[j] != b[j - 1])
			return false;
		return cons(j);
	}

	/**
	 * cvc(i) is true <=> i-2,i-1,i has the form consonant - vowel - consonant
	 * and also if the second c is not w,x or y. this is used when trying to
	 * restore an e at the end of a short word. e.g.
	 * cav(e), lov(e), hop(e), crim(e), but snow, box, tray.
	 * 
	 * cvc(i)：参数i：int型；返回值bool型。
	 * cvc(i) 为真 <=> i-2,i-1,i 有形式： 辅音 - 元音 - 辅音并且第二个c不是 w,x 或者 y. 
	 * 这个用来处理以e结尾的短单词。 e.g.cav(e), lov(e), hop(e), crim(e), 
	 * 而不是snow, box, tray.
	 */
	private final boolean cvc(int i) {
		if (i < 2 || !cons(i) || cons(i - 1) || !cons(i - 2))
			return false;
		{
			int ch = b[i];
			if (ch == 'w' || ch == 'x' || ch == 'y')
				return false;
		}
		return true;
	}

	/**
	 * ends(s)：参数：String；返回值：bool型。顾名思义，判断b是否以s结尾。 
	 * @param s
	 * @return bool
	 */
	private final boolean ends(String s) {
		int l = s.length();
		int o = k - l + 1;
		if (o < 0)
			return false;
		for (int i = 0; i < l; i++)
			if (b[o + i] != s.charAt(i))
				return false;
		j = k - l;
		return true;
	}

	/**
	 * setto(s) sets (j+1),...k to the characters in the string s, readjusting  k.
	 * setto(s) 设置 (j+1),...k 到s字符串上的字符, 并且调整k值
	 */
	private final void setto(String s) {
		int l = s.length();
		int o = j + 1;
		for (int i = 0; i < l; i++)
			b[o + i] = s.charAt(i);
		k = j + l;
	}

	/**
	 * r(s) is used further down.
	 * r(s)：参数：String；void类型。在m()>0的情况下，调用setto(s)。 
	 * @param s
	 */
	private final void r(String s) {
		if (m() > 0)
			setto(s);
	}

	/**
	 * step1() gets rid of plurals and -ed or -ing. e.g.
	 * 接下来，就是分六步来进行处理的过程。
	 * 第一步，处理复数，以及ed和ing结束的单词。比如：
	 * caresses -> caress 
	 * ponies -> poni 
	 * ties -> ti 
	 * caress -> caress 
	 * cats -> cat
	 * feed -> feed 
	 * agreed -> agree 
	 * disabled -> disable
	 * matting -> mat 
	 * mating -> mate 
	 * meeting -> meet 
	 * milling -> mill 
	 * messing ->mess
	 * meetings -> meet
	 */
	private final void step1() {
		if (b[k] == 's') {
			if (ends("sses"))  			k -= 2; 	// 以“sses结尾”
			else if (ends("ies"))  		setto("i");	// 以ies结尾，置为i
			else if (b[k - 1] != 's')  	k--; 		// 两个s结尾不处理
		}
		if (ends("eed")) {	if (m() > 0)	k--;} 	// 以“eed”结尾，当m>0时，左移一位
		else if ((ends("ed") || ends("ing")) && vowelinstem()) {
			k = j;
			if (ends("at"))	setto("ate");
			else if (ends("bl"))	setto("ble");
			else if (ends("iz"))	setto("ize");
			else if (doublec(k)){					// 如果有两个相同辅音
				k--;
				int ch = b[k];
				if (ch == 'l' || ch == 's' || ch == 'z')	k++;
			} else if (m() == 1 && cvc(k))	setto("e");
		}
	}
	
	/* step2() turns terminal y to i when there is another vowel in the stem. */
	/*第二步，如果单词中包含元音，并且以y结尾，将y改为i。代码很简单：*/
	private final void step2() {
		if (ends("y") && vowelinstem())
			b[k] = 'i';
	}

	/**
	 * step3() maps double suffices to single ones. so -ization ( = -ize plus
	 * -ation) maps to -ize etc. note that the string before the suffix must
	 * give m() > 0.
	 * 第三步，将双后缀的单词映射为单后缀。
	 * step3() 将双后缀的单词映射为单后缀。 所以 -ization ( = -ize 加上
	 * -ation) 被映射到 -ize 等等。 注意在去除后缀之前必须确
	 * m() > 0.
	 */
	private final void step3() { 
		if (k == 0) return;  switch (b[k-1]){
		    case 'a': if (ends("ational")) { r("ate"); break; }
		              if (ends("tional")) { r("tion"); break; }
		              break;
		    case 'c': if (ends("enci")) { r("ence"); break; }
		              if (ends("anci")) { r("ance"); break; }
		              break;
		    case 'e': if (ends("izer")) { r("ize"); break; }
		              break;
		    case 'l': if (ends("bli")) { r("ble"); break; }
		              if (ends("alli")) { r("al"); break; }
		              if (ends("entli")) { r("ent"); break; }
		              if (ends("eli")) { r("e"); break; }
		              if (ends("ousli")) { r("ous"); break; }
		              break;
		    case 'o': if (ends("ization")) { r("ize"); break; }
		              if (ends("ation")) { r("ate"); break; }
		              if (ends("ator")) { r("ate"); break; }
		              break;
		    case 's': if (ends("alism")) { r("al"); break; }
		              if (ends("iveness")) { r("ive"); break; }
		              if (ends("fulness")) { r("ful"); break; }
		              if (ends("ousness")) { r("ous"); break; }
		              break;
		    case 't': if (ends("aliti")) { r("al"); break; }
		              if (ends("iviti")) { r("ive"); break; }
		              if (ends("biliti")) { r("ble"); break; }
		              break;
		    case 'g': if (ends("logi")) { r("log"); break; }
	    } 
	}
	/* step4() deals with -ic-, -full, -ness etc. similar strategy to step3. */
	/* 第四步，处理-ic-，-full，-ness等等后缀。和步骤3有着类似的处理。   */
	private final void step4() { 
		switch (b[k])
		{
		    case 'e': if (ends("icate")) { r("ic"); break; }
		              if (ends("ative")) { r(""); break; }
		              if (ends("alize")) { r("al"); break; }
		              break;
		    case 'i': if (ends("iciti")) { r("ic"); break; }
		              break;
		    case 'l': if (ends("ical")) { r("ic"); break; }
		              if (ends("ful")) 	{ r(""); break; }
		              break;
		    case 's': if (ends("ness")) { r(""); break; }
		              break;
		} 
	}
	
	/* step5() takes off -ant, -ence etc., in context <c>vcvc<v>. */
	/*第五步，在<c>vcvc<v>情形下，去除-ant，-ence等后缀。   */
	private final void step5(){   
		if (k == 0) return;  
		switch (b[k-1]){ 
		   case 'a': if (ends("al")) 	break; return;
	       case 'c': if (ends("ance")) 	break;
	                 if (ends("ence")) 	break; return;
	       case 'e': if (ends("er")) 	break; return;
	       case 'i': if (ends("ic")) 	break; return;
	       case 'l': if (ends("able")) 	break;
	                 if (ends("ible")) 	break; return;
	       case 'n': if (ends("ant")) 	break;
	                 if (ends("ement")) break;
	                 if (ends("ment")) 	break;
	                 /* element etc. not stripped before the m */
	                 if (ends("ent")) 	break; return;
	       case 'o': if (ends("ion") && j >= 0 && 
	    		   			(b[j] == 's' || b[j] == 't')) 
	    	   							break;	
	       			 /* j >= 0 fixes Bug 2 */
	                 if (ends("ou")) 	break; return;
	                 /* takes care of -ous */
	       case 's': if (ends("ism")) 	break; return;
	       case 't': if (ends("ate")) 	break;
	                 if (ends("iti")) 	break; return;
	       case 'u': if (ends("ous")) 	break; return;
	       case 'v': if (ends("ive")) 	break; return;
	       case 'z': if (ends("ize")) 	break; return;
	       default: return;
	    }
	    if (m() > 1) k = j;
	}
	
	/* step6() removes a final -e if m() > 1. */
	/*第六步，也就是最后一步，在m()>1的情况下，移除末尾的“e”。*/
	private final void step6() {
		j = k;
		if (b[k] == 'e') {
			int a = m();
			if (a > 1 || a == 1 && !cvc(k - 1))
				k--;
		}
		if (b[k] == 'l' && doublec(k) && m() > 1)
			k--;
	}

	
	/**
	 * Stem the word placed into the Stemmer buffer through calls to add().
	 * Returns true if the stemming process resulted in a word different from
	 * the input. You can retrieve the result with
	 * getResultLength()/getResultBuffer() or toString().
	 *
	 *在了解了步骤之后，我们写一个stem()方法，来完成得到词干的工作。
	 * 通过调用add()方法来讲单词放入词干器数组b中
	 * 可以通过下面的方法得到结果：
	 * getResultLength()/getResultBuffer() or toString().
	 */
	public void stem() {
		k = i - 1;
		if (k > 1) {step1();step2();step3();step4();step5();step6();}
		i_end = k + 1;
		i = 0;
	}

	/**
	 * Test program for demonstrating the Stemmer. It reads text from a a list
	 * of files, stems each word, and writes the result to standard output. Note
	 * that the word stemmed is expected to be in lower case: forcing lower case
	 * must be done outside the Stemmer class. Usage: Stemmer file-name
	 * file-name ...
	 * @throws IOException
	 * @param stemFile 		待stemming的文本文件路径组成的字符串数组
	 * 对一系列文本中的所有单词stemming
	 * 最后要提醒的就是，传入的单词必须是小写。关于Porter Stemmer的实现，就看到这里。
	 * 更多内容请参考官方网站。另外，波特词干算法有第二个版本，它的处理结果要比文中所介绍的算法准确度高.
	 * 但是，相应地也就更复杂，消耗的时间也就更多。详细参考官方网站The Porter2 stemming algorithm。
	 */
	public static String stemming(String stemFile) throws IOException {
		//System.out.println("Begin stemming. ");
		Stemmer stemmer = new Stemmer();
		String word = new String(stemFile);
		
		word = word.toLowerCase();
		for(int index = 0; index < word.length(); index++){
			stemmer.add(word.charAt(index));
		}
		stemmer.stem();
		return stemmer.toString();
		
	}
}