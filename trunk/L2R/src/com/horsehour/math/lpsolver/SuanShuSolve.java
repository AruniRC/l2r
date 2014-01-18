package com.horsehour.math.lpsolver;

import java.util.Arrays;

import com.numericalmethod.suanshu.matrix.doubles.matrixtype.dense.DenseMatrix;
import com.numericalmethod.suanshu.optimization.constrained.convex.sdp.socp.qp.lp.problem.CanonicalLPProblem1;
import com.numericalmethod.suanshu.optimization.constrained.convex.sdp.socp.qp.lp.simplex.solution.LPSimplexSolution;
import com.numericalmethod.suanshu.optimization.constrained.convex.sdp.socp.qp.lp.simplex.solver.LPCanonicalSolver;
import com.numericalmethod.suanshu.vector.doubles.dense.DenseVector;

/**
 * ����Suanshu��������Թ滮����
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20130124
 * �Ƚ϶��ԣ�suanshu��Ч���ϲ���lpSolve
 */
public class SuanShuSolve {
	public double[] objVect = null;//Ŀ�꺯��ϵ��:c
	public double[] bound = null;//Լ���½�:b
	public double[][] dataMatrix = null;//Լ������A
	public ObjDirection direction = ObjDirection.MIN;//Ĭ�������С������

	public SuanShuSolve(double[] objVect, double[][] dataMatrix, double[] bound){
		this.objVect = Arrays.copyOf(objVect, objVect.length);
		this.dataMatrix = Arrays.copyOf(dataMatrix, dataMatrix.length);
		this.bound = Arrays.copyOf(bound, bound.length);
	}
	
	/**
	 * Լ�� Ax >= b
	 * @return result
	 */
	public double suanshuSolve(){
		int direct = 1;
		DenseVector objVector = new DenseVector(objVect);
		if(direction == ObjDirection.MAX){
			objVector = objVector.scaled(-1);
			direct = -1;
		}
		CanonicalLPProblem1 problem = new CanonicalLPProblem1(objVector, 
				new DenseMatrix(dataMatrix), new DenseVector(bound));
		
		LPCanonicalSolver solver = new LPCanonicalSolver(); 
		LPSimplexSolution soln = solver.solve(problem);//simplex method
		soln.minimizer();
		return direct * soln.minimum();
	}
}
