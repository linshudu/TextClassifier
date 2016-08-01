package pers.textClassifier.LinShudu;


/**	文本分类器的主入口，先对如新闻等文本资料进行预处理，然后根据朴素贝叶斯分类器设计程序
 *	@author 	linshudu
 *	@qq 		617486329
 *	@version 	1.0
 */
public class ClassifierMain {
	
	
	/**	
	 * @param args			文本分类器的主入口函数参数
	 * @param start			记录分类器开始时间
	 * @param srcDir		训练样本和测试样本源文件目录
	 * @param targetDir		训练样本和测试样本预处理目标目录
	 * @param targetPath	预处理结束目标文件地址
	 * @param wordPath		单词表集合地址
	 * @param specialDir	测试集的特征词目录
	 * @param dataPreProcess文本预处理对象
	 * @param wordList		生成训单词表集合对象
	 * @param specail		测试集的特征词对象
	 * @param navieBayesianClassifier	朴素贝叶斯分类器对象
	 */
	public static void main(String[] args) throws Exception{
		
		System.out.println("Star classifying!");
		long start = System.currentTimeMillis();
		String srcDir,targetDir,targetPath;
		String wordPath;// = "C:/Users/do/Desktop/文本分类器/Classfier/train/WordList";
		String specialDir;// = "C:/Users/do/Desktop/文本分类器/Classfier/test/SpecialSamples";

		DataPreProcess dataPreProcess = 
				new DataPreProcess();
		WordList wordList = 
				new WordList();
		SpecailSamples specail = 
				new SpecailSamples();
		//train	预处理和生成单词表
		srcDir = new String("F:/DataMiningSample/orginSample");
		targetDir = new String("C:/Users/do/Desktop/文本分类器");
		targetPath = dataPreProcess.main(srcDir,targetDir,false);
		wordPath = wordList.main(targetPath);
		//test	预处理和特征词生成
		srcDir = new String("F:/DataMiningSample/TestSample");
		targetDir = new String("C:/Users/do/Desktop/文本分类器");
		targetPath = dataPreProcess.main(srcDir,targetDir,true);
		specialDir = specail.main(targetPath);
		//采用朴素贝叶斯分类器
		NaiveBayesianClassifier naiveBayesianClassifier = 
				new NaiveBayesianClassifier();
		naiveBayesianClassifier.main(wordPath,specialDir);
		System.out.println("The end of work!");
		System.out.println("All the work takes time: " + 
				(float)(System.currentTimeMillis()-start)/1000 
				+ "  seconds");
	}
}
