package com.horsehour.model;

import java.util.Arrays;

import com.horsehour.datum.Sample;

/**
 * ����ģ��
 * @author Chunheng Jiang
 * @version 2.0
 * @since 20121228
 */
public class LinearModel extends Model{
	private static final long serialVersionUID = -3492924447957799775L;
	
	public double[] weight;

	public LinearModel(){}

	public LinearModel(double[] w){
		weight = Arrays.copyOf(w, w.length);
	}

	public LinearModel(LinearModel model){
		double[] w = model.getWeight();
		weight = Arrays.copyOf(w, w.length);
	}

	/**
	 * Ԥ�������ķ�ֵ
	 * @param sample
	 * @return score of sample
	 */
	public double predict(Sample sample){
		double[] feature = sample.getFeatures();
		double ret = 0;
		for(int i = 0; i < feature.length; i++)
			ret += weight[i] * feature[i];
		
		return ret;
	}

	/**
	 * �ھ�ģ�ͻ����ϸ���
	 * @param delta
	 */
	public void addUpdate(double[] delta){
		for(int i = 0; i < weight.length; i++)
			weight[i] += delta[i];
	}

	/**
	 * ʹ����ģ�������ģ��
	 * @param newweight
	 */
	public void update(double[] newweight){
		weight = Arrays.copyOf(newweight, newweight.length);
	}

	public double[] getWeight(){
		return weight;
	}

	public String toString(){
		int sz = weight.length;
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < sz - 1; i++)
			sb.append(weight[i] + "\t");
		sb.append(weight[sz-1]);
		return sb.toString();
	}
}
