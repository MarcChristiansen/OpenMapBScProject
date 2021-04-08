package openmap.parsing;

import java.util.ArrayList;
import java.util.List;

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
