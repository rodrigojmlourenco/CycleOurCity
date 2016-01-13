package org.cycleourcity.otp;

import java.io.File;

import org.cycleourcity.driver.database.structures.GeoLocation;
import org.cycleourcity.otp.graph.GraphManager;
import org.cycleourcity.otp.graph.exceptions.UnableToSerializeGraph;
import org.cycleourcity.otp.planner.RoutePlanner;
import org.cycleourcity.otp.planner.SimpleBicycleRoutePlanner;
import org.cycleourcity.otp.planner.preferences.UserPreferences;
import org.opentripplanner.standalone.Router;

import jj2000.j2k.NotImplementedError;


/**
 * The OTPGraphManager is the main entry point for the coc-otp library.
 * This component makes available three core methods:<br>
 * <ul>
 * <li>Route planning</li>
 * <li>CycleOurCity and OTP information integration</li>
 * <li>Graph updating from more recent OSM information</li>
 * <ul>
 * @author samm
 */
public class OTPGraphManager {

	public final String ROUTER = "mRouter";
	
	private GraphManager gManager;
	
	public OTPGraphManager(String baseDir){
		this(new File(baseDir));
	}
	
	/**
	 * Creates a new OTPGraphManager with a newly generated graph
	 * found in the baseDir directory.
	 * 
	 * @param baseDir The directory where the graph generation input files are located.
	 */
	public OTPGraphManager(File baseDir){
		try {
			gManager = new GraphManager(baseDir);
		} catch (UnableToSerializeGraph e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates a new OTPGraphManager, however, it first attempts
	 * to fetch previously stored graphs.
	 * 
	 * @param load If true attempt load, otherwise create new graph.
	 * @param baseDir The directory where the graph generation input files are located.
	 */
	public OTPGraphManager(boolean load, String baseDir){
		try {
			gManager = new GraphManager(load, baseDir);
		} catch (UnableToSerializeGraph e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Initiates the process of integrating the graph with data
	 * from the CycleOurCity database.
	 */
	public void integrateCycleOurCityRatings(){
		gManager.runIntegration();
	}
	
	/**
	 * Generates a new graph, given a set of osm input file found in baseDir.
	 * This new graph is then compared with the currently employed by the system.
	 * If there are not differences between the two graphs, then the new graph is
	 * discarded. Otherwise, it is the GraphManager responsibility to merge
	 * the two.
	 * 
	 * @param baseDir Directory where the osm files are located at.
	 */
	public void updateFromOSM(String baseDir){
		throw new NotImplementedError();
	}

	/**
	 * Instantiates and returns a new runnable RoutePlanner, that may
	 * be executed in order to plan a route.
	 * 
	 * @param from Starting point
	 * @param to Ending point
	 * @param prefs The user's preferences
	 * 
	 * @return Runnable RoutePlanner
	 * 
	 * @see RoutePlanner
	 */
	public RoutePlanner planRoute(GeoLocation from, GeoLocation to, UserPreferences prefs){
	   	Router r = new Router("myR", gManager.getGraph());
	   	return new SimpleBicycleRoutePlanner(r, from, to, prefs);
	}
}
