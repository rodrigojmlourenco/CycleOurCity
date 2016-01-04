package org.cycleourcity.cycleourcity_web_server.graph_builder;

public interface Utils {
	
	public static enum Criterion {
		safety,
		elevation,
		pavement,
		rails
	}
	
	public static interface DatabaseFields{
		
		public static final String
			USER_ID = "IdUser",
			USERNAME = "Username",
			STREETEDGE_ID = "IdStreetEdge",
			SAFETY_CRITERION = "IdSafety",
			ELEVATION_CRITERION = "IdElevation",
			PAVEMENT_CRITERION = "IdPave",
			RAILS_CRITERION = "IdRails";
		
		
		
	}

}
