package com.horsehour.datum;

import java.util.List;

import com.horsehour.filter.L2RLineParser;
import com.horsehour.filter.LineParserFilter;
import com.horsehour.util.FileManager;
import com.horsehour.util.TickClock;

/**
 * FeaturePlus�����ݼ����������DEA���Ч��
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20130610
 */
public class FeaturePlus {
	/**
	 * ���¹������ݼ������dea���Ч��ֵ����
	 * @param corpus
	 * @param foldId
	 */
	public void reconstruct(String corpus, int foldId){
		String root = "F:/Research/Data/CCRData/" + corpus + "/Fold" + foldId;
		String database = "F:/Research/Data/" + corpus + "/Fold" + foldId + "/";
		String dest = "F:/Research/Data/ICCR-" + corpus + "/Fold" + foldId + "/";

		String[] letorFiles = 
			{database + "train.txt", database + "vali.txt", database + "test.txt"};
		String[] deaFiles = 
			{root + "/Train-IC2R.txt", root + "/Vali-IC2R.txt", root + "/Test-IC2R.txt"};
		String[] types = {"train.txt", "vali.txt", "test.txt"};

		DataSet letorDatum;
		List<double[]> deaDatum;		
		
		LineParserFilter lineParser = new L2RLineParser();

		for(int i = 0; i < letorFiles.length; i++){
			deaDatum = DataManager.loadDatum(deaFiles[i], "utf-8", "	");	
			letorDatum = DataManager.loadDataSet(letorFiles[i], "utf-8", lineParser);
			merge(deaDatum, letorDatum, dest + types[i]);
		}
	}
	
	/**
	 * ��dea���Ч�����뵽���ݼ�
	 * @param dea
	 * @param letor
	 * @param dest
	 */
	public void merge(List<double[]> dea, DataSet letor, String dest){
		int pos = 0, sz = 0;

		double qid = 0;
		String query = "";
		
		double[] deascore;
		for(SampleSet sampleset : letor.getSampleSets()){
			sz = sampleset.size();
			deascore = new double[sz];

			if(pos < dea.size()){
				qid = dea.get(pos)[2];
				query = sampleset.getSample(0).getQid();

				if(checkDocBlock(query, qid)){
					for(int i = 0; i < sz; i++)
						deascore[i] = dea.get(pos + i)[1];
					pos += sz;
				}
			}
			
			addFeatures(sampleset, deascore);
			archive(sampleset, dest);
		}
	}
	
	/**
	 * �ж����ݿ��Ƿ�����ͬһ��������
	 * @param query
	 * @param qid
	 */
	private boolean checkDocBlock(String query, double qid){
		return (Integer.parseInt(query) == qid);
	}
	
	/**
	 * ��features��ӵ�sampleset
	 * @param sampleset
	 * @param features
	 */
	public void addFeatures(SampleSet sampleset, double[] features){
		int sz = sampleset.size();
		for(int i = 0; i < sz; i++)
			sampleset.getSample(i).addFeature(features[i]);
	}

	/**
	 * ��sampleset������dest
	 * @param sampleset
	 * @param dest
	 */
	private void archive(SampleSet sampleset, String dest){
		StringBuffer sb = new StringBuffer();
		for(Sample sample : sampleset.getSamples()){
			sb.append(sample.getLabel() + " qid:" + sample.getQid());
			for(int i = 0; i < sample.getDim(); i++)
				sb.append(" " + (i + 1) + ":" + sample.getFeature(i));
			sb.append("\r\n");
		}
		
		FileManager.writeFile(dest, sb.toString());
	}
	
	public static void main(String[] args){
		TickClock.beginTick();

		String[] corpusName = {"MQ2007", "MQ2008", "OHSUMED"}; 
		FeaturePlus plus = new FeaturePlus();
		
		for(int i = 0; i < corpusName.length; i++)
			for(int j = 1; j <= 5; j++)
				plus.reconstruct(corpusName[i], j);

		TickClock.stopTick();
	}
}
