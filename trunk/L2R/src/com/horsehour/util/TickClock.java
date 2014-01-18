package com.horsehour.util;

/**
 * @author Chunheng Jiang
 * @version 1.0
 * @resume Helps to record time cost by process
 */
public class TickClock {
	private static long start = 0, end = 0;
	//��ʼ��ʱ
	public static void beginTick(){
		start = System.currentTimeMillis();
	}
	//������ʱ
	public static void stopTick(){
		end = System.currentTimeMillis();
		showElapsed();
	}
	//��ʾ��ʱ
	private static void showElapsed(){
		float elapsedSec = (float)(end - start)/1000;
		StringBuilder sb = new StringBuilder("[Time Elapsed: ");
		if(elapsedSec > 3600)
			sb.append(elapsedSec/3600 + " hours.]");
		else if(elapsedSec > 60)
			sb.append(elapsedSec/60 + " minutes.]");
		else
			sb.append(elapsedSec + " seconds.]");
		System.out.println(sb.toString());
	}
}
