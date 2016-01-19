package org.cycleourcity.otp;

public class StandaloneTest {

	public static void main(String[] args){
		
		OTPGraphManager manager = new OTPGraphManager("/var/otp");
		manager.integrateCycleOurCityRatings();
		
	}
}
