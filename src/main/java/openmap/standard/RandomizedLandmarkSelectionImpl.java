package openmap.standard;

import openmap.framework.Graph;
import openmap.framework.LandmarkSelection;
import openmap.framework.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomizedLandmarkSelectionImpl implements LandmarkSelection {

    Graph graph;
    List<Node> landmarks;

    public RandomizedLandmarkSelectionImpl(Graph graph){
        this.graph = graph;
        this.landmarks = new ArrayList<Node>();
    }

    @Override
    public void findLandmarks(int k) {
        Object[] values = graph.getNodeMap().values().toArray();
        landmarks.clear();
        Random random = new Random();
        for(int i = 0; i < k; i++){
            Node landmark = (Node)values[random.nextInt(values.length)];
            landmarks.add(landmark);
        }
    }

    @Override
    public List<Node> getLandmarks() {
        return landmarks;
    }

    @Override
    public void preProcessNodes() {
        Object[] values = graph.getNodeMap().values().toArray();
        for(Object n : values){
            for(Node L : landmarks){

                ((Node)n).addLandmarkDistance(distance(((Node)n), L));
            }
        }
    }

    private double distance(Node n1, Node n2){
        return Math.sqrt(Math.pow(n1.getX()-n2.getX(), 2) + Math.pow(n1.getY()-n2.getY(), 2));
    }
}
