package com.nautilus.itmc;

import java.util.List;

public class DataRecord {
	private ValueItem[] values;
	
	/**
	 * Get value idx
	 * @param idx
	 * @return
	 */
	public ValueItem getValue(int idx) {
		return values[idx];
	}
	
	public void setValues(String[] v, Attribute[] atts) {
		for(int i=0; i<v.length; i++) {
			
		}
	}
	
	/**
	 * The last value is the classifying value so it's ALWAY String
	 * @return
	 */
	public String lastValue() {
		return values[values.length-1].getDValue();
	}
}
