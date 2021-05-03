package openmap.utility;

import java.util.Arrays;
import java.util.List;

public  class LatexUtility {

    public static String generateTableRow(List<String> entries){
        String res = "";

        res = String.join(" & ", entries);

        res = res + " \\\\ \\hline";

        return res;
    }

    public static String generateStandardTable(List<List<String>> rows){
        String res = "";

        if(rows.size() == 0){
            return null;
        }

        String[] r = new String[rows.get(0).size()];
        Arrays.fill(r, "l");


        res = res + "\\begin{table}[H] \n";
        res = res + "\\begin{tabular} {|"+ String.join("|", r)+"|} \n";
        res = res + "\\hline \n";

        for(List<String> ls : rows){
            res = res + generateTableRow(ls) + "\n";
        }

        res = res + "\\end{tabular} \n";
        res = res + "\\end{table}";
        return res;
    }
}
