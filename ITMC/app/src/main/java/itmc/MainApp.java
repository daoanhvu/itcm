
package com.nautilus.itmc;

public class MainApp {
	public static void main(String args[]) {
		
		//Test ham tinh xac suat chi-square
//		ChiSquaredDistribution chidistCalculator = new ChiSquaredDistribution(1);
//		double dist = 1 - chidistCalculator.cumulativeProbability(56.7);
//		System.out.println("chidist(56.7, 1) = " + dist);

		InformationNetwork aNetwork = new InformationNetwork();
		//aNetwork.readdData("E:\\TruongBinh\\GocHocTap\\CaoHocKHTN\\MachineLearning\\DoAnMayHoc\\Slide\\itcm\\data\\CreditApprovalDS.txt");
		aNetwork.readdData("C:\\Users\\vdao5\\Documents\\Research\\ML\\AssignmentProject\\itcm\\data\\CreditApprovalDS1.txt");
		aNetwork.printNetworkInfo();
		aNetwork.initData();
//		aNetwork.testContinouseData();
		aNetwork.runAlgorithm();
		//Test for case of Balance
	}
}

