package com.horsehour.math;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.horsehour.datum.DataManager;
import com.horsehour.metric.KendallTau;
import com.horsehour.util.FileManager;
import com.horsehour.util.MatlabUtil;
import com.horsehour.util.TickClock;

/**
 * PowerMethodʵ���˻������ݷ�
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20121207
 */

public class PowerMethod {
	public int nIter = 100;
	public double precision = 1.0E-15;

	public float[] desireProb;
	public float[] predictProb;

	public PowerMethod(){}

	/**
	 * �������������������������
	 * @param matrix
	 * @param measuerFile
	 * @param trackFile
	 */
	public void run(float[][] matrix, String measureFile, String trackFile){
		float epsilon = 1;
		float[] curr = new float[matrix.length];
		init(curr);
		float[] pre = Arrays.copyOf(curr, curr.length);
		desireProb = calcPermuteProb(pre);
		KendallTau metric = new KendallTau();

		int count = nIter;
		
		StringBuffer sb = null;
		while(count-- > 0){
			sb = new StringBuffer();
			for(int i = 0; i < matrix.length; i++)
				for(int j = 0; j < matrix.length; j++)
					curr[i] += matrix[j][i] * pre[j];

			epsilon = residual(curr, pre);
			if(epsilon <= precision)
				break;

			predictProb = calcPermuteProb(curr);
			
			//epsilon v.s kl-divergence v.s kendall tau distance
			sb.append(nIter - count + "\t" + epsilon + "\t" + divergence() + 
					"\t" + metric.tauDistance(curr, pre) + "\n");
			
			FileManager.writeFile(measureFile, sb.toString());
			track(curr, trackFile);
			pre = Arrays.copyOf(curr, curr.length);
			desireProb = Arrays.copyOf(predictProb, predictProb.length);
		}
	}

	/**
	 * ��ʼ��
	 * @param x
	 */
	private void init(float[] x){
		Random rand = new Random();
		for(int id = 0; id < x.length; id++)
			x[id] = rand.nextFloat();

		MathLib.maxNormalize(x);
	}

	/**
	 * ����в�
	 * @param x
	 * @param y
	 * @return �в�
	 */
	private float residual(float[] x, float[] y){
		MathLib.maxNormalize(x);
		x = MathLib.diff(x, y);
		return MathLib.getEuclideanNorm(x);
	}

	/**
	 * ���ϴε��������Ϊ��ʵֵ,�Ե��ε������ΪԤ��ֵ
	 * ������Ե�Top one permutation prob
	 * ʹ��KL-Divergence�������ֲַ��ľ���-��ʧ����
	 * 
	 * @param ground truth
	 * @param vect
	 */
	public float[] calcPermuteProb(float[] vect){
		float expSum = 0;
		int len = vect.length;
		float[] permuteProb = new float[len];

		for(int id = 0; id < len; id++)
			expSum += Math.exp(vect[id]);

		for(int id = 0; id < len; id++)
			permuteProb[id] = (float) (Math.exp(vect[id])/expSum);

		return permuteProb;
	}

	/**
	 * ���������ʷֲ�����:KL-Divergence
	 * @return divergence
	 */
	public float divergence(){
		int len = predictProb.length;
		float loss = 0;
		for(int id = 0; id < len; id++)
			loss += desireProb[id] * Math.log(desireProb[id]/predictProb[id]);

		return loss;
	}

	/**
	 * �������ټ����浽�����ļ�
	 * @param ret
	 * @param trackFile
	 */
	public void track(float[] ret, String trackFile){
		FileWriter writer = null;
		try {
			writer = new FileWriter(trackFile, true);
			StringBuffer sb = new StringBuffer();
			for(int id = 0; id < ret.length; id++)
				sb.append(ret[id] + "\t");

			sb.append("\n");
			writer.write(sb.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	/**
	 * ��ÿһ��track����,ȡ�ø��Ե�����,������������ļ�
	 * @param dataFile
	 * @param destFile
	 */
	public void trackRank(String dataFile, String destFile){
		List<double[]> datum = DataManager.loadDatum(dataFile, "utf-8", "\t");
		int[] rank;
		StringBuffer sb;
		for(double[] data : datum){
			rank = MathLib.getRank(data, false);
			sb = new StringBuffer();
			for(int i = 0; i < rank.length; i++)
				sb.append(rank[i] + 1 + "\t");
			
			sb.append("\n");
			
			FileManager.writeFile(destFile, sb.toString());
		}
	}
	
	/**
	 * @param srcFile
	 * @param rankId
	 * @param len
	 * @return ȡ��������startIdx��startIdx + len - 1���б�
	 */
	public int[] getkIdx(String srcFile, int rankId, int len){
		int[] idx = new int[len];
		List<double[]> datum = DataManager.loadDatum(srcFile, "utf-8", "\t");
		double[] rank = datum.get(datum.size() - 1);//���һ��
		for(int i = rankId; i < rankId + len; i++)
			for(int j = 0; j < rank.length; j++)
				if(rank[j] == i)
					idx[i - rankId] = j;

		return idx;
	}
	
	/**
	 * ��srcFileȡ��������startIdx��startIdx + len - 1����
	 * @param srcFile
	 * @param rankId
	 * @param len
	 */
	public void getRankTrack(String srcFile, String destFile, int rankId, int len){
		int[] idx = getkIdx(srcFile, rankId, len);
		List<double[]> datum = DataManager.loadDatum(srcFile, "utf-8", "\t");
		StringBuffer sb = new StringBuffer();
		for(double[] data : datum){
			for(int i : idx)
				sb.append(data[i] + "\t");
			
			sb.append("\n");
		}
		
		FileManager.writeFile(destFile, sb.toString());
	}
	
	/**
	 * ��ÿһ��track,���������α�׼��,����ķ�Χ�Ǵ�startIdx����Ϊlen(��������)
	 * @param dataFile
	 * @param destFile
	 * @param startIdx
	 * @param len
	 */
	public void getTrackRankVar(String dataFile, String destFile, int startIdx, int len){
		List<double[]> datum = DataManager.loadDatum(dataFile, "utf-8", "\t");

		int dim = datum.get(0).length;
		float[] mean = new float[dim];
		float[] stdvar = new float[dim];
		for(int j = 0; j < dim; j++){
			for(int i = startIdx; i < startIdx + len; i++)
				mean[j] += datum.get(i)[j];
			mean[j] /= len;
		}
		
		for(int j = 0; j < dim; j++){
			for(int i = startIdx; i < startIdx + len; i++)
				stdvar[j] += (datum.get(i)[j] - mean[j]) * (datum.get(i)[j] - mean[j]);
			stdvar[j] = (float) Math.sqrt(stdvar[j] / (len - 1));
		}
		
		StringBuffer sb = new StringBuffer();
		for(float var : stdvar)
			sb.append(var + "\t");

		sb.append("\n");
		FileManager.writeFile(destFile, sb.toString());
	}

	public static void main(String[] args){
		TickClock.beginTick();

		PowerMethod pm = new PowerMethod();
		String measure = "c:/users/dell/desktop/measure.txt",
				track = "c:/users/dell/desktop/track.txt",
				rank =  "c:/users/dell/desktop/rank.txt",
				selectedrank = "c:/users/dell/desktop/",
				rankvar =  "c:/users/dell/desktop/rankvar.txt";

		int dim = 10000;
		float[][] matrix = MathLib.stochasticMatrix(dim);
		pm.run(matrix, measure, track);

		MatlabUtil.plot(track, 1, dim);

		pm.trackRank(track, rank);

		int k = 10;
		pm.getRankTrack(rank, selectedrank + "sr1.txt", 1, k);
		pm.getRankTrack(rank, selectedrank + "sr2.txt", 4996, k);
		pm.getRankTrack(rank, selectedrank + "sr3.txt", 9991, k);
		
		pm.getTrackRankVar(rank, rankvar, 0, 10);

		TickClock.stopTick();
	}
}
