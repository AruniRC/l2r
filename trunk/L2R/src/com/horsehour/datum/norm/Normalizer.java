package com.horsehour.datum.norm;

import com.horsehour.datum.DataSet;
import com.horsehour.datum.SampleSet;

/**
 * Normalizer�������ݵı�׼������
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20121231
 */
public abstract class Normalizer {
	
	public Normalizer(){}
	
	public void normalize(DataSet dataset){
		for(SampleSet sampleSet : dataset.getSampleSets())
			normalize(sampleSet);
	}

	public abstract void normalize(SampleSet sampleSet);
}
