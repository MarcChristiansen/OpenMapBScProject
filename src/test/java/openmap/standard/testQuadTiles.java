package openmap.standard;

import openmap.framework.Bounds;
import openmap.framework.Node;
import openmap.gui.QuadTile;
import org.junit.Before;
import org.junit.Test;


public class testQuadTiles {

    QuadTile rootQt;

    @Before
    public void setUp() {
        Bounds b = new BoundsImpl();
        b.setMinX(0);
        b.setMinY(0);
        b.setMaxX(1000);
        b.setMaxY(1000);
        rootQt = new QuadTile((byte)4, b, 0);
    }

    @Test
    public void testNodePropegation(){
        Node n1 = new NodeImpl(0, 0, 0,null);
        rootQt.addNode(n1);
        assert(rootQt.getNodeList().contains(n1));
        assert(rootQt.getChild(2).getNodeList().contains(n1));
        assert(rootQt.getChild(2).getChild(2).getNodeList().contains(n1));
        assert(rootQt.getChild(2).getChild(2).getChild(2).getNodeList().contains(n1));
        assert(rootQt.getChild(2).getChild(2).getChild(2).getChild(2) == null);


        Node n2 = new NodeImpl(0, 725, 350,null);
        rootQt.addNode(n2);
        assert(rootQt.getNodeList().contains(n2));
        assert(rootQt.getChild(3).getNodeList().contains(n2));
        assert(rootQt.getChild(3).getChild(0).getNodeList().contains(n2));
        assert(rootQt.getChild(3).getChild(0).getChild(3).getNodeList().contains(n2));
        assert(rootQt.getChild(3).getChild(0).getChild(3).getChild(1) == null);
    }

}

