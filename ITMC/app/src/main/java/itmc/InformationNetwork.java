package com.nautilus.itmc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import com.nautilus.itmc.util.MathUtil;

public class InformationNetwork {
	
	static final double SIGNIFICANT_THRESHOLD = 0.001;
	
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
				att = new Attribute(aname, i);
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
	 * @author
	 * Ham nay dung de khoi tao gia tri ban dau cho mang, vi du gan toan bo record vao node 0
	 */
	public void initData() {
		targetClass = new String[]{"-", "+"};
		
		// Add the first layer to the network
		Layer l0 = new Layer();
		Node n0 = new Node();
		for(int i=0; i<database.length; i++) {
			n0.addRecord(database[i]);
		}
		l0.addNode(n0);
		
		layers = new ArrayList<Layer>();
		layers.add(l0);
	}
	
	/**
	 * @author
	 * Ham nay dung de khoi tao gia tri ban dau cho mang, vi du gan toan bo record vao node 0
	 */
	public void testContinouseData() {
		targetClass = new String[]{"-", "+"};
		
		// Add the first layer to the network
		Layer l0 = new Layer();
		Node n0 = new Node();
		for(int i=0; i<database.length; i++) {
			n0.addRecord(database[i]);
		}
		l0.addNode(n0);
		
		layers = new ArrayList<Layer>();
		layers.add(l0);
		
		////// for testing purpose
		//n0.splitFlag = 1;
		//n0.splitAttributeIndex = 7;
		//Hard code here to test table 4, 5
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
			
		System.out.println(n1);
		System.out.println(n2);
		java.util.HashMap<Node, SplitInfo> splitInfos = new java.util.HashMap<Node, SplitInfo>();
		SubInterval s = new SubInterval(13);
		s.determineDistinctValues(database);
		List<Double> values = partition2(13, s, splitInfos);
		for(int i=0; i<values.size(); i++) {
			System.out.println("Interval: " + values.get(i));
		}
		//////////////////////////
		
	}

	/**
	 * Thuat toan chinh cua bai bao
	 */
	public void runAlgorithm() {
		String[] attValues;
		List<Attribute> I = new ArrayList<Attribute>();
		double mi, maxMI = -999.9;
		double G2, df;
		int maxAtt = -1;
		Attribute ai;
		Node nodez, splitNode = null;
		java.util.HashMap<Node, SplitInfo> splitInfos = new java.util.HashMap<Node, SplitInfo>();
		//Step 2 - while the number of layer l < m
		for(int l=0; l<attributes.length-1; l++) {
			splitInfos.clear();
			// Step 2.1
			for(int i=0; i<attributes.length-1; i++) {
				if(!I.contains(attributes[i])) {
					Layer finalLayer = layers.get(layers.size()-1);
					//Candidate input attribute
					ai = attributes[i];
					
					//Step 2.1 - For each candidate input attribute that not in I
					if(ai.isNominal()) {
						attValues = ai.getValues();
						
						for(int z=0; z<finalLayer.size(); z++) {
							nodez = finalLayer.getNode(z);
							double pz = (1.0 * nodez.size()) / database.length;
							mi = 0;
							for(int k=0; k<attValues.length; k++) {
								int countVij = ai.countVijz(k, nodez);
								//Cot L trong bang du lieu
								double pVijz = (1.0 * countVij) / nodez.size();
								for(int t=0; t<targetClass.length; t++) {
									
									int countCt = nodez.countCt(targetClass[t]);
									
									int countCtVijZ = ai.countCtVijz(targetClass[t], k, nodez);
									
									//O F3 trong bang du lieu
									double pVijtz = (1.0 * countCtVijZ) / nodez.size();
									
									//O G3 trong bang du lieu
									double pCt_Vij_z = pz * ((1.0*countCtVijZ)/countCt) * ((1.0*countCt)/nodez.size());
									
									mi += pCt_Vij_z * MathUtil.log2(pVijtz/(pVijz*((1.0*countCt)/nodez.size())));
								}
							}

							//Tinh G2 cua thuoc tinh roi rac
							G2 = 2 * Math.log(2) * nodez.size() * mi;
							df = ai.countNumberOfDistinct(nodez, targetClass.length) - 1;

							if(df <= 0.0){
//								nodez.splitFlag = -1;
//								break;
							} else {
								// Step 2.2.4 - Test H0 using Likelihook - ratio test
								ChiSquaredDistribution chidistCalculator = new ChiSquaredDistribution(df);
								double chidist = 1 - chidistCalculator.cumulativeProbability(G2);
								if(chidist < SIGNIFICANT_THRESHOLD) {
									if(mi >= maxMI) {
										maxMI = mi;
										maxAtt = i;
										splitNode = nodez;
										if(splitInfos.containsKey(nodez)) {
											double currentMaxMI = splitInfos.get(nodez).mi;
											if(maxMI > currentMaxMI) {
												splitInfos.get(nodez).attIndex = maxAtt;
												splitInfos.get(nodez).mi = maxMI;
											}
										} else {
											SplitInfo splitInfo = new SplitInfo(nodez, mi, -9999);
											splitInfo.attIndex = maxAtt;
											splitInfos.put(nodez, splitInfo);
										}
										System.out.println("G2: " + G2 + "; Chidist: " + chidist);
									}
								}
							}
						} // End browse nodes in final layer
						
					} else {
						// Continuous attribute
						for(int z=0; z<finalLayer.size(); z++) {
							nodez = finalLayer.getNode(z);
							SubInterval s = new SubInterval(i);
							s.determineDistinctValues(nodez.getAllRecords().toArray(new DataRecord[nodez.size()]));
							partition2(i, s, splitInfos);
						}
					}
				}
			} // End step 2.1
			
			// Step 2.3
			if(maxMI > 0) {
				System.out.println("Split Attribute(" + maxAtt + "): MI = " + maxMI);
				I.add(attributes[maxAtt]);
				
				//Expand the network
				Layer newLayer = new Layer();
				Node[] nodes = splitNode.splitNode(attributes[maxAtt], (double[])null);
				for(int i=0; i<nodes.length; i++)
					newLayer.addNode(nodes[i]);
				layers.add(newLayer);
				
			} else {
				break;
			}
			
		} // End step 2
	}
		
	static class SplitInfo {
		SplitInfo(Node node, double mi, double th) {
			this.node = node;
			this.mi = mi;
			this.th = th;
		}
		Node node;
		double mi;
		double th;
		int attIndex;
	}
	
	public List<Double> partition2(int attrI, SubInterval s, HashMap<Node, SplitInfo> splitInfos) {
		
		//day la distinct values cua attribute doi voi toan bo bang du lieu
		double[] sortedDistinctValues = s.getDistinctValues();
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
		double mi=0;
		DataRecord[] recordsZ;
		
		List<SplitInfo> lst = new ArrayList<SplitInfo>();
		
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
				
//				System.out.println("So luong record S1:" + s1.size(recordsZ) + "; S2:" + s2.size(recordsZ));
				
				s1.determineDistinctValuesWithoutUpdateBounds(recordsZ);
				s2.determineDistinctValuesWithoutUpdateBounds(recordsZ);
				
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
//					if(Double.isInfinite( rP1 )) {
//						rP1 = -9999.0;
//					}
					rP1 = Double.isInfinite( rP1 )?0:rP1;
					
					double rP2 = MathUtil.log2(pS2_Ctz / (pS2z * pCtz[k]));
//					if(Double.isInfinite( rP2 )) {
//						rP2 = -9999.0;
//					}
					rP2 = Double.isInfinite( rP2 )?0:rP2;
						
					//TODO: log base 2
					mi += p1 * rP1 + p2 * rP2;
				}
					
				// Step 2.2.2 - Calculate Likelihook-ratio
				G2 = 0;
				for(int k=0; k<targetClass.length; k++) {
					nij = s1.countCtz(targetClass[k], node);
					G2 += nij * Math.log((1.0 * nij)/(pCtz[k] * s1.size(recordsZ)));
						
					nij = s2.countCtz(targetClass[k], node);
					G2 += nij * Math.log((1.0 * nij)/(pCtz[k] * s2.size(recordsZ)));
				}
				G2 = 2 * G2;
					
				// Step 2.2.3 - Calculate the Degrees of Freedom
				df = s.countNumberOfClassTz(node, targetClass.length) - 1;
				
				if(df <= 0.0){
					System.out.println("Nij: " + s1.countCtz(targetClass[0], node));
					//node.splitFlag = -1;
					break;
				}
				
				// Step 2.2.4 - Test H0 using Likelihook - ratio test
				ChiSquaredDistribution chidistCalculator = new ChiSquaredDistribution(df);
				double chidist = 1 - chidistCalculator.cumulativeProbability(G2);
				if(chidist < SIGNIFICANT_THRESHOLD) {
					//TODO: mark node as split by attribute Ai
					//node.setAttribute(attributes[attrI]);
					//splitNodes.add(new SplitInfo(node, attrI, th));
					//System.out.println("P_VALUE (Node:" + node + ", AttId:" + attrI + "): " + chidist);
					lst.add(new SplitInfo(node, mi, th));
					attributes[attrI].setConditionMutualInformation(mi);
				} else {
				}
				
			// Step 2.2.5 - Go to next node
			}
		// Step 2.3 - Go to next distinct Value
		}
		
		// Step 3 - Find the Thmax and assign it to attribute Ai
		double maxMI = -9999.9;
		double thmax = 0;
		Node maxNode = null;
		for(i=0; i<lst.size(); i++) {
			if(lst.get(i).mi > maxMI) {
				thmax = lst.get(i).th;
				maxMI = lst.get(i).mi;
				maxNode = lst.get(i).node;
			}
		}
		//attributes[attrI].setConditionMutualInformation(maxMI);
		
		// Step 4
		if(maxMI > 0) {
			// Step 4.1 - repeate for every node in final layer and get nodes those split
//			for(i=0; i<finalLayer.size(); i++){
//				if(finalLayer.getNode(i).splitFlag == 1) {
//					finalLayer.getNode(i).splitAttributeIndex = attrI;
//				}
//			}
			//maxNode.splitAttributeIndex = attrI;
			System.out.println("maxMI:" + maxMI + "; thmax:" + thmax);
			
			//Tinh lai s1, s2 theo nguong thmax
			subs = s.getTwoSubInterval(thmax, sortedDistinctValues);
			s1 = subs[0];
			s2 = subs[1];
			
			// Step 4.2
			if(thmax == sortedDistinctValues[0]) {
				// mark Thmax as the lower bound of a new discretization interval
				intervals.add(thmax);
			} else {
				s1.determineDistinctValuesWithoutUpdateBounds(database);
				List<Double> sub1 = partition2(attrI, s1, splitInfos);
				intervals.addAll(sub1);
			}
			
			// Step 4.3
			s2.determineDistinctValuesWithoutUpdateBounds(database);
			List<Double> sub2 = partition2(attrI, s2, splitInfos);
			intervals.addAll(sub2);
		} else {
			// Step 5 - define a new discretization interval (VU: with th???) for attribute Ai
			//intervals.add(th);
			intervals.add(s.getUpperBound());
		}
		
		return intervals;
	
	}
	
	public void printNetworkInfo(){
		int count=0;
		DataRecord dr;
		for(int i=0;i<database.length;i++){
			dr = database[i];
			for(int j=0;j<attributes.length-1;j++){
				if (dr.getValue(j).isMissing())	{
					count++;
					break;
				}
			}
		}
		
		System.out.println("=========================================");
		System.out.println("So luong Attribute: " + attributes.length);
		System.out.println("So luong record: " + database.length);
		System.out.println("So luong record missing: " + count);
		System.out.println("=========================================");
		
		//For testing purpose: Here we test attribute Account Balance
//		SubInterval s = new SubInterval(13);
//		s.determineDistinctValues(database);
//		List<Double> values = partition2(13, s);
//		for(int i=0; i<values.size(); i++) {
//			System.out.println("Interval: " + values.get(i));
//		}
//		
//		for(int i=0; i<attributes.length; i++) {
//			System.out.println("Attribute(" + i + "): "+ attributes[i]);
//		}
	}
	
	
}
