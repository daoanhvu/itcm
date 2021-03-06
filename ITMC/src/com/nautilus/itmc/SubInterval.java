package com.nautilus.itmc;

import java.util.ArrayList;
import java.util.List;

public class SubInterval {
	
	private int attrIdx;
	private double lowerBound;
	private double upperBound;
	private double[] distinctValues;
	
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
	
	public int size(DataRecord[] db) {
		int c = 0;
		double v;
		for(int i=0; i<db.length; i++) {
			v = db[i].getValue(attrIdx).getRValue();
			if( (v >= lowerBound) && (v <= upperBound)) {
				c++;
			}
		}
		return c;
	}
	
	public double[] getDistinctValues() {
		return distinctValues;
	}
	
	//distinct index without sort
	/**
	 * set toan bo CSDL va lay ra danh sach distinct value
	 */
	public double[] determineDistinctValues(DataRecord[] db) {
		double[] distincts = new double[db.length];
		DataRecord rci;
		boolean flag = false;
		int n = 0;
		double v;
		for(int i=0; i<db.length; i++) {
			rci = db[i];
			v = rci.getValue(attrIdx).getRValue();
			
			flag = false;
			for(int j=0; j<n; j++) {
				if(v == distincts[j]) {
					flag = true;
					break;
				}
			}
			
			if(!flag) {
				distincts[n++] = v;
			}
				
		}
		
		distinctValues = new double[n];
		System.arraycopy(distincts, 0, distinctValues, 0, n);
		double tmp;
		//sort the distinct values
		for(int i=0; i<n-1; i++) {
			for(int j=i+1; j<n; j++) {
				if( distinctValues[i] > distinctValues[j] ) {
					tmp = distinctValues[i];
					distinctValues[i] = distinctValues[j];
					distinctValues[j] = tmp;
				}
			}
		}
		
		lowerBound = distinctValues[0];
		upperBound = distinctValues[n-1];
		
		return distinctValues;
	}
	
	/**
	 * set toan bo CSDL va lay ra danh sach distinct value
	 */
	public double[] determineDistinctValuesWithoutUpdateBounds(DataRecord[] db) {
		double[] distincts = new double[db.length];
		DataRecord rci;
		boolean flag = false;
		int n = 0;
		double v;
		for(int i=0; i<db.length; i++) {
			rci = db[i];
			v = rci.getValue(attrIdx).getRValue();
			
			flag = false;
			for(int j=0; j<n; j++) {
				if(v == distincts[j]) {
					flag = true;
					break;
				}
			}
			
			if(!flag && (v >= lowerBound) && (v <= upperBound) ) {
				distincts[n++] = v;
			}
				
		}
		
		distinctValues = new double[n];
		System.arraycopy(distincts, 0, distinctValues, 0, n);
		double tmp;
		//sort the distinct values
		for(int i=0; i<n-1; i++) {
			for(int j=i+1; j<n; j++) {
				if( distinctValues[i] > distinctValues[j] ) {
					tmp = distinctValues[i];
					distinctValues[i] = distinctValues[j];
					distinctValues[j] = tmp;
				}
			}
		}
		return distinctValues;
	}
	
	public SubInterval[] getTwoSubInterval(double threshold, double[] distinctValues) {	
		SubInterval[] subs = new SubInterval[2];
		SubInterval s1 = new SubInterval(attrIdx);
		SubInterval s2 = new SubInterval(attrIdx);
		s1.setLowerBound(lowerBound);
		double u2 = 0;
		for(int i=0; i<distinctValues.length; i++)
			if(distinctValues[i] > threshold) {
				u2 = distinctValues[i];
				s1.setUpperBound(distinctValues[i-1]);
				break;
			}
		s2.setLowerBound(u2);
		s2.setUpperBound(upperBound);
		subs[0] = s1;
		subs[1] = s2;
		return subs;
	}
	
	
	/**
	 * Dem so luong record co target class la ct trong interval nay
	 * @param ct
	 * @param records
	 * @return
	 */
	public int countCt(String ct, DataRecord[] records) {
		int c = 0;
		double v;
		for(int i=0; i<records.length; i++) {
			v = records[i].getValue(attrIdx).getRValue();
			if( (v >= lowerBound) && (v <= upperBound) && records[i].lastValue().equals(ct))
				c++;
		}
		
		return c;
	}
	
	/**
	 * Dem so luong record co target class la ct trong interval nay
	 * @param ct
	 * @param records
	 * @return
	 */
	public int countCtz(String ct, Node z) {
		int c = 0;
		DataRecord[] records = z.getAllRecords().toArray(new DataRecord[z.size()]);
		double v;
		for(int i=0; i<records.length; i++) {
			v = records[i].getValue(attrIdx).getRValue();
			if( (v >= lowerBound) && (v <= upperBound) && records[i].lastValue().equals(ct))
				c++;
		}
		
		return c;
	}
	
	/**
	 * Tinh hang tu thu nhat cho cong thuc 12 trong bai bao
	 * @param clsT
	 * @param z
	 * @param totalS
	 * @param pz xat suat cua node z tren tong so records trogn DB
	 * @return
	 */
	public double calcPSyCtz(DataRecord[] db, String clsT, Node z, int totalS, double pz) {
		double p1, p2;
		
		int countTotalClsT = 0;
		for(int i=0; i<z.size(); i++) {
			DataRecord d = z.getAllRecords().get(i);
			if(d.lastValue().equals(clsT))
				countTotalClsT++;
		}
		
		p1 = (1.0 * countCt(clsT, db)) / countTotalClsT;
		p2 = (1.0 * countTotalClsT) / z.size();
		
		return (p1 * p2 * pz);
	}
	
	/*
	 * Dem so luong target class trong subinterval nay
	 */
	public int countNumberOfClassTz(Node z, int maxT) {
		int c = 1;
		
		if(z.size() <= 0)
			return 0;
		
		DataRecord[] records = z.getAllRecords().toArray(new DataRecord[z.size()]);
		
		String[] distincClass = new String[maxT];
		distincClass[0] = records[0].lastValue();
		boolean existed;
		double v;
		for(int i=1; i<records.length; i++) {
			existed = false;
			v = records[i].getValue(attrIdx).getRValue();
			if( (v >= lowerBound) && (v <= upperBound)) {  //Dieu kien de record i thuoc ve interval nay
				for(int j=0; j<c; j++) {
					if(records[i].lastValue().equals(distincClass[j])) {
						existed = true;
						break;
					}
				}
				if(!existed) {
					distincClass[c] = records[i].lastValue();
					c++;
				}
			}
		}
		
		return c;
	}

	public double getLowerBound() {
		return lowerBound;
	}

	public void setLowerBound(double lowerBound) {
		this.lowerBound = lowerBound;
	}

	public double getUpperBound() {
		return upperBound;
	}

	public void setUpperBound(double upperBound) {
		this.upperBound = upperBound;
	}
}
