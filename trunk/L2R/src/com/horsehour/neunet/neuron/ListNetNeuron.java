package com.horsehour.neunet.neuron;

import java.util.ArrayList;
import java.util.List;

import com.horsehour.datum.SampleSet;
import com.horsehour.function.IdentityFunction;
import com.horsehour.neunet.Connector;

/**
 * @author Chunheng Jiang
 * @version 1.0
 * @see Cao, Z., T. Qin, et al. (2007). Learning to Rank: From pairwise approach 
 * to listwise approach. ICML '07: Proceedings of the 24th international conference
 * on Machine learning: 129-136.
 *
 */
public class ListNetNeuron extends Neuron{
	private List<Float> desirePermuteProb = null;
	private List<Float> predictPermuteProb = null;
	
	public ListNetNeuron(){
		super();
		activFunc = new IdentityFunction();		
	}
	
	//�����������ӵ�Ȩ��
	public void updateWeight(SampleSet sampleSet){
		//���ȼ���PermuteProb
		calcPermuteProb(sampleSet);
		
		Connector inLink = null;
		for(int inputId = 0; inputId < inputConnectors.size(); inputId++){
			float deltaWeight = 0.0f;
			inLink = inputConnectors.get(inputId);

			for(int idx = 0; idx < outputs.size(); idx++){
				deltaWeight += (predictPermuteProb.get(idx) - desirePermuteProb.get(idx)) *
						inLink.getSrcNeuron().getOutput(idx);
			}
			inLink.setWeight(inLink.getWeight() - learningRate * deltaWeight);
		}
	}
	
	//TODO: How about top k, where k=10, for example?
	//����Top One���и��ʷֲ�-��ʵ�����и��ʷֲ���Ԥ������и��ʷֲ�
	public void calcPermuteProb(SampleSet sampleSet){
		desirePermuteProb = new ArrayList<Float>();
		predictPermuteProb = new ArrayList<Float>();
		
		float desireExpSum = 0.0f, predictExpSum = 0.0f;
		
		int len = outputs.size();
		for(int idx = 0; idx < len; idx++){
			desireExpSum += Math.exp(sampleSet.getLabel(idx));
			predictExpSum += Math.exp(outputs.get(idx));
		}
		
		for(int idx = 0; idx < len; idx++){
			desirePermuteProb.add((float)Math.exp(sampleSet.getLabel(idx))/desireExpSum);
			predictPermuteProb.add((float)Math.exp(outputs.get(idx))/predictExpSum);
		}
	}
	
	//Ԥ������зֲ�
	public List<Float> getPredictPermuteProb(){
		return predictPermuteProb;
	}
	
	//��ʵ�����зֲ�
	public List<Float> getDesirePermuteProb(){
		return desirePermuteProb;
	}
}