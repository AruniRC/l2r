package com.horsehour.math.lpsolver;

import java.util.Arrays;

/** 
 * ���ڴ洢�滮ģ�����Ľ��
 * @author Chunheng Jiang
 * @since 20130302
 * @since 20130408
 * @version 2.0
 */
public class Solution {
	public double obj;
	public double[] vars;

	public Solution(){}
	
	public Solution(double obj, double[] vars){
		this.obj = obj;
		this.vars = Arrays.copyOf(vars, vars.length);
	}
}
