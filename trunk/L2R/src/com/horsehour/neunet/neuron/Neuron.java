package com.horsehour.neunet.neuron;

import java.util.ArrayList;
import java.util.List;

import com.horsehour.function.ActivationFunction;
import com.horsehour.function.LogisticFunction;
import com.horsehour.neunet.Connector;
import com.horsehour.neunet.Layer;



public class Neuron {
	protected ActivationFunction activFunc;

	protected List<Connector> inputConnectors;
	protected List<Connector> outputConnectors;
	
	protected List<Double> outputs;
	
	protected double netInput = 0.0;
	
	private double localGradient = 0.0;
	
	protected float learningRate = 0.00005f;
//	protected float momentum = 0.0;
	
	public Neuron(){
		activFunc = new LogisticFunction();
		
		inputConnectors = new ArrayList<Connector>();
		outputConnectors = new ArrayList<Connector>();
		
		outputs = new ArrayList<Double>();
	}
	
	//��Ŀ��Layer����
	public void connectTo(Layer destLayer){
		for(Neuron neuron : destLayer.getNeurons())
			new Connector(this, neuron);
	}
	
	//��ǰ���������ź�
	public void propagate(){
		calcNetInput();
		calcOutput();
	}
	
	//TODO:����Neuroph��ܶ���WeightedSum����
	//���㾻����/Local Field-������ԴNeuron�������������Ȩ��
	private void calcNetInput(){
		netInput = 0.0f;
		Neuron srcNeuron = null;
		for(Connector connector : inputConnectors){
			srcNeuron = connector.getSrcNeuron();
			netInput += srcNeuron.getOutput() * connector.getWeight();
		}
	}
	
	//���ݾ�����/Local Field����ڵ�����-�ɴ̼�����ת��
	public void calcOutput(){
		double output = activFunc.calc(netInput);
		addOutput(output);
	}
	
	//Make sure the outputs has no content
	public void clearOutputs(){
		outputs = new ArrayList<Double>();
	}
	
	//������������Ȩ��
	public void updateWeight() {
		double weight = 0.0f, input = 0.0f;
		for(Connector inLink : inputConnectors){
			weight = inLink.getWeight();
			input = inLink.getSrcNeuron().getOutput();
			weight -= learningRate * localGradient * input;
			inLink.setWeight(weight);
		}
	}
	
	//�����������Local Gradientֵ-��Ҫ�ṩ��ʵ���ֵ����Backward Prop��ǰ��
	public void calcOutputLocalGradient(int desiredOutput) {
		double output = outputs.get(outputs.size() - 1);
		localGradient = (output - desiredOutput) * activFunc.calcDerivation(netInput); 
	}
	
	//�����������ؽ���Local Gradientֵ,ǰ����֪����ǰһ���Local Gradientֵ
	public void calcLocalGradient() {
		float weightSum = 0.0f;
		for(Connector outLink : outputConnectors)
			weightSum += outLink.getDestNeuron().getLocalGradient() * outLink.getWeight();
		
		localGradient = activFunc.calcDerivation(netInput) * weightSum;
	}
	
	//���ɱ��������ֵ
	public List<Double> getOutputList(){
		return outputs;
	}
	
	//���ɱ�������ĵ�idx�����ֵ
	public double getOutput(int idx){
		return outputs.get(idx);
	}
	
	//���ɱ������������ģ�Ҳ��outputs�����һ�����ֵ
	public double getOutput(){
		return outputs.get(outputs.size()-1);
	}
	
	//������б���������ֵ-Mainly for Input Neurons
	public void addOutput(double output){
		outputs.add(output);
	}
	
	//ȡ�ñ����ȫ������������
	public List<Connector> getInputConnectors(){
		return inputConnectors;
	}
	
	//ȡ�ñ����ȫ�����������
	public List<Connector> getOutputConnectors(){
		return outputConnectors;
	}
	
	//ȡ�ý���Local Gradient
	private double getLocalGradient() {
		return localGradient;
	}
	
	//����ѧϰ��
	public void setLearningRate(float lr){
		this.learningRate = lr;
	}
	
	public float getLearningRate(){
		return learningRate;
	}
}