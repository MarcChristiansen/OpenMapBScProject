package openmap.benchmarks;

import crosby.binary.osmosis.OsmosisReader;
import openmap.parsing.ParsingUtil;
import openmap.utility.ConsoleUtils;
import openmap.utility.LatexUtility;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.container.v0_6.NodeContainer;
import org.openstreetmap.osmosis.core.container.v0_6.RelationContainer;
import org.openstreetmap.osmosis.core.container.v0_6.WayContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

import java.io.*;
import java.util.*;

public class OsmDataCounter {
    public static void main(String[] args) throws IOException {
        String fileIn = "";
        String wayTypeListSelection;
        boolean single = false;
        if(args != null && args.length > 1){
            wayTypeListSelection = args[1];
        }
        else{
            single = true;

            fileIn = ConsoleUtils.readLine(
                    "Enter pbf path : ");

            wayTypeListSelection = ConsoleUtils.readLine(
                    "Enter path type set (normal or mini) : ");

        }


        List<List<String>> tableRows = new ArrayList<>();
        String[] names = {"Kort", "\\# Elementer", /*"Antal knuder", "Antal veje", "Antal relationer",*/ "\\# Vejelementer", /* "\\# Knuder", "\\# Veje",*/ "Vejrelateret (\\%)"};
        tableRows.add(Arrays.asList(names));

        if(single){
            OsmiumCounter counter = getFinishedOsmiumCounter(fileIn, wayTypeListSelection);
            tableRows.add(getEntry(fileIn , counter));
            printFinishedCounter(fileIn, counter);
        }
        else{
            for (int i = 1; i < args.length; i++) {
                OsmiumCounter counter = getFinishedOsmiumCounter(args[i], wayTypeListSelection);
                tableRows.add(getEntry(args[i], counter));
            }
        }

        System.out.println("\n###### Latex full table ######");
        System.out.println(LatexUtility.generateStandardTable(tableRows));

    }

    private static void printFinishedCounter(String fileIn, OsmiumCounter counter) {
        System.out.println("###### Counts recorded for file: " + fileIn + " ######");
        System.out.println("Total:     " + counter.getTotalCount());
        System.out.println("Nodes:     " + counter.getNodeCount());
        System.out.println("Ways:      " + counter.getWayCount());
        System.out.println("relations: " + counter.getRelationCount());

        System.out.println("\n###### Relevant counts based on path tag selection ######");
        System.out.println("Total relevant: " + counter.getTotalRelevantCount());
        System.out.println("Relevant Nodes: " + counter.getRelevantNodeCount());
        System.out.println("Relevant ways:  " + counter.getRelevantWayCount());

        System.out.println("\n###### Percentages ######");

        System.out.println("Total relevant percentage: " + (double)(counter.getTotalRelevantCount())/(double) counter.getTotalCount()*100);

        System.out.println("\n###### Latex row ######");
    }

    private static List<String> getEntry(String name, OsmiumCounter counter) {
        double totalPercentage = (double)(counter.getTotalRelevantCount())/(double) counter.getTotalCount()*100;

        String[] nameArr = name.split("\\\\");
        String finalName = nameArr[nameArr.length-1];

        String[] entries = {finalName,
                Long.toString(counter.getTotalCount()),
                //Long.toString(counter.getNodeCount()),
                //Long.toString(counter.getWayCount()),
                //Long.toString(counter.getRelationCount()),
                Long.toString(counter.getTotalRelevantCount()),
                //Long.toString(counter.getRelevantNodeCount()),
                //Long.toString(counter.getRelevantWayCount()),
                String.format(Locale.GERMANY,"%.2f", totalPercentage)+"\\%"};
        return Arrays.asList(entries);
    }

    private static OsmiumCounter getFinishedOsmiumCounter(String fileIn, String wayTypeListSelection) {
        OsmiumCounter counter = new OsmiumCounter(ParsingUtil.getAllowedValues(wayTypeListSelection));
        runReaderWithSink(counter, fileIn);
        return counter;
    }

    private static void runReaderWithSink(Sink sink, String fileIn) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(fileIn);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        OsmosisReader reader = new OsmosisReader(inputStream);
        reader.setSink(sink);
        reader.run();

        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class OsmiumCounter implements Sink {

        long totalCount;
        long nodeCount;
        long wayCount;
        long relevantWayCount;
        long relationCount;
        HashMap<Long, Boolean> nodeWayCounter;
        List<String> relevantWays;

        public OsmiumCounter(List<String> relevantWays) {
            this.relevantWays = relevantWays;
        }


        @Override
        public void process(EntityContainer entityContainer) {
            totalCount++;
            if (entityContainer instanceof NodeContainer) {
                nodeCount++;
            }
            else if(entityContainer instanceof WayContainer){
                wayCount++;

                Way myWay = ((WayContainer)(entityContainer)).getEntity();
                for (Tag testTag : myWay.getTags()) {
                    if ("highway".equalsIgnoreCase(testTag.getKey()) && relevantWays.stream().anyMatch(testTag.getValue()::equalsIgnoreCase)) {
                        relevantWayCount++;
                        myWay.getWayNodes().forEach(n -> nodeWayCounter.put(n.getNodeId(), true));
                        break; //No need to look at other tags, nodes have been recorded...
                    }
                }
            }
            else if(entityContainer instanceof RelationContainer){
                relationCount++;
            }
        }

        @Override
        public void initialize(Map<String, Object> metaData) {
            totalCount = 0;
            nodeCount = 0;
            wayCount = 0;
            relevantWayCount = 0;
            relationCount = 0;

            nodeWayCounter = new HashMap<>();
        }

        @Override
        public void complete() { }

        @Override
        public void close() {
        }

        public long getTotalCount() {
            return totalCount;
        }

        public long getNodeCount() {
            return nodeCount;
        }

        public long getWayCount() {
            return wayCount;
        }

        public long getRelationCount() {
            return relationCount;
        }

        public long getTotalRelevantCount() {
            return getRelevantWayCount() + nodeWayCounter.size();
        }

        public long getRelevantWayCount() {
            return relevantWayCount;
        }

        public long getRelevantNodeCount() {
            return nodeWayCounter.size();
        }

        public HashMap<Long, Boolean> getNodeWayCounter() {
            return nodeWayCounter;
        }
    }
}
