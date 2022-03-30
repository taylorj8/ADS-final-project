// @author Jamie Taylor

package com.company;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

public class Network {

    private int noStops, noConnections;
    private ArrayList<Stop> stops;

    public Network(String stopsFile, String timesFile, String transfersFile)
    {
        try
        {
            stops = new ArrayList<>();
            File file = new File("src\\com\\company\\input-files\\" + stopsFile);
            BufferedReader br = new BufferedReader(new FileReader(file));

            int count = 0;
            String line = br.readLine();    // first line of file skipped as just info about contents of file
            line = br.readLine();
            while(line != null)
            {
                // pulls info on street from file, trims off whitespace from beginning and end if present and divides it on spaces into String array
                String[] contents = line.split(",");
                stops.add(new Stop(Integer.parseInt(contents[0]), contents[2]));
                line = br.readLine();
                count++;
            }
            noStops = count;

            br.close();
        }
        catch(Exception e)
        {
            System.err.print(Arrays.toString(e.getStackTrace()));
        }
    }
}


