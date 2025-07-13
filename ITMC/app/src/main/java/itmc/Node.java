package com.nautilus.itmc;

import java.util.List;
import java.util.ArrayList;

public class Node {
	private List<DataRecord> records = new ArrayList<DataRecord>();
	
	public void addRecord(DataRecord dr) {
		records.add(dr);
	}
	
	public void addRecords(List<DataRecord> drs) {
		records.addAll(drs);
	}
	
	public List<DataRecord> getAllRecords() {
		return records;
	}
	
	public DataRecord[] getArrayRecords() {
		return records.toArray(new DataRecord[records.size()]);
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
	
	class NodeData {
		String value;
		Node node = new Node();
	}
	
	/**
	 * Split a node base on input attribute
	 * @param att
	 * @param distincts
	 * @return
	 */
	public Node[] splitNode(Attribute att, double[] distincts) {
		Node[] nodes = null;
		if(att.isNominal()) {
			if(records.size() <= 0)
				return null;
			List<NodeData> list = new ArrayList<NodeData>();
			boolean existed;
			String v;
			for(int i=1; i<records.size(); i++) {
				existed = false;
				v = records.get(i).getValue(att.getIndex()).getDValue();
				for(int j=0; j<list.size(); j++) {
					if(v.equals(list.get(j).value)) {
						list.get(j).node.addRecord(records.get(i));
						existed = true;
						break;
					}
				}
					
				if(!existed && !v.equals("?")) {
					NodeData aNode = new NodeData();
					aNode.node.addRecord(records.get(i));
					aNode.value = v;
					list.add(aNode);
				}
			}
			nodes = new Node[list.size()];
			for(int i=0; i<list.size(); i++)
				nodes[i] = list.get(i).node;
		} else {
			Node[] arrNodes = new Node[distincts.length -1];
			double rvalue;
			for(int i=1; i<records.size(); i++) {
				rvalue = records.get(i).getValue(att.getIndex()).getRValue();
				for(int j=0; j<distincts.length-1; j++) {
					if( rvalue <= distincts[j] ) {
						if(j == 0) {
							if(arrNodes[j] == null) {
								arrNodes[j] = new Node();
							}
							arrNodes[j].addRecord(records.get(i));
						} else {
							if(rvalue > distincts[j-1]) {
								if(arrNodes[j] == null) {
									arrNodes[j] = new Node();
								}
								arrNodes[j].addRecord(records.get(i));
							}
						}
					}
				}
			}
			
		}
		return nodes;
	}
	
	@Override
	public String toString() {
		return "Number of record: " + records.size();
	}
}
