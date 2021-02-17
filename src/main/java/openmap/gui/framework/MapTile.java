package openmap.gui.framework;

import openmap.framework.Node;

import java.util.List;

public interface MapTile {

    List<Node> getNodeList();

    int getX();

    int getY();
}
