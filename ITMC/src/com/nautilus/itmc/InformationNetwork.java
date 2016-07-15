package com.nautilus.itmc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import com.nautilus.itmc.util.MathUtil;

public class InformationNetwork {
	
	static final double SIGNIFICANT_THRESHOLD = 0.0001;
	
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
		
		// Add the first layer to the network
		Layer l0 = new Layer();
		Node n0 = new Node();
		for(int i=0; i<database.length; i++) {
			n0.addRecord(database[i]);
		}
		l0.addNode(n0);
		
		layers = new ArrayList<Layer>();
		layers.add(l0);
		
		//Hard code here to test table 4, 5
		
	}
	
	/**
	 * Thuat toan chinh cua bai bao
	 */
	public void runAlgorithm() {
		
		////// Hard code here for testing purpose
		Layer l1 = new Layer();
		l1.setAttribute(attributes[7]);
		Node n1 = new Node();
		Node n2 = new Node();
		
		for(int i=0; i<database.length; i++) {
			if(database[i].getValue(7).getDValue().equals("f"))
				n1.addRecord(database[i]);
			else
				n2.addRecord(database[i]);
		}
		
		l1.addNode(n1);
		l1.addNode(n2);
		layers.add(l1);
		attributes[7].setSplit(true);
		
		System.out.println(n1);
		System.out.println(n2);
		//////////////////////////
		
//		for(int i=0; i<attributes.length-1; i++) {
//			if(!attributes[i].isSplit()) {
//				if(attributes[i].isNominal()) {
//					
//				} else {
//					
//				}
//			}
//		}
	}
	
	/**
	 * 
	 * @param attrI index of continuous attribute
	 * @param s
	 */
//	public void partition(int attrI, SubInterval s) {
//		double[] distincValues = s.getDistinctValues(database);
//		
//		double th;
//		//int valueCount = distincValues.length;
//		int i, j;
//		Layer finalLayer = layers.get(layers.size()-1);
//		Node node;
//		double p1, p2, pz, pS1_Ctz, pS2_Ctz;
//		double pS1z, pS2z, G2;
//		double[] pCtz = new double[targetClass.length];
//		SubInterval[] subs;
//		SubInterval s1, s2;
//		int nij, nt, eSyz, df;
//		double mi, maxMI = 0;
//		Node nodeMax = null;
//		
//		//TODO: Xem ky cho nay, co the sai y cua bai bao
//		// Step 2	
//		th = findPartitionThreshold(distincValues);
//		// Compute S1 tai node z
//		subs = s.getTwoSubInterval(th);
//		s1 = subs[0];
//		s2 = subs[1];
//			
//		// Step 2.2
//		for(i=0; i<finalLayer.size(); i++) {
//			node = finalLayer.getNode(i);
//			mi = 0;
//			pz = (1.0 * node.size()) / database.length;
//			//Step 2.2.1 - Calculate conditional mutual information for node i
//			for(int k=0; k<targetClass.length; k++) {
//					
//				pCtz[k] = ( 1.0 * s.countCt(targetClass[k])) / node.size();
//					
//				//Tinh P(Sy, Ct, z) voi y = 1
//				p1 = s1.calcPSyCtz(targetClass[k], node, s.size(), pz);
//				pS1_Ctz = (1.0 * s1.countCt(targetClass[k])) / node.size();
//				pS1z = (1.0 * s1.size()) / node.size();
//					
//				p2 = s2.calcPSyCtz(targetClass[k], node, s.size(), pz);
//				pS2_Ctz = (1.0 * s2.countCt(targetClass[k])) / node.size();
//				pS2z = (1.0 * s2.size()) / node.size();
//					
//				//TODO: log base 2
//				mi += p1 * MathUtil.log2(pS1_Ctz / (pS1z * pCtz[k])) + p2 * MathUtil.log2(pS2_Ctz / (pS2z * pCtz[k]));
//			} 
//			
//			// This is an improvement that used for step 3 - Please see Step 3
//			if(mi > maxMI) {
//				maxMI = mi;
//				nodeMax = node;
//			}
//				
//			// Step 2.2.2 - Calculate Likelihook-ratio
//			G2 = 0;
//			for(int k=0; k<targetClass.length; k++) {
//				nij = s1.countCt(targetClass[k]);
//				G2 += nij * Math.log((1.0 * nij)/(pCtz[k] * s1.size()));
//					
//				nij = s2.countCt(targetClass[k]);
//				G2 += nij * Math.log((1.0 * nij)/(pCtz[k] * s1.size()));
//			}
//				
//			// Step 2.2.3 - Calculate the Degrees of Freedom
//			df = s.countClsT() - 1;
//		}
//		
//		// Step 3 - Find the Thmax and assign it to attribute Ai
//		attributes[attrI].setConditionMutualInformation(maxMI);
//		
//		// Step 4
//		if(maxMI > 0) {
//			// Step 4.1 - Set node max split by attribute Ai
//			//nodeMax.setAttribute(attributes[attrI]);
//			
//			// Step 4.2
//			if(maxMI == distincValues[0]) {
//				// mark Thmax as the lower bound of a new discretization interval
//			} else {
//				partition(attrI, s1);
//			}
//			
//			// Step 4.3
//			partition(attrI, s2);
//		} else {
//			// Step 5 - define a new discretization interval for attribute Ai
//			
//		}
//	}
//	
	
	public List<Double> partition2(int attrI, SubInterval s) {
		
		//day la distinct values cua attribute doi voi toan bo bang du lieu
		double[] sortedDistinctValues = s.getDistinctValues(database);
		List<Double> intervals = new ArrayList<Double>();
		
		double th;
		int distinctCount = sortedDistinctValues.length;
		int i;
		Layer finalLayer = layers.get(layers.size()-1);
		Node node;
		double p1, p2, pz, pS1_Ctz, pS2_Ctz;
		double pS1z, pS2z, G2;
		double[] pCtz = new double[targetClass.length];
		SubInterval[] subs;
		SubInterval s1 = null, s2 = null;
		int nij, nt, eSyz, df;
		double mi=0, maxMI = 0;
		double thmax = 0;
		DataRecord[] recordsZ;
				
		// Step 2
		for(int j=0; j<distinctCount-1; j++) {
			
			// Step 2.1 - define a threshold, o day ta lay gia tri distinct thu j lam th luon
			th = sortedDistinctValues[j];
			
			subs = s.getTwoSubInterval(th, sortedDistinctValues);
			s1 = subs[0];
			s2 = subs[1];
			
			// Step 2.2
			for(i=0; i<finalLayer.size(); i++) {
				node = finalLayer.getNode(i);
				
				recordsZ = node.getAllRecords().toArray(new DataRecord[node.size()]);
				
				mi = 0;
				pz = (1.0 * node.size()) / database.length;
				//Step 2.2.1 - Calculate conditional mutual information for node i
				for(int k=0; k<targetClass.length; k++) {
						
					pCtz[k] = ( 1.0 * s.countCt(targetClass[k], recordsZ)) / node.size();
						
					//Tinh P(Sy, Ct, z) voi y = 1
					p1 = s1.calcPSyCtz(recordsZ, targetClass[k], node, s.size(recordsZ), pz);
					pS1_Ctz = (1.0 * s1.countCt(targetClass[k], recordsZ)) / node.size();
					pS1z = (1.0 * s1.size(recordsZ)) / node.size();
						
					p2 = s2.calcPSyCtz(recordsZ, targetClass[k], node, s.size(recordsZ), pz);
					pS2_Ctz = (1.0 * s2.countCt(targetClass[k], recordsZ)) / node.size();
					pS2z = (1.0 * s2.size(recordsZ)) / node.size();
					
					double rP1 = MathUtil.log2(pS1_Ctz / (pS1z * pCtz[k]));
					rP1 = Double.isInfinite( rP1 )?0:rP1;
					
					double rP2 = MathUtil.log2(pS2_Ctz / (pS2z * pCtz[k]));
					rP2 = Double.isInfinite( rP2 )?0:rP2;
						
					//TODO: log base 2
					mi += p1 * rP1 + p2 * rP2;
				} 
				
				// This is an improvement that used for step 3 - Please see Step 3
				if(mi > maxMI) {
					maxMI = mi;
					thmax = th;
				}
					
				// Step 2.2.2 - Calculate Likelihook-ratio
				G2 = 0;
				for(int k=0; k<targetClass.length; k++) {
					nij = s1.countCtz(targetClass[k], node);
					G2 += nij * Math.log((1.0 * nij)/(pCtz[k] * s1.size(recordsZ)));
						
					nij = s2.countCtz(targetClass[k], node);
					G2 += nij * Math.log((1.0 * nij)/(pCtz[k] * s1.size(recordsZ)));
				}
					
				// Step 2.2.3 - Calculate the Degrees of Freedom
				df = s.countNumberOfClassTz(node, targetClass.length) - 1;
				
				// Step 2.2.4 - Test H0 using Likelihook - ratio test
				ChiSquaredDistribution chidistCalculator = new ChiSquaredDistribution(df);
				double chidist = 1 - chidistCalculator.cumulativeProbability(G2);
				if(chidist > SIGNIFICANT_THRESHOLD) {
					//TODO: mark node as split by attribute Ai
					//node.setAttribute(attributes[attrI]);
				}
				
			// Step 2.2.5 - Go to next node
			}
		// Step 2.3 - Go to next distinct Value
		}
		
		// Step 3 - Find the Thmax and assign it to attribute Ai
		attributes[attrI].setConditionMutualInformation(maxMI);
		
		// Step 4
		if(maxMI > 0) {
			// Step 4.1 - repeate for every node in final layer and get nodes those split
			for(i=0; i<finalLayer.size(); i++){
				
			}
			
			// Step 4.2
			if(thmax == sortedDistinctValues[0]) {
				// mark Thmax as the lower bound of a new discretization interval
				intervals.add(thmax);
			} else {
				List<Double> sub1 = partition2(attrI, s1);
				intervals.addAll(sub1);
			}
			
			// Step 4.3
			List<Double> sub2 = partition2(attrI, s2);
			intervals.addAll(sub2);
		} else {
			// Step 5 - define a new discretization interval (VU: with th???) for attribute Ai
			//intervals.add(th);
			intervals.add(s.getUpperBound());
		}
		
		return intervals;
	
	}
	
	public void printNetworkInfo(){
//		int count=0;
//		DataRecord dr;
//		System.out.println("So luong thuoc tinh: " + attributes.length);
//		for(int i=0;i<database.length;i++){
//			dr = database[i];
//			for(int j=0;j<attributes.length-1;j++){
//				if (dr.getValue(j).isMissing())	{
//					count++;
//					System.out.println(dr);
//					break;
//				}
//			}
//		}
//		
//		System.out.println("So luong record missing: " + count);
		
		//For testing purpose: Here we test attribute Account Balance
		SubInterval s = new SubInterval(13);
		List<Double> values = partition2(13, s);
		for(int i=0; i<values.size(); i++) {
			System.out.println("Interval: " + values.get(i));
		}
	}
	
	
}
