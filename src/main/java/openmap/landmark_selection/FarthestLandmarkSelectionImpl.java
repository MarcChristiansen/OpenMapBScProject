package openmap.landmark_selection;

import openmap.framework.Graph;
import openmap.framework.Node;

import java.util.Map;
import java.util.Random;

public class FarthestLandmarkSelectionImpl extends LandmarkSelectionAbstract{

    public FarthestLandmarkSelectionImpl(Graph graph){
        super(graph);
    }

    @Override
    public void findLandmarks(int k) {
        Object[] values = graph.getNodeMap().values().toArray();
        landmarks.clear();
        this.clearPreviousLandmarksFromNodes();


        Random random = new Random();
        //add random landmark initially
        Node landmark = (Node)values[random.nextInt(values.length)];

        //find node farthest from the random node and add to landmarks
        pf.getShortestPath(landmark, landmark);
        double distance = 0;
        Node bestNode = null;
        for(Map.Entry<Long, Node> e : graph.getNodeMap().entrySet()){
            if(e.getValue().getDistance() > distance &&
                    e.getValue().getDistance() != Double.MAX_VALUE &&
                    e.getValue().getDistance2() != Double.MAX_VALUE &&
                    e.getValue().getIncomingPaths().size() != 0 &&
                    e.getValue().getOutgoingPaths().size() != 0){

                distance = e.getValue().getDistance();
                bestNode = e.getValue();
            }
        }
        processLandmark(bestNode);
        landmarks.add(bestNode);
        System.out.println("Landmark number " + 1 + " Processed");

        System.out.println(bestNode.getId());

        //find node farthest from all known landmarks
        for(int i = 1; i < k; i++){
            System.out.println("Landmark number " + (i+1) + " Processed");
            distance = 0;
            double bestDistance = 0;
            bestNode = null;
            for(Map.Entry<Long, Node> e : graph.getNodeMap().entrySet()){
                for(int j = 0; j < e.getValue().getDistancesFromLandmarks().size() ; j++){
                    double d1 = e.getValue().getDistancesFromLandmarks().get(j);
                    double d2 = e.getValue().getDistancesToLandmarks().get(j);

                    if(d1 == Double.MAX_VALUE || d2 == Double.MAX_VALUE){ //don't select islands
                        distance = 0;
                        break;
                    }

                    distance = distance + Math.sqrt(d1);
                }

                distance = distance / e.getValue().getDistancesFromLandmarks().size();

                boolean hasPaths = e.getValue().getIncomingPaths().size() != 0 && e.getValue().getOutgoingPaths().size() != 0;

                if(distance > bestDistance && hasPaths){
                    bestDistance = distance;
                    bestNode = e.getValue();
                }
            }
            processLandmark(bestNode);
            landmarks.add(bestNode);
        }

    }

    private void processLandmark(Node l){
        System.out.println(l.getOutgoingPaths().size());
        System.out.println(l.getId());
        pf.getShortestPath(l, l);
        for(Map.Entry<Long, Node> e : graph.getNodeMap().entrySet()){
            e.getValue().addLandmarkDistance(e.getValue().getDistance(), e.getValue().getDistance2());
        }
    }
}
