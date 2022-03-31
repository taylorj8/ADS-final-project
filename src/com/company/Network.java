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

                // code field can be blank - below handles that case
                int code = -1;
                if(!contents[1].equals(" "))
                    code = Integer.parseInt(contents[1]);

                // moves prefix to end of name
                String name = contents[2];
                String prefix = name.substring(0,3);
                if(prefix.equals("WB ") || prefix.equals("NB ") || prefix.equals("SB ") || prefix.equals("EB "))
                {
                    name = name.substring(3) + " " + prefix.trim();
                }

                stops.add(new Stop(Integer.parseInt(contents[0]), code, name, contents[3],
                        Double.parseDouble(contents[4]), Double.parseDouble(contents[5]), contents[6]));
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


