package pers.textClassifier.LinShudu;


/**	�ı�������������ڣ��ȶ������ŵ��ı����Ͻ���Ԥ����Ȼ��������ر�Ҷ˹��������Ƴ���
 *	@author 	linshudu
 *	@qq 		617486329
 *	@version 	1.0
 */
public class ClassifierMain {
	
	
	/**	
	 * @param args			�ı�������������ں�������
	 * @param start			��¼��������ʼʱ��
	 * @param srcDir		ѵ�������Ͳ�������Դ�ļ�Ŀ¼
	 * @param targetDir		ѵ�������Ͳ�������Ԥ����Ŀ��Ŀ¼
	 * @param targetPath	Ԥ�������Ŀ���ļ���ַ
	 * @param wordPath		���ʱ��ϵ�ַ
	 * @param specialDir	���Լ���������Ŀ¼
	 * @param dataPreProcess�ı�Ԥ�������
	 * @param wordList		����ѵ���ʱ��϶���
	 * @param specail		���Լ��������ʶ���
	 * @param navieBayesianClassifier	���ر�Ҷ˹����������
	 */
	public static void main(String[] args) throws Exception{
		
		System.out.println("Star classifying!");
		long start = System.currentTimeMillis();
		String srcDir,targetDir,targetPath;
		String wordPath;// = "C:/Users/do/Desktop/�ı�������/Classfier/train/WordList";
		String specialDir;// = "C:/Users/do/Desktop/�ı�������/Classfier/test/SpecialSamples";

		DataPreProcess dataPreProcess = 
				new DataPreProcess();
		WordList wordList = 
				new WordList();
		SpecailSamples specail = 
				new SpecailSamples();
		//train	Ԥ��������ɵ��ʱ�
		srcDir = new String("F:/DataMiningSample/orginSample");
		targetDir = new String("C:/Users/do/Desktop/�ı�������");
		targetPath = dataPreProcess.main(srcDir,targetDir,false);
		wordPath = wordList.main(targetPath);
		//test	Ԥ���������������
		srcDir = new String("F:/DataMiningSample/TestSample");
		targetDir = new String("C:/Users/do/Desktop/�ı�������");
		targetPath = dataPreProcess.main(srcDir,targetDir,true);
		specialDir = specail.main(targetPath);
		//�������ر�Ҷ˹������
		NaiveBayesianClassifier naiveBayesianClassifier = 
				new NaiveBayesianClassifier();
		naiveBayesianClassifier.main(wordPath,specialDir);
		System.out.println("The end of work!");
		System.out.println("All the work takes time: " + 
				(float)(System.currentTimeMillis()-start)/1000 
				+ "  seconds");
	}
}
