package com.nautilus.itmc;

import java.util.List;
import java.util.ArrayList;

public class Node {
	private List<DataRecord> records = new ArrayList<DataRecord>();
	
	//Split information
	// 0: unknown; -1: unsplit; 1: split
	int splitFlag = 0;
	int splitAttributeIndex = -1;
	double sliptThreshold;
	
	public void addRecord(DataRecord dr) {
		records.add(dr);
	}
	
	public void addRecords(List<DataRecord> drs) {
		records.addAll(drs);
	}
	
	public List<DataRecord> getAllRecords() {
		return records;
	}
	
	public void clear() {
		records.clear();
	}
	
	public int size() {
		return records.size();
	}
	
	public int countCt(String ct) {
		int c = 0;
		for(int i=0; i<records.size(); i++) {
			if(records.get(i).lastValue().equals(ct))
				c++;
		}
		
		return c;
	}
		@Override
	public String toString() {
		return "Number of record: " + records.size();
	}
}
