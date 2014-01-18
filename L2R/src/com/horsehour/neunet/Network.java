package com.horsehour.neunet;

import java.util.ArrayList;
import java.util.List;

import com.horsehour.datum.DataSet;
import com.horsehour.datum.Sample;
import com.horsehour.datum.SampleSet;
import com.horsehour.math.MathLib;
import com.horsehour.model.Model;
import com.horsehour.neunet.neuron.Neuron;

/**
 * @author Chunheng Jiang
 * @version 3.0
 * @see Simon Haykin, Neural Networks and Learning Machines, 3rd edition, p122-38 
 * @since 20131216
 */
public class Network extends Model{
	private static final long serialVersionUID = 3033820560795032478L;
	
	public List<Layer> layers;
	public List<List<Double>> netWeights;
	public int numLayer = 2;//At least(����㡢�����)

	protected NetUser user;
	protected boolean bias = false;
	protected int numOutputNeuron = 1;

	public Network(Network net){
		layers = net.layers;
		numLayer = net.numLayer;
		numOutputNeuron = net.numOutputNeuron;
		bias = net.bias;
		user = net.user;
		netWeights = net.netWeights;
	}

	/**
	 * @param user
	 * @param nInput
	 * @param nHidden
	 * @param nOutput
	 * @param bias
	 */
	public Network(NetUser user, boolean bias, int nInput, int nOutput, int... nHidden){
		this.user = user;
		this.bias = bias;

		this.numOutputNeuron = nOutput;
		layers = new ArrayList<Layer>();
		
		if(bias)
			nInput += 1;//plus bias

		layers.add(new Layer(nInput, user));//input layer

		int nHiddenLayer;
		if(nHidden == null)
			nHiddenLayer = 0;
		else
			nHiddenLayer = nHidden.length;
		numLayer = nHiddenLayer + 2;
		
		for(int i = 0; i < nHiddenLayer; i++)
			layers.add(new Layer(nHidden[i], user));//hidden layer
				
		layers.add(new Layer(nOutput, user));//output layer
		
		connectNet();
	}

	/**
	 * ��������
	 */
	private void connectNet(){
		Layer inputLayer = layers.get(0);
		inputLayer.connectTo(layers.get(1));//���������(����bias,����)�����ز�
		
		if(bias){
			Neuron biasNeuron = inputLayer.getNeuron(inputLayer.size() - 1);
			for(int id = 1; id < numLayer - 1; id++)
				biasNeuron.connectTo(layers.get(id + 1));//����bias neuron����̸����neuron
		}
		
		for(int id = 1; id < numLayer - 1; id++){
			Layer layer = layers.get(id);
			layer.connectTo(layers.get(id + 1));//����ǰһ������һ��
		}
	}
	
	/**
	 * ��������Feed����,����ǰ����
	 * @param sample
	 */
	public void forwardProp(Sample sample){
		//Feed data
		Layer inputLayer = layers.get(0);
		int numBias = bias ? 1 : 0;

		List<Neuron> neurons = inputLayer.getNeurons();
		for(int idx = 0; idx < neurons.size() - numBias; idx++)
			neurons.get(idx).addOutput(sample.getFeature(idx));

		if(bias)
			neurons.get(neurons.size() - 1).addOutput(1.0f);//for bias

		//Propagate data
		for(int idx = 1; idx < numLayer; idx++)
			layers.get(idx).propagate();
	}
	
	/**
	 * ������н�������б��е�����Ϊ����ѭ����׼��
	 * ��ѵ��ʹ������ʱһ��Ҫ�ڲ�ʹ��ʱ�������,��Ȼ�ڴ潫Ѹ�����
	 */
	public void clearOutputs(){
		for(Layer layer : layers)
			for(Neuron neuron : layer.getNeurons())
				neuron.clearOutputs();
	}
	
	/**
	 * ��ȡ���������Ŀ
	 * @return numOutputNeuron
	 */
	public int getNumOutputNeuron(){
		return numOutputNeuron;
	}
	
	/**
	 * ָ��id��layer
	 * @param layerId
	 * @return layer at layerId
	 */
	public Layer getLayer(int layerId){
		return layers.get(layerId);
	}
	
	/**
	 * ����Learning Rate
	 * @param lr
	 */
	public void setLearningRate(float lr) {
		for(Layer layer : layers)
			for(Neuron neuron : layer.getNeurons())
				neuron.setLearningRate(lr);
	}
	
	/**
	 * ȡ����Ԫ��Learning Rate
	 * @return learning rate of neuron
	 */
	public float getLearningRate() {
		return layers.get(0).getNeuron(0).getLearningRate(); 
	}
	
	/**
	 * ���浱ǰ������Ȩֵ: ÿһ�㱣�浽һ��List��
	 */
	public void updateWeight(){
		netWeights = new ArrayList<List<Double>>();
		for(int idx = 0; idx < numLayer - 1; idx ++){
			List<Double> weights = new ArrayList<Double>();
			
			for(Neuron neuron : layers.get(idx).getNeurons()){
				List<Connector> outCon = neuron.getOutputConnectors();
				for(int id = 0; id < outCon.size(); id++)
					weights.add(outCon.get(id).getWeight());
			}
			netWeights.add(weights);
		}
	}

	/**
	 * ��netWeights�е�Ȩ�����¸�������
	 */
	public void reweight(){
		for(int idx = 0; idx < numLayer - 1; idx ++){
			List<Double> weights = netWeights.get(idx);
			int count = 0;			
			for(Neuron neuron : layers.get(idx).getNeurons()){
				for(int id = 0, sz = neuron.getOutputConnectors().size(); id < sz; id++){
					neuron.getOutputConnectors().get(id).setWeight(weights.get(count));
					count++;
				}
			}
		}
	}

	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < numLayer - 1; i ++){
			List<Double> weights = netWeights.get(i);
			for(double weight : weights)
				sb.append(weight + "\t");
			sb.append("\r\n");
		}
		return sb.toString();
	}

	/**
	 * Ԥ�����ݼ�
	 * @param dataset
	 * @return Ԥ���ֵ
	 */
	public double[][] predict(DataSet dataset){
		reweight();
		return super.predict(dataset);
	}

	/**
	 * @param sampleset
	 * @return Ԥ���ֵ
	 */
	public double[] predict(SampleSet sampleset){
		clearOutputs();
		Sample sample;
		for(int id = 0; id < sampleset.size(); id++){
			sample = sampleset.getSample(id);
			forwardProp(sample);
		}
		List<Double> predict;
		Layer outputLayer;
		Neuron outputNeuron;

		outputLayer = layers.get(layers.size() - 1);
		outputNeuron = outputLayer.getNeuron(0);

		predict = outputNeuron.getOutputList();

		return MathLib.listToArray(predict);
	}

	@Override
	public double predict(Sample sample) {
		return 0;
	}
}