package com.horsehour.ranker.trainer;

import java.util.Properties;

import com.horsehour.datum.DataSet;
import com.horsehour.datum.SampleSet;
import com.horsehour.metric.MAP;
import com.horsehour.metric.Metric;
import com.horsehour.model.Model;

/**
 * RankTrainer��������������ѵ����
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20130311
 */
public abstract class RankTrainer{
	public DataSet trainset;
	public DataSet valiset;

	public Metric trainMetric;
	public Metric valiMetric = new MAP();
	
	public Model plainModel;//��ϰģ��
	public Model bestModel;//ϰ��ģ��
	
	public String modelFile;
	public int nIter = 200;
	
	public Properties prop; 

	/**
	 * ��ʼ��
	 */
	public abstract void init();
	
	/**
	 * ѵ��
	 */
	public void train(){
		init();

		double vali = 0;
		double bestvali = -1;
		for(int iter = 0; iter < nIter; iter++){
			learn();//ѧϰ
			vali = validate();//��֤

			if(vali > bestvali){
				bestvali = vali;
				updateModel();
			}
		}
		storeModel();
	}
	
	/**
	 * ѧϰģ��
	 */
	protected abstract void learn();

	/**
	 * ʹ��ָ������ָ�����ģ���ڸ������ݼ��ϵ�����
	 * @param dataset
	 * @param metric
	 * @return ģ������
	 */
	protected double validate(DataSet dataset, Metric metric){
		double perf = 0;
		double[] predict;
		int m = dataset.size();
		SampleSet sampleset;

		for(int i = 0; i < m; i++){
			sampleset = dataset.getSampleSet(i);
			predict = plainModel.predict(sampleset);
			perf += metric.measure(sampleset.getLabels(), predict);
		}

		return perf/m;
	}

	/**
	 * ����ģ������֤���ϵ�����
	 * @return ģ������
	 */
	protected double validate(){
		return validate(valiset, valiMetric);
	}
	
	/**
	 * ����ģ��
	 */
	public abstract void updateModel();

	/**
	 * ����ģ��
	 */
	public abstract void storeModel();
	
	/**
	 * ���ļ�������ģ��
	 * @param modelFile
	 * @return
	 */
	public abstract Model loadModel(String modelFile);

	/**
	 * @return name
	 */
	public abstract String name();
}
