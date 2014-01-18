package com.horsehour.metric;

import java.util.ArrayList;
import java.util.List;

/**
 * Metric������query-level��׼�����Ļ�����Ϊ
 * @author Chunheng Jiang
 * @version 2.0
 * @since 20130409
 */

public abstract class Metric {
	protected List<Integer> desire;//��ʵ�б�
	protected List<Double> predict;//Ԥ���б�
	
	public Metric(){}

	/**
	 * @param desire
	 * @param predict
	 * @return �����㷨������ָ��
	 */
	public abstract double measure(List<Integer> desire, List<Double> predict);

	public double measure(int[] desire, double[] predict){
		List<Integer> desireList = new ArrayList<Integer>();
		List<Double> predictList = new ArrayList<Double>();
		
		for(int i = 0; i < desire.length; i++){
			desireList.add(desire[i]);
			predictList.add(predict[i]);
		}
		return measure(desireList, predictList);
	}

	/**
	 * @return Metric����
	 */
	public abstract String name();
}
