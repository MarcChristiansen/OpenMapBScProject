package openmap.utility;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileWriter {
    public static void main(String args[]) throws IOException {

        List<List<String>> data = new ArrayList<>();
        List<String> data1 = new ArrayList<>();
        List<String> data2 = new ArrayList<>();

        data1.add("a");
        data1.add("b");
        data1.add("c");

        data2.add("a2");
        data2.add("b2");
        data2.add("c2");

        data.add(data1);
        data.add(data2);

        writeCSV(data, "testfile");

    }


    public static void writeCSV(List<List<String>> data, String filename){
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filename+".csv"), "utf-8"))) {
            for(List<String> l : data){
                String line = "";
                for(String s : l){
                    line = line + s + "; ";
                }
                writer.write(line.subSequence(0, line.length()-2)+"\n");
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
