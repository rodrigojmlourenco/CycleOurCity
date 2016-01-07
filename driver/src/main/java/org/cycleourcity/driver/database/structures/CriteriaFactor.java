package org.cycleourcity.driver.database.structures;

public class CriteriaFactor {
	
	private final int factorID;
	private final String description;
	private final float factor;
	
	public CriteriaFactor(int factorID, String description, float factor){
		this.factor = factor;
		this.factorID = factorID;
		this.description = description;
	}
	
	public int getFactorID(){ return this.factorID; }
	
	public float getFactor(){ return this.factor; }
	
	public String getDescription(){ return this.description; }
	
	@Override
	public String toString() {
		return ""+factorID+" | "+description+" | "+factor;
	}

}
