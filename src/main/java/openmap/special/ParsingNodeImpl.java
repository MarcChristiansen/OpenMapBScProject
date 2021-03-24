package openmap.special;

import openmap.standard.BaseLineNodeImpl;
import org.apache.commons.lang3.NotImplementedException;

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
    public Long getPredecessor() {
        throw new NotImplementedException("Not intended to be used in parsing");
    }

    @Override
    public void setPredecessor(Long predecessorId) {
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


}
