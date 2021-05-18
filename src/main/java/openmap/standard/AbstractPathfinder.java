package openmap.standard;

import openmap.framework.Graph;
import openmap.framework.Node;
import openmap.framework.NodeWrapper;
import openmap.framework.PathFinder;
import openmap.gui.NodeDrawingInfo;

import java.util.List;
import java.util.function.Function;

abstract public class AbstractPathfinder implements PathFinder {
    protected Graph graph;

    protected long executionTime;
    protected int nodesVisited;
    protected int nodesScanned;

    public AbstractPathfinder(Graph graph){
        this.graph = graph;
        this.executionTime = 0;
        this.nodesVisited = 0;
        this.nodesScanned = 0;
    }

    @Override
    abstract public List<Node> getShortestPath(Node source, Node destination);

    @Override
    public long getLastExecutionTime() {
        return executionTime;
    }

    @Override
    public int getNodesVisited() {
        return nodesVisited;
    }

    @Override
    public int getNodesScanned() {
        return nodesScanned;
    }

    @Override
    abstract public  Function<Node, NodeDrawingInfo> getVisitedCheckFunction();

    @Override
    public List<Integer> getLandmarksUsedTo() {
        return null;
    }

    @Override
    public List<Integer> getLandmarksUsedFrom() {
        return null;
    }

    @Override
    public void SetLandmarkSubsetSize(int i) { }
}
