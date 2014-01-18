package com.horsehour.math.lpsolver;

import com.horsehour.datum.SampleSet;
import com.horsehour.function.ActivationFunction;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;


/**
 * LpProblem�����˻��������Թ滮����
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20130124
 * @since 20130228
 * @see http://web.mit.edu/lpsolve_v5520/java/docs/api/index-all.html
 * @see http://blog.csdn.net/com_stu_zhang/article/details/7534651
 */
public class LpProblem {
	public double[] bound = null;//Լ���½�:b
	public double[][] dataMatrix = null;//Լ������A

	public LpProblem(){}
	
	/**
	 * ���ģ��
	 * ��sample set����������data matrix
	 * ���bound
	 * @param sampleSet
	 */
	public void designProb(SampleSet sampleSet, ActivationFunction trnsFunc){
		populateDataMatrix(sampleSet);
		
		if(trnsFunc == null)
			populateBound();
		else{
			int sz = sampleSet.size();
			double[] label = new double[sz];
			for(int id = 0; id < sz; id++)
				label[id] = trnsFunc.calc(sampleSet.getLabel(id));
			
			populateBound(label);
		}
	}
	
	/**
	 * ���data matrix
	 * @param sampleSet
	 */
	public void populateDataMatrix(SampleSet sampleSet){
		int nDMU = sampleSet.size(),
				dim = sampleSet.getDim();

		dataMatrix = new double[nDMU][dim];
		for(int idx = 0; idx < nDMU; idx++){
			for(int fid = 0; fid < dim; fid++)
				dataMatrix[idx][fid] = sampleSet.getSample(idx).getFeature(fid);
		}
	}
	
	/**
	 * ���bound 
	 * @param label
	 */
	public void populateBound(double[] label){
		int sz = dataMatrix.length;
		bound = new double[sz];
		for(int idx = 0; idx < sz; idx++)
			bound[idx] = label[idx];
	}
	
	public void populateBound(){
		int sz = dataMatrix.length;
		bound = new double[sz];
		for(int idx = 0; idx < sz; idx++)
			bound[idx] = 1;
	}
	
	/**
	 * ���ODEAģ��
	 * @param id-Ŀ��DMU��id
	 * @param optimalWeight
	 * @return ���Ч��ֵ
	 */
	public double[][] solveDEAProb(int oriented){
		LpSolve solver = null;
		int nDMU = dataMatrix.length;
		int nVar = dataMatrix[0].length;
		
		double[][] optimalWeight = new double[nDMU][nVar];
		
		try {
			solver = LpSolve.makeLp(nDMU, nVar);
			solver.setVerbose(0);//hide solution report
			
			int ineqType = 0;
			if(oriented == 1){
				solver.setMaxim();
				ineqType = 1;
			}else{
				solver.setMinim();
				ineqType = 2;
			}
			
			//add constraints
			for(int idx = 0; idx < nDMU; idx++){
				solver.addConstraint(dataMatrix[idx], ineqType, bound[idx]);
			}
			
			for(int id = 0; id < nDMU; id++){
				solver.setObjFn(dataMatrix[id]);//set objective function			
				solver.solve();

				//��ȡ����Ȩֵ
				solver.getVariables(optimalWeight[id]);
			}

		} catch (LpSolveException e) {
			return null;
		}
		if(solver.getLp() != 0)
			solver.deleteLp();//release the memory
		
		return optimalWeight;
	}
}