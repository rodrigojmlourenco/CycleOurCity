package org.cycleourcity.otp.data;

public class UserStats {

	private final long userId;
	private final double safetyReputation;
	private final double elevationReputation;
	
	public UserStats(long userId, double safetyReputation, double elevationReputation){
		this.userId = userId;
		this.safetyReputation = safetyReputation;
		this.elevationReputation = elevationReputation;
	}
	
	public long getUserId() { return this.userId; }
	
	public double getSafetyReputation(){ return this.safetyReputation; }
	
	public double getElevationReputation() { return this.elevationReputation; }
	
}
