package com.nautilus.itmc;

import java.util.ArrayList;
import java.util.List;

public class SubInterval {
	
	private int attrIdx;
	private final List<DataRecord> records = new ArrayList<DataRecord>();
	int[] sortedDistinctIndex;
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
	
	public double getDistinctValue(int idx) {
		return records.get(distincts[idx]).getValue(attrIdx).getRValue();
	}
	
	public DataRecord getDistinctRecord(int idx) {
		return records.get(distincts[idx]);
	}
	
	//distinct index without sort
	int[] distincts;
	public double[] getDistinctValues() {
		distincts = new int[records.size()];
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
		
		sortedDistinctIndex = new int[n];
		result = new double[n];
		System.arraycopy(distincts, 0, sortedDistinctIndex, 0, n);
		
		int tmp;
		//sort the distinct values
		for(int i=0; i<n-1; i++) {
			for(int j=i+1; j<n; j++) {
				if(records.get(sortedDistinctIndex[i]).getValue(attrIdx).getRValue() > records.get(sortedDistinctIndex[j]).getValue(attrIdx).getRValue()) {
					tmp = sortedDistinctIndex[i];
					sortedDistinctIndex[i] = sortedDistinctIndex[j];
					sortedDistinctIndex[j] = tmp;
				}
			}
		}
		
		for(int i=0; i<n; i++) {
			result[i] = records.get(sortedDistinctIndex[i]).getValue(attrIdx).getRValue();
		} 
		
		return result;
	}
	
	public SubInterval[] getTwoSubInterval(double threshold) {	
		SubInterval[] subs = new SubInterval[2];
		SubInterval s1 = new SubInterval(attrIdx);
		SubInterval s2 = new SubInterval(attrIdx);
		for(int i=0; i<sortedDistinctIndex.length; i++) {
			if(records.get(sortedDistinctIndex[i]).getValue(attrIdx).getRValue() <= threshold ) {
				s1.addRecord(records.get(sortedDistinctIndex[i]));
			} else {
				s2.addRecord(records.get(sortedDistinctIndex[i]));
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
	
	/**
	 * 
	 * @param clsT
	 * @param z
	 * @param totalS
	 * @param pz
	 * @return
	 */
	public double calcP(String clsT, Node z, int totalS, double pz) {
		double p1, p2;
		p1 = (1.0 * records.size()) / totalS;
		p2 = (1.0 * countCt(clsT)) / z.size();
		
		return (p1 * p2 * pz);
	}
	
	/*
	 * Dem so luong target class trong subinterval nay
	 */
	public int countClsT() {
		int c = 1;
		
		if(records.size() <= 0)
			return 0;
		
		String[] distincClass = new String[records.size()];
		distincClass[0] = records.get(0).lastValue();
		boolean existed;
		for(int i=1; i<records.size(); i++) {
			existed = false;
			for(int j=0; j<c; j++) {
				if(records.get(i).lastValue().equals(distincClass[j])) {
					existed = true;
					break;
				}
			}
			
			if(!existed)
				c++;
		}
		
		return c;
	}
}
