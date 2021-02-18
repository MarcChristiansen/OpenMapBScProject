package openmap.gui;

import openmap.framework.Node;
import openmap.gui.framework.MapTile;

import java.util.List;

public class MapTileImpl implements MapTile {

    int x;
    int y;
    List<Node> nodeList;

    public MapTileImpl(int x, int y, List<Node> nodeList) {
        this.x = x;
        this.y = y;
        this.nodeList = nodeList;
    }


    @Override
    public List<Node> getNodeList() {
        return nodeList;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }
}
