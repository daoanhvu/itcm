package com.nautilus.itmc;

import java.util.ArrayList;
import java.util.List;

public class SubInterval {
	
	private int attrIdx;
	private final List<DataRecord> records = new ArrayList<DataRecord>();
	int[] distinctIndex;
	double lowerBound;
	
	public SubInterval() {
		
	}
	
	public SubInterval(int aIdx) {
		attrIdx = aIdx;
	}
	
	public int getAttribute() {
		return attrIdx;
	}
	
	public void setAttribute(int attribute) {
		this.attrIdx = attribute;
	}
	
	public void addRecord(DataRecord dr) {
		records.add(dr);
	}
	
	public void clear() {
		records.clear();
	}
	
	public int size() {
		return records.size();
	}
	
	public double[] getDistincValue() {
		int[] distincts = new int[records.size()];
		double[] result;
		DataRecord rci;
		boolean flag = false;
		int n = 0;
		double v;
		for(int i=0; i<records.size(); i++) {
			rci = records.get(i);
			v = rci.getValue(attrIdx).getRValue();
			
			flag = false;
			for(int j=0; j<n; j++) {
				if(v == records.get(distincts[j]).getValue(attrIdx).getRValue()) {
					flag = true;
					break;
				}
			}
			
			if(!flag) {
				distincts[n++] = i;
			}
				
		}
		
		
		distinctIndex = new int[n];
		result = new double[n];
		System.arraycopy(distincts, 0, distinctIndex, 0, n);
		
		int tmp;
		//sort the distinct values
		for(int i=0; i<n-1; i++) {
			for(int j=i+1; j<n; j++) {
				if(records.get(distinctIndex[i]).getValue(attrIdx).getRValue() > records.get(distinctIndex[j]).getValue(attrIdx).getRValue()) {
					tmp = distinctIndex[i];
					distinctIndex[i] = distinctIndex[j];
					distinctIndex[j] = tmp;
				}
			}
		}
		
		for(int i=0; i<n; i++) {
			result[i] = records.get(distinctIndex[i]).getValue(attrIdx).getRValue();
		} 
		
		return result;
	}
	
	public SubInterval[] getTwoSubInterval(double threshold) {	
		SubInterval[] subs = new SubInterval[2];
		SubInterval s1 = new SubInterval(attrIdx);
		SubInterval s2 = new SubInterval(attrIdx);
		for(int i=0; i<distinctIndex.length; i++) {
			if(records.get(distinctIndex[i]).getValue(attrIdx).getRValue() <= threshold ) {
				s1.addRecord(records.get(distinctIndex[i]));
			} else {
				s2.addRecord(records.get(distinctIndex[i]));
			}
		}
		subs[0] = s1;
		subs[1] = s2;
		return subs;
	}
	
	public int countCt(String ct) {
		int c = 0;
		for(int i=0; i<records.size(); i++) {
			if(records.get(i).lastValue().equals(ct))
				c++;
		}
		
		return c;
	}
	
	public double calcP(String clsT, Node z, int totalS, double pz) {
		double p1, p2;
		p1 = (1.0 * records.size()) / totalS;
		p2 = (1.0 * countCt(clsT)) / z.size();
		
		return (p1 * p2 * pz);
	}
}
