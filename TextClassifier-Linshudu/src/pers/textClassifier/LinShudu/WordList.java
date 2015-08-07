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
 * NewsGroups	����ѵ���������ʱ�
 * @author 		linshudu
 * @qq 			617486329 
 */
public class WordList {
	private String srcDir;
	private String targetPath;
	private long start;
	private double degree = 1;

	/**�������ʱ��ļ�
	 * @param fileDir 		Ԥ����õ�newsgroup�ļ�Ŀ¼
	 * @param srcFile		newsgroupԤ������Ŀ���ļ�
	 * @param groupFiles	newsgroup�������ļ�
	 * @param newsFiles		newsgroupÿ�����µ������ı����ϣ���Ԥ�������
	 * @param targetPath	���ʱ��ϵ�ַ
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
			//�ж��Ƿ���Ŀ¼�ļ����ǵĻ��ҳ�Ŀ¼�����ļ�
			if (group.isDirectory()) {
				generateWordList(group);
			}
		}
	}
	
	/**
	 * generateWordList	ͳ��ÿ���ʵ��ܵĳ��ִ��������صĴʻ㹹�����յ����Դʵ�
	 * @param group		�������ļ�
	 * @param news		�����ı��ļ�
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
		//���س��� degree �����ϵĵ���
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
	 * ����ѵ���������ϳ��������
	 * @param wordList	���ɵ��ʱ����
	 * @param fileDir	����Դ�ļ��õ����ʱ�
	 */
	public String main(String fileDir) throws IOException {
		this.start = System.currentTimeMillis();
		System.out.println("step 2: Get word list.");
		WordList wordList = new WordList();
		wordList.creatWordListFile(fileDir);
		System.out.println("\tԴ�ļ�->" + wordList.srcDir );
		System.out.println("\tĿ��->"+wordList.targetPath);
		System.out.println("\t\t\t\tTake time: " 
				+ (float)(System.currentTimeMillis() - this.start)/1000 
				+ "  seconds");
		return wordList.targetPath;
		
	}
}
