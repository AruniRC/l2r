package com.horsehour.datum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.horsehour.math.MathLib;

/**
 * SampleSet�������ݼ�
 * @author Chunheng Jiang
 * @version 2.0
 * @since 20131024 �����Ӽ���ȡ
 */
public class SampleSet{
	private List<Sample> samples;
	
	public SampleSet(){
		samples = new ArrayList<Sample>();
	}
	
	/**
	 * ȡ��������Ŀ
	 * @return samples' size
	 */
	public int size(){
		return samples.size();
	}
	
	/**
	 * ȡ��ȫ������
	 * @return all the samples in sampleset
	 */
	public List<Sample> getSamples(){
		return samples;
	}

	/**
	 * @param num
	 * @param with replacement
	 * @return �ز�����num��������
	 */
	public SampleSet resample(int num, boolean with){
		int sz = size(), id;
		SampleSet ss = new SampleSet();
		if(with){
			for(int i = 0; i < num; i++){
				id = MathLib.rand(0, sz - 1);
				ss.addSample(samples.get(id));
			}
		}else if(num < sz){
			List<Integer> ids = MathLib.randUnique(0, sz-1, num);
			Collections.sort(ids);

			for(int i = num - 1; i >= 0; i--)
				ss.addSample(samples.remove(i));
		}
		
		return ss;
	}

	/**
	 * Retrieves and removes num sample from samples
	 * @param num
	 * @return ��samples�г���num������
	 */
	public SampleSet pollSamples(int num){
		int sz = size();
		if(num > sz)
			return null;

		List<Integer> ids = MathLib.randUnique(0, sz-1, num);
		Collections.sort(ids);
		
		SampleSet ss = new SampleSet();
		for(int i = num - 1; i >= 0; i--)
			ss.addSample(samples.remove(i));

		return ss;
	}

	/**
	 * �������
	 * @param sample
	 */
	public void addSample(Sample sample){
		samples.add(sample);
	}
	
	/**
	 * ȡ��ȫ��������label
	 * @return all the label of sample in sampleset
	 */
	public List<Integer> getLabelList() {
		List<Integer> labels = new ArrayList<Integer>();
		for(Sample sample : samples)
			labels.add(sample.getLabel());
		
		return labels;
	}

	public int[] getLabels() {
		int sz = size();
		int[] labels = new int[sz];
		for(int i = 0; i < sz; i++)
			labels[i] = getLabel(i);
		return labels;
	}
	
	/**
	 * @return ������꼯��
	 */
	public List<Integer> getUniqueLabels(){
		TreeSet<Integer> labels = new TreeSet<Integer>();
		labels.addAll(getLabelList());
		List<Integer> group = new ArrayList<Integer>();
		Iterator<Integer> iter = labels.iterator();
		while(iter.hasNext())
			group.add(iter.next());
		return group;
	}

	/**
	 * ȡ��ָ��λ��������label
	 * @param idx
	 * @return given sample's label
	 */
	public int getLabel(int idx){
		return samples.get(idx).getLabel();
	}
	
	/**
	 * ȡ��ָ��λ�õ�����
	 * @param idx
	 * @return sample at idx
	 */
	public Sample getSample(int idx) {
		return samples.get(idx);
	}
	
	public void removeSample(int idx){
		samples.remove(idx);
	}
	/**
	 * ȡ��ά��
	 * @return sample's dimension
	 */
	public int getDim() {
		return samples.get(0).getDim();
	}
	
	/**
	 * ��ȡָ��ȫ��������ָ��ά������ֵ
	 * @param fid
	 * @return feature list at fid
	 */
	public List<Double> getFeatureList(int fid){
		List<Double> featureVals = new ArrayList<Double>();
		for(Sample sample : samples)
			featureVals.add(sample.getFeature(fid));
		
		return featureVals;
	}
	
	public double[] getFeatures(int fid){
		int sz = size();
		double[] featureValue = new double[sz];
		for(int i = 0; i < sz; i++)
			featureValue[i] = samples.get(i).getFeature(fid);
		return featureValue;
	}
	/**
	 * @param fid
	 * @param theta
	 * @return ��ȡ����ֵ����theta�������б�
	 */
	public List<Integer> getSampleIndex(int fid, double theta){
		List<Integer> idx = new ArrayList<Integer>();
		int i = 0;
		for(double val : getFeatureList(fid)){
			if(val > theta)
				idx.add(i);
			i++;
		}
		return idx;
	}
	
	/**
	 * @param subset
	 * @return ����ѡȡָ����һ���Ӽ�
	 */
	public SampleSet subset(List<Integer> subset){
		SampleSet sub = new SampleSet();
		for(int i : subset)
			sub.addSample(samples.get(i));
		return sub;
	}
	
	/**
	 * ͳ��ָ������µ���������
	 * @param labels
	 * @return �����ֲ�
	 */
	public int[] getDistribute(List<Integer> labels){
		int m = samples.size(), n = labels.size();
		Map<Integer, Integer> stat = new HashMap<Integer, Integer>();
		for(int label : labels)
			stat.put(label, 0);
		
		int count = 0;
		for(int i = 0; i < m; i++){
			int label = samples.get(i).getLabel();
			if(stat.containsKey(label)){
				count = stat.get(label);
				stat.put(label, count + 1);
			}
		}

		int[] distr = new int[n];
		for(int i = 0; i < n; i++)
			distr[i] = stat.get(labels.get(i));
		
		return distr;
	}
	
	/**
	 * ���ݱ�ǩ���з���
	 * @return ���ձ�ǩ������
	 */
	public List<List<Integer>> group(){
		List<Integer> levelList = new ArrayList<Integer>();//��صȼ��б�
		List<List<Integer>> cluster = new ArrayList<List<Integer>>();//ÿ����صȼ���Ӧ���ĵ��б�

		int n = size();
		for(int i = 0; i < n; i++){
			int level = getLabel(i);
			int idx = levelList.indexOf(level);
			if(idx == -1){
				levelList.add(level);
				cluster.add(new ArrayList<Integer>());
				idx = cluster.size() - 1;
			}
			cluster.get(idx).add(i);
		}
		return cluster;
	}
}