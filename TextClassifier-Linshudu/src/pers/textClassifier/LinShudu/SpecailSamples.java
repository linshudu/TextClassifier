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
 * NewsGroups	生成测试集的特征词
 * @author 		linshudu
 * @qq 			617486329 
 */
public class SpecailSamples {
	private String srcDir;
	private String targetPath;
	private long start;
	private double degree = 1;

	/**统计每个词的总的出现次数，返回出现次数大于2次的词汇构成最终的特征词
	 * @param fileDir 			预处理好的newsgroup文件目录的绝对路径
	 * @param srcFile			newsgroup预处理后的目标文件
	 * @param groupFiles 		newsgroup新闻类文件
	 * @param newsFiles			newsgroup每个类下的新闻文本资料（已预处理过）
	 * @param targetPath		测试集生成特征词的地址
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
			System.out.println("\tProcess the group：" + group.getName());
			targetPath2 = new String(
					this.targetPath + "/" + group.getName());
			File targetFile = new File(targetPath2);
			if(!targetFile.exists())
				targetFile.mkdirs();
			//判断是否是目录文件，是的话找出目录下子文件
			if (group.isDirectory()) {
				File[] newsFiles = group.listFiles();
				for (File news : newsFiles) {
					generateTestSample(news,targetFile);
				}
			}
		}
	}
	/**
	 * @param news		新闻文本
	 * @param targetFile目标文件
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
		//只返回出现次数大于 degree 的单词
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
	 * 生成训练样例集合程序主入口
	 * @param getTrainningSample 生成训练集对象
	 */
	public String main(String fileDir) throws IOException {
		
		System.out.println("step 4: Get special words from test samples.");
		this.start = System.currentTimeMillis();
		SpecailSamples getTS = new SpecailSamples();
		getTS.creatSpecialSample(fileDir);
		System.out.println("\t源文件->" + getTS.srcDir );
		System.out.println("\t目标->"+getTS.targetPath);
		System.out.println("\t\t\t\tTake time: " 
				+ (float)(System.currentTimeMillis() - this.start)/1000 
				+ "  seconds");
		return getTS.targetPath;
	}
}
