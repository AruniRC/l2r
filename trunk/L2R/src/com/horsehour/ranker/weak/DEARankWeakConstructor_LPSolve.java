package com.horsehour.ranker.weak;

import java.util.Arrays;
import java.util.List;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;

import com.horsehour.datum.DataManager;
import com.horsehour.datum.DataSet;
import com.horsehour.datum.SampleSet;
import com.horsehour.filter.L2RLineParser;
import com.horsehour.function.ActivationFunction;
import com.horsehour.function.Log1pFunction;
import com.horsehour.math.lpsolver.ObjDirection;
import com.horsehour.math.lpsolver.Solution;
import com.horsehour.util.FileManager;
import com.horsehour.util.TickClock;

/**
 * ʹ��lp_solve�����CCRģ��,��������浽�����ļ�-CCRData
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
 * @see http://web.mit.edu/lpsolve_v5520/java/docs/api/index-all.html
 */

public class DEARankWeakConstructor_LPSolve {
	private DataSet trainset;
	private String dest = "";

	private int oriented = 0;

	//ֱ��ʹ��lp_solve���ccrģ��
	public LpSolve problem;
	public double[][] dataMatrix;
	ObjDirection dir = ObjDirection.MAX;
	public ActivationFunction activeFun = new Log1pFunction((float) Math.E);

	public double[] bound;
	public int eqType = -1;
	
	public int nRow = -1, nCol = -1;
	
	public DEARankWeakConstructor_LPSolve(String corpus, int foldId, int oriented){
		String src = "F:/Research/Data/" + corpus + "/Fold" + foldId + "/train.txt"; 
		trainset = DataManager.loadDataSet(src, "utf-8", new L2RLineParser());
		nCol = trainset.getDim();
		
		dest = "F:/Research/Data/CCRData/" + corpus + "/Fold" + foldId + "/";
		
		this.oriented = oriented;
		if(oriented == 0)
			dest += "OLPDEA.txt";
		else
			dest += "ILPDEA.txt";

	}
	
	/**
	 * ����lp_solve���CCRģ��
	 */
	public void solveCCR(){
		dir = (oriented == 0) ? ObjDirection.MIN : ObjDirection.MAX;
		
		SampleSet sampleSet;
		Solution[] sols;
		
		StringBuffer sb;
		for(int qid = 0; qid < trainset.size(); qid++){
			sampleSet = trainset.getSampleSet(qid);
			nRow = sampleSet.size();
			
			prepareData(sampleSet);
			makeProblem();
			
			String query = sampleSet.getSample(0).getQid();
			sb = new StringBuffer();
			sols = solveProblem();
			
			for(int id = 0; id < sols.length; id++){
				if(sols[id] == null)
					continue;

				sb.append(sampleSet.getLabel(id));
				sb.append("\t" + sols[id].obj);
				sb.append("\t" + query);

				for(double weight : sols[id].vars)
					sb.append("\t" + weight);

				sb.append("\r\n");
			}
			FileManager.writeFile(dest, sb.toString());
		}
	}

	/**
	 * ����ģ�ʹ�sampleset�����ñ�Ҫ������
	 * 1. dataMatrix
	 * 2. bound
	 * 3. eqType 
	 * 
	 * @param sampleset
	 */
	private void prepareData(SampleSet sampleset){
		dataMatrix = new double[nRow][nCol];
		for(int i = 0; i < nRow; i++)
			for(int j = 0; j < nCol; j++)
				dataMatrix[i][j] = sampleset.getSample(i).getFeature(j);
		
		bound = new double[nRow];
		//����Ŀ�꺯������������Լ������(����ʽ����,��)
		if(dir == ObjDirection.MAX){
			eqType = 1;//leq
			Arrays.fill(bound, 1);
		}else{
			eqType = 2;//geq
			
			List<Integer> labels = sampleset.getLabelList();
			for(int i = 0; i < nRow; i++)
				bound[i] = activeFun.calc(labels.get(i));
		}
	}

	/**
	 * ����ģ��-problem
	 */
	private void makeProblem(){
		try {
			problem = LpSolve.makeLp(0, nCol);
			
			//����Ŀ�꺯������
			if (dir == ObjDirection.MAX)
				problem.setMaxim();
			else
				problem.setMinim();
			
			if(problem.getLp() != 0){
				problem.setAddRowmode(true);
				//���Լ������
				for(int i = 0; i < nRow; i++)
					problem.addConstraint(dataMatrix[i], eqType, bound[i]);

				problem.setAddRowmode(false);
				problem.setVerbose(0);//����ӡ��־
			}
		} catch (LpSolveException e) {
			e.printStackTrace();
			return;
		}
	}

	/**
	 * ���ģ��
	 * @return ����Ľ�
	 */
	private Solution[] solveProblem(){
		Solution[] sols = new Solution[nRow];

		double[] weight = new double[nCol];
		for(int i = 0; i < nRow; i++){
			try {
				//����Ŀ�꺯��
				problem.setObjFn(dataMatrix[i]);

				//���ģ��
				int code = problem.solve();
				if(code == LpSolve.OPTIMAL) {
					problem.getVariables(weight);
					sols[i] = new Solution(problem.getObjective(), weight);
				}

			} catch (LpSolveException e) {
				e.printStackTrace();
				return null;
			}
		}

		if(problem.getLp() != 0)
			problem.deleteLp();

		return sols;
	}
	
	public static void main(String[] args) {
		TickClock.beginTick();
		
		DEARankWeakConstructor_LPSolve computer;
//		String[] corpusList = {"TD2003", "TD2004", "NP2003", "NP2004", "HP2003", "HP2004"};
		String[] corpusList = {"MQ2008"};
		for(int o = 0; o <= 1; o++){//oriented
			for(String corpus : corpusList)//corpus
				for(int i = 1; i <= 5; i++)//foldid
				{ 
					computer = new DEARankWeakConstructor_LPSolve(corpus, i, o);
					computer.solveCCR();
				}
		}

		TickClock.stopTick();
	}
}
