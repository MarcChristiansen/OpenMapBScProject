package openmap.special;

import openmap.framework.Node;
import openmap.framework.Path;
import openmap.standard.BaseLineNodeImpl;
import org.apache.commons.lang3.NotImplementedException;

import java.util.List;

/**
 * Node used for parsing only.
 *
 * Contains some info for parsing and does not contain some other info.
 *
 * Serves as a memory optimization primarily
 *
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 01-04-2021
 */
public class ParsingNodeImpl extends BaseLineNodeImpl {
    byte wayCounter;

    public ParsingNodeImpl(long id, double lat, double lon, byte wayCounter){
        super(id, lat, lon);

        this.wayCounter = wayCounter;

    }

    public byte getWayCounter() {
        return wayCounter;
    }

    @Override
    public double getDistance() {
        throw new NotImplementedException("Not intended to be used in parsing");

    }

    @Override
    public void setDistance(double distance) {
        throw new NotImplementedException("Not intended to be used in parsing");
    }

    @Override
    public double getDistance2() {
        throw new NotImplementedException("Not intended to be used in parsing");
    }

    @Override
    public void setDistance2(double distance) {
        throw new NotImplementedException("Not intended to be used in parsing");
    }

    @Override
    public Node getPredecessor() {
        throw new NotImplementedException("Not intended to be used in parsing");
    }

    @Override
    public Node getPredecessor2() {
        throw new NotImplementedException("Not intended to be used in parsing");
    }

    @Override
    public void setPredecessor(Node predecessorId) {
        throw new NotImplementedException("Not intended to be used in parsing");
    }

    @Override
    public void setPredecessor2(Node predecessorId) {
        throw new NotImplementedException("Not intended to be used in parsing");
    }

    @Override
    public boolean getVisited() {
        throw new NotImplementedException("Not intended to be used in parsing");
    }

    @Override
    public void setVisited(boolean b) {
        throw new NotImplementedException("Not intended to be used in parsing");
    }

    @Override
    public boolean getVisited2() {
        throw new NotImplementedException("Not intended to be used in parsing");
    }

    @Override
    public void setVisited2(boolean b) {
        throw new NotImplementedException("Not intended to be used in parsing");
    }

    @Override
    public List<Path> getIncomingPaths() {
        throw new NotImplementedException("Not intended to be used in parsing");
        //Technically not needed as information is still stored for decoding in the nodes so we don't use this in parsing
    }

    @Override
    public List<Double> getDistancesFromLandmarks() {
        throw new NotImplementedException("Not intended to be used in parsing");
    }

    @Override
    public List<Double> getDistancesToLandmarks() {
        throw new NotImplementedException("Not intended to be used in parsing");
    }

    @Override
    public void addLandmarkDistanceTo(double dist) {
        throw new NotImplementedException("Not intended to be used in parsing");
    }

    public void addLandmarkDistanceFrom(double dist) {
        throw new NotImplementedException("Not intended to be used in parsing");
    }

    @Override
    public void addIncomingPath(Path path) {
        throw new NotImplementedException("Not intended to be used in parsing");
        //Technically not needed as information is still stored for decoding in the nodes so we don't use this in parsing
    }
}
