package com.nautilus.itmc;

public class ValueItem {
	private boolean nominal;
	private boolean missing;
	private String dValue;
	private double rValue;
	
	public double getRValue() {
		return rValue;
	}
	
	public void setRValue(double rv) {
		rValue = rv;
	}
	
	public String getDValue() {
		return dValue;
	}
	
	public void setDValue(String dv) {
		dValue = dv;
	}
	
	@Override
	public String toString() {
		return nominal?dValue:(""+rValue);
	}

	public boolean isNominal() {
		return nominal;
	}

	public void setNominal(boolean nominal) {
		this.nominal = nominal;
	}

	public boolean isMissing() {
		return missing;
	}

	public void setMissing(boolean missing) {
		this.missing = missing;
	}
}
