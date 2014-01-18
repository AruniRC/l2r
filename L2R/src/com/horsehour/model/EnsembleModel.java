package com.horsehour.model;

import java.util.ArrayList;
import java.util.List;

import com.horsehour.datum.Sample;
import com.horsehour.datum.SampleSet;
import com.horsehour.math.MathLib;

/**
 * ����ģ��
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20131115
 */
public class EnsembleModel extends Model{
	private static final long serialVersionUID = -6085714636543844831L;
	
	public List<Model> member;
	public List<Double> weight;

	public EnsembleModel(){}

	public EnsembleModel(List<Model> m, List<Double> w){
		member = m;
		weight = w;
	}

	/**
	 * ��ӻ���ģ��
	 * @param model
	 * @param alpha
	 */
	public void addMember(Model model, double alpha){
		if(member == null){
			member = new ArrayList<Model>();
			weight = new ArrayList<Double>();
		}

		member.add(model);
		weight.add(alpha);
	}

	/**
	 * �Ƴ�ָ��λ�õĻ���ģ��
	 * @param id
	 */
	public void removeMember(int id){
		if(member == null || id > size() - 1)
			return;

		member.remove(id);
		weight.remove(id);
	}
	
	public void removeLastMember(){
		if(member == null || size() == 0)
			return;
		
		int sz = size();
		member.remove(sz - 1);
		weight.remove(sz - 1);
	}
	
	public void addModel(Model model){
		if(member == null || size() == 0)
			member = new ArrayList<Model>();
		
		member.add(model);
	}

	/**
	 * @param id
	 * @return ����ģ��
	 */
	public Model getModel(int id){
		return member.get(id);
	}
	public Model getLastModel(){
		int idx = member.size();
		return member.get(idx - 1);
	}
	
	public void addWeight(double alpha){
		if(weight == null)
			weight = new ArrayList<Double>();
		
		weight.add(alpha);
	}
	/**
	 * @param id
	 * @return ����ģ�͵�Ȩֵ
	 */
	public double getWeight(int id){
		return weight.get(id);
	}
	
	public double getLastWeight(){
		int idx = weight.size();
		return weight.get(idx - 1);
	}
	/**
	 * ����ָ��λ�û���ģ�͵�Ȩֵ
	 * @param id
	 * @param alpha
	 */
	public void updateWeight(int id, double newalpha){
		weight.set(id, newalpha);
	}

	/**
	 * @return ����ģ�͵ĸ���
	 */
	public int size(){
		return member.size();
	}
	
	/**
	 * ��ȡ����ģ���Ӽ�
	 * @param fromIdx
	 * @param toIdx
	 * @return [fromIdx, toIdx]��Χ�ڵ�ģ��
	 */
	public EnsembleModel getSubEnsemble(int fromIdx, int toIdx){
		EnsembleModel subEns = new EnsembleModel();
		for(int i = fromIdx; i <= toIdx; i++)
			subEns.addMember(getModel(i), getWeight(i));
		return subEns;
	}

	@Override
	public double[] normPredict(SampleSet sampleset){
		double[] score = new double[sampleset.size()];
		int m = member.size(); 
		for(int i = 0; i < m; i++)
			score = MathLib.linearCombinate(score, 1.0, 
					member.get(i).normPredict(sampleset), weight.get(i));

		MathLib.maxNormalize(score);
		return score;	
	}

	@Override
	public double predict(Sample sample) {
		double score = 0;
		int sz = member.size(); 
		for(int i = 0; i < sz; i++)
			score += weight.get(i) * member.get(i).predict(sample);
		return score;
	}

	public String toString(){
		StringBuffer sb = new StringBuffer();
		int sz = member.size();
		for(int i = 0; i < sz; i++)
			sb.append(member.get(i).toString() + "\t" + weight.get(i) + "\r\n");

		return sb.toString();
	}
}
