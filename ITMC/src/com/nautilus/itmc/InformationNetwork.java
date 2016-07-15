package com.nautilus.itmc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.nautilus.itmc.util.MathUtil;

public class InformationNetwork {
	
	private Attribute[] attributes;
	private List<Layer> layers;
	private DataRecord[] database;
	private String[] targetClass;	
	
	public void readdData(String filename) {
		BufferedReader fsr = null;
		
		try {
			String line, aname;
			String[] values;
			fsr = new BufferedReader(new FileReader(new File(filename)));
			Attribute att;
			//Read number of Attribute
			line = fsr.readLine();
			int n = Integer.parseInt(line);
			attributes = new Attribute[n];
			for(int i=0; i<n; i++) {
				line = fsr.readLine();
				String[] s1 = line.split(":");
				aname = s1[0];
				att = new Attribute();
				if(s1[1].contains("continuous")) {
					att.setNominal(false);
				} else {
					att.setNominal(true);
					values = s1[1].split(",");
					att.setValues(values);
					
					
				}
				
				
				attributes[i] = att;
			}
			
			//Read number of records
			line = fsr.readLine();
			n = Integer.parseInt(line);
			database = new DataRecord[n];
			for( int i=0; i<n; i++ ) {
				line = fsr.readLine();
				
				System.out.println(line);
				
				DataRecord dr = new DataRecord();
				values = line.split(",");
				dr.setValues(values, attributes);
				database[i] = dr;
			}
		}catch(IOException ex) {
			ex.printStackTrace();
		} finally {
			
		}
	}
	
	public void initData() {
		targetClass = new String[]{"+", "-"};
		
		
	}
	/**
	 * HuuPhuong
	 * mutual information for nominal attribute
	 */
	public double nominalAttribute(int attr, SubInterval s) {
		double[] distincValues = s.getDistincValue();
		
		int ai = distincValues.length;
		int mt=targetClass.length;
		int i;
		Layer finalLayer = layers.get(layers.size()-1);
		Node node;
		double p1, pz, pS1_Ctz;
		double pS1z, pCtz;
		double mi,nt;
		for(i=0; i<finalLayer.size(); i++) {
			node = finalLayer.getNode(i);
			mi = 0;
			pz = (1.0 * node.size()) / database.length;
			for(int k=0; k<mt; k++) {
				
				pCtz = ( 1.0 * s.countCt(targetClass[k])) / node.size();
				//Tinh P(ct, vij, z) voi y = 1
				p1 = s.calcP(targetClass[k], node, s.size(), pz);
				pS1_Ctz = (1.0 * s.countCt(targetClass[k])) / node.size();
				pS1z = (1.0 * s.size()) / node.size();
	
				mi += p1 * MathUtil.log2(pS1_Ctz / (pS1z * pCtz)) ;
			} 
			
			// Step 2.2.2 - Calculate Likelihook-ratio
			nt = 2*(Math.log(2)/Math.log(Math.E))*node.size()*mi;
			
		}	
		return 1.1;
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
		int nt;
		double mi;
		for(i=0; i<finalLayer.size(); i++) {
			node = finalLayer.getNode(i);
			
			// Compute S1 tai node z
			subs = s.getTwoSubInterval(th);
			s1 = subs[0];
			s2 = subs[2];
			mi = 0;
			pz = (1.0 * node.size()) / database.length;
			//Step 2.2.1
			for(int k=0; k<targetClass.length; k++) {
				
				pCtz = ( 1.0 * s.countCt(targetClass[k])) / node.size();
				
				//Tinh P(Sy, Ct, z) voi y = 1
				p1 = s1.calcP(targetClass[k], node, s.size(), pz);
				pS1_Ctz = (1.0 * s1.countCt(targetClass[k])) / node.size();
				pS1z = (1.0 * s1.size()) / node.size();
				
				p2 = s2.calcP(targetClass[k], node, s.size(), pz);
				pS2_Ctz = (1.0 * s2.countCt(targetClass[k])) / node.size();
				pS2z = (1.0 * s2.size()) / node.size();
				
				//TODO: log base 2
				mi += p1 * MathUtil.log2(pS1_Ctz / (pS1z * pCtz)) + p2 * MathUtil.log2(pS2_Ctz / (pS2z * pCtz));
			} 
			
			
			// Step 2.2.2 - Calculate Likelihook-ratio
			//nt = 
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
	
	public void thongKeDataSet(){
		int count=0;
		DataRecord dr;
		System.out.println("So luong thuoc tinh: " + attributes.length);
		for(int i=0;i<database.length;i++){
			dr = database[i];
			for(int j=0;j<attributes.length-1;j++){
				if (dr.getValue(j).isMissing())	{
					count++;
					System.out.println(dr);
					break;
				}
			}
		}
		
		System.out.println("So luong record missing: " + count);
	}
	
	
}
