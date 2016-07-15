package com.nautilus.itmc;

import java.util.List;
import java.util.ArrayList;

public class Node {
	private Attribute attribute;
	private List<DataRecord> records = new ArrayList<DataRecord>();
	
	public void addRecord(DataRecord dr) {
		records.add(dr);
	}
	
	public void addRecords(List<DataRecord> drs) {
		records.addAll(drs);
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

	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}
}
