package com.nautilus.itmc;

import java.util.List;

public class Node {
	private Attribute attribute;
	private List<DataRecord> records;
	
	public void addRecord(DataRecord dr) {
		records.add(dr);
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
}
