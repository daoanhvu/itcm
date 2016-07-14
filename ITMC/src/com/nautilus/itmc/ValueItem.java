package com.nautilus.itmc;

public class ValueItem {
	private boolean nominal;
	private String dValue;
	private double rValue;
	
	public double getRValue() {
		return rValue;
	}
	
	public String getDValue() {
		return dValue;
	}
	
	@Override
	public String toString() {
		return nominal?dValue:(""+rValue);
	}
}
