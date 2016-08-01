package pers.textClassifier.LinShudu;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

/**	
 * NewsGroups	文档集预处理类
 * @author		linshudu
 * @qq			617486329
 */
public class DataPreProcess {
	
	/**
	 * @param strDir    	newsgroup 文件目录的绝对路径
	 * @param targetDir 	newsgroup 文件预处理后的存储路径
	 * @param stopWordsPath	停止词文件地址
	 * @param targetPath	预处理结果地址
	 * @param start			记录开始时间
	 * @param stopWordsSet	停止词结果集
	 */
	private String strDir;
	private String stopWordsPath;
	private String targetPath;
	private long start;
	private HashSet<String> stopWordsSet; 
	
	/**
	 * process1				输入文件调用处理数据函数
	 * @param strDir		newsgroup 文件目录的绝对路径
	 * @param targetPath	newsgroup 文件预处理后的存储路径
	 * @param srcGroupFiles newsgroup 地址中文件的所有种类
	 * @param srcGF			newsgroup 中的一个类，里面包含该类的许多news
	 * @throws IOException 
	 */
	private void process1(String srcDir, String targetPath) throws IOException{
		File srcFile = new File(srcDir);				
		//生成源文件对象和目标文件对象
		File[] srcGroupFiles = srcFile.listFiles();
		File targetFile = new File(targetPath);
		if(!srcFile.exists()){							
			//若源文件地址不存在
			System.out.println("File not exist:" + strDir);
			return;
		}		
		if(!targetFile.exists()){
			//若目标文件不存在,注意最好先建出来，否则可能母目录不存在，会报错
			targetFile.mkdirs();
		}
		for(File srcGF : srcGroupFiles){				
			//一一提取 newsgroup 的每一个类进行预处理
			File targetGroupFile = new File(targetPath+"/"+srcGF.getName());
			if(!srcGF.isDirectory()){
				System.out.println("\t(Ignore)The "+srcGF.getName() 
				+ " is not a group of News files.");
				continue;
			}
			if(!targetGroupFile.exists()){
				targetGroupFile.mkdirs();
			}
			//对单一个类数据预处理
			process2(srcGF.getCanonicalPath(), 
					targetGroupFile.getCanonicalPath());
			System.out.println("group  " + srcGF.getName());
		}
	}
	
	/**对每一个类处理数据函数
	 * @param srcGroupPath		newsgroup 单一类的路径
	 * @param targetGroupPath	newsgroup 单一类处理后存储的路径
	 * @param srcGF				newsgroup 中的一个类，里面包含该类的许多news
	 * @param srcNewsFiles		多个 news 组成的数组
	 * @param counts			news 个数统计
	 * @param srcNF				如果是一个的 news 对象,里面含有很多 words
	 * @throws IOException 
	 */
	private void process2(String srcGroupPath, String targetGroupPath) 
			throws IOException{
		File srcGF = new File(srcGroupPath);
		File[] srcNewsFiles = srcGF.listFiles();
		int counts = 0;
		for(File srcNF : srcNewsFiles){
			if(srcNF.isDirectory()){
				//确认子文件名不是目录如果是可以再次递归调用
				process2(srcNF.getCanonicalPath(), targetGroupPath);
			}
			else{//对类下面每一个文本数据预处理
				process3(srcNF.getCanonicalPath(), targetGroupPath);
				counts++;
			}
		}
		System.out.print("\tThere are  " + counts+"\tfiles in ");
	}
	
	/**对每一个文本处理数据函数
	 * @param srcNewsPath			newsgroup 单一文本的绝对路径
	 * @param targetGroupPath		newsgroup 单一类处理后的存储路径
	 * @throws IOException 
	 */
	private void process3(String srcNewsPath, String targetGroupPath) 
			throws IOException{
		File srcNewsFile = new File(srcNewsPath);
		BufferedReader srcFileBR = new BufferedReader(
				new FileReader(srcNewsPath));//装饰模式
		BufferedWriter targetFileWriter = new BufferedWriter(
				new FileWriter(targetGroupPath + "/" + srcNewsFile.getName()));	
		String line;
		String text;
		String[] words;
		while(( line = srcFileBR.readLine()) != null){
			//step1	 英文词法分析，去除数字、连字符、标点符号、特殊字符，可以考虑用正则表达式
			words = line.split("[^a-zA-Z]");
			for(String word : words){
				//step2		所有大写字母转换成小写
				text = word.toLowerCase();
				//step3  	词根还原
				text = Stemmer.stemming(text);
				//step4		去停用词
				if(!word.isEmpty() && 
						!this.stopWordsSet.contains(text)){
					targetFileWriter.append(text + "\r\n");		
					//windows 换行为\r\n, unix 为\n, mac 为\r
				}
			}
		}
		targetFileWriter.flush();
		targetFileWriter.close();
		srcFileBR.close();
	}
	
	
	/**获取停止词函数
	 * @param stopWordsPath		停止词文本的绝对路径
	 * @param stopWordsSet		停止词结果集
	 * @throws IOException 
	 */
	private void getStopWords(String stopWordsPath) throws IOException{
		File stopWordsFile = new File(stopWordsPath);
		if(!stopWordsFile.exists()){//若源文件地址不存在
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
	 * pathName			根据阶段判断生成文件的名字
	 * @param process	true 表示测试阶段，false 表示训练阶段
	 * @return String	生成文件名字符串
	 */
	private String pathName(boolean process){
		if(process)
			return new String("/Classfier/test/pre-process");
		else
			return new String("/Classfier/train/pre-process");
	}
	
	/**	
	 * 预处理程序入口
	 * @param args
	 * @param strDir		源文本群地址
	 * @param targetDir		预处理结果地址
	 * @param stopWordsPath	停止词文件地址
	 * @param process		true 表示测试阶段，false 表示训练阶段
	 * @param targetPath	目标文件地址
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
		System.out.println("\t源文件--->" + strDir );
		System.out.println("\t目标->" + targetPath);
		System.out.println("\t停止词文件："+stopWordsPath);
		System.out.println("\t\t\t\tTake time: " 
				+ (float)(System.currentTimeMillis() - this.start)/1000 
				+ "  seconds");
		return targetPath;
	}
}
