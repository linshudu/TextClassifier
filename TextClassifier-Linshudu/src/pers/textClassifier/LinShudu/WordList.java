package pers.textClassifier.LinShudu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * NewsGroups	生成训练样例单词表
 * @author 		linshudu
 * @qq 			617486329 
 */
public class WordList {
	private String srcDir;
	private String targetPath;
	private long start;
	private double degree = 1;

	/**创建单词表文件
	 * @param fileDir 		预处理好的newsgroup文件目录
	 * @param srcFile		newsgroup预处理后的目标文件
	 * @param groupFiles	newsgroup新闻类文件
	 * @param newsFiles		newsgroup每个类下的新闻文本资料（已预处理过）
	 * @param targetPath	单词表集合地址
	 * @throws IOException 
	 */
	private void creatWordListFile(String fileDir) throws IOException{
		File srcFile = new File(fileDir);
		File[] groupFiles = srcFile.listFiles();
		this.srcDir = srcFile.getCanonicalPath().replace("\\", "/");
		File target = new File(fileDir + "/../WordList");
		if(!target.exists())
			target.mkdirs();
		this.targetPath = target.getCanonicalPath().replace("\\", "/");
		for(File group : groupFiles){
			System.out.println("\tClass: " + group.getName() + ".txt");
			//判断是否是目录文件，是的话找出目录下子文件
			if (group.isDirectory()) {
				generateWordList(group);
			}
		}
	}
	
	/**
	 * generateWordList	统计每个词的总的出现次数，返回的词汇构成最终的属性词典
	 * @param group		新闻类文件
	 * @param news		新闻文本文件
	 */
	private void generateWordList(File group) throws IOException{
		File[] newsFiles = group.listFiles();
		HashMap<String,Double> wordMap = new HashMap<>();
		wordMap.clear();
		FileWriter wordFW = new FileWriter(
				this.targetPath + "/" + group.getName() + ".txt");
		for (File news : newsFiles) {
			String word;
			double count;
			BufferedReader wordBR = new BufferedReader(
					new FileReader(news.getCanonicalFile()) );
			while( (word = wordBR.readLine()) != null ){
				if(!word.isEmpty() && wordMap.containsKey(word)){
					count = wordMap.get(word) + 1;
					wordMap.put(word, count);
				}
				else 
					wordMap.put(word, 1.0);
			}
			wordBR.close();
		}
		//返回出现 degree 次以上的单词
		Set<String> key = wordMap.keySet();
		Iterator<String> it = key.iterator() ;
		for( ; it.hasNext();){
			String s = (String) it.next();
			if(wordMap.get(s) > degree)
				 wordFW.write(s + " " + wordMap.get(s) + "\r\n");
		}
		wordFW.flush();
		wordFW.close();
	}
	
	/**
	 * 生成训练样例集合程序主入口
	 * @param wordList	生成单词表对象
	 * @param fileDir	根据源文件得到单词表
	 */
	public String main(String fileDir) throws IOException {
		this.start = System.currentTimeMillis();
		System.out.println("step 2: Get word list.");
		WordList wordList = new WordList();
		wordList.creatWordListFile(fileDir);
		System.out.println("\t源文件->" + wordList.srcDir );
		System.out.println("\t目标->"+wordList.targetPath);
		System.out.println("\t\t\t\tTake time: " 
				+ (float)(System.currentTimeMillis() - this.start)/1000 
				+ "  seconds");
		return wordList.targetPath;
		
	}
}
