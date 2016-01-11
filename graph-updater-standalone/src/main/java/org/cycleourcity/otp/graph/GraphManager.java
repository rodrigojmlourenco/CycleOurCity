package org.cycleourcity.otp.graph;

import java.io.File;
import java.io.IOException;

import org.cycleourcity.otp.coc.CoCtoOTPUpdater;
import org.cycleourcity.otp.graph.exceptions.UnableToSerializeGraph;
import org.cycleourcity.otp.utils.Utils.Criterion;
import org.opentripplanner.graph_builder.GraphBuilder;
import org.opentripplanner.routing.graph.Graph;

public class GraphManager 
{

	protected static final String
		BASE_DIR  = "/var/otp",
		NEW_GRAPH = System.getenv("HOME")+"/otp/graph/new/Graph.obj",
		OLD_GRAPH = System.getenv("HOME")+"/otp/graph/old/Graph.obj";
	
	
	private final Graph graph;
	private final CoCtoOTPUpdater merger;
	
	public GraphManager() throws UnableToSerializeGraph{
		this(BASE_DIR);
	}
	
	public GraphManager(String baseDir) throws UnableToSerializeGraph{
		this(new File(baseDir));
	}
	
	public GraphManager(File baseDir) throws UnableToSerializeGraph{
		
		// 1 - For the directory whesre the input files are located at
		//		generate the city's graph.
		GraphBuilder builder = GraphBuilder.forDirectory(null, baseDir);
		this.graph = builder.getGraph();
		
		// 2 - Save the graph file in both locations
		try {
			graph.save(new File(NEW_GRAPH));
			graph.save(new File(OLD_GRAPH));
		} catch (IOException e) {
			throw new UnableToSerializeGraph(e.getMessage());
		}
		
		// 3 - If there are CoC classification add them to the graph
		merger = new CoCtoOTPUpdater(graph);
		merger.updateGraph(Criterion.safety);
		merger.updateGraph(Criterion.elevation);
		//merger.saveChanges(NEW_GRAPH);
		
	}
	
	public Graph getGraph(){
		return this.graph;
	}
}
