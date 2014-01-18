package com.horsehour.datum;

import java.util.Arrays;

/**
 * Sample����������������������ݳ�Ա��
 * y��<x1,x2,...,xn>
 * ���У�y��ʾ�����ı��
 * 
 * �����������ļ��еı����ʽ��������ʾ��
 * 0 qid:167 1:0.34 2:1.0 ... 45:0.20 ...
 * ���������֣���һ�����������/��صȼ����ڶ��б�ʾ���������ļ�����id�������б�ʾ��������/����ֵ
 * 
 * @author Chunheng Jiang
 * @version 1.0
 */
public class Sample implements Comparable<Sample>{
	private int dim = 0;
	private int label = 0;
	private String qid = "";
	private double[] features;
	
	/**
	 * ������֪����������������ݹ�����������
	 * @param features
	 * @param label
	 */
	public Sample(double[] features, int label){
		this.dim = features.length;
		this.features = Arrays.copyOf(features, dim);
		this.label = label;
	}
	
	public Sample(double[] features, int label, String qid){
		this(features, label);
		this.qid = qid;
	}

	/**
	 * ʹ�ò������������µ�Sample����
	 * @param sample
	 * @param fids
	 */
	public Sample(Sample sample, int[] fids) {
		this.dim = fids.length;
		this.label = sample.label;
		this.features = new double[dim];
		double[] oldFeatures = sample.features;
		for(int i = 0; i < dim; i++)
			features[i] = oldFeatures[fids[i]];
	}

	/**
	 * ��ȡ����
	 */
	public double[] getFeatures(){
		return features;
	}
	
	/**
	 * ��ȡָ��ά�ȵ�����ֵ
	 * @param featureId
	 * @return given feature
	 */
	public double getFeature(int featureId){
		return features[featureId];
	}
	
	/**
	 * ����ָ��ά�ȵ�����
	 * @param featureId
	 * @param val
	 */
	public void setFeature(int featureId, double val){
		features[featureId] = val;
	}

	/**
	 * ��չ��������
	 * @param f
	 */
	public void addFeature(double f){
		double[] precFeature;
		dim += 1;
		precFeature = Arrays.copyOf(features, dim);
		precFeature[dim - 1] = f;
		features = Arrays.copyOf(precFeature, dim);
	}
	
	/**
	 * ����������Label
	 * @return lable of sample
	 */
	public int getLabel(){
		return label;
	}
	
	/**
	 * ����label
	 * @param label
	 */
	public void setLabel(int label){
		this.label = label;
	}
	
	/**
	 * ����������ά��
	 * @return dimension of sample
	 */
	public int getDim(){
		return dim;
	}

	/**
	 * �����������������ݼ���
	 * @return query_id the sample belongs to
	 */
	public String getQid(){
		return qid;
	}

	@Override
	public int compareTo(Sample sample) {
		return Integer.compare(sample.getLabel(), label);
	}
}