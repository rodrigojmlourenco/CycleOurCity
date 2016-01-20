package org.cycleourcity.otp.planner.preferences;

import org.cycleourcity.otp.planner.exceptions.InvalidPreferenceSetException;

public class UserPreferences {

	private final float safety, slope, time;
	
	public UserPreferences(float safety, float slope, float time) throws InvalidPreferenceSetException{
		
		if(Math.round(safety + slope + time) != 1.0f) throw new InvalidPreferenceSetException();
		
		this.safety = safety;
		this.slope 	= slope;
		this.time 	= time;
		
		
	}
	
	public float getSafetyPreference(){ return safety; }
	
	public float getSlopePreference(){ return slope; }
	
	public float getTimePreference(){ return time; }
}
