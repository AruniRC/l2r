package com.horsehour.neunet.neuron;

import java.util.ArrayList;
import java.util.List;

import com.horsehour.neunet.Connector;

/**
 * ����ѵ��ListMLE����Ԫ
 * @author Chunheng Jiang
 * @version 1.0
 */
public class ListMLENeuron extends Neuron{
	private float[] expPredict = null;
	
	/**
	 * ��������Ȩֵ
	 * @param topk
	 * @param rank
	 */
	public void updateWeight(int topk, int[] rank){
		calcExpPredict(rank);
		
		topk = (outputs.size() > topk) ? topk : outputs.size();
		
		Connector inCon = null;
		for(int id = 0; id < inputConnectors.size(); id++){
			inCon = inputConnectors.get(id);

			float deltaWeight = 0;
			List<Float> lambda = null;
			
			for(int i = 0; i < topk; i++){
				lambda = calcLambda(i);
				for(int l = i; l < outputs.size(); l++)
					deltaWeight += lambda.get(l - i) * 
					(inCon.getSrcNeuron().getOutput(rank[l]) - inCon.getSrcNeuron().getOutput(rank[i]));
			}
			
			inCon.setWeight(inCon.getWeight() - learningRate * deltaWeight);
		}		
	}

	/**
	 * ��������Ȩֵ
	 * ���û�о�������topk,��Ĭ��ʹ��ȫ�����
	 */
	public void updateWeight(int[] rank){
		updateWeight(outputs.size(), rank);
	}
	
	/**
	 * ָ����
	 */
	private void calcExpPredict(int[] rank) {
		expPredict = new float[outputs.size()];
		for(int idx = 0; idx < outputs.size(); idx++)
			expPredict[idx] = (float) Math.exp(outputs.get(rank[idx]));
	}
	
	/**
	 * ����Lambda,�ο��Ƶ��ı�
	 * @param currentId
	 * @return lambda
	 */
	private List<Float> calcLambda(int currentId){
		List<Float> lambda = new ArrayList<Float>();
		float sumExpPredict = 0;
		for(int l = currentId; l < outputs.size(); l++)
			sumExpPredict += expPredict[l];
		
		for(int l = currentId; l < outputs.size(); l++)
			lambda.add(expPredict[l]/sumExpPredict);
		
		return lambda;
	}
}
