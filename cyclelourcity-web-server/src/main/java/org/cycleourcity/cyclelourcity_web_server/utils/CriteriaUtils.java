package org.cycleourcity.cyclelourcity_web_server.utils;

import org.cycleourcity.cyclelourcity_web_server.utils.exceptions.UnsupportedCriterionException;

public class CriteriaUtils {

	public static enum Criteria {
		elevation,
		safety,
		pavement,
		rails,
		consolidatedElevation,
		consolidatedSafety
	}
	
	public static String getCriteriaAsString(Criteria criterion) throws UnsupportedCriterionException{
		switch (criterion) {
		case safety:
			return "safety";
		case elevation:
			return "elevation";
		case rails:
			return "rails";
		case pavement:
			return "pavement";
		default:
			throw new UnsupportedCriterionException();
		}
	}
	
	public static String getCriterionFactorTable(Criteria criterion) throws UnsupportedCriterionException{
		return getCriteriaAsString(criterion);
	}
	
	public static String getCriterionClassificationTable(Criteria criterion) throws UnsupportedCriterionException{
		
		switch (criterion) {
		case consolidatedElevation:
			return "streetedge_consolidatedelevation";
		case consolidatedSafety:
			return "streetedge_consolidatedsafety";
		default:
			return "streetedge_"+getCriteriaAsString(criterion);
		}

	}
}
