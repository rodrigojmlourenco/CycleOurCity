package org.cycleourcity.otp;

import java.io.File;

import org.cycleourcity.otp.graph.GraphManager;
import org.opentripplanner.graph_builder.GraphBuilder;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.standalone.Router;

public class OTPGraphManager {

	public final String ROUTER = "mRouter";
	
	private Graph graph;
	private Router router;
	private GraphManager updater;
	
	public OTPGraphManager(String baseDir){
		this(new File(baseDir));
	}
	
	
	public OTPGraphManager(File baseDir){
		
		GraphBuilder builder = GraphBuilder.forDirectory(null, baseDir);
		this.graph = builder.getGraph();
		this.router = new Router(ROUTER, graph);
		
	}
	
	public void update(){
		
	}
	
}
