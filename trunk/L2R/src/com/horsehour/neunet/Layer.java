package com.horsehour.neunet;

import java.util.ArrayList;
import java.util.List;

import com.horsehour.neunet.neuron.ListMLENeuron;
import com.horsehour.neunet.neuron.ListNetNeuron;
import com.horsehour.neunet.neuron.Neuron;
import com.horsehour.neunet.neuron.RankNetNeuron;

/**
 * �������-����㡢���ز�������
 * @author Chunheng Jiang
 * @version 1.0
 */
public class Layer {
	private List<Neuron> neurons = null;
	private int size = 0;
	
	//Construction based on the user
	public Layer(int size, NetUser user) {
		this.size = size;
		neurons = new ArrayList<Neuron>();
		
		for(int i = 0; i < size; i++){
			if(user == NetUser.RankNet)//For RankNet
				neurons.add(new RankNetNeuron());
			
			else if(user == NetUser.ListNet)//For ListNet
				neurons.add(new ListNetNeuron());
			
			else if(user == NetUser.ListMLE)//For ListMLE
				neurons.add(new ListMLENeuron());
			
			else
				neurons.add(new Neuron());
		}
	}

	//����Layer-Build Net
	public void connectTo(Layer destLayer) {
		for(Neuron neuron : neurons)
			neuron.connectTo(destLayer);
	}
	
	//���������ź�
	public void propagate(){
		for(Neuron neuron : neurons)
			neuron.propagate();
	}
	
	//����ÿ��������
	public void calcOutput(){
		for(Neuron neuron : neurons)
			neuron.calcOutput();
	}
	
	//�������ؽ���Local Gradient
	public void calcLocalGradient() {
		for(Neuron neuron : neurons)
			neuron.calcLocalGradient();
	}
	
	//�����������Local Gradient
	public void calcOutputLocalGradient(int[] labels) {
		Neuron neuron = null;
		for(int idx = 0; idx < neurons.size(); idx++){
			neuron = neurons.get(idx);
			neuron.calcOutputLocalGradient(labels[idx]);
		}
	}
	
	//RankNet:����Local Gradient
	public void calcLocalGradient(int current, int[] pair) {
		for(Neuron neuron : neurons)
			((RankNetNeuron)neuron).calcLocalGradient(current, pair);
	}
	
	//RankNet:����Ȩ��
	public void updateWeight(int current, int[] pair){
		for(Neuron neuron : neurons)
			((RankNetNeuron)neuron).updateWeight(current, pair);
	}
	
	//��������Ȩ��
	public void updateWeight() {
		for(Neuron neuron : neurons)
			neuron.updateWeight();
	}
	
	//ȡ�ñ���ȫ������б�
	public List<Neuron> getNeurons() {
		return neurons;
	}
	
	//ȡ��ָ��id�Ľ��
	public Neuron getNeuron(int idx) {
		return neurons.get(idx); 
	}
	
	//ȡ�ñ���Ľ����Ŀ
	public int size() {
		return size;
	}
}