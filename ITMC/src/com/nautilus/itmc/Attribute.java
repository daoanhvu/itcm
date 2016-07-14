package com.nautilus.itmc;

public class Attribute {
	private String name;
	private boolean nominal;
	private boolean split;
	
	//for nominal attribute
	private String[] values;
	
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
}
