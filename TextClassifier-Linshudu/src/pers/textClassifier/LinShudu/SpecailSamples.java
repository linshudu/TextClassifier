package pers.textClassifier.LinShudu;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * NewsGroups	���ɲ��Լ���������
 * @author 		linshudu
 * @qq 			617486329 
 */
public class SpecailSamples {
	private String srcDir;
	private String targetPath;
	private long start;
	private double degree = 1;

	/**ͳ��ÿ���ʵ��ܵĳ��ִ��������س��ִ�������2�εĴʻ㹹�����յ�������
	 * @param fileDir 			Ԥ����õ�newsgroup�ļ�Ŀ¼�ľ���·��
	 * @param srcFile			newsgroupԤ������Ŀ���ļ�
	 * @param groupFiles 		newsgroup�������ļ�
	 * @param newsFiles			newsgroupÿ�����µ������ı����ϣ���Ԥ�������
	 * @param targetPath		���Լ����������ʵĵ�ַ
	 * @throws IOException 
	 */
	private void creatSpecialSample(String fileDir) throws IOException{
		File srcFile = new File(fileDir);
		File[] groupFiles = srcFile.listFiles();
		String targetPath2;
		this.srcDir = srcFile.getCanonicalPath().replace("\\", "/");
		File target = new File(fileDir + "/../SpecialSamples");
		this.targetPath = target.getCanonicalPath().replace("\\", "/");
		
		for(File group : groupFiles){
			System.out.println("\tProcess the group��" + group.getName());
			targetPath2 = new String(
					this.targetPath + "/" + group.getName());
			File targetFile = new File(targetPath2);
			if(!targetFile.exists())
				targetFile.mkdirs();
			//�ж��Ƿ���Ŀ¼�ļ����ǵĻ��ҳ�Ŀ¼�����ļ�
			if (group.isDirectory()) {
				File[] newsFiles = group.listFiles();
				for (File news : newsFiles) {
					generateTestSample(news,targetFile);
				}
			}
		}
	}
	/**
	 * @param news		�����ı�
	 * @param targetFileĿ���ļ�
	 */
	private void generateTestSample(File news,File targetFile) throws IOException{
		String word;
		double count;
		HashMap<String,Double> wordMap = new HashMap<>();
		BufferedReader wordBR = new BufferedReader(
				new FileReader(news.getCanonicalFile()));
		BufferedWriter wordFW = new BufferedWriter(
				new FileWriter(targetFile.getCanonicalPath() 
				+"/" + news.getName()));
		while( (word = wordBR.readLine()) != null ){
			if(!word.isEmpty() && wordMap.containsKey(word)){
				count = wordMap.get(word) + 1;
				wordMap.put(word, count);
			}
			else 
				wordMap.put(word, 1.0);
		}
		//ֻ���س��ִ������� degree �ĵ���
		Set<String> key = wordMap.keySet();
		Iterator<String> it = key.iterator() ;
		for( ; it.hasNext();){
			 String s = (String) it.next();
			 if(wordMap.get(s) > degree)
				 wordFW.write(s + " " + wordMap.get(s) + "\r\n");
		}
		wordBR.close();
		wordFW.flush();
		wordFW.close();
	}
	
	/**
	 * ����ѵ���������ϳ��������
	 * @param getTrainningSample ����ѵ��������
	 */
	public String main(String fileDir) throws IOException {
		
		System.out.println("step 4: Get special words from test samples.");
		this.start = System.currentTimeMillis();
		SpecailSamples getTS = new SpecailSamples();
		getTS.creatSpecialSample(fileDir);
		System.out.println("\tԴ�ļ�->" + getTS.srcDir );
		System.out.println("\tĿ��->"+getTS.targetPath);
		System.out.println("\t\t\t\tTake time: " 
				+ (float)(System.currentTimeMillis() - this.start)/1000 
				+ "  seconds");
		return getTS.targetPath;
	}
}
