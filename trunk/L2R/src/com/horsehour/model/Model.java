package com.horsehour.model;

import java.io.Serializable;

import com.horsehour.datum.DataSet;
import com.horsehour.datum.Sample;
import com.horsehour.datum.SampleSet;
import com.horsehour.math.MathLib;
import com.horsehour.util.DeepCopy;

/**
 * ����ģ��-���ࡢ���ࡢ�ع�������
 * @author Chunheng Jiang
 * @version 2.0
 * @since 20130311
 * @since 20131115
 */
public abstract class Model implements Serializable{
	private static final long serialVersionUID = 716029837150978339L;

	/**
	 * Ԥ�����ݼ�
	 * @param dataset
	 * @return Ԥ����
	 */
	public double[][] predict(DataSet dataset){
		int sz = dataset.size();
		double[][] scores = new double[sz][];
		for(int i = 0; i < sz; i++)
			scores[i] = predict(dataset.getSampleSet(i));
		return scores;
	}

	/**
	 * Ԥ�����ݼ�
	 * @param dataset
	 * @return �����ʲ㼶�ϵı�׼��Ԥ���ֵ
	 */
	public double[][] normPredict(DataSet dataset){
		int sz = dataset.size();
		double[][] scores = new double[sz][];
		for(int i = 0; i < sz; i++)
			scores[i] = normPredict(dataset.getSampleSet(i));
		return scores;
	}

	/**
	 * Ԥ�ⵥ�������ʹ����Ķ���ĵ�����ط�ֵ
	 * @param sampleset
	 * @return ��ط�ֵ�б�
	 */
	public double[] predict(SampleSet sampleset){
		int sz = sampleset.size();
		double[] score = new double[sz];

		for(int i = 0; i < sz; i++)
			score[i] = predict(sampleset.getSample(i));
		
		return score;
	}

	/**
	 * Ԥ�ⵥ�������ʹ����ĵ��ϵ���ط�ֵ
	 * @param sampleset
	 * @return �����ʲ㼶�ϵı�׼��Ԥ����
	 */
	public double[]	normPredict(SampleSet sampleset){
		int sz = sampleset.size();
		double[] score = new double[sz];

		for(int i = 0; i < sz; i++)
			score[i] = predict(sampleset.getSample(i));
		
		//Ĭ��ʹ�����ֵ��׼������,һ����Ա�׼����Ӱ���ĵ�����,�����ڼ���ģ����Ӱ��
		MathLib.maxNormalize(score);
		return score;
	}

	/**
	 * Ԥ�ⵥ������
	 * @param sample
	 * @return ����������Ԥ���ֵ
	 */
	public abstract double predict(Sample sample);

	public Model copy(){
		return (Model) DeepCopy.copy(this);
	}
}
