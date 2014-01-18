package com.horsehour.datum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.horsehour.math.MathLib;


/**
 * RateSet���Ƽ�ϵͳ��ʹ�õ����ݼ���ÿһ���Ǹ���Ԫ��(userId,itemId,rate)
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20130409
 */
public class RateSet {
	public Map<Integer, List<Preference>> rateData = null;
	public Map<Integer, List<Integer>> map = null;

	public RateSet(){
		rateData = new HashMap<Integer, List<Preference>>();
		map = new HashMap<Integer, List<Integer>>();
	}

	/**
	 * ���preference
	 * @param usrId
	 * @param preference
	 */
	public void addPreference(int usrId, Preference pref) {
		List<Preference> prefList = null;
		
		if(rateData.containsKey(usrId))
			prefList = rateData.get(usrId);
		else
			prefList = new ArrayList<Preference>();

		prefList.add(pref);
		rateData.put(usrId, prefList);
	}
	
	/**
	 * @param usrId
	 * @return �����û���idȡ����preference�б�
	 */
	public List<Preference> getPrefList(int usrId){
		List<Preference> prefList = null;
		if(rateData.containsKey(usrId))
			prefList = rateData.get(usrId);
		return prefList;
	}
	
	/**
	 * @param usrId
	 * @return ��ȡ�û��������б�
	 */
	public float[] getRateList(int usrId){
		List<Preference> prefList = getPrefList(usrId);
		float[] rates = new float[prefList.size()];
		for(int i = 0; i < rates.length; i++)
			rates[i] = prefList.get(i).rate;
		return rates; 
	}

	/**
	 * @param userId
	 * @return �����û�����ʷ���֣�����ƽ������
	 */
	public float getAverageRateByUser(int userId){
		float[] rates = getRateList(userId);
		return MathLib.sum(rates)/rates.length;
	}
	
	/**
	 * @param itemId
	 * @return ������Ʒ����ʷ���֣�����ƽ������
	 */
	public float getAverageRateOfItem(int itemId){
		List<Integer> userList = map.get(itemId);
		float[] rates = new float[userList.size()];
		List<Preference> prefList = null;
		for(int i = 0; i < rates.length; i++){
			prefList = rateData.get(userList.get(i));
			rates[i] = searchRate(itemId, prefList); 				
		}
		return MathLib.sum(rates)/rates.length;
	}
	
	/**
	 * @param itemId
	 * @param prefList
	 * @return ��prefList�ҵ�itemId��Ӧ��rate
	 */
	private float searchRate(int itemId, List<Preference> prefList){
		Preference pref = null;
		float rate = 0;
		for(int i = 0; i < prefList.size(); i++){
			pref = prefList.get(i);
			if(itemId == pref.itemId)
				rate = pref.rate;
		}
		return rate;
	}
	/**
	 * @return ȡ��rate����Ŀ
	 */
	public int getNRates(){
		int nRates = 0;
		Set<Integer> userset = rateData.keySet();
		for(int usrId : userset)
			nRates += getNRates(usrId);
		
		return nRates;
	}
	
	/**
	 * @param usrId
	 * @return ȡ��ָ���û���������Ŀ
	 */
	public int getNRates(int usrId){
		if(rateData.containsKey(usrId))
			return rateData.get(usrId).size();
		else
			return 0;
	}
	/**
	 * @return ȡ�ò������ֵ��û���Ŀ
	 */
	public int getNUsers(){
		return rateData.size();
	}
}