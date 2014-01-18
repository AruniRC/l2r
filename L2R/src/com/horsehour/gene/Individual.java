package com.horsehour.gene;

import java.util.BitSet;
/**
 * Individual������,���Ŵ��㷨�еĻ�����λ
 * @author Chunheng Jiang
 * @version 1.0
 * @since 2012-12-18
 */
public class Individual {
	private BitSet genes = null;
	private int geneLen = 0;
	
	private double lowerBound = 0;
	private int nDigit = 0;
	
	public Individual(int geneLen){
		this.genes = new BitSet(geneLen);
	}

	/**
	 * ��ʮ������ת��Ϊ����������
	 * @param dec
	 * @return such as 01000100
	 */
	public void encode(double dec){
		//������lowerBound���ı���,ȷ������������
		int diff = (int)((dec - lowerBound) * Math.pow(10, nDigit));
		String bin = Integer.toBinaryString(diff);
		
		genes.clear();
		
		for(int idx = 0; idx < bin.length(); idx++){
			if('1' == bin.charAt(idx))
				genes.set(idx);
		}
	}
	
	/**
	 * ��������ת��Ϊʮ����
	 * @return
	 */
	public double decode(){
		double dec = 0;
		for(int i = 0; i < genes.length(); i++){
			if(genes.get(i))
				dec+=Math.pow(2, geneLen-1-i);
		}
		return dec/Math.pow(10, nDigit)+lowerBound;
	}
	
	/**
	 * ���������彻��ָ����Χ�Ļ�������
	 * @param individual
	 * @param startId
	 * @param endId
	 */
	public void exchangeRange(Individual individual, int startId, int endId){
		if(endId < startId)
			return;

		for(int idx = startId; idx <= endId; idx++){
			if(genes.get(idx)^individual.genes.get(idx))
				if(individual.genes.get(idx)){
					genes.set(idx);
					individual.genes.clear(idx);
				}else{
					genes.clear(idx);
					individual.genes.set(idx);
				}
		}
	}
	
	/**
	 * ȷ��ָ��λ���ϵĶ����Ʒ���0/1
	 * @param idx
	 * @return binary digit
	 */
	public int getGeneAt(int idx){
		if(genes.get(idx))
			return 1;

		return 0;
	}
}
