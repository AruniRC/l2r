package com.horsehour.function;

/**
 * �̼������ӿ�
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20120820
 */
public interface ActivationFunction {
	public double calc(double netInput);
	public double calcDerivation(double input);
}
