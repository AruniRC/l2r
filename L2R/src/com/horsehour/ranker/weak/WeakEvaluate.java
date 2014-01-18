package com.horsehour.ranker.weak;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.horsehour.datum.DataManager;
import com.horsehour.datum.DataSet;
import com.horsehour.datum.SampleSet;
import com.horsehour.filter.L2RLineParser;
import com.horsehour.filter.LineParserFilter;
import com.horsehour.math.MathLib;
import com.horsehour.metric.MAP;
import com.horsehour.metric.Metric;
import com.horsehour.metric.NDCG;
import com.horsehour.metric.Precision;
import com.horsehour.model.LinearModel;
import com.horsehour.util.FileManager;

/**
 * ���ۻ���ģ��/��ģ�͵ı��֣�Ŀ�ģ�1����֤Local (Over)fitting����;
 * 2���������۽������; 3��ѡ��Ԥ�������в����ԵĻ���ģ�ͼ���
 * 
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20130721
 */
public class WeakEvaluate {
	public List<LinearModel> weakModels;
	public double[][] perfMatrix;
	public DataSet dataset;

	public int mm = -1;//����ģ�͵ĸ���
	public int mq = -1;//�����ʵĸ���-��������������ά��
	public int kcv = 5;
	
	public String[] corpus;
	public String database;
	public String evalbase;
	
	public List<Integer> homeset;
	public List<Double> homeperf;

	public Metric[] testMetrics;
	
	public WeakEvaluate(){
		int m = 21;
		testMetrics = new Metric[m];
		testMetrics[10] = new MAP();

		for(int k = 0; k < 10; k++){
			testMetrics[k] = new NDCG(k + 1);
			testMetrics[k + 11] = new Precision(k + 1);
		}
	}
	
	/**
	 * ���ػ���ģ��
	 * @param weakFile
	 */
	public int loadWeakModels(String weakFile){
		weakModels = new ArrayList<LinearModel>();
		List<double[]> list;
		list = DataManager.loadDatum(weakFile, "\t");

		int len = list.get(0).length;

		//��������֮�͹����ظ���Ŀ
		Set<Double> sumset = new HashSet<Double>();
		int p = 0;//������Ԫ��֮ǰ�Ĵ�С
		int q = 0;//������Ԫ��֮��Ĵ�С
		double sum = 0;
		int count = 0;//Ȩֵ֮��С��0.5�ĸ���

		homeset = new ArrayList<Integer>();
		int qid = 0;
		int sz = list.size();
		for(int i = 0; i < sz; i++){
			qid = (int) list.get(i)[2];

			double[] weight = Arrays.copyOfRange(list.get(i), 3, len);//�޳�ͷ������

			sum = MathLib.sum(weight);//Ȩ�����
			if(sum < 0.5){//�޳�С��0.5��
				count++;
				continue;
			}

			sumset.add(sum);

			if((q = sumset.size()) > p){//��Ԫ�ؿ�����ӱ������ظ�
				weakModels.add(new LinearModel(weight));
				homeset.add(new Float(qid).intValue());
				p = q;
			}
		}
		mm = weakModels.size();
		return count;
	}

	/**
	 * �������ݼ�
	 * @param file
	 * @param lineParser
	 */
	public void loadDataSet(String file, LineParserFilter lineParser){
		dataset = DataManager.loadDataSet(file, lineParser);
		mq = dataset.size();
	}

	/**
	 * ����ģ�����ܾ���
	 */
	public void buildPerfMatrix(){
		perfMatrix = new double[mm][mq];
		homeperf = new ArrayList<Double>();
		SampleSet sampleset;
		LinearModel weak;

		for(int i = 0; i < mm; i++){
			weak = weakModels.get(i);

			for(int qid = 0; qid < mq; qid++){
				sampleset = dataset.getSampleSet(qid);

				double perf = testMetrics[10].measure(sampleset.getLabels(), weak.predict(sampleset));
						
				perfMatrix[i][qid] = perf;
				
				String query = sampleset.getSample(0).getQid();
				if(Integer.parseInt(query) == homeset.get(i))
					homeperf.add(perf);
			}
		}
	}

	/**
	 * @param dest
	 */
	public void reportLocalOverfit(String dest){
		int n = homeset.size();
		for(int i = 0; i < n; i++){
			StringBuffer sb = new StringBuffer();
			sb.append(homeperf.get(i));//home performance
			for(int j = 0; j < mq; j++)
				sb.append("\t" + perfMatrix[i][j]);

			sb.append("\r\n");
			FileManager.writeFile(dest, sb.toString());
		}
	}
	
	/**
	 * ����Ƿ����Local Overfitting
	 */
	public void verifyLocalOverfit(){
		LineParserFilter lineParser = new L2RLineParser();
		String train;
		String weakFile;
		String weakEvalFile;
		
		StringBuffer sb = new StringBuffer();
		for(int ori = 0; ori <= 1; ori++){
			char flag = (ori == 1) ? 'I' : 'O';
			String logFile = evalbase + "BaseModel/CCR-" + flag + "-Log.txt";

			int m = corpus.length;
			for(int i = 0; i < m; i++){
				train = database + corpus[i] + "/Fold";
				weakFile = database + "DEAData/" + corpus[i] + "/Fold";
				weakEvalFile = evalbase + "BaseModel/WeakPerformance/CCR-" 
						+ flag + "-" + corpus[i] + "-Fold";
				sb.append(corpus[i] + "\r\n");
				
				for(int cv = 1; cv <= kcv; cv++){
					loadDataSet(train + cv + "/train.txt", lineParser);
					int count = loadWeakModels(weakFile + cv + "/" + flag + "LPDEA.txt");
					buildPerfMatrix();
					reportLocalOverfit(weakEvalFile + cv + ".txt");
					sb.append("--Fold" + cv + count + "(" + weakModels.size() + ")\r\n");
				}
			}
			FileManager.writeFile(logFile, sb.toString());
		}
	}

	/**
	 * ȡ��local overfitting�ı���
	 * @param src
	 */
	public void getLOPercentage(String src){
		File[] files = FileManager.getFileList(src);
		List<double[]> eval;
		
		for(File f : files){
			eval = DataManager.loadDatum(f.getAbsolutePath(), "\t");

			int total = eval.size();
			int n = eval.get(0).length;
			int count = 0;
			for(int i = 0; i < total; i++){
				double[] line = eval.get(i);
				double sum = MathLib.sum(line);
				if(sum < n * line[0])
					count++;
			}
			System.out.println(f.getName() + ":" + count + "/" + total); 
		}
	}

	/**
	 * ����ƽ��������topk��ģ��ͳ��local-overfitting�ı���
	 * @param src
	 * @param topk
	 */
	public void countLocalFit(String src, int topk){
		File[] files = FileManager.getFileList(src);
		List<double[]> eval;

		for(File f : files){
			eval = DataManager.loadDatum(f.getAbsolutePath(), "\t");

			int total = eval.size();
			int n = eval.get(0).length;
			
			double[] sum = new double[total];
			for(int i = 0; i < total; i++){
				double[] line = eval.get(i);
				sum[i] = MathLib.sum(line);
			}

			int[] rank = MathLib.getRank(sum, false);
			int count = 0;
			for(int i = 0; i < topk; i++){
				int idx = rank[i];
				if(sum[idx] < n * eval.get(idx)[0])
					count++;
			}

			System.out.println(f.getName() + ":" + count + "/" + topk); 
		}
	}

	/**
	 * ���ݶ���������ϵ����ֲܷ�, ��topk��ƽ��������ߵ�ģ�ͼ���, ͳ��local-overfitting�ı���
	 * @param src
	 * @param topk
	 * @param median
	 */
	public void countLocalFit(String src, int topk, float median){
		File[] files = FileManager.getFileList(src);
		List<double[]> eval;

		for(File f : files){
			eval = DataManager.loadDatum(f.getAbsolutePath(), "\t");

			int total = eval.size();
			int n = eval.get(0).length;
			
			double[] sum = new double[total];
			for(int i = 0; i < total; i++){
				double[] line = eval.get(i);
				sum[i] = MathLib.sum(line);
			}

			int[] rank = MathLib.getRank(sum, false);
			int count = 0;
			for(int i = 0; i < topk; i++){
				int idx = rank[i];
				double[] line = eval.get(idx);
				double home = line[0];
				line[0] = -1;

				int[] rankList = MathLib.getRank(line, false);
				idx = rankList[(int) (n * median)];
				if(line[idx] < home)
					count++;
			}
			System.out.println(f.getName() + ":" + count + "/" + topk); 
		}
	}
	
	/**
	 * �������Ч�ʷ�ֵ��relative efficiency score��Ԥ������
	 */
	public void evalRES(){
		String weakFile;
		String evalFile;

		int m = corpus.length;
		for(int i = 0; i < m; i++){
			weakFile = database + "DEAData/" + corpus[i] + "/Fold";
			
			StringBuffer sb = new StringBuffer();
			evalFile = evalbase + "BaseModel/RESEvaluation/RESEval-" + corpus[i] + ".eval";
			for(int ori = 0; ori <= 1; ori++){
				char flag = (ori == 1) ? 'I' : 'O';
				for(int cv = 1; cv <= kcv; cv++)
					sb.append(evalRES(weakFile + cv + "/" + flag + "LPDEA.txt"));
			}
			
			FileManager.writeFile(evalFile, sb.toString());
		}
	}

	/**
	 * �������Ч��Ȩֵ��Ԥ������
	 * @param resFile
	 * @return �������
	 */
	public String evalRES(String resFile){
		List<double[]> list;
		list = DataManager.loadDatum(resFile, "\t");

		int m = testMetrics.length;
		double[] perf = new double[m];
		int sz = list.size();
		int nq = 0;
		for(int i = 0; i < sz;){
			double qid = list.get(i)[2];
			List<Integer> label = new ArrayList<Integer>();
			List<Double> res = new ArrayList<Double>();
			while(i < sz && qid == list.get(i)[2]){
				label.add((int) list.get(i)[0]);
				res.add(list.get(i)[1]);
				i += 1;
			}
			
			for(int j = 0; j < m; j++)
				perf[j] += testMetrics[j].measure(label, res);
			
			nq ++;
		}
		
		StringBuffer sb = new StringBuffer();
		for(int k = 0; k < m; k++){
			perf[k] /= nq;
			sb.append(perf[k] + "\t");
		}
		sb.append("\r\n");
		return sb.toString();
	}

	/**
	 * �ɶԱȽ�ͳ��Ӯ�Ĵ���
	 * @param tbl
	 */
	public void countWinningNumber(String tbl){
		List<double[]> eval;
		eval = DataManager.loadDatum(tbl, "\t");
		
		int n = eval.size();
		int[][] count = new int[n][n];
		
		int m = eval.get(0).length;
		for(int i = 0; i < n; i++){
			double[] rowI = eval.get(i);
			for(int j = 0; j < n; j++){
				double[] rowJ = eval.get(j);
				for(int k = 0; k < m; k++){
					if(rowI[k] > rowJ[k])
						count[i][j] += 1;
					else if(rowI[k] < rowJ[k])
						count[j][i] += 1;
				}
			}
		}
		
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < n; i++){
			for(int j = 0; j < n; j++)
				sb.append(count[i][j] + "\t");
			
			sb.append("\r\n");
		}
		
		System.out.println(sb.toString());
	}
}
