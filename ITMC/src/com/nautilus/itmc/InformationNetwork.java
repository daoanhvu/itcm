package com.nautilus.itmc;

import java.util.List;

public class InformationNetwork {
	
	private List<Layer> layers;
	private List<DataRecord> database;
	private String[] targetClass;
	
	public void initData() {
		targetClass = new String[]{"+", "-"};
	}
	
	/**
	 * 
	 * @param attr index of continuous attribute
	 * @param s
	 */
	public void partition(int attr, SubInterval s) {
		double[] distincValues = s.getDistincValue();
		
		double th = findPartitionThreshold(distincValues);
		int valueCount = distincValues.length;
		int i, j;
		
		// Step 2.2
		Layer finalLayer = layers.get(layers.size()-1);
		Node node;
		double p1, p2, pz, pS1_Ctz, pS2_Ctz;
		double pS1z, pS2z, pCtz;
		SubInterval[] subs;
		SubInterval s1, s2;
		for(i=0; i<finalLayer.size(); i++) {
			node = finalLayer.getNode(i);
			
			// Compute S1 tai node z
			subs = s.getTwoSubInterval(th);
			s1 = subs[0];
			s2 = subs[2];
			
			pz = (1.0 * node.size()) / database.size();
			//Step 2.2.1
			for(int k=0; k<targetClass.length; k++) {
				//Tinh P(Sy, Ct, z) voi y = 1
				p1 = s1.calcP(targetClass[k], node, s.size(), pz);
				pS1_Ctz = (1.0 * s1.countCt(targetClass[k])) / node.size();
				pS1z = (1.0 * s1.size()) / node.size();
				
				p2 = s2.calcP(targetClass[k], node, s.size(), pz);
				pS2_Ctz = (1.0 * s2.countCt(targetClass[k])) / node.size();
				pS2z = (1.0 * s2.size()) / node.size(); 
			} 
			
			
			// Step 2.2.2
		}
	}
	
	/**
	 * Step 2.1
	 * @param distincValues
	 * @return
	 */
	private double findPartitionThreshold(double[] distincValues) {
		
		int midpos = distincValues.length;
		return distincValues[midpos];
	}
	
}
