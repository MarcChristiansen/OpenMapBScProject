package openmap.framework;

import com.fasterxml.jackson.core.JsonGenerator;
import org.json.simple.JSONObject;

import java.io.IOException;

/**
 * Interface that represents the bounds of a graph.
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 09-02-2021
 */
public interface Bounds {
    public double getMinLat();
    public double getMaxLat();
    public double getMinLon();
    public double getMaxLon();

    public double getMinX();
    public double getMaxX();
    public double getMinY();
    public double getMaxY();

    public void setMinX(double v);
    public void setMaxX(double v);
    public void setMinY(double v);
    public void setMaxY(double v);

    public JSONObject getJSONObject();
    public void WriteToJsonGenerator(JsonGenerator jGenerator) throws IOException;

}
