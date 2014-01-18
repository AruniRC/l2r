package com.horsehour.evaluate;

import java.util.ArrayList;
import java.util.List;

import com.horsehour.datum.DataManager;
import com.horsehour.datum.DataSet;
import com.horsehour.datum.SampleSet;
import com.horsehour.datum.norm.Normalizer;
import com.horsehour.datum.norm.SumNormalizer;
import com.horsehour.filter.L2RLineParser;
import com.horsehour.filter.LineParserFilter;
import com.horsehour.math.MathLib;
import com.horsehour.metric.MAP;
import com.horsehour.metric.Metric;
import com.horsehour.metric.NDCG;
import com.horsehour.metric.Precision;
import com.horsehour.model.EnsembleModel;
import com.horsehour.model.Model;
import com.horsehour.ranker.trainer.RankTrainer;
import com.horsehour.util.FileManager;

/**
 * ��������������������
 * @author Chunheng Jiang
 * @version 3.0
 * @since 20131219
 */
public class RankEvaluator {
	public RankTrainer ranker;

	public String predictFile;
	public String evalFile;

	public String trainFile;
	public String valiFile;
	public String testFile;

	public String database;
	public String evalbase;

	public boolean storePredict = false;
	public boolean preprocess = false;
	public boolean normalize = false;//��׼������

	public Normalizer normalizer = new SumNormalizer();

	public DataSet trainset;
	public DataSet valiset;
	public DataSet testset;

	public Metric[] trainMetrics;
	public Metric[] testMetrics;
	public String[] corpus;

	public int nIter;
	public int code;
	public int kcv;//k-cross-validation

	public RankEvaluator(){
		int m = 21;
		testMetrics = new Metric[m];
		testMetrics[10] = new MAP();

		for(int k = 0; k < 10; k++){
			testMetrics[k] = new NDCG(k + 1);
			testMetrics[k + 11] = new Precision(k + 1);
		}
	}

	/**
	 * ����ѵ������֤�Ͳ������ݼ�
	 */
	public void loadDataSet(){
		LineParserFilter lineParser = new L2RLineParser();

		if(!trainFile.isEmpty())
			trainset = DataManager.loadDataSet(trainFile, lineParser);
		if(!valiFile.isEmpty())
			valiset = DataManager.loadDataSet(valiFile, lineParser);
		if(!testFile.isEmpty())
			testset = DataManager.loadDataSet(testFile, lineParser);

		if(trainset != null)
			if(preprocess)
				DataManager.preprocess(trainset);

		if(normalize){
			if(trainset != null)
				normalizer.normalize(trainset);

			if(valiset != null)
				normalizer.normalize(valiset);

			if(testset != null)
				normalizer.normalize(testset);
		}
	}

	/**
	 * �������ݼ�
	 * @param file
	 * @param normalize
	 */
	public DataSet loadDataSet(String file, boolean normalize){
		DataSet dataset;
		dataset = DataManager.loadDataSet(file, new L2RLineParser());

		if(normalize)
			normalizer.normalize(dataset);

		return dataset;
	}

	/**
	 * ʹ��ָ��ģ��Ԥ������������ֵ,д��ָ���ļ�
	 * @param trainer
	 * @param modelFile
	 * @param dataFile
	 * @param output
	 */
	public void predict(RankTrainer trainer, String modelFile, String dataFile, String output){
		Model model = trainer.loadModel(modelFile);
		DataSet dataset = loadDataSet(dataFile, normalize);
		int m = dataset.size();
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < m; i++){
			double[] predict = model.predict(dataset.getSampleSet(i));
			int n = predict.length;
			for(int j = 0; j < n; j++)
				sb.append(predict[j] + "\r\n");
		}

		FileManager.writeFile(output, sb.toString());
	}

	/**
	 * ʹ��ָ��ģ��Ԥ������������ֵ,д��ָ���ļ�
	 * @param modelFile
	 * @param dataFile
	 * @param output
	 */
	public void predict(String modelFile, String dataFile, String output){
		Model model = ranker.loadModel(modelFile);
		DataSet dataset = loadDataSet(dataFile, normalize);
		int m = dataset.size();
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < m; i++){
			double[] predict = model.predict(dataset.getSampleSet(i));
			int n = predict.length;
			for(int j = 0; j < n; j++)
				sb.append(predict[j] + "\r\n");
		}

		FileManager.writeFile(output, sb.toString());
	}


	/**
	 * ��ģ��Ԥ�����������ķ�ֵ,д��ָ���ļ�
	 * @param model
	 * @param dataFile
	 * @param output
	 */
	public void predict(Model model, String dataFile, String output){
		DataSet dataset = loadDataSet(dataFile, normalize);
		int m = dataset.size();
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < m; i++){
			double[] predict = model.predict(dataset.getSampleSet(i));
			int n = predict.length;
			for(int j = 0; j < n; j++)
				sb.append(predict[j] + "\r\n");
		}

		FileManager.writeFile(output, sb.toString());
	}

	/**
	 * ��ģ��Ԥ�����������ķ�ֵ,д��ָ���ļ�
	 * @param model
	 * @param dataset
	 * @param output
	 */
	public void predict(Model model, DataSet dataset, String output){
		int m = dataset.size();
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < m; i++){
			double[] predict = model.predict(dataset.getSampleSet(i));
			int n = predict.length;
			for(int j = 0; j < n; j++)
				sb.append(predict[j] + "\r\n");
		}

		FileManager.writeFile(output, sb.toString());
	}

	/**
	 * ��������������Ԥ���������ݼ���ǵ����/�ȼ�, ʹ��ָ��ָ������Ԥ�⾫��,д��ָ���ļ�
	 * @param ranker
	 * @param predictFile
	 * @param dataFile
	 * @param metrics
	 * @param output
	 */
	public void eval(String predictFile, String dataFile, Metric[] metrics, String output)
	{
		List<String> predictLines = new ArrayList<String>(); 
		FileManager.readLines(predictFile, predictLines);

		DataSet dataset = loadDataSet(dataFile, normalize);
		int k = metrics.length;
		double[] perf = new double[k];

		int count = 0;
		int m = dataset.size();
		for(int i = 0; i < m; i++){
			List<Double> predict = new ArrayList<Double>();
			SampleSet sampleset = dataset.getSampleSet(i);
			int n = sampleset.size();
			for(int j = 0; j < n; j++){
				predict.add(Double.parseDouble(predictLines.get(count)));
				count++;
			}

			for(int j = 0; j < k; j++)
				perf[j] += metrics[j].measure(sampleset.getLabelList(), predict);
		}

		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < k; i++)
			sb.append(perf[i]/m + "\t");
		sb.append("\r\n");

		FileManager.writeFile(output, sb.toString());
	}

	/**
	 * ��������������Ԥ���������ݼ���ǵ����/�ȼ�, ʹ��ָ��ָ������Ԥ�⾫��,д��ָ���ļ�
	 * @param ranker
	 * @param predict
	 * @param dataFile
	 * @param metrics
	 * @param output
	 */
	public void eval(double[][] predict, DataSet dataset, Metric[] metrics, String output)
	{
		int k = metrics.length;
		double[] perf = new double[k];

		int m = dataset.size();
		for(int i = 0; i < m; i++){
			SampleSet sampleset = dataset.getSampleSet(i);
			for(int j = 0; j < k; j++)
				perf[j] += metrics[j].measure(sampleset.getLabels(), predict[i]);
		}

		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < k; i++)
			sb.append(perf[i]/m + "\t");
		sb.append("\r\n");

		FileManager.writeFile(output, sb.toString());
	}

	/**
	 * ��Ԥ����д��ָ���ļ�
	 * @param predict
	 * @param predictFile
	 */
	public void store(double[][] predict, String predictFile){
		StringBuffer sb = new StringBuffer();
		int size = predict.length;
		for(int i = 0; i < size; i++){
			int n = predict[i].length;

			for(int j = 0; j < n; j++)
				sb.append(predict[i][j] + "\r\n");
		}
		FileManager.writeFile(predictFile, sb.toString());
	}

	/**
	 * ���ݼ���ģ������֤��ָ��ָ���ϵı���ѡ��ģ��,��ʹ�����ڲ��Լ��Ͻ��в���
	 * @param ens
	 * @param valiset
	 * @param metric
	 * @param evalFile
	 * @return ��Ѽ���ģ�ͻ���ģ�͵ĸ���-1
	 */
	public int selectModel(Model ens, DataSet valiset, DataSet testset, 
			Metric metric, String evalFile)
	{
		int sz = ((EnsembleModel) ens).size();
		int m = valiset.size();
		double[][] prediction = new double[m][];
		
		int bestId = -1;
		double best = 0;
		for(int i = 0; i < sz; i++){
			Model weak = ((EnsembleModel) ens).getModel(i);
			double alpha = ((EnsembleModel) ens).getWeight(i);

			double perf = 0;
			SampleSet sampleset;
			for(int j = 0; j < m; j++){
				sampleset = valiset.getSampleSet(j);
				if(prediction[j] == null)
					prediction[j] = new double[sampleset.size()];
				
				prediction[j] = MathLib.linearCombinate(prediction[j], 1.0,
						weak.predict(sampleset), alpha);

				perf += metric.measure(sampleset.getLabels(), prediction[j]);
			}

			perf /= m;

			if(perf > best){
				best = perf;
				bestId = i;
			}
		}

		//����֤���ϱ�����ѵļ���ģ��
		Model model = ((EnsembleModel) ens).getSubEnsemble(0, bestId);
		prediction = model.predict(testset);
		eval(prediction, testset, testMetrics, evalFile);

		return bestId;
	}
}