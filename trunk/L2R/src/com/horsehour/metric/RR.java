package com.horsehour.metric;

import java.util.List;

import com.horsehour.util.Sorter;

/**
 * <p>RRMetric�ǵ���������Reciprocal Rank������, һ�������ʵĵ�������ָֻ��֮��ص��ĵ������������ĵ�����
 * ��Ҫ���������ʴ�ϵͳQuestion Answering</p>
 * @author Chunheng Jiang
 * @version 1.0
 * @since 2012-12-15
 * @see Wiki:http://en.wikipedia.org/wiki/Mean_reciprocal_rank
 */
public class RR extends Metric{

	@Override
	public double measure(List<Integer> desire, List<Double> predict) {
		Sorter.linkedSort(predict, desire, true);
		double rr = 0;
		for(int idx = 0; idx < desire.size(); idx++)
			if(desire.get(idx) > 0){
				rr = (double)1/(idx + 1);
				break;
			}
		
		return rr;
	}

	@Override
	public String name() {
		return "MRR";
	}
}
