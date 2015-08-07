package pers.textClassifier.LinShudu;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

/**	
 * NewsGroups	�ĵ���Ԥ������
 * @author		linshudu
 * @qq			617486329
 */
public class DataPreProcess {
	
	/**
	 * @param strDir    	newsgroup �ļ�Ŀ¼�ľ���·��
	 * @param targetDir 	newsgroup �ļ�Ԥ�����Ĵ洢·��
	 * @param stopWordsPath	ֹͣ���ļ���ַ
	 * @param targetPath	Ԥ��������ַ
	 * @param start			��¼��ʼʱ��
	 * @param stopWordsSet	ֹͣ�ʽ����
	 */
	private String strDir;
	private String stopWordsPath;
	private String targetPath;
	private long start;
	private HashSet<String> stopWordsSet; 
	
	/**
	 * process1				�����ļ����ô������ݺ���
	 * @param strDir		newsgroup �ļ�Ŀ¼�ľ���·��
	 * @param targetPath	newsgroup �ļ�Ԥ�����Ĵ洢·��
	 * @param srcGroupFiles newsgroup ��ַ���ļ�����������
	 * @param srcGF			newsgroup �е�һ���࣬���������������news
	 * @throws IOException 
	 */
	private void process1(String srcDir, String targetPath) throws IOException{
		File srcFile = new File(srcDir);				
		//����Դ�ļ������Ŀ���ļ�����
		File[] srcGroupFiles = srcFile.listFiles();
		File targetFile = new File(targetPath);
		if(!srcFile.exists()){							
			//��Դ�ļ���ַ������
			System.out.println("File not exist:" + strDir);
			return;
		}		
		if(!targetFile.exists()){
			//��Ŀ���ļ�������,ע������Ƚ��������������ĸĿ¼�����ڣ��ᱨ��
			targetFile.mkdirs();
		}
		for(File srcGF : srcGroupFiles){				
			//һһ��ȡ newsgroup ��ÿһ�������Ԥ����
			File targetGroupFile = new File(targetPath+"/"+srcGF.getName());
			if(!srcGF.isDirectory()){
				System.out.println("\t(Ignore)The "+srcGF.getName() 
				+ " is not a group of News files.");
				continue;
			}
			if(!targetGroupFile.exists()){
				targetGroupFile.mkdirs();
			}
			//�Ե�һ��������Ԥ����
			process2(srcGF.getCanonicalPath(), 
					targetGroupFile.getCanonicalPath());
			System.out.println("group  " + srcGF.getName());
		}
	}
	
	/**��ÿһ���ദ�����ݺ���
	 * @param srcGroupPath		newsgroup ��һ���·��
	 * @param targetGroupPath	newsgroup ��һ�ദ���洢��·��
	 * @param srcGF				newsgroup �е�һ���࣬���������������news
	 * @param srcNewsFiles		��� news ��ɵ�����
	 * @param counts			news ����ͳ��
	 * @param srcNF				�����һ���� news ����,���溬�кܶ� words
	 * @throws IOException 
	 */
	private void process2(String srcGroupPath, String targetGroupPath) 
			throws IOException{
		File srcGF = new File(srcGroupPath);
		File[] srcNewsFiles = srcGF.listFiles();
		int counts = 0;
		for(File srcNF : srcNewsFiles){
			if(srcNF.isDirectory()){
				//ȷ�����ļ�������Ŀ¼����ǿ����ٴεݹ����
				process2(srcNF.getCanonicalPath(), targetGroupPath);
			}
			else{//��������ÿһ���ı�����Ԥ����
				process3(srcNF.getCanonicalPath(), targetGroupPath);
				counts++;
			}
		}
		System.out.print("\tThere are  " + counts+"\tfiles in ");
	}
	
	/**��ÿһ���ı��������ݺ���
	 * @param srcNewsPath			newsgroup ��һ�ı��ľ���·��
	 * @param targetGroupPath		newsgroup ��һ�ദ���Ĵ洢·��
	 * @throws IOException 
	 */
	private void process3(String srcNewsPath, String targetGroupPath) 
			throws IOException{
		File srcNewsFile = new File(srcNewsPath);
		BufferedReader srcFileBR = new BufferedReader(
				new FileReader(srcNewsPath));//װ��ģʽ
		BufferedWriter targetFileWriter = new BufferedWriter(
				new FileWriter(targetGroupPath + "/" + srcNewsFile.getName()));	
		String line;
		String text;
		String[] words;
		while(( line = srcFileBR.readLine()) != null){
			//step1	 Ӣ�Ĵʷ�������ȥ�����֡����ַ��������š������ַ������Կ�����������ʽ
			words = line.split("[^a-zA-Z]");
			for(String word : words){
				//step2		���д�д��ĸת����Сд
				text = word.toLowerCase();
				//step3  	�ʸ���ԭ
				text = Stemmer.stemming(text);
				//step4		ȥͣ�ô�
				if(!word.isEmpty() && 
						!this.stopWordsSet.contains(text)){
					targetFileWriter.append(text + "\r\n");		
					//windows ����Ϊ\r\n, unix Ϊ\n, mac Ϊ\r
				}
			}
		}
		targetFileWriter.flush();
		targetFileWriter.close();
		srcFileBR.close();
	}
	
	
	/**��ȡֹͣ�ʺ���
	 * @param stopWordsPath		ֹͣ���ı��ľ���·��
	 * @param stopWordsSet		ֹͣ�ʽ����
	 * @throws IOException 
	 */
	private void getStopWords(String stopWordsPath) throws IOException{
		File stopWordsFile = new File(stopWordsPath);
		if(!stopWordsFile.exists()){//��Դ�ļ���ַ������
			System.out.println("File not exist:" + 
					stopWordsPath.substring(stopWordsPath.lastIndexOf("/")));
			System.exit(0);
		}
		BufferedReader stopWordsBR = new BufferedReader(
				new FileReader(stopWordsPath));
		this.stopWordsSet = new HashSet<>();
		String stopWordsLine;
		while((stopWordsLine = stopWordsBR.readLine()) != null){
			if(!stopWordsLine.isEmpty())
				this.stopWordsSet.add(stopWordsLine);
		}
		stopWordsBR.close();
	}
	
	/**
	 * pathName			���ݽ׶��ж������ļ�������
	 * @param process	true ��ʾ���Խ׶Σ�false ��ʾѵ���׶�
	 * @return String	�����ļ����ַ���
	 */
	private String pathName(boolean process){
		if(process)
			return new String("/Classfier/test/pre-process");
		else
			return new String("/Classfier/train/pre-process");
	}
	
	/**	
	 * Ԥ����������
	 * @param args
	 * @param strDir		Դ�ı�Ⱥ��ַ
	 * @param targetDir		Ԥ��������ַ
	 * @param stopWordsPath	ֹͣ���ļ���ַ
	 * @param process		true ��ʾ���Խ׶Σ�false ��ʾѵ���׶�
	 * @param targetPath	Ŀ���ļ���ַ
	 * @throws IOException
	 */
	public String main(String strDir,String targetDir,boolean process) throws IOException{
		this.start = System.currentTimeMillis();
		this.stopWordsPath = 
				new String("F:/DataMiningSample/stopwords.txt");
		this.targetPath = 
				new String(targetDir + this.pathName(process));
		if(process)
			System.out.println("step 3: Pre-process the test samples.");
		else
			System.out.println("step 1: Pre-process the train samples.");
		this.getStopWords(stopWordsPath);
		this.process1(strDir,targetPath);
		System.out.println("\tԴ�ļ�--->" + strDir );
		System.out.println("\tĿ��->" + targetPath);
		System.out.println("\tֹͣ���ļ���"+stopWordsPath);
		System.out.println("\t\t\t\tTake time: " 
				+ (float)(System.currentTimeMillis() - this.start)/1000 
				+ "  seconds");
		return targetPath;
	}
}
