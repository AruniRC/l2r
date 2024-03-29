package com.horsehour.metric;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.horsehour.util.Sorter;


/**
 * Expected Reciprocal Rank is based on the cascade model of search. The cascade model 
 * assumes a user scans through ranked search results in order, and for each document, 
 * evaluates whether the document satisfies the query, and if it does, stops the search. 
 * It's proposed by Chapelle on 2009.
 * @author Chunheng Jiang
 * @version 1.0
 * @since 2012/12/16
 * @see  Chapelle, O., D. Metlzer, et al. (2009). "Expected reciprocal rank for graded relevance." 
 * CIKM '09: Proceeding of the 18th ACM conference on Information and knowledge management.
 */

public class ERR extends Metric{
	private List<Double> satisfactionProb;
	
	public ERR(){
		satisfactionProb = new ArrayList<Double>();
	}
	
	@Override
	public double measure(List<Integer> desire, List<Double> predict) {
		Sorter.linkedSort(predict, desire, true);
		for(int i = 0; i < desire.size(); i++)
			satisfactionProb.add(desire.get(i) + 0.0);
		
		int maxLevel = Collections.max(desire);
		
		double hitProb = 0;
		for(int idx = 0; idx < satisfactionProb.size(); idx++){
			hitProb = (Math.pow(2, satisfactionProb.get(idx)) - 1)/Math.pow(2, maxLevel);
			satisfactionProb.set(idx, hitProb);
		}
		
		float quality = 0;
		for(int idx = 0; idx < satisfactionProb.size(); idx++)
			quality += getTopkProb(idx + 1);
		
		return quality;
	}

	//ȡ��top k ERR
	protected double getTopkProb(int k){
		double topkERR = 1.0;
		for(int idx = 0; idx < k - 1; idx++)
			topkERR *= (1 - satisfactionProb.get(idx));
		
		topkERR *= satisfactionProb.get(k - 1)/k;
		return topkERR;
	}
	
	@Override
	public String name() {
		return "ERR";
	}
}
