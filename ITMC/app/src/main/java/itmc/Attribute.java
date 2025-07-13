package com.nautilus.itmc;

import java.util.List;
import java.util.ArrayList;

public class Attribute {
	private String name;
	private int index;
	private boolean nominal;
	
	//for nominal attribute
	private String[] values;
	
	private double mutualInformation = Double.NaN;
	
	// This property is just used for continuous attributes
	private List<SubInterval> discretizationIntervals = null;
	
	public Attribute() {
		index = -1;
	}
	
	public Attribute(String name, int idx) {
		this.name = name;
		this.index = idx;
	}
	
	public void setValues(String[] v) {
		values = new String[v.length];
		for(int i=0; i<v.length; i++) {
			values[i] = v[i].trim();
		}
	}
	
	public String[] getValues() {
		return values;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getIndex() { return index;}

	public boolean isNominal() {
		return nominal;
	}

	public void setNominal(boolean nominal) {
		this.nominal = nominal;
	}

//	public boolean isSplit() {
//		return split;
//	}
//
//	public void setSplit(boolean split) {
//		this.split = split;
//	}
	
//	public void addSplitInfo(Node node, double th) {
//		splitInfo.add(new SplitInfo(node, th));
//	}
	
	/**
	 * Dem so luong record co target class la ct va value tai Attribute nay la value thu j tai node z
	 * @param ct
	 * @param records
	 * @return
	 */
	public int countCtVijz(String ct, int j, Node z) {
		int c = 0;
		DataRecord[] records = z.getAllRecords().toArray(new DataRecord[z.size()]);
		for(int i=0; i<records.length; i++) {
			if( records[i].getValue(index).getDValue().equals(values[j]) && records[i].lastValue().equals(ct))
				c++;
		}
		
		return c;
	}
	
	public int countVijz(int j, Node z) {
		int c = 0;
		DataRecord[] records = z.getAllRecords().toArray(new DataRecord[z.size()]);
		for(int i=0; i<records.length; i++) {
			if( records[i].getValue(index).getDValue().equals(values[j]) )
				c++;
		}
		
		return c;
	}

	public double getConditionMutualInformation() {
		return mutualInformation;
	}

	public void setConditionMutualInformation(double mutualInformation) {
		this.mutualInformation = mutualInformation;
	}
	
	public void addInterval(SubInterval interval) {
		if(discretizationIntervals == null) {
			discretizationIntervals = new ArrayList<SubInterval>();
		}
		
		discretizationIntervals.add(interval);
	}
	
	/**
	 * For nominal attribute only
	 * @param z
	 * @param maxT
	 * @return
	 */
	public int countNumberOfDistinct(Node z, int maxT) {
		int c = 1;
		
		if(z.size() <= 0)
			return 0;
		
		DataRecord[] records = z.getAllRecords().toArray(new DataRecord[z.size()]);
		
		String[] distincClass = new String[values.length];
		distincClass[0] = records[0].getValue(index).getDValue();
		boolean existed;
		String v;
		for(int i=1; i<records.length; i++) {
			existed = false;
			v = records[i].getValue(index).getDValue();
				for(int j=0; j<c; j++) {
					if(v.equals(distincClass[j])) {
						existed = true;
						break;
					}
				}
				if(!existed && !v.equals("?")) {
					distincClass[c] = v;
					c++;
				}
			
		}
		
		return c;
	}
	
	@Override
	public String toString() {
		return name + ":" + mutualInformation;
	}
}
