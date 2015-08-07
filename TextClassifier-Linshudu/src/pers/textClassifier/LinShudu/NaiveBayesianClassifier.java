package pers.textClassifier.LinShudu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;

/**利用朴素贝叶斯算法对newsgroup文档集做分类，采用十组交叉测试取平均值
 * 采用多项式模型,stanford信息检索导论课件上面言多项式模型比伯努利模型准确度高
 * 类条件概率P(tk|c)=(类c 下单词tk 在各个文档中出现过的次数之和+1)/(类c下单词总数+|V|)
 * |V| 训练样本中不重复特征词总数
 * @author 	ShuduLin
 * @qq 		617486329
 * @version 1.0
 */
public class NaiveBayesianClassifier {

	private long start;
	private BigDecimal accuracy = new BigDecimal("0");
	private BigDecimal accuracyAvg = new BigDecimal("0");
	private double totalWordNum = 0.0;
	private double totalSpecialNum = 0.0;//训练样本中不重复特征词总数
	private String line;
	private String[] words;
	private HashMap<String,HashMap<String,Double>> wordListMap = new HashMap<>();
	private HashMap<String,Double> cateNumMap = new HashMap<>();
	private HashMap<String,Double> proMap = new HashMap<>();
	private File[] wordLists;
	
	/**获得训练集的信息
	 * wordList		为 wordLists 下的所有单词表，text 为单一单词表
	 * wordListMap	所有类的单词表集合，key 为类名，value 为单词表map
	 * wordMap 		一个类的单词表集合，key 为单词，value 为次数
	 * cateNumMap 	类 c 下单词总数
	 * proMap 		类 c 先验概率
	 */
	private void getTrainInfo(String wordListPath) throws FileNotFoundException, IOException{
		File wordListFile = new File(wordListPath);
		this.wordLists = wordListFile.listFiles();
		for (File wordList : wordLists) {
			HashMap<String, Double> wordMap = new HashMap<>();
			double cateWordNum=0;
			totalSpecialNum += wordList.length();
			wordMap.clear();
			BufferedReader br = new BufferedReader(
					new FileReader(wordList.getCanonicalPath()));
			while ((line = br.readLine()) != null) {
				words = line.split(" ");
				if(Double.parseDouble(words[1]) > 3){
					wordMap.put(words[0], Double.parseDouble(words[1]));
					totalWordNum += Double.parseDouble(words[1]);//训练样本中不重复特征词总数
					cateWordNum += Double.parseDouble(words[1]);
				}
			}
		br.close();
			cateNumMap.put(wordList.getName(), cateWordNum);
			wordListMap.put(wordList.getName(), wordMap);
		}
		System.out.println("\t训练文档特征词总数:\t"+totalWordNum);
		for(File wordList : wordLists){
			double cateWordNum = cateNumMap.get(wordList.getName());
			StringBuilder wordListName = new StringBuilder(
						wordList.getName().replace(".txt", ""));
			while(wordListName.length() < 26)
				wordListName = wordListName.append(" "); 
			System.out.print("\t训练类  "+ wordListName +"特征词数："+cateWordNum);
			cateWordNum /= totalWordNum;
			proMap.put(wordList.getName(), cateWordNum);
			DecimalFormat df = new DecimalFormat("00.000");
			System.out.println("\t先验概率:"+df.format(cateWordNum*100)+"%");
		}
	}
	
	/**对测试集进行贝叶斯分类，并计算正确率
	 * @param testFile	测试集文本
	 * @param text		测试文本
	 * @param rightRate	正确率
	 * @param errorRate	错误率
	 */
	private void testClassifier(File testFile)throws IOException{
		StringBuilder wordListName = new StringBuilder(
				testFile.getName().replace(".txt", ""));
		while(wordListName.length() < 26)
			wordListName = wordListName.append(" "); 
		System.out.print("\t测试类  "+ wordListName + "的测试结果,");
		File[] texts = testFile.listFiles();
		double rightRate = 0.0;
		double errorRate = 0.0;
		for(File text : texts){
			BigDecimal probMax = new BigDecimal("0.0");
			String bestCate = null;
			for(File wordList : this.wordLists){//wordtext 为一个单词表
				BufferedReader br = new BufferedReader(
						new FileReader(text.getCanonicalPath()));
				BigDecimal probability = new BigDecimal("1.0");
				while ((line = br.readLine()) != null) {
					/**
					 * 类条件概率 p(tk/c)=(类c下单词tk在各个文档中出现过的次数之和+1)/(类c下单词总数+|V|)
					 * @param xcProb 		类条件概率
					 * @param cateWordPro 	表示类c下单词tk在各个文档中出现过的次数之和
					 * @param wordListMap	包含类c下单词tk在各个文档中出现过的次数之和
					 * @param cateNumMap	中有类c下单词总数
					 * @param totalWordNum	表示|V|训练样本单词总数
					 */
					double cateWordPro = 0.0;
					double wordNumInCate = 0.0;
					words = line.split(" ");
					if(wordListMap.get(wordList.getName()).get(words[0]) != null)
						cateWordPro = wordListMap.get(wordList.getName()).get(words[0]);
					if(cateNumMap.get(wordList.getName()) != null )
						wordNumInCate = cateNumMap.get(wordList.getName());
					BigDecimal testFileWordNumInCateBD = 
							new BigDecimal(String.valueOf(cateWordPro));
					BigDecimal wordNumInCateBD = 
							new BigDecimal(String.valueOf(wordNumInCate));
					BigDecimal totalWordsNumBD = 
							new BigDecimal(String.valueOf(totalSpecialNum));
					BigDecimal xcProb = (testFileWordNumInCateBD.add(
							new BigDecimal("1"))).divide(totalWordsNumBD.
									add(wordNumInCateBD),10, BigDecimal.ROUND_CEILING);
					probability = probability.multiply(xcProb.multiply(
							new BigDecimal(words[1])));
				}
				if(proMap.get(wordList.getName()) != null)
					probability = probability.multiply(new BigDecimal(
							proMap.get(wordList.getName()).toString()));
				else
					probability = new BigDecimal("0.0");
				if(probability.compareTo(probMax) == 1){
					probMax = probability;
					bestCate = wordList.getName();
				}
				br.close();
			}
			if(bestCate.contains(testFile.getName()))
				rightRate++;
			else
				errorRate++;
		}
		this.accuracy = new BigDecimal(String.valueOf(
				rightRate/(rightRate+errorRate)*100));
		this.accuracyAvg = this.accuracyAvg.add(this.accuracy);
		System.out.println("共有测试文本："+(rightRate+errorRate)+"\t分类正确率：" + 
				this.accuracy.setScale(2, BigDecimal.ROUND_HALF_UP) + "%");
	}
	
/**贝叶斯分类器入口
 * @param args
 * @throws Exception 
 */
public void main(String wordListPath, String specialSamplesDir) throws IOException{
	this.start = System.currentTimeMillis();
	System.out.println("step 5: Test the Naive Bayesian Classifier.");
	this.getTrainInfo(wordListPath);
	System.out.println("-----------------------------------------------------");
	System.out.println("\t测试结果：");
	/**
	 * testFiles 	为 testSamples 下的所有测试类
	 * testFile 	为一个测试类
	 * texts 		为 testFile 下的所有测试文本
	 * text 		为一个测试文本
	 */
	File wordListFile = new File(wordListPath);
	this.wordLists = wordListFile.listFiles();
	File testSamples = new File(specialSamplesDir);
	File[] testFiles = testSamples.listFiles();
	for(File testFile : testFiles){
		this.testClassifier(testFile);
	}
	this.accuracyAvg = this.accuracyAvg.divide(
			new BigDecimal(String.valueOf(testFiles.length)),10, BigDecimal.ROUND_HALF_UP);
	System.out.println("\tThe average of accuracy for Naive Bayesian Classifier is: \n\t\t" 
			+ this.accuracyAvg.setScale(4, BigDecimal.ROUND_HALF_UP) +"%.");
	System.out.println("\t\t\t\tTake time: " 
			+ (float)(System.currentTimeMillis() - this.start)/1000 
			+ "  seconds");
	
}
}
