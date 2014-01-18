package com.horsehour.math;

import java.util.Random;

/**
 * ģ���˻��㷨����׼��ʽ����С��
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20130714
 */
public class SimulatedAnnealing {
	public int iter = 100;

	public double temperature = 10000.0;
	public double minTemperature = 0.00001;

	public double boltzman = 0.9999;

	protected ParticleState previousState = null;
	protected ParticleState disturbedState = null;
	
	public SimulatedAnnealing(){}

	public void init(ParticleState state){
		previousState = state;
	}
	
	/**
	 * @param state
	 * @return ��������״̬�µ����� 
	 */
	public double calcEnergy(ParticleState state){
		return 0;
	}
	
	/**
	 * ����״̬�����Ŷ�
	 * @param initState
	 * @param initEnergy ��ʼ״̬������
	 * @return �Ŷ�������״̬�����ı�
	 */
	public double disturbState(ParticleState initState, double initEnergy){
		//���ѡ��-����Ŀ�꺯����ȷ��������ʽ�ı�
		disturbedState = null;
		return calcEnergy(disturbedState) - initEnergy;
	}

	/**
	 * ���ܲ����µ���״̬
	 * @param state
	 * @return ��ʼ״̬����
	 */
	public void acceptState(ParticleState state){
		previousState = state;
	}
	
	public void simulate(){
		double energy = calcEnergy(previousState), energyDiff = 0, prob = 0;
		
		Random rand = new Random();
		boolean accept = false;

		while((temperature > minTemperature)||(iter > 0)){
			energyDiff = disturbState(previousState, energy);

			prob = Math.exp(-energyDiff/(boltzman*temperature));//TODO:�����Ƿ���Ҫboltzman����
			
			accept = ((energyDiff < 0)||(energyDiff * (prob - rand.nextDouble())>= 0));
			if(accept){
				acceptState(disturbedState);
				energy += energyDiff;
			}

			//�����˻�
			temperature *= boltzman;
			
			iter--;
		}
	}

	/**
	 * ����״̬
	 * @author Chunheng Jiang
	 * @version 1.0
	 * @since 20130714
	 */
	protected class ParticleState{
		
	};
}
