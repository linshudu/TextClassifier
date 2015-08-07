package pers.textClassifier.LinShudu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;

/**�������ر�Ҷ˹�㷨��newsgroup�ĵ��������࣬����ʮ�齻�����ȡƽ��ֵ
 * ���ö���ʽģ��,stanford��Ϣ�������ۿμ������Զ���ʽģ�ͱȲ�Ŭ��ģ��׼ȷ�ȸ�
 * ����������P(tk|c)=(��c �µ���tk �ڸ����ĵ��г��ֹ��Ĵ���֮��+1)/(��c�µ�������+|V|)
 * |V| ѵ�������в��ظ�����������
 * @author 	ShuduLin
 * @qq 		617486329
 * @version 1.0
 */
public class NaiveBayesianClassifier {

	private long start;
	private BigDecimal accuracy = new BigDecimal("0");
	private BigDecimal accuracyAvg = new BigDecimal("0");
	private double totalWordNum = 0.0;
	private double totalSpecialNum = 0.0;//ѵ�������в��ظ�����������
	private String line;
	private String[] words;
	private HashMap<String,HashMap<String,Double>> wordListMap = new HashMap<>();
	private HashMap<String,Double> cateNumMap = new HashMap<>();
	private HashMap<String,Double> proMap = new HashMap<>();
	private File[] wordLists;
	
	/**���ѵ��������Ϣ
	 * wordList		Ϊ wordLists �µ����е��ʱ�text Ϊ��һ���ʱ�
	 * wordListMap	������ĵ��ʱ��ϣ�key Ϊ������value Ϊ���ʱ�map
	 * wordMap 		һ����ĵ��ʱ��ϣ�key Ϊ���ʣ�value Ϊ����
	 * cateNumMap 	�� c �µ�������
	 * proMap 		�� c �������
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
					totalWordNum += Double.parseDouble(words[1]);//ѵ�������в��ظ�����������
					cateWordNum += Double.parseDouble(words[1]);
				}
			}
		br.close();
			cateNumMap.put(wordList.getName(), cateWordNum);
			wordListMap.put(wordList.getName(), wordMap);
		}
		System.out.println("\tѵ���ĵ�����������:\t"+totalWordNum);
		for(File wordList : wordLists){
			double cateWordNum = cateNumMap.get(wordList.getName());
			StringBuilder wordListName = new StringBuilder(
						wordList.getName().replace(".txt", ""));
			while(wordListName.length() < 26)
				wordListName = wordListName.append(" "); 
			System.out.print("\tѵ����  "+ wordListName +"����������"+cateWordNum);
			cateWordNum /= totalWordNum;
			proMap.put(wordList.getName(), cateWordNum);
			DecimalFormat df = new DecimalFormat("00.000");
			System.out.println("\t�������:"+df.format(cateWordNum*100)+"%");
		}
	}
	
	/**�Բ��Լ����б�Ҷ˹���࣬��������ȷ��
	 * @param testFile	���Լ��ı�
	 * @param text		�����ı�
	 * @param rightRate	��ȷ��
	 * @param errorRate	������
	 */
	private void testClassifier(File testFile)throws IOException{
		StringBuilder wordListName = new StringBuilder(
				testFile.getName().replace(".txt", ""));
		while(wordListName.length() < 26)
			wordListName = wordListName.append(" "); 
		System.out.print("\t������  "+ wordListName + "�Ĳ��Խ��,");
		File[] texts = testFile.listFiles();
		double rightRate = 0.0;
		double errorRate = 0.0;
		for(File text : texts){
			BigDecimal probMax = new BigDecimal("0.0");
			String bestCate = null;
			for(File wordList : this.wordLists){//wordtext Ϊһ�����ʱ�
				BufferedReader br = new BufferedReader(
						new FileReader(text.getCanonicalPath()));
				BigDecimal probability = new BigDecimal("1.0");
				while ((line = br.readLine()) != null) {
					/**
					 * ���������� p(tk/c)=(��c�µ���tk�ڸ����ĵ��г��ֹ��Ĵ���֮��+1)/(��c�µ�������+|V|)
					 * @param xcProb 		����������
					 * @param cateWordPro 	��ʾ��c�µ���tk�ڸ����ĵ��г��ֹ��Ĵ���֮��
					 * @param wordListMap	������c�µ���tk�ڸ����ĵ��г��ֹ��Ĵ���֮��
					 * @param cateNumMap	������c�µ�������
					 * @param totalWordNum	��ʾ|V|ѵ��������������
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
		System.out.println("���в����ı���"+(rightRate+errorRate)+"\t������ȷ�ʣ�" + 
				this.accuracy.setScale(2, BigDecimal.ROUND_HALF_UP) + "%");
	}
	
/**��Ҷ˹���������
 * @param args
 * @throws Exception 
 */
public void main(String wordListPath, String specialSamplesDir) throws IOException{
	this.start = System.currentTimeMillis();
	System.out.println("step 5: Test the Naive Bayesian Classifier.");
	this.getTrainInfo(wordListPath);
	System.out.println("-----------------------------------------------------");
	System.out.println("\t���Խ����");
	/**
	 * testFiles 	Ϊ testSamples �µ����в�����
	 * testFile 	Ϊһ��������
	 * texts 		Ϊ testFile �µ����в����ı�
	 * text 		Ϊһ�������ı�
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
