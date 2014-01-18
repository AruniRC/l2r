package com.horsehour.ranker.weak;

import java.util.Arrays;

import org.opensourcedea.dea.DEAProblem;
import org.opensourcedea.dea.ModelType;
import org.opensourcedea.dea.SolverReturnStatus;
import org.opensourcedea.dea.VariableOrientation;

import com.horsehour.datum.DataManager;
import com.horsehour.datum.DataSet;
import com.horsehour.datum.SampleSet;
import com.horsehour.filter.L2RLineParser;
import com.horsehour.function.ActivationFunction;
import com.horsehour.function.Log1pFunction;
import com.horsehour.math.lpsolver.Solution;
import com.horsehour.util.FileManager;
import com.horsehour.util.TickClock;

/**
 * ʹ��Open Source DEA������CCRģ��,��������浽�����ļ�-CCRData
 * <p>���������CCRģ��,����������ߵ�Ԫ���������Ȩֵ,�����浽�����ļ�</p>
 * <p>�ر��,Ӧ�õ�����ѧϰ����,����ѵ�����ݼ������м�����-�ĵ��Ե�����Ȩֵ������</p>
 * <p>���ݱ����ʽ��l2rԭʼ�������ƣ���ʽ����<br/>
 * 1 0.89 902 0.29 0.03 ...<br/>
 * 0 0.38 902 0.04 0.09 ...<br/>
 * ��һ�б�ʾ�ĵ�����ʵ����,�ڶ��б�ʾ���Ȩֵ,�����б�ʾ������id,�������б�ʾ��Ӧ������Ȩֵ����
 * </p>
 * 
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20130508
 * @see http://www.opensourcedea.org/index.php?title=Open_Source_DEA
 */
public class DEARankWeakConstructor_OSDEA {
	private DataSet trainset = null;
	private String dest = "";
	
	private int oriented = 0;
	
	protected VariableOrientation[] varOrientation = null;
	protected double[][] dataMatrix = null;
	
	public int const_b = 1;
	public ActivationFunction trnsFunc = new Log1pFunction((float) Math.E);

	private DEAProblem problem = null;

	public DEARankWeakConstructor_OSDEA(String corpus, int foldId, int oriented){
		String src = "F:/Research/Data/" + corpus + "/Fold" + foldId + "/train.txt"; 
		trainset = DataManager.loadDataSet(src, "utf-8", new L2RLineParser());
		dest = "F:/Research/Data/CCRData/" + corpus + "/Fold" + foldId + "/";

		this.oriented = oriented;
		if(oriented == 0)
			dest += "ODEA.txt";
		else
			dest += "IDEA.txt";
	}

	/***
	 * ���ĳ���������������ĵ����ɵ�CCRģ��
	 */
	public void solveCCR(){
		int nRow = 0, nVar = trainset.getDim() + 1;
		initProblem(nVar);
		
		Solution[] sols = null;
		SampleSet sampleSet = null;
		for(int qid = 0; qid < trainset.size(); qid++){
			sampleSet = trainset.getSampleSet(qid);
			nRow = sampleSet.size();
			
			designProblem(nRow, nVar);
			populateDataMatrix(sampleSet);
			
			double[][] efficientWeight = null;
			double[] weight = null;
			
			try {
				problem.solve();
				
				SolverReturnStatus status = problem.getOptimisationStatus();
				
				if(SolverReturnStatus.OPTIMAL_SOLUTION_FOUND.equals(status)){
					
					efficientWeight = problem.getWeight();
					sols = new Solution[nRow];

					for(int id = 0; id < nRow; id++){
						weight = Arrays.copyOf(efficientWeight[id], nVar - 1);
						if(Double.isNaN(weight[0]))
							Arrays.fill(weight, 0);

						sols[id] = new Solution(problem.getObjective(id), weight);
					}
				}
			} catch (Exception e){
				return;
			}
			archiveSolution(sampleSet, sampleSet.getSample(0).getQid(), sols);
		}
	}
	
	/**
	 * ����������浽�����ļ�
	 * @param sb
	 */
	private void archiveSolution(SampleSet sampleset, String query, Solution[] sols){
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < sols.length; i++){
			sb.append(sampleset.getLabel(i));
			sb.append("\t" + sols[i].obj);
			sb.append("\t" + query);

			for(double weight : sols[i].vars)
				sb.append("\t" + weight);

			sb.append("\r\n");
		}
		FileManager.writeFile(dest, sb.toString());
	}
	
	/**
	 * ��ʼ������-��ʼ������������orientation(input/output)
	 * @param nVar
	 */
	protected void initProblem(int nVar){
		varOrientation = new VariableOrientation[nVar];
		if(oriented == 0){
			Arrays.fill(varOrientation, VariableOrientation.INPUT);
			varOrientation[nVar - 1] = VariableOrientation.OUTPUT;
		}else{
			Arrays.fill(varOrientation, VariableOrientation.OUTPUT);
			varOrientation[nVar - 1] = VariableOrientation.INPUT;
		}
	}

	/**
	 * ���DEAģ��
	 * @param nDMU
	 * @param dim
	 */
	protected void designProblem(int nDMU, int dim){
		problem = new DEAProblem(nDMU, dim);
		dataMatrix = new double[nDMU][dim];//The last is output
		
		problem.setVariableOrientations(varOrientation);
		problem.setDataMatrix(dataMatrix);

		if(oriented == 0)
			problem.setModelType(ModelType.CCR_O);//output oriented
		else
			problem.setModelType(ModelType.CCR_I);//input oriented
	}
	
	/**
	 * ʹ�������������problem��������
	 * @param sampleSet
	 */
	protected void populateDataMatrix(SampleSet sampleSet){
		int nDMU = sampleSet.size(),
				dim = sampleSet.getDim();
		for(int idx = 0; idx < nDMU; idx++){
			for(int fid = 0; fid < dim; fid++)
				dataMatrix[idx][fid] = sampleSet.getSample(idx).getFeature(fid);
			
			dataMatrix[idx][dim] = 
					(oriented == 0) ? trnsFunc.calc(sampleSet.getSample(idx).getLabel()) : const_b;
		}
	}
	
	public static void main(String[] args) {
		TickClock.beginTick();
		
		DEARankWeakConstructor_OSDEA computer = null;
		String[] corpusList = {"MQ2007", "MQ2008", "OHSUMED"};
		for(int o = 0; o <= 1; o++){//oriented
			for(String corpus : corpusList)//corpus
				for(int i = 1; i <= 5; i++)//foldid
				{ 
					computer = new DEARankWeakConstructor_OSDEA(corpus, i, o);
					computer.solveCCR();
				}
		}

		TickClock.stopTick();
	}
}
