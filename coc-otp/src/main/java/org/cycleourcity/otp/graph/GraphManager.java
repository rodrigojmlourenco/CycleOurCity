package org.cycleourcity.otp.graph;

import java.io.File;
import java.io.IOException;

import org.cycleourcity.driver.utils.CriteriaUtils.Criteria;
import org.cycleourcity.otp.coc.GraphIntegrator;
import org.cycleourcity.otp.coc.exceptions.RepeatedIdsException;
import org.cycleourcity.otp.exceptions.EmptyMapException;
import org.cycleourcity.otp.graph.exceptions.UnableToSerializeGraph;
import org.cycleourcity.otp.planner.exceptions.UnsupportedCriterionException;
import org.opentripplanner.graph_builder.GraphBuilder;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.standalone.CommandLineParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphManager 
{

	private static Logger LOG = LoggerFactory.getLogger(GraphManager.class);

	protected static final String
	BASE_DIR  = "/var/otp",
	NEW_GRAPH = System.getenv("HOME")+"/otp/test/graph/new/Graph.obj",
	OLD_GRAPH = System.getenv("HOME")+"/otp/test/graph/old/Graph.obj";


	private Graph graph;
	private GraphIntegrator merger;

	public GraphManager() throws UnableToSerializeGraph{
		this(BASE_DIR);
	}

	public GraphManager(String baseDir) throws UnableToSerializeGraph{
		this(new File(baseDir));
	}

	public GraphManager(File baseDir) throws UnableToSerializeGraph{

		// 1 - For the directory where the input files are located at
		//		generate the city's graph.
		buildGraphFromScratch(baseDir);

		// 3 - If there are CoC classification add them to the graph
		try {
			merger = new GraphIntegrator(graph);

			merger.updateGraph(Criteria.safety);
			merger.updateGraph(Criteria.elevation);
			
			merger.saveChanges(NEW_GRAPH);

		} catch (UnsupportedCriterionException e) {
			throw new UnableToSerializeGraph(e.getMessage());
		} catch (RepeatedIdsException e) {
			throw new UnableToSerializeGraph(e.getMessage());
		} catch (EmptyMapException e) {
			e.printStackTrace();
			throw new UnableToSerializeGraph(e.getMessage());
		}
	}

	public GraphManager(boolean tryLoad, String baseDir) throws UnableToSerializeGraph{

		File graphFile = new File(NEW_GRAPH);

		if(graphFile.exists()){
			LOG.info("Graph found, loading it from "+NEW_GRAPH);
			try {
				
				this.graph = Graph.load(graphFile, Graph.LoadLevel.DEBUG);
				LOG.info("Graph succesfully loaded.");
				
			} catch (ClassNotFoundException | IOException e) {
				LOG.error("Failed graph loading... Generating new graph from "+baseDir);
				buildGraphFromScratch(new File(baseDir));
				
			}
		}else
			buildGraphFromScratch(new File(baseDir));
		
		updateGraphIntegrator();
	}

	private void buildGraphFromScratch(File baseDir) throws UnableToSerializeGraph{
		// 1 - For the directory where the input files are located at
		//		generate the city's graph.
		CommandLineParameters params = new CommandLineParameters();
		params.inMemory = true;
		params.preFlight = false;

		GraphBuilder builder = GraphBuilder.forDirectory(params, baseDir);
		builder.run();
		this.graph = builder.getGraph();

		// 2 - Save the graph file in both locations
		try {
			graph.save(new File(NEW_GRAPH));
			graph.save(new File(OLD_GRAPH));
		} catch (IOException e) {
			throw new UnableToSerializeGraph(e.getMessage());
		}
	}
	
	private void updateGraphIntegrator() throws UnableToSerializeGraph{
		
		try {
			merger = new GraphIntegrator(graph);

			merger.updateGraph(Criteria.safety);
			merger.updateGraph(Criteria.elevation);
			merger.saveChanges(NEW_GRAPH);

		} catch (UnsupportedCriterionException e) {
			throw new UnableToSerializeGraph(e.getMessage());
		} catch (RepeatedIdsException e) {
			throw new UnableToSerializeGraph(e.getMessage());
		} catch (EmptyMapException e) {
			LOG.error(e.getMessage());
		}
	}

	public Graph getGraph(){
		return this.graph;
	}
	
	public void runIntegration(){

		try {
			merger.updateGraph(Criteria.safety);
			merger.updateGraph(Criteria.elevation);
			merger.saveChanges(NEW_GRAPH);
		} catch (UnsupportedCriterionException e) {
			e.printStackTrace();
		}
	}
}
