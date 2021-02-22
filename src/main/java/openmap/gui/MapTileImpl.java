package openmap.gui;

import openmap.framework.Node;
import openmap.gui.framework.MapTile;

import java.util.ArrayList;
import java.util.List;

public class MapTileImpl implements MapTile {

    List<Node> nodeList;

    public MapTileImpl() {
        this.nodeList = new ArrayList<>();
    }


    @Override
    public List<Node> getNodeList() {
        return nodeList;
    }

    @Override
    public void addNode(Node n) {
        nodeList.add(n);
    }
}
