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
		ValueItem vi;
		values = new ValueItem[atts.length];
		for(int i=0; i<v.length; i++) {
			vi = new ValueItem();
			if(atts[i].isNominal())
			{
				vi.setNominal(true);
				
				if(v[i].equals("?"))
				{
					vi.setMissing(true);
				}
				else{
					vi.setDValue(v[i]);
				}
			}
			else
			{
				vi.setNominal(false);
				if(v[i].equals("?"))
				{
					vi.setMissing(true);
				}
				else{
					vi.setRValue(Double.parseDouble(v[i]));
				}
			}	
			
			values[i] = vi;
		}
		
	}
	
	/**
	 * The last value is the classifying value so it's ALWAY String
	 * @return
	 */
	public String lastValue() {
		//return values[values.length-1].getDValue();
		String s = "";
		for(int i=0; i<values.length; i++)
			s += ", " + values[i];
		
		return s;
	}
}
