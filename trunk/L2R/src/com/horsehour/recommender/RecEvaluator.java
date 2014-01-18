package com.horsehour.recommender;

import com.horsehour.datum.DataManager;
import com.horsehour.datum.RateSet;

/**
 * RecEvaluator�����Ƽ�ϵͳģ�͵ı���
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20130413
 */
public class RecEvaluator {
	public Recommender rec = null;
	public RateSet testset = null;
	
	/**
	 * �������ݼ�
	 * @param files
	 * @param enc
	 */
	public void loadRateSet(String[] files, String enc){
		if(files == null){
			System.err.println("Dataset Lost!");
			return;
		}

		RateSet trainset = null;
		RateSet valiset = null;
		if(files.length == 3){
			trainset = DataManager.loadRateSet(files[0], enc);
			valiset = DataManager.loadRateSet(files[1], enc);
			testset = DataManager.loadRateSet(files[2], enc);
		}
		rec.trainset = trainset;
		rec.valiset = valiset;
	}

	/**
	 * @return ģ�͵��Ƽ�����
	 */
	public float evaluate(){
		float perf = 0;
		
		return perf;
	}

	public static void main(String[] args){
		
	}
}
