package openmap.gui.framework;

import openmap.framework.Node;

import java.awt.*;
import java.util.List;

public interface MapTile {

    List<Node> getNodeList();

    void addNode(Node n);
}
