
package com.nautilus.itmc;

public class MainApp {
	public static void main(String args[]) {

		InformationNetwork aNetwork = new InformationNetwork();
		aNetwork.readdData("E:\\TruongBinh\\GocHocTap\\CaoHocKHTN\\MachineLearning\\DoAnMayHoc\\Slide\\itcm\\data\\CreditApprovalDS.txt");
		aNetwork.printNetworkInfo();
	}
}

