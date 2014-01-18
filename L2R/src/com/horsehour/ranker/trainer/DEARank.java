package com.horsehour.ranker.trainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.horsehour.datum.DataManager;
import com.horsehour.datum.SampleSet;
import com.horsehour.math.MathLib;
import com.horsehour.model.EnsembleModel;
import com.horsehour.model.LinearModel;
import com.horsehour.model.Model;
import com.horsehour.util.FileManager;
import com.horsehour.util.Sorter;

/**
 * @author Chunheng Jiang
 * @version 3.0
 * @since 20131204
 */
public class DEARank extends RankTrainer{
	protected List<LinearModel> candidatePool;
	protected List<double[]> currentPredict;//����ģ����ѵ�����ϵ�Ԥ��
	protected int currentWeakId;//��ǰ����ģ�͵�id
	
	protected double[][] perfMatrix;
	protected double[] perfPlain;
	protected double[] queryWeight;

	protected String candidateFile = "";
	
	public String candidateDir;//��ѡ��Ŀ¼
	
	public int topkWeak = -1;
	
	public int oriented = 0;//output-oriented(0),input-oriented(1)
	
	protected List<Integer> dominantModel;//ǰ����������ѡ��ģ��
	protected List<Integer> popularModel;//����ѡ�еĴ����������޵�ģ��

	protected double prevTrainScore;
	protected double backupTrainScore;
	protected double bestValiScore;
	protected double epsilon = 0.002;
	protected int selCount = 0;
	protected int maxSelCount = 5;//ÿ������ģ����������ѡ�е�������

	protected double[] backupQueryWeight;
	protected boolean enqueue = true;
	
	public DEARank(){}

	public DEARank(int oriented){
		this.oriented = oriented;
	}

	public void init(){
		int sz = trainset.size();
		perfPlain = new double[sz];
		queryWeight = new double[sz];
		Arrays.fill(queryWeight, (double)1/sz);

		plainModel = new EnsembleModel();
		currentPredict = new ArrayList<double[]>();
		currentWeakId = -1;

		dominantModel = new ArrayList<Integer>();
		popularModel = new ArrayList<Integer>();
		backupQueryWeight = Arrays.copyOf(queryWeight, sz);
		
		prevTrainScore = -1;
		backupTrainScore = -1;
		bestValiScore = -1;

		buildCandidatePool();
		buildPerfMatrix();//����candidates
		selectCandidate();//ɸѡcandidates
	}

	/**
	 * ��ѡģ��·���ļ�·��
	 * @param corpus
	 * @param foldId
	 */
	public void setCandidateFile(String corpus, int foldId){
		candidateFile = candidateDir + corpus + "/Fold" + foldId;

		if(oriented == 0)
			candidateFile += "/ODEA.txt";
		else
			candidateFile += "/IDEA.txt";
	}

	/***
	 * ���ݸ����������¸����ĵ���Ӧ������CCRȨֵ���챸ѡ����ģ�ͼ���
	 */
	protected void buildCandidatePool(){
		candidatePool = new ArrayList<LinearModel>();

		List<double[]> weightLine;
		weightLine = DataManager.loadDatum(candidateFile, "\t");

		int m = weightLine.size();
		int n = weightLine.get(0).length;
		
		Set<Double> sumset = new HashSet<Double>();
		int presize = 0, size = 0;//������Ԫ��֮ǰ/��Ĵ�С

		for(int i = 0; i < m; i++){
			double[] weight = Arrays.copyOfRange(weightLine.get(i), 3, n);//�޳�ͷ������
			double sum = MathLib.sum(weight);//Ȩ�����

			if(sum < 0.5)//�˳��ӽ���0��Ȩֵ
				continue;

			sumset.add(sum);

			if((size = sumset.size()) > presize){//��Ԫ�ؿ�����ӱ������ظ�
				candidatePool.add(new LinearModel(weight));
				presize = size;
			}
		}
	}

	/**
	 * ����ÿһ��candidate�ڸ����������ϵ�����
	 */
	protected void buildPerfMatrix(){
		int m = trainset.size();
		int n = candidatePool.size();
		perfMatrix = new double[n][m];
		
		SampleSet sampleset;
		LinearModel weak;
		for(int qid = 0; qid < m; qid++){
			sampleset = trainset.getSampleSet(qid);
			currentPredict.add(new double[sampleset.size()]);

			for(int idx = 0; idx < n; idx++){
				weak = candidatePool.get(idx);
				perfMatrix[idx][qid] = trainMetric.measure(sampleset.getLabels(), 
						weak.predict(sampleset));
			}
		}
	}

	/**
	 * ɸѡ��ѡģ�ͣ�������ã�
	 */
	protected void selectCandidate(){
		if(topkWeak == -1)
			return;

		List<Integer> id = new ArrayList<Integer>();
		List<Double> meanperf = new ArrayList<Double>();
		int m = candidatePool.size();
		for(int i = 0; i < m; i++){
			id.add(i);
			meanperf.add(MathLib.mean(perfMatrix[i]));
		}

		Sorter.linkedSort(meanperf, id, true);//��������

		if(topkWeak > m)
			topkWeak = m;

		int n = trainset.size();				
		double[][] perf = new double[topkWeak][n];
		for(int i = 0; i < topkWeak; i++)
			perf[i] = Arrays.copyOf(perfMatrix[id.get(i)], n);

		perfMatrix = new double[topkWeak][n];
		List<LinearModel> selected = new ArrayList<LinearModel>();
		
		for(int i = 0; i < topkWeak; i++){
			selected.add(candidatePool.get(id.get(i)));
			perfMatrix[i] = Arrays.copyOf(perf[i], n);
		}

		candidatePool.clear();
		candidatePool.addAll(selected);
	}
	
	public void train(){
		init();
		int iter = train(0, true);
		for(int i = dominantModel.size() - 1; i >= 0; i--){
			dominantModel.remove(i);//requeue
			iter = train(iter, false);
		}
		storeModel();
	}

	private int train(int startIter, boolean enqueue){
		int iter = startIter;
		for(; iter < nIter; iter++){
			int id = 0;
			int sz = candidatePool.size();
			double maxperf = 0;
			for(int i = 0; i < sz; i++){
				if(dominantModel.contains(i) || popularModel.contains(i))
					continue;

				double weightperf = MathLib.innerProduct(perfMatrix[i], queryWeight);
				if(weightperf > maxperf){
					maxperf = weightperf;
					id = i;
				}
			}

			if(enqueue){
				if(id == currentWeakId){//selected twice in a row
					dominantModel.add(id);//enter queue
					//roll back, since it's dominated by this model
					((EnsembleModel) plainModel).removeLastMember();
					queryWeight = Arrays.copyOf(backupQueryWeight, queryWeight.length);
					prevTrainScore = backupTrainScore;
					bestValiScore = 0;
					continue;
				}

				currentWeakId = id;
				backupQueryWeight = Arrays.copyOf(queryWeight, queryWeight.length);
				backupTrainScore = prevTrainScore;
			}

			double alpha = 0.5 * Math.log((1 + maxperf)/(1 - maxperf));
			((EnsembleModel) plainModel).addMember(candidatePool.get(id), alpha);

			double norm = 0;
			double trainscore = 0;
			SampleSet sampleset;
			int n = trainset.size();
			for(int i = 0; i < n; i++){
				sampleset = trainset.getSampleSet(i);
				double[] predict = plainModel.predict(sampleset);
				double score = trainMetric.measure(sampleset.getLabels(), predict);
				queryWeight[i] = Math.exp(-score);
				trainscore += score;
				norm += queryWeight[i];
			}

			trainscore /= n;

			if(!enqueue){
				//performance has no change & consecutively selected
				if(trainscore == prevTrainScore && currentWeakId == id){
					selCount++;
					if(selCount == maxSelCount){
						selCount = 0;
						popularModel.add(id);
					}
				}else{
					selCount = 0;
					popularModel.clear();//all removed models are added back to the pool
				}
				currentWeakId = id;
			}

			double valiscore = validate(valiset, valiMetric);
			if(valiscore > bestValiScore){
				bestValiScore = valiscore;
				updateModel();
			}

			double delta = trainscore + epsilon - prevTrainScore;
			if(delta <= 0){//stop criteria met
				((EnsembleModel) plainModel).removeLastMember();
				break;
			}

			prevTrainScore = trainscore;

			//reweighting
			for(int i = 0; i < n; i++)
				queryWeight[i] /= norm;
		}
		return iter;
	}

	
	@Override
	protected void learn() {
		weakLearn();
		weightWeak();

		Model weak = candidatePool.get(currentWeakId);
		double alpha = ((EnsembleModel) plainModel).getLastWeight();

		SampleSet sampleset;
		int n = trainset.size();
		for(int i = 0; i < n; i++){
			sampleset = trainset.getSampleSet(i);
			double[] predict = MathLib.linearCombinate(currentPredict.get(i), 1.0,
					weak.predict(sampleset), alpha);

			currentPredict.set(i, predict);
			perfPlain[i] = trainMetric.measure(sampleset.getLabels(), predict);
		}

		reweightQuery();
	}
	
	/**
	 * ��������������ѡ����ѡ��һ������������
	 */
	protected void weakLearn(){
		int id = 0;
		int sz = candidatePool.size();
		double maxperf = 0;
		for(int i = 0; i < sz; i++){
			double weightperf = MathLib.innerProduct(perfMatrix[i], queryWeight);
			if(weightperf > maxperf){
				maxperf = weightperf;
				id = i;
			}
		}

		currentWeakId = id;
		((EnsembleModel) plainModel).addMember(candidatePool.get(id), maxperf);
	}

	/**
	 * ������ģ�͸�Ȩֵ
	 */
	protected void weightWeak(){
		int idx = ((EnsembleModel) plainModel).size();
		double gamma = ((EnsembleModel) plainModel).getWeight(idx - 1);
		double alpha = 0.5 * Math.log((1 + gamma)/(1 - gamma));
		((EnsembleModel) plainModel).updateWeight(idx - 1, alpha);
	}

	/**
	 * ����ѵ�����ݼ�������Ȩ��
	 */
	protected void reweightQuery(){
		int sz = perfPlain.length;
		double norm = 0;
		for(int qid = 0; qid < sz; qid++){
			queryWeight[qid] = Math.exp(-perfPlain[qid]);
			norm += queryWeight[qid] ;
		}

		for(int qid = 0; qid < sz; qid++)
			queryWeight[qid] /= norm;
	}

	@Override
	public void updateModel() {
		bestModel = (EnsembleModel) plainModel.copy();
	}

	@Override
	public void storeModel() {
		FileManager.writeFile(modelFile, bestModel.toString(), false);
	}

	@Override
	public Model loadModel(String modelFile) {
		List<double[]> lines = DataManager.loadDatum(modelFile, "\t"); 
		EnsembleModel model = new EnsembleModel();

		int m = lines.size();
		int n = lines.get(0).length;		
		for(int i = 0; i < m; i++){
			double[] line = lines.get(i);
			model.addMember(new LinearModel(Arrays.copyOf(line, n - 1)), line[n - 1]);
		}
		
		return model;
	}

	@Override
	public String name() {
		String nm = "";

		if(oriented == 1)
			nm = "IDEARank";
		else
			nm = "ODEARank";			

		nm += "." + trainMetric.name();
		
		return nm;
	}
}
