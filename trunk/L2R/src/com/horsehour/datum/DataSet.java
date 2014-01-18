package com.horsehour.datum;

import java.util.ArrayList;
import java.util.List;

/**
 * DataSet���������ѧϰ���ݼ��ĸ������SampleSet�ļ��ϣ���������Sample�ļ���
 * @author Chunheng Jiang
 * @version 1.0
 */

public class DataSet {
	private int numTotalSample = 0;
	private int dim = 0;

	private List<SampleSet> samplesets;
	
	public DataSet(){
		samplesets = new ArrayList<SampleSet>();
	}
	
	public DataSet(List<SampleSet> sampleSets, int dim, int numTotalSample){
		this.samplesets = sampleSets;
		this.numTotalSample = numTotalSample;
		this.dim = dim;
	}

	/**
	 * number of SampleSet instances in sampleSets collection
	 * @return size of sampleSets
	 */
	public int size() {
		return samplesets.size();
	}
	
	public List<SampleSet> getSampleSets() {
		return samplesets;
	}
	public SampleSet getSampleSet(int idx) {
		return samplesets.get(idx);
	}
	public void removeSampleSet(int idx) {
		samplesets.remove(idx);
	}
	
	public int getNumTotalSample(){
		return numTotalSample;
	}
	public int getDim(){
		return dim; 
	}

	public void addDim(int d) {
		dim += d;
	}
}