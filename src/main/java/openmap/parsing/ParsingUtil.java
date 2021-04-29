package openmap.parsing;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple utility class that contains utilities related to parsing
 * The primary focus of this class is the creation of a list of allowed path "types", but this might extend in the future.
 * @author Kristoffer Villadsen and Marc Christiansen
 * @version 1.0
 * @since 29-02-2021
 */
public class ParsingUtil {
    public static List<String> getAllowedValues(String wayTypeListSelection) {

        if(wayTypeListSelection.equalsIgnoreCase("mini")){
            return getMinimizedAllowedValues();
        }
        return getDefaultAllowedValues();
    }

    public static List<String> getDefaultAllowedValues(){
        List<String> allowedList = new ArrayList<>();

        allowedList.add("motorway");
        allowedList.add("trunk");
        allowedList.add("primary");
        allowedList.add("secondary");
        allowedList.add("tertiary");

        allowedList.add("motorway_link");
        allowedList.add("trunk_link");
        allowedList.add("primary_link");
        allowedList.add("secondary_link");
        allowedList.add("tertiary_link");

        allowedList.add("unclassified");
        allowedList.add("residential");

        return allowedList;
    }

    public static List<String> getMinimizedAllowedValues(){
        List<String> allowedList = new ArrayList<>();

        allowedList.add("motorway");
        allowedList.add("trunk");
        allowedList.add("primary");
        allowedList.add("secondary");
        //allowedList.add("tertiary");

        allowedList.add("motorway_link");
        allowedList.add("trunk_link");
        allowedList.add("primary_link");
        allowedList.add("secondary_link");
        //allowedList.add("tertiary_link");


        return allowedList;
    }


}
