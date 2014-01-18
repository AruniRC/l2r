package com.horsehour.math;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A tiny mathematical utility
 * @author Chunheng Jiang
 * @version 3.0
 * @since 20131208
 */
public class MathLib {
	/**
	 * ����ŷʽ����
	 * @param array
	 * @return sqrt(��array[i]^2)
	 */
	public static double getEuclideanNorm(double[] array){
		double sum = 0;
		int len = array.length;
		for(int i = 0; i < len; i++)
			sum += array[i] * array[i];

		return Math.sqrt(sum);
	}
	public static float getEuclideanNorm(float[] array){
		float sum = 0;
		int len = array.length;
		for(int i = 0; i < len; i++)
			sum += array[i] * array[i];

		return (float) Math.sqrt(sum);
	}
	
	/**
	 * �����Ȩŷʽ����
	 * @param array
	 * @param weight
	 * @return sqrt(��w[i]*arr[i]^2)
	 */
	public static double getWeightedEuclideanNorm(double[] array, double[] weight){
		double wsum = 0;
		int len = array.length;
		for(int i = 0; i < len; i++)
			wsum += weight[i] * array[i] * array[i];
		
		return Math.sqrt(wsum);
	}
	
	public static float getWeightedEuclideanNorm(float[] array, float[] weight){
		float wsum = 0;
		int len = array.length;
		for(int i = 0; i < len; i++)
			wsum += weight[i] * array[i] * array[i];
		
		return (float) Math.sqrt(wsum);
	}
	
	/**
	 * ����L2����
	 * @param array
	 * @return ��array[i]^2
	 */
	public static double getL2Norm(double[] array){
		return getEuclideanNorm(array);
	}
	public static float getL2Norm(float[] array){
		return getEuclideanNorm(array);
	}
	
	/**
	 * ����L1����
	 * @param array
	 * @return ��|array[i]|
	 */
	public static double getL1Norm(double[] array){
		double sum = 0;
		int len = array.length;
		for(int i = 0; i < len; i++)
			sum += Math.abs(array[i]);
		
		return sum;
	}
	public static float getL1Norm(float[] array){
		float sum = 0;
		int len = array.length;
		for(int i = 0; i < len; i++)
			sum += Math.abs(array[i]);
		
		return sum;
	}
	
	/**
	 * �����ڻ�
	 * @param arr1
	 * @param arr2
	 * @return ��arr1[i]*arr2[i]
	 */
	public static double innerProduct(double[] arr1, double[] arr2){
		if(arr1 == null || arr2 == null){
			System.err.println("Empty Array.");
			System.exit(0);
		}else if(arr1.length != arr2.length){
			System.err.println("Dimensions are inconsistent.");
			System.exit(0);
		}

		double sum = 0;
		int len = arr1.length;
		for(int i = 0; i < len; i++)
			sum += arr1[i] * arr2[i];
		
		return sum;
	}
	
	public static float innerProduct(float[] arr1, float[] arr2){
		if(arr1 == null || arr2 == null){
			System.err.println("Empty Array.");
			System.exit(0);
		}else if(arr1.length != arr2.length){
			System.err.println("Dimensions are inconsistent.");
			System.exit(0);
		}

		float sum = 0;
		int len = arr1.length;
		for(int i = 0; i < len; i++)
			sum += arr1[i] * arr2[i];
		
		return sum;
	}
	/**
	 * �����Ȩ�ڻ�
	 * @param arr1
	 * @param arr2
	 * @return ��w[i]arr1[i]*arr2[i]
	 */
	public static double weightedInnerProduct(double[] arr1, double[] arr2, double[] weight){
		if(arr1 == null || arr2 == null){
			System.err.println("Empty Array.");
			System.exit(0);
		}else if(arr1.length != arr2.length){
			System.err.println("Dimensions are inconsistent.");
			System.exit(0);
		}

		double wsum = 0;
		int len = arr1.length;
		for(int i = 0; i < len; i++)
			wsum += weight[i] * arr1[i] * arr2[i];

		return wsum;
	}
	
	public static float weightedInnerProduct(float[] arr1, float[] arr2, float[] weight){
		if(arr1 == null || arr2 == null){
			System.err.println("Empty Array.");
			System.exit(0);
		}else if(arr1.length != arr2.length){
			System.err.println("Dimensions are inconsistent.");
			System.exit(0);
		}

		float wsum = 0;
		int len = arr1.length;
		for(int i = 0; i < len; i++)
			wsum += weight[i] * arr1[i] * arr2[i];

		return wsum;
	}
	
	/**
	 * �Ӻ�
	 * @param array
	 * @return ��array[i]
	 */
	public static double sum(double[] array){
		double sum = 0;
		int len = array.length;
		for(int i = 0; i < len; i++)
			sum += array[i];
		return sum;
	}
	
	public static float sum(float[] array){
		float sum = 0;
		int len = array.length;
		for(int i = 0; i < len; i++)
			sum += array[i];
		return sum;
	}
	
	public static int sum(int[] array){
		int sum = 0;
		int len = array.length;
		for(int i = 0; i < len; i++)
			sum += array[i];
		return sum;
	}
	
	/**
	 * ��׼��,����ŷʽ����(2����)
	 * @param array
	 */
	public static void normalize(double[] array){
		double norm = getEuclideanNorm(array);
		if(norm > Double.MIN_VALUE){
			int len = array.length;
			for(int i = 0; i < len; i++)
				array[i] /= norm;
		}
	}
	
	public static void normalize(float[] array){
		double norm = getEuclideanNorm(array);
		if(norm > Double.MIN_VALUE){
			int len = array.length;
			for(int i = 0; i < len; i++)
				array[i] /= norm;
		}
	}
	/**
	 * ��׼��,���ں�
	 * @param array
	 */
	public static void sumNormalize(double[] array){
		double sum = sum(array);
		if(sum > Double.MIN_VALUE){
			int len = array.length;
			for(int i = 0; i < len; i++)
				array[i] /= sum;
		}
	}
	
	public static void sumNormalize(float[] array){
		float sum = sum(array);
		if(sum > Double.MIN_VALUE){
			int len = array.length;
			for(int i = 0; i < len; i++)
				array[i] /= sum;
		}
	}
	/**
	 * ��׼��,�������ֵ
	 * @param array
	 */
	public static void maxNormalize(double[] array){
		double max = getMax(array);
		if(max > Double.MIN_VALUE){
			int len = array.length;
			for(int i = 0; i < len; i++)
				array[i] /= max;			
		}
	}
	public static void maxNormalize(float[] array){
		float max = getMax(array);
		if(max > Double.MIN_VALUE){
			int len = array.length;
			for(int i = 0; i < len; i++)
				array[i] /= max;
		}
	}
	
	/**
	 * zscore��׼��
	 * @param array
	 */
	public static void zscoreNormalize(double[] array){
		int n = array.length;
		double mean = sum(array)/n;
		double std = 0;
		for(int i = 0; i < n; i++){
			double val = array[i] - mean;
			std += val * val;
		}
		if(std > 0){
			std = Math.sqrt(std/(n - 1));
			for(int i = 0; i < n; i++)
				array[i] = (array[i] - mean)/std;
		}
	}

	public static void zscoreNormalize(float[] array){
		int n = array.length;
		float mean = sum(array)/n;
		float std = 0;
		for(int i = 0; i < n; i++){
			float val = array[i] - mean;
			std += val * val;
		}
		if(std > 0){
			std = (float) Math.sqrt(std/(n - 1));
			for(int i = 0; i < n; i++)
				array[i] = (array[i] - mean)/std;
		}
	}
	
	/**
	 * Ѱ��������
	 * @param array
	 * @return largest
	 */
	public static double getMax(double[] array){
		if(array == null){
			System.err.println("Array is Empty.");
			System.exit(0);
		}
		
		double max = array[0];
		if(array.length == 1)
			return max;

		int len = array.length;
		for(int i = 1; i < len; i++)
			max = (max > array[i]) ? max : array[i];

		return max;
	}

	public static float getMax(float[] array){
		if(array == null){
			System.err.println("Array is Empty.");
			System.exit(0);
		}
		
		float max = array[0];
		
		if(array.length == 1)
			return max;

		int len = array.length;
		for(int i = 1; i < len; i++)
			max = (max > array[i]) ? max : array[i];

		return max;
	}
	
	/**
	 * Ѱ����С����
	 * @param array
	 * @return smallest
	 */
	public static double getMin(double[] array){
		if(array == null){
			System.err.println("Array is Empty.");
			System.exit(0);
		}
		
		double min = array[0];
		if(array.length == 1)
			return min;

		int len = array.length;
		for(int i = 1; i < len; i++)
			min = (min < array[i]) ? min : array[i];

		return min;
	}

	public static float getMin(float[] array){
		if(array == null){
			System.err.println("Array is Empty.");
			System.exit(0);
		}
		
		float min = array[0];
		if(array.length == 1)
			return min;

		int len = array.length;
		for(int i = 1; i < len; i++)
			min = (min < array[i]) ? min : array[i];

		return min;
	}
	
	/**
	 * �����������ƶ�
	 * @param arr1
	 * @param arr2
	 * @return ����ֵ
	 */
	public static double getSimCosine(double[] arr1, double[] arr2){
		if(arr1 == null || arr2 == null){
			System.err.println("At least one array is Empty.");
			System.exit(0);
		}else if(arr1.length != arr2.length){
			System.err.println("Dimensions are inconsistent.");
			System.exit(0);
		}

		double norm1 = 0;
		double norm2 = 0;
		double inprod = 0;
		int n = arr1.length;
		for(int i = 0; i < n; i++){
			inprod += arr1[i] * arr2[i];
			norm1 += arr1[i] * arr1[i];
			norm2 += arr2[i] * arr2[i];
		}

		double ret = 0;
		if(norm1 * norm2 > Double.MIN_VALUE)
			ret = inprod / (Math.sqrt(norm1 * norm2));
		return ret;
	}
	public static float getSimCosine(float[] arr1, float[] arr2){
		if(arr1 == null || arr2 == null){
			System.err.println("At least one array is Empty.");
			System.exit(0);
		}else if(arr1.length != arr2.length){
			System.err.println("Dimensions are inconsistent.");
			System.exit(0);
		}

		float norm1 = 0;
		float norm2 = 0;
		float inprod = 0;
		int n = arr1.length;
		for(int i = 0; i < n; i++){
			inprod += arr1[i] * arr2[i];
			norm1 += arr1[i] * arr1[i];
			norm2 += arr2[i] * arr2[i];
		}

		float ret = 0;
		if(norm1 * norm2 > Float.MIN_VALUE)
			ret = (float) (inprod / (Math.sqrt(norm1 * norm2)));
		return ret;
	}

	/**
	 * �����Ȩ�������ƶ�
	 * @param arr1
	 * @param arr2
	 * @param weight
	 * @return weighted cosine similarity
	 */
	public static float getWeightedSimCosine(float[] arr1, float[] arr2, float[] weight){
		if(arr1 == null || arr2 == null){
			System.err.println("At least one array is Empty.");
			System.exit(0);
		}

		float ret = 0;
		if(arr1.length == arr2.length && arr2.length == weight.length){
			float norm1 = 0;
			float norm2 = 0;
			float inprod = 0;
			int n = arr1.length;
			for(int i = 0; i < n; i++){
				inprod += arr1[i] * arr2[i] * weight[i];
				norm1 += arr1[i] * arr1[i] * weight[i];
				norm2 += arr2[i] * arr2[i] * weight[i];
			}

			if(norm1 * norm2 > Float.MIN_VALUE)
				ret = (float) (inprod / (Math.sqrt(norm1 * norm2)));
		}else{
			System.err.println("Dimensions are inconsistent.");
			System.exit(0);
		}
		return ret;
	}

	public static double getWeightedSimCosine(double[] arr1, double[] arr2, double[] weight){
		if(arr1 == null || arr2 == null){
			System.err.println("At least one array is Empty.");
			System.exit(0);
		}

		double ret = 0;
		if(arr1.length == arr2.length && arr2.length == weight.length){
			double norm1 = 0;
			double norm2 = 0;
			double inprod = 0;
			int n = arr1.length;
			for(int i = 0; i < n; i++){
				inprod += arr1[i] * arr2[i] * weight[i];
				norm1 += arr1[i] * arr1[i] * weight[i];
				norm2 += arr2[i] * arr2[i] * weight[i];
			}

			if(norm1 * norm2 > Float.MIN_VALUE)
				ret =  inprod / (Math.sqrt(norm1 * norm2));
		}else{
			System.err.println("Dimensions are inconsistent.");
			System.exit(0);
		}

		return ret;
	}
	
	/**
	 * ���������ַ�����Hamming���루�ȳ��ַ�������ͬλ�ò�ͬ�ַ��ĸ�����
	 * @param str1
	 * @param str2
	 * @return ��ͬ�ַ��ĸ���
	 */
	public static int hammingDistance(String str1, String str2){
		int len = str1.length();
		if(len != str2.length()){
			System.err.println("Inconsistent Dimensions.");
			System.exit(0);
		}

		int concordant = 0;
		for(int i = 0; i < len; i++){
			if(str1.charAt(i) == str2.charAt(i)) 
				concordant++;
		}
		return len - concordant;
	}
	
	/**
	 * �������������ŷʽ����
	 * @param arr1
	 * @param arr2
	 * @return sqrt(��(arr1[i] - arr2[i]^2))
	 */
	public static double euclideanDistance(double[] arr1, double[] arr2){
		return getEuclideanNorm(diff(arr1, arr2));
	}
	public static float euclideanDistance(float[] arr1, float[] arr2){
		return getEuclideanNorm(diff(arr1, arr2));
	}

	/**
	 * �Ŵ�
	 * @param array
	 * @param factor
	 * @return ����Ŵ�
	 */
	public static double[] scale(double[] array, double factor){
		int len = array.length;
		double arr[] = new double[len];
		for(int i = 0; i < len; i++)
			arr[i] = array[i] * factor;

		return arr;
	}

	public static float[] scale(float[] array, float factor){
		int len = array.length;
		float arr[] = new float[len];
		for(int i = 0; i < len; i++)
			arr[i] = array[i] * factor;
		
		return arr;
	}

	/**
	 * �Ӻ�
	 * @param arr1
	 * @param arr2
	 * @return arr[i] = arr1[i] + arr2[i]
	 */
	public static double[] add(double[] arr1, double[] arr2){
		int len = arr1.length;
		double[] arr = new double[len];
		for(int i = 0; i < len; i++)
			arr[i] = arr1[i] + arr2[i];

		return arr;
	}

	public static float[] add(float[] arr1, float[] arr2){
		int len = arr1.length;
		float[] arr = new float[len];
		for(int i = 0; i < len; i++)
			arr[i] = arr1[i] + arr2[i];
		return arr;
	}
	/**
	 * ��
	 * @param arr1
	 * @param arr2
	 * @return arr[i] = arr1[i] - arr2[i]
	 */
	public static double[] diff(double[] arr1, double[] arr2){
		int len = arr1.length;
		double[] arr = new double[len];
		
		for(int i = 0; i < len; i++)
			arr[i] = arr1[i] - arr2[i];
		
		return arr;
	}
	
	public static float[] diff(float[] arr1, float[] arr2){
		int len = arr1.length;
		float[] arr = new float[len];
		for(int i = 0; i < len; i++)
			arr[i] = arr1[i] - arr2[i];
		
		return arr;
	}
	/**
	 * �������
	 * @param arr1
	 * @param f1
	 * @param arr2
	 * @param f2
	 * @return arr[i] = arr1[i]*f1 + arr2[i]*f2
	 */
	public static double[] linearCombinate(double[] arr1, double f1, double[] arr2, double f2){
		if(arr1 == null || arr2 == null){
			System.err.println("Empty Array.");
			System.exit(0);
		}else if(arr1.length != arr2.length){
			System.err.println("Dimensions are inconsistent.");
			System.exit(0);
		}

		int len = arr1.length;
		double[] arr = new double[len];
		for(int i = 0; i < len; i++)
			arr[i] = arr1[i] * f1 + arr2[i] * f2;
		
		return arr;
	}
	
	public static float[] linearCombinate(float[] arr1, float f1, float[] arr2, float f2){
		if(arr1 == null || arr2 == null){
			System.err.println("Empty Array.");
			System.exit(0);
		}else if(arr1.length != arr2.length){
			System.err.println("Dimensions are inconsistent.");
			System.exit(0);
		}

		int len = arr1.length;
		float[] arr = new float[len];
		for(int i = 0; i < len; i++)
			arr[i] = arr1[i] * f1 + arr2[i] * f2;

		return arr;
	}
	
	/**
	 * ����ת��Ϊ����
	 * @param <K>
	 * @param list
	 * @return array[i] = list.get(i)
	 */
	public static double[] listToArray(List<Double> list){
		int len = list.size();
		double[] array = new double[len];
		for(int i = 0; i < len; i++)
			array[i] = list.get(i);
	
		return array;
	}

	/**
	 * ������ת��Ϊ����
	 * @param array
	 * @return list.get(i) = array[i]
	 */
	public static List<Double> arrayToList(double[] array){
		List<Double> list = new ArrayList<Double>();
		int len = array.length;
		for(int i = 0; i < len; i++)
			list.add(array[i]);
		return list;
	}
	public static List<Float> arrayToList(float[] array){
		List<Float> list = new ArrayList<Float>();
		int len = array.length;
		for(int i = 0; i < len; i++)
			list.add(array[i]);
		return list;
	}

	/**
	 * ��������
	 * @param valueToRound
	 * @param numberOfDecimalPlaces
	 * @return ��ȷ��С�����ĳ��λ 
	 */
	public static double round(double valueToRound, int numberOfDecimalPlaces)
	{
	    double multipicationFactor = Math.pow(10, numberOfDecimalPlaces);
	    double interestedInZeroDPs = valueToRound * multipicationFactor;
	    return Math.round(interestedInZeroDPs) / multipicationFactor;
	}
	
	public static float round(float valueToRound, int numberOfDecimalPlaces){
		float multipicationFactor = (float)Math.pow(10, numberOfDecimalPlaces);
	    float interestedInZeroDPs = valueToRound * multipicationFactor;
	    return Math.round(interestedInZeroDPs) / multipicationFactor;
	}

	/**
	 * �������ָ����Χ�ڵ�ʮ������,��Ҫ��ȷ��С�����precisionλ
	 * @param lowerBound
	 * @param upperBound
	 * @param nDigit
	 * @return random number in [lowerBound, upperBound] with precision
	 */
	public static double rand(double lowerBound, double upperBound, int nDigit){
		double rand = lowerBound + Math.random() * (upperBound - lowerBound);
		StringBuilder digits = new StringBuilder();
		while(nDigit-- > 0)
			digits.append("0");
		
		DecimalFormat dFormat = new DecimalFormat("#." + digits.toString());
		return new Double(dFormat.format(rand));
	}

	/**
	 * @param lowerB
	 * @param upperB
	 * @param num
	 * @return ��[lowerB,upperB]�������ȡnum��������ͬ������
	 */
	public static List<Integer> randUnique(int lowerB, int upperB, int num){
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		for(int i = upperB; i >= lowerB; i--)
			numbers.add(i);
		
		Collections.shuffle(numbers);
		return numbers.subList(0, num);
	}

	/**
	 * @param lowerB
	 * @param upperB
	 * @return ��[lowerB,upperB]�������ȡ1������
	 */
	public static int rand(int lowerB, int upperB){
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		for(int i = upperB; i >= lowerB; i--)
			numbers.add(i);
		
		Collections.shuffle(numbers);
		return numbers.get(0);
	}

	/**
	 * @param lowerB
	 * @param upperB
	 * @param num
	 * @return ��[lowerB,upperB]�����ȡnum������
	 */
	public static List<Integer> rand(int lowerB, int upperB, int num){
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		for(int i = 0; i < num; i++)
			numbers.add(rand(lowerB, upperB));

		return numbers;
	}

	/**
	 * ���ɷ��Ӹ�˹�ֲ����������
	 * @param miu
	 * @param sigma
	 * @return x~N(miu, sigma)
	 */
	public static double randNorm(double miu, double sigma){
		Random rand = new Random();
		return miu + rand.nextGaussian() * sigma;
	}
	
	/**
	 * ���ɷ��Ӿ��ȷֲ����������
	 * @param lowerBound
	 * @param upperBound
	 * @return x ~ U(lowerBound,upperBound)
	 */
	public static float randUniform(float lowerBound, float upperBound){
		return (float) (lowerBound + Math.random()*(upperBound - lowerBound));
	}
	
	/**
	 * ���ɸ������ȵ�������ʷֲ�
	 * @param len
	 * @return prob distribution
	 */
	public static float[] randDistribution(int len){
		float[] distr = new float[len];
		for(int i = 0; i < len; i++)
			distr[i] = randUniform(0,1);
		
		sumNormalize(distr);
		
		return distr;
	}

	/**
	 * ����ָ����С�����������
	 * @param dim
	 * @return �������
	 */
	public static float[][] stochasticMatrix(int dim){
		float[][] matrix = new float[dim][dim];
		for(int i = 0; i < dim; i++)
			matrix[i] = randDistribution(dim);
		
		return matrix;
	}
	
	/**
	 * ��ȡ����array����ascend = true)/����ascend = false)�����б�
	 * @param array
	 * @param ascend
	 * @return rank of array in ascending or descending order
	 */
	public static int[] getRank(double[] array, boolean ascend){
		int sz = array.length;
		int[] rank = new int[sz];
		for(int i = 0; i < sz; i++)
			rank[i] = i;
		for(int i = 0; i < sz - 1; i++){
			int max = i;
			for(int j = i + 1; j < sz; j++){
				if(ascend){
					if(array[rank[max]] > array[rank[j]])
						max = j;
					
				}else{
					if(array[rank[max]] <  array[rank[j]])
						max = j;
				}
			}
			//swap
			int tmp = rank[i];
			rank[i] = rank[max];
			rank[max] = tmp;
		}
		return rank;
	}
	
	public static int[] getRank(float[] array, boolean ascend){
		int sz = array.length;
		int[] rank = new int[sz];
		for(int i = 0; i < sz; i++)
			rank[i] = i;
		for(int i = 0; i < sz - 1; i++){
			int max = i;
			for(int j = i + 1; j < sz; j++){
				if(ascend){
					if(array[rank[max]] > array[rank[j]])
						max = j;
					
				}else{
					if(array[rank[max]] <  array[rank[j]])
						max = j;
				}
			}
			//swap
			int tmp = rank[i];
			rank[i] = rank[max];
			rank[max] = tmp;
		}
		return rank;
	} 
	
	/**
	 * ��ȡlist����ascend = true)/����ascend = false)�����б�
	 * @param list
	 * @param ascend
	 * @return rank of list in ascending or descending order
	 */
	public static int[] getRank(List<Double> list, boolean ascend){
		int len = list.size();
		int[] rank = new int[len];
		for(int i = 0; i < len; i++)
			rank[i] = i;
		
		for(int i = 0; i < len - 1; i++){
			int max = i;
			for(int j = i + 1; j < len; j++){
				if(ascend){
					if(list.get(rank[max]) > list.get(rank[j]))
						max = j;
					
				}else{
					if(list.get(rank[max]) <  list.get(rank[j]))
						max = j;
				}
			}
			
			//swap
			int tmp = rank[i];
			rank[i] = rank[max];
			rank[max] = tmp;
		}
		return rank;
	}

	public static int[] getRank(int[] array, boolean ascend){
		int len = array.length;
		int[] rank = new int[len];
		for(int i = 0; i < len; i++)
			rank[i] = i;
		
		for(int i = 0; i < len - 1; i++){
			int max = i;
			for(int j = i + 1; j < len; j++){
				if(ascend){
					if(array[rank[max]] > array[rank[j]])
						max = j;
					
				}else{
					if(array[rank[max]] <  array[rank[j]])
						max = j;
				}
			}
			
			//swap
			int tmp = rank[i];
			rank[i] = rank[max];
			rank[max] = tmp;
		}
		return rank;
	}

	/**
	 * @param array
	 * @return array�ľ�ֵ
	 */
	public static double mean(double[] array){
		int len = array.length;
		return sum(array)/len; 
	}
	public static float mean(float[] array){
		int len = array.length;
		return sum(array)/len; 
	}
	
	/**
	 * @param array
	 * @return array����ֵ
	 */
	public static double median(double[] array){
		List<Double> list = new ArrayList<Double>();
		int len = array.length;
		for(int i = 0; i < len; i++)
			list.add(array[i]);
		
		Collections.sort(list);
		return array[len/2];
	}
	
	public static float median(float[] array){
		List<Float> list = new ArrayList<Float>();
		int len = array.length;
		for(int i = 0; i < len; i++)
			list.add(array[i]);

		Collections.sort(list);
		return array[len/2];
	}
	
	/**
	 * @param array
	 * @return array�ķ���
	 */
	public static double var(double[] array){
		int len = array.length;
		if(len == 1)
			return 0;
		
		double mean = mean(array), sum = 0;
		
		for(int i = 0; i < len; i++)
			sum += (array[i] - mean) * (array[i] - mean);

		return sum/(len - 1);  
	}
	
	/**
	 * @param array
	 * @return array�ı�׼��
	 */
	public static double stdVar(double[] array){
		return Math.sqrt(var(array));
	}

	/**
	 * ������ĸ������֮���ӳ���ϵ
	 * <p>ʹ��26��Ӣ����ĸ����������,������26����(����Ԫ)��<br/>
	 * a-z: 1-26<br/>
	 * aa-az: 27-52<br/>
	 * ba-bz: 53-78<br/>
	 * ......<br/>
	 * aaa-zzz: 703-18278<br/>
	 * 
	 * ���磺΢��Excel��������ʹ�õľ������ֱ��뷽ʽ
	 * </p>
	 * 
	 * ��������ֵת��Ϊ��ĸ��
	 * @param num
	 * @return alpha represents the num
	 */
	public static String num2alpha(int num){
		if(num < 1)
			System.err.println("Idx is less than 1!");
		
		List<Integer> remainderList = new ArrayList<Integer>();

		int remainder = 0;
		//ȡ�ඨԪ
		while((remainder = num % 26) != num){
			num = (num - remainder)/26;
			remainderList.add(remainder);
		}
		remainderList.add(remainder);

		int product = 1;//�ж���Ԫ���ڷ�
		for(int i = 0; i < remainderList.size(); i++)
			product *= remainderList.get(i);

		if(product == 0)
			//��λ�滻ͷ����Ԫ
			while(remainderList.get(remainderList.size() - 1) > 0){
				for(int j = remainderList.size() - 1; j >= 0; j--){
					if(remainderList.get(j) == 0){
						remainderList.set(j, 26);
						remainder = remainderList.get(j + 1);
						remainderList.set(j + 1, remainder - 1);
					}
				}
			}
		
		//ת������ĸ
		String alpha = "";
		for(int i = remainderList.size() - 1; i >= 0; i--){
			remainder = remainderList.get(i);
			if(remainder == 0)//�޳�β����Ԫ
				continue;
			
			alpha += (char)(remainder + 64);
		}
		return alpha;
	}

	/**
	 * ����ĸ��ת��Ϊ������ֵ
	 * @param alpha
	 * @return num represented by alpha
	 */
	public static int alpha2num(String alpha){
		alpha = alpha.toUpperCase();
		int idx = 0, len = alpha.length();
		for(int i = 0; i < len; i++)
			idx += (int)(alpha.codePointAt(i) - 64) * Math.pow(26, len - i - 1);
		return idx;
	}
	
	/**
	 * <p><b>BitComputeִ��λ����,����λ����ʽ��ʾ:</b></p>
	 * <p>���Ƚ��ܼ��ֻ�����λ����, HashCode, BitSet��MessageDigest</p>
	 * <p>����32λ(4byte)��Integer����,���λ����1,���ʾ����,����1,������Ʊ��룺<br/>
	 * 00000000 00000000 00000000 00000001<br/>
	 * ����������31λ(1 << 31),��<br/>
	 * 10000000 00000000 00000000 00000000,��ʾ����,���������30λ,��<br/>
	 * 01000000 00000000 00000000 00000000,��ʾ����2^30<br/>
	 * ����֪��������������2^31-1=Integer.MAX_VALUE=2,147,483,647,��������ʽΪ��<br/>
	 * 01111111 11111111 11111111 11111111<br/>
	 * �ڴ˻�����+1:<br/>
	 *   01111111 11111111 11111111 11111111 ( 2^31-1 )<br/>
	 * + 00000000 00000000 00000000 00000001 ( 1 )<br/>
	 * = 10000000 00000000 00000000 00000000 ( 1 << 31 )</p>
	 * 
	 * <p>��������е������������ɻ�״,��0��ʼ˳ʱ������Ϊ1,2,...,��Integer.MAX_VALUE��<br/>
	 * Integer.MIN_VALUE����,�Ӷ�����ֿ���ì�ܵļ�����: 2,147,483,647 + 1 = <br/>
	 * -2,147,483,648, 2,147,483,647 + 2 = -2,147,483,647<br/>
	 * �����κ����͵������ڼ�����ж���һ����ʾ��Χ,������������Χ�ͻ�����쳣,<br/>
	 * ����ͨ��ǿ��ת�����ܹ���ʾ����Χ������ʱ�Ϳ��Խ���쳣����,����:<br/>
	 * (long) Integer.MAX_VALUE + 1 = 2,147,483,648</p>
	 * 
	 * @param n
	 * @return �������ִ�
	 */
	public static String getBinary(int n){
		String bin = Integer.toBinaryString(n);
		int len = bin.length();
		int missed = 0;
		if((missed = Integer.SIZE - len) > 0){
			for(int i = 0; i < missed; i++)
				bin = "0" + bin;
		}

		return bin;
	}

	/**
	 * @param n
	 * @return �������ִ�
	 */
	public static String getBinary(long n){
		String bin = Long.toBinaryString(n);
		int len = bin.length();
		int missed = 0;
		if((missed = Long.SIZE - len) > 0){
			for(int i = 0; i < missed; i++)
				bin = "0" + bin;
		}
		return bin;
	}
	
	/**
	 * ��������ת��Ϊʮ������
	 * <p>ÿ��λһ�黻�㵽��ʮ�����ƣ�����<br/>
	 * bin: 1001 0010 1100<br/>
	 * Dec:  9    2    12<br/>
	 * Hex:  9    2    c
	 * </p>
	 * @param bin
	 * @return hex of bin
	 */
	public static String bin2hex(String bin){
		// i -> dec2hex[i]
		char[] dec2hex = {'0', '1', '2', '3', '4', 
				'5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f'};

		int len = bin.length(), mod = len % 4;
		StringBuffer hashcode = new StringBuffer();
		
		String subbin = "";		
		if(mod > 0){
			subbin = bin.substring(0, mod);
			hashcode.append(dec2hex[bin2dec(subbin)]);
			if(mod == len)
				return hashcode.toString();
			
			bin = bin.substring(mod);
		}

		for(int i = 0; i < bin.length()/4; i++){
			subbin = bin.substring(4 * i, 4 * i + 4);
			hashcode.append(dec2hex[bin2dec(subbin)]);
		}
		return hashcode.toString();
	}
	
	/**
	 * �Ӷ�����ת��Ϊʮ����
	 * @param bin
	 * @return dec of bin
	 */
	public static int bin2dec(String bin){
		int len = bin.length();
		int dec = 0;
		for(int i = 0; i < len; i++)
			dec += (bin.charAt(i) - 48) * Math.pow(2, len - i - 1);
		return dec;
	}
}
