package com.horsehour.neunet.neuron;

import com.horsehour.function.LogisticFunction;
import com.horsehour.neunet.Connector;

/**
 * @version 1.0
 * @modified 11/21/2012
 * @author Chunheng Jiang
 * @see Burges, C. J. C., T. Shaked, et al. (2005). Learning to rank using gradient descent. 
 * ICML '05:  Proceedings of the 22th International Conference on Machine Learning. New York: 89-96.
 *
 */
public class RankNetNeuron extends Neuron{
	private double currentLocalGradient = 0.0f;
	private double[] pairLocalGradient;
	
	public RankNetNeuron(){
		super();
		activFunc = new LogisticFunction();
	}
	
	/**
	 * ���㵱ǰsample��Ӧ��pairs��Local Gradient
	 * @param current
	 * @param pair
	 */
	public void calcOutputLocalGradient(int current, int[] pair){
		currentLocalGradient = 0;
		pairLocalGradient = new double[pair.length];
		
		double sumProb = 0;
		double derivation;
		for(int idx = 0; idx < pair.length; idx++){
			double prob = (float) (-1/(1 + Math.exp(outputs.get(current) - outputs.get(pair[idx]))));//P-1

			//TODO��������/Induced Local Fieldδ����,�������ֱ�Ӽ��㵼��ֵ���������ڴ̼����������ޣ�
			derivation = outputs.get(pair[idx]) * (1- outputs.get(pair[idx]));
			pairLocalGradient[idx] = prob * derivation;

			sumProb += prob;
		}

		derivation = outputs.get(current) * (1 - outputs.get(current));
		currentLocalGradient = sumProb * derivation;
	}
	
	/**
	 * ����ָ��sample��Local Gradient,��������pairs
	 * @param current
	 * @param pair
	 */
	public void calcLocalGradient(int current, int[] pair){
		currentLocalGradient = 0;
		pairLocalGradient = new double[pair.length];
		
		for(Connector outLink : outputConnectors){
			RankNetNeuron dest = (RankNetNeuron)outLink.getDestNeuron();
			currentLocalGradient += outLink.getWeight() * dest.getCurrentLocalGradient();
		}
		double derivation = outputs.get(current) * (1 - outputs.get(current));
		currentLocalGradient *= derivation;
		
		for(int idx = 0; idx < pair.length; idx++){
			for(Connector outLink : outputConnectors){
				RankNetNeuron dest = (RankNetNeuron)outLink.getDestNeuron();
				pairLocalGradient[idx] += outLink.getWeight() * dest.getPairLocalGradient(idx);
			}

			derivation = outputs.get(pair[idx]) * (1 - outputs.get(pair[idx]));
			pairLocalGradient[idx] *= derivation;
		}
	}
	
	/**
	 * ���õ�ǰsample��id����pairs��id��������Ȩֵ
	 * @param current
	 * @param pair
	 */
	public void updateWeight(int current, int[] pair){
		double deltaWeight = 0.0f;
		for(Connector inLink : inputConnectors){
			deltaWeight += currentLocalGradient * inLink.getSrcNeuron().getOutput(current);
			for(int idx = 0; idx < pair.length; idx++)
				deltaWeight -= pairLocalGradient[idx] * inLink.getSrcNeuron().getOutput(pair[idx]);
			
			double weight = inLink.getWeight() - learningRate * deltaWeight;
			inLink.setWeight(weight);
		}
	}
	
	/**
	 * ȡ�õ�ǰsample,ĳ��pair��Local Gradient
	 * @param pairId
	 * @return
	 */
	private double getPairLocalGradient(int pairId){
		return pairLocalGradient[pairId];
	}
	
	/**
	 * ȡ�õ�ǰsample��Local Gradient
	 * @return
	 */
	private double getCurrentLocalGradient(){
		return currentLocalGradient;
	}
}
