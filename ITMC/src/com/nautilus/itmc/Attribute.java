package com.nautilus.itmc;

import java.util.List;
import java.util.ArrayList;

public class Attribute {
	private String name;
	private boolean nominal;
	private boolean split;
	
	//for nominal attribute
	private String[] values;
	
	private double mutualInformation;
	
	// This property is just used for continuous attributes
	private List<SubInterval> discretizationIntervals = null;
	
	public void setValues(String[] v) {
		values = new String[v.length];
		for(int i=0; i<v.length; i++) {
			values[i] = v[i].trim();
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isNominal() {
		return nominal;
	}

	public void setNominal(boolean nominal) {
		this.nominal = nominal;
	}

	public boolean isSplit() {
		return split;
	}

	public void setSplit(boolean split) {
		this.split = split;
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
}
