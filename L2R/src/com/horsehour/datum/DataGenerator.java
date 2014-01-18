package com.horsehour.datum;

import java.util.Random;

public class DataGenerator {

	private static float theta = -0.8f;
	
	//����ָ����Ŀ��ָ��ά����������� 
	public static void generate(SampleSet sampleSet, int numSample, int dim){
		
		Random rand = new Random();
		double[] features = new double[dim];
		
		for(int i = 0; i < numSample; i++){
			for(int d = 0; d < dim; d++)
				features[d] = rand.nextFloat();
			
			sampleSet.addSample(new Sample(features, getLabel(features)));
		}
	}

	//ȷ��Label
	//�������2*x1 - e^x2 > theta, ��Ϊ��������֮Ϊ����
	private static int getLabel(double[] features){
		int label = (2* features[0] - Math.exp(features[1]) > theta)? 1 : -1;
		return label;
	}
}
