package com.horsehour.gene;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.Date;

/**
 * GAOptimizationʹ���Ŵ��㷨�����Ż�����
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20110910
 * 
 * <p>solve maximum f(x) = sin(pi*x/256) where 0 <= x <= 255</p>
 * <p>ʵ�ֽ������,��Ҫ��������ռ��е�256������,����֮ǰ,���ȶ�����ռ䱸ѡ���ϱ���,ѡȡ8��λ������,
 * ��Ҫ������Ⱥ(population)�͸���(Individual)</p>
 */
public class GAOptimization {
	private double rangeInf;//�������½�
	private double rangeSup;//�������Ͻ�

	private int resolution;//�������ϵľ���,��Ҫ��ȷ��С�����resolutionλ
	private int geneLength;
	private int popSize;
	private int maxGeneration;

	private Population pop;
	private String outPath;
	private FileWriter writer;
	
//	һ��أ����������½���Ϊ0�㣬Ȼ���������½�ļ�����Լ����ȶ������ڵ�������
//	��[2.5,3.5],��ȷ��С�����2λ�����������������Ҫ7λ,��2.50-->0000000��2.51-->0000001
//	�ɣ�2.80-2.50��*100=30,��2.80-->0011110����3.50-->1100100
	
	public GAOptimization(){}
	
	public GAOptimization(double rangeInf,double rangeSup,int resolution,
			int popSize,int maxGeneration,float crossRate,String outPath){
		this.rangeInf=rangeInf;
		this.rangeSup=rangeSup;
		this.resolution=resolution;
		this.geneLength=computeGeneStrLen();
		
		this.outPath=outPath;
		letOut();
		initPop(popSize,maxGeneration,crossRate);
	}
	
	/**
	 * ��ʼ����Ⱥ
	 * @param popSize ��Ⱥ��С
	 * @param maxGeneration ������
	 * @param crossRate ������
	 */
	private void initPop(int popSize,int maxGeneration,float crossRate){
		this.popSize=popSize;
		this.maxGeneration=maxGeneration;
		this.pop=new Population(crossRate);
	}
	
	/**
	 * ��ʵĿ�꺯��
	 * @param x
	 * @return sin(��*x/256)
	 */
	private static double objectFunction(double x){
		return Math.sin(Math.PI * x/256);
	}
	
	/**
	 * ��Ӧ����
	 * @param x
	 * @return Ŀ�꺯��ֵ
	 */
	private static double fitnessFunction(double x){
		return objectFunction(x);
	}
	
	/**
	 * ������������
	 */
	private void run(){
		try {
			while(pop.generation <= maxGeneration){
				
				pop.evolute();
				
				StringBuilder sb = new StringBuilder();
				sb.append("\nGeneration:" + pop.generation + "\n");
				
				for(int i = 0; i < popSize; i++)
					pop.individuals[i].formatedGenes(sb);
				
				writer.append(sb.toString());
			}
			
			writer.close();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
	}

	/**
	 * ������������浽�����ļ�
	 */
	private void letOut(){
		try {
			writer = new FileWriter(outPath, true);
			writer.append("Time:" + new SimpleDateFormat("yyyyMMddHHmmss")
				.format(new Date(System.currentTimeMillis())) + "\n");
		} catch (IOException e) {
			return;
		}
	}
	/**
	 * �������ָ����Χ�ڵ�ʮ������,��Ҫ��ȷ��С�����resolutionλ
	 * @param rangeInf
	 * @param rangeSup
	 * @param resolution
	 * @return random number generated
	 */
	private double randomer(double rangeInf,double rangeSup,int resolution){
		double ret = rangeInf + Math.random()*(rangeSup-rangeInf);
		StringBuilder zeroStr = new StringBuilder();
		while(resolution-- > 0)
			zeroStr.append("0");
		
		DecimalFormat dFormat = new DecimalFormat("#." + zeroStr.toString());
		return new Double(dFormat.format(ret));
	}
	
	/**
	 * �ɹ�ʽ N = floor[log2(range*10^resolution)]+1 ��ȷ����Ҫ����С������λ��
	 * @return Least numbers of bits needed
	 */
	private int computeGeneStrLen(){
		double range = rangeSup - rangeInf;
		double ret = (Math.log10(range)+resolution)/Math.log10(2);
		return new Double(Math.floor(ret)).intValue()+1;
	}
	
	public static void main(String[] args) throws IOException {
		String outPath=".\\log";
		GAOptimization ga=new GAOptimization(0,255,3,20,50,0.4F,outPath);
		ga.run();
		for(int i=0;i<ga.popSize;i++){
			System.out.print(ga.pop.individuals[i].decoder()+" ");
		}
	}
	
//	�������
	public class Individual{
		private BitSet genes;
		public Individual(){
			this.genes=new BitSet(geneLength);
			double rand=randomer(rangeInf,rangeSup,resolution);			
			encoder(rand);
		}

//		��ʮ���������ɶ����ƴ�
		private BitSet encoder(double rand){
//			������rangeInf�������
			int multi=(int)((rand-rangeInf)*Math.pow(10, resolution));
			String geneString=Integer.toBinaryString(multi);
			int focus=geneLength-1;
			if(genes!=null){
				genes.clear();
			}
			for(int i=geneString.length()-1;i>=0;i--){
				if('1'==geneString.charAt(i)){
					genes.set(focus);
				}
				focus--;
			}
			return genes;
		}
		
//		��������ת��Ϊʮ����
		private double decoder(){
			double ret=0D;
			for(int i=0;i<geneLength;i++){
				if(genes.get(i))
					ret+=Math.pow(2, geneLength-1-i);
			}
			return ret/Math.pow(10, resolution)+rangeInf;
		}
		
//		���������彻��ָ����Χ�Ļ�������
		private void exchangeRange(Individual ind,int fromIndex,int toIndex){
			if(toIndex>=fromIndex){
				for(int i=fromIndex;i<=toIndex;i++){
					if(this.genes.get(i)^ind.genes.get(i)){
						if(ind.genes.get(i)){
							this.genes.set(i);
							ind.genes.clear(i);
						}else{
							this.genes.clear(i);
							ind.genes.set(i);
						}
					}
				}
			}
		}
		
//		ȷ��ָ��λ���ϵĶ����Ʒ���0/1
		public int getGeneAt(int position){
			if(genes.get(position)){
				return 1;
			}
			return 0;
		}
		
//		�Ի������и�ʽ�����
		private void formatedGenes(StringBuilder sb){
			for(int j=0;j<popSize;j++){
				sb.append(getGeneAt(j));
			}
			sb.append("-->"+decoder()+"\n");
		}
	}
	
//	������Ⱥ
	public class Population{
		private Individual[] individuals;
		private Individual[] elits;
		private double[] fit; 
		private float crossRate;
		private int generation;//������
		
		public Population(float crossRate){
			this.crossRate=crossRate;
			initialize();
			this.generation=1;//��һ��
		}
		
//		��ʼ��ָ����С����Ⱥ
		private void initialize(){
			individuals=new Individual[popSize];
			StringBuilder sb=new StringBuilder();
			sb.append("Initialization:\n");
			for(int i=0;i<popSize;i++){
				individuals[i]=new Individual();
				individuals[i].formatedGenes(sb);
			}
			try {
				writer.write(sb.toString()+"\n");
			} catch (IOException e) {}
		}
		
//		Ⱥ�����
		private void evolute(){
			getFitness();
			rws();
			int pairs=0;
			if(popSize%2==0){
				pairs=popSize/2;
			}else{
				pairs=(popSize-1)/2;
				individuals[2*pairs].encoder(elits[2*pairs].decoder());
			}
			
			for(int i=0;i<pairs;i++){
				cross(elits[2*i],elits[2*i+1]);
				individuals[2*i].encoder(elits[2*i].decoder());
				individuals[2*i+1].encoder(elits[2*i+1].decoder());
			}
			StringBuilder sb=new StringBuilder();
			sb.append("\n");
			for(int i=0;i<popSize;i++){
				individuals[i].formatedGenes(sb);
			}
			try {
				writer.append(sb.toString());
			} catch (IOException e) {}
			
			generation++;//�������µ�һ��
		}
		
//		��������������Ӧ��(0~1)
		private double[] getFitness(){
			fit=new double[popSize];
			double sum=0D;
			for(int i=0;i<popSize;i++){
				double ret=individuals[i].decoder();
				fit[i]=fitnessFunction(ret);
				sum+=fit[i];
			}
			if(sum>0D){
				for(int j=0;j<popSize;j++){
					fit[j]/=sum;
				}
			}
			return fit;
		}
		
//		�����ۻ���Ӧ��
		private double[] cumulativeFitness(){
			double[] cumulativeFit=new double[popSize];
			cumulativeFit[0]=fit[0];
			StringBuilder sb=new StringBuilder();
			sb.append("Fitness:");
			for(int i=1;i<popSize;i++){
				cumulativeFit[i]=cumulativeFit[i-1]+fit[i];
				DecimalFormat dFormat=new DecimalFormat("#.000");
				sb.append(dFormat.format(cumulativeFit[i])+"\t");
			}
			try {
				writer.write(sb.toString()+"\n");
			} catch (IOException e) {}
			return cumulativeFit;
		}
		
//		�������̶ķ�ʽѡ�񸸴���������Roulette Wheel Select)
		private void rws(){
			double[] cumulativeFit=cumulativeFitness();
			double rands=0.0D;
			this.elits=new Individual[popSize];
			StringBuilder sb=new StringBuilder();
			sb.append("Roulette Wheel Select:\n");
			for(int i=0;i<popSize;i++){
				rands=Math.random();
				elits[i]=new Individual();
				for(int j=0;j<popSize;j++){
					if(rands<=cumulativeFit[j]){
//						����individuals��elits����ָ��仯��Ӱ��Է�
//						�мɣ�ֱ�Ӹ�ֵ
						elits[i].encoder(individuals[j].decoder());
						elits[i].formatedGenes(sb);
						break;
					}
				}
			}
			try {
				writer.append(sb.toString()+"\nCrossPoint:");
			} catch (IOException e) {}
		}
		
//		�����ӽ�crossover���ӽ�����ΪcrossRate
		private void cross(Individual ind1,Individual ind2){
			int tail=geneLength-1;
			int crossIndex=(int)Math.floor(randomer(0,tail,0));
			try {
				writer.append(crossIndex+" ");
			} catch (IOException e) {}
			
			int endIndex=(int)(crossIndex+(tail+1)*crossRate)-1;
			int leftLen=0;
			if((leftLen=endIndex-tail)>0){
				ind1.exchangeRange(ind2, crossIndex, tail);
				ind1.exchangeRange(ind2,0,leftLen-1);
			}else{
				ind1.exchangeRange(ind2, crossIndex, endIndex);
			}
		}
	}
}