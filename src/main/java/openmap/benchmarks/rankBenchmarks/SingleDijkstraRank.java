package openmap.benchmarks.rankBenchmarks;

import openmap.framework.Node;

import java.util.List;

public class SingleDijkstraRank {

    private Node start;
    private List<Node> ranks;


    public SingleDijkstraRank(Node start, List<Node> ranks) {
        this.start = start;
        this.ranks = ranks;
    }

    public Node getStart() {
        return start;
    }

    public List<Node> getRanks() {
        return ranks;
    }
}
