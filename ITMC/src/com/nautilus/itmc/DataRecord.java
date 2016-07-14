package com.nautilus.itmc;

import java.util.List;

public class DataRecord {
	private List<ValueItem> values;
	
	/**
	 * Get value idx
	 * @param idx
	 * @return
	 */
	public ValueItem getValue(int idx) {
		return values.get(idx);
	}
	
	/**
	 * The last value is the classifying value so it's ALWAY String
	 * @return
	 */
	public String lastValue() {
		return values.get(values.size()-1).getDValue();
	}
}
