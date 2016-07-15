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
				
				//System.out.println(line);
				
				DataRecord dr = new DataRecord();
				values = line.split(",");
				dr.setValues(values, attributes);
				database[i] = dr;
			}
		}catch(IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if(fsr != null)
					fsr.close();
			}catch(IOException e2){}
		}
	}
	
	/**
	 * @author Dao Anh Vu
	 * Ham nay dung de khoi tao gia tri ban dau cho mang, vi du gan toan bo record vao node 0
	 */
	public void initData() {
		targetClass = new String[]{"+", "-"};
		
		
	}
	
	/**
	 * 
	 * @param attrI index of continuous attribute
	 * @param s
	 */
	public void partition(int attrI, SubInterval s) {
		double[] distincValues = s.getDistincValue();
		
		double th;
		int valueCount = distincValues.length;
		int i, j;
		Layer finalLayer = layers.get(layers.size()-1);
		Node node;
		double p1, p2, pz, pS1_Ctz, pS2_Ctz;
		double pS1z, pS2z, G2;
		double[] pCtz = new double[targetClass.length];
		SubInterval[] subs;
		SubInterval s1, s2;
		int nij, nt, eSyz, df;
		double mi, maxMI = 0;
		Node nodeMax = null;
		
		//TODO: Xem ky cho nay, co the sai y cua bai bao
		// Step 2	
		th = findPartitionThreshold(distincValues);
		// Compute S1 tai node z
		subs = s.getTwoSubInterval(th);
		s1 = subs[0];
		s2 = subs[2];
			
		// Step 2.2
		for(i=0; i<finalLayer.size(); i++) {
			node = finalLayer.getNode(i);
			mi = 0;
			pz = (1.0 * node.size()) / database.length;
			//Step 2.2.1 - Calculate conditional mutual information for node i
			for(int k=0; k<targetClass.length; k++) {
					
				pCtz[k] = ( 1.0 * s.countCt(targetClass[k])) / node.size();
					
				//Tinh P(Sy, Ct, z) voi y = 1
				p1 = s1.calcP(targetClass[k], node, s.size(), pz);
				pS1_Ctz = (1.0 * s1.countCt(targetClass[k])) / node.size();
				pS1z = (1.0 * s1.size()) / node.size();
					
				p2 = s2.calcP(targetClass[k], node, s.size(), pz);
				pS2_Ctz = (1.0 * s2.countCt(targetClass[k])) / node.size();
				pS2z = (1.0 * s2.size()) / node.size();
					
				//TODO: log base 2
				mi += p1 * MathUtil.log2(pS1_Ctz / (pS1z * pCtz[k])) + p2 * MathUtil.log2(pS2_Ctz / (pS2z * pCtz[k]));
			} 
			
			// This is an improvement that used for step 3 - Please see Step 3
			if(mi > maxMI) {
				maxMI = mi;
				nodeMax = node;
			}
				
			// Step 2.2.2 - Calculate Likelihook-ratio
			G2 = 0;
			for(int k=0; k<targetClass.length; k++) {
				nij = s1.countCt(targetClass[k]);
				G2 += nij * Math.log((1.0 * nij)/(pCtz[k] * s1.size()));
					
				nij = s2.countCt(targetClass[k]);
				G2 += nij * Math.log((1.0 * nij)/(pCtz[k] * s1.size()));
			}
				
			// Step 2.2.3 - Calculate the Degrees of Freedom
			df = s.countClsT() - 1;
		}
		
		// Step 3 - Find the Thmax and assign it to attribute Ai
		attributes[attrI].setConditionMutualInformation(maxMI);
		
		// Step 4
		if(maxMI > 0) {
			// Step 4.1 - Set node max split by attribute Ai
			nodeMax.setAttribute(attributes[attrI]);
			
			// Step 4.2
			if(maxMI == distincValues[0]) {
				// mark Thmax as the lower bound of a new discretization interval
			} else {
				partition(attrI, s1);
			}
			
			// Step 4.3
			partition(attrI, s2);
		} else {
			// Step 5 - define a new discretization interval for attribute Ai
			
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
	
	public void printNetworkInfo(){
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
