// @author Jamie Taylor

package com.company;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class Network {

    private int noStops;
    private final ArrayList<Stop> stops;
    private final HashMap<Integer, Integer> idIndex;
    private final TST<Stop> names;
    private final TreeMap<Integer, TreeSet<Stop>> times;

    public Network(String stopsFile, String transfersFile, String timesFile)
    {
        stops = new ArrayList<>();
        idIndex = new HashMap<>();
        names = new TST<>();
        times = new TreeMap<>();

        try
        {
            File file = new File("src\\com\\company\\input-files\\" + stopsFile);
            BufferedReader br = new BufferedReader(new FileReader(file));

            noStops = 0;        // noStops is incremented every time a new stop is added
            br.readLine();      // first line of file skipped as just info about contents of file
            String line = br.readLine();
            while(line != null)
            {
                // pulls info on street from file, trims off whitespace from beginning and end if present and divides it on spaces into String array
                String[] info = line.split(",");

                // code field can be blank - below handles that case
                int code = -1;
                if(!info[1].equals(" "))
                    code = Integer.parseInt(info[1]);

                // moves prefix to end of name. flagstop and 2-letter prefix can both be present, with flagstop first,
                // so checks flagstop, moves it to end then checks if the new string has a 2-letter prefix also
                String name = info[2];
                String prefix = name.substring(0,9);
                if(prefix.equals("FLAGSTOP "))
                    name = name.substring(9) + " " + prefix.trim();

                prefix = name.substring(0,3);
                if(prefix.equals("WB ") || prefix.equals("NB ") || prefix.equals("SB ") || prefix.equals("EB "))
                    name = name.substring(3) + " " + prefix.trim();

                // added for clarity
                int id = Integer.parseInt(info[0]);
                String desc = info[3];
                double lat = Double.parseDouble(info[4]);
                double lon = Double.parseDouble(info[5]);
                String zoneID = info[6];

                Stop stop = new Stop(id, code, name, desc, lat, lon, zoneID);
                stops.add(stop);

                // Ternary search tree with id of stop as value
                names.put(name.toLowerCase(), stop);

                // index allows me to map a stop's id to its index in the arraylist, allowing for constant time
                // lookup on average
                idIndex.put(id, noStops);

                line = br.readLine();
                noStops++;
            }

            // change the buffered reader's file to transfers
            br.close();
            file = new File("src\\com\\company\\input-files\\" + transfersFile);
            br = new BufferedReader(new FileReader(file));

            br.readLine();   // first line of file skipped as just info about contents of file
            line = br.readLine();
            while(line != null)
            {
                String[] info = line.split(",");        // split comma separated line into its components
                int origin = Integer.parseInt(info[0]);
                int destination = Integer.parseInt(info[1]);
                int type = Integer.parseInt(info[2]);

                // cost of edges from this file is 2.0, unless type is 2
                double cost = 2.0;
                if(type == 2)
                    cost = Double.parseDouble(info[3]) / 100.0;     // info[3] is min_transfer_time

                int index = idIndex.get(origin);
                if(index != -1)     // if stop doesn't exist, don't add edge
                {
                    stops.get(index).addConnection(destination, cost);
                }
                line = br.readLine();
            }

            // change the buffered reader's file to stop times
            br.close();
            file = new File("src\\com\\company\\input-files\\" + timesFile);
            br = new BufferedReader(new FileReader(file));

            br.readLine();      // first line of file skipped as just info about contents of file
            line = br.readLine();
            // lines processed in pairs, so need to keep track of first and second line in pair
            String[] first, second = new String[0];

            while(line != null)
            {
                first = line.split(",");    //split first line into its components
                int currentTripId = Integer.parseInt(first[0]);
                second = br.readLine().split(",");      // split second line into its components

                int origin = Integer.parseInt(first[3]);
                // inner while represents each trip - exits loop when trip id changes
                while(Integer.parseInt(second[0]) == currentTripId && line != null)
                {
                    int destination = Integer.parseInt(second[3]);
                    int index = idIndex.get(origin);

                    if(index != -1)     // if stop doesn't exist, skip
                    {
                        // add connection only if not already added
                        if(!stops.get(index).containsDest(destination))
                        {
                            stops.get(index).addConnection(destination, 1);
                        }

                        // converts time from string representation to int, returns negative number if error
                        int time = convertTime(second[1]);
                        if(time >= 0)
                            addToTimes(time, stops.get(idIndex.get(destination)));
                    }

                    origin = destination;
                    line = br.readLine();
                    if(line != null)
                        second = line.split(",");
                }
            }
            br.close();
            //add the last time in the file to the tree
            int time = convertTime(second[1]);
            if(time >= 0)
                addToTimes(time, stops.get(idIndex.get(Integer.parseInt(second[3]))));
        }
        catch(Exception e)
        {
            System.err.print(Arrays.toString(e.getStackTrace()));
        }
    }

    // returns cost of shortest path between source and destination
    public double dijkstraSingleDest(int source, int destination, int[] edgeTo)
    {
        // set of settled stops
        Set<Integer> settled = new HashSet<>();
        PriorityQueue<Connection> pq = new PriorityQueue<>(noStops);
        double[] distTo = new double[noStops];

        // initialise distTo array to max values except source which is 0
        Arrays.fill(distTo, Double.MAX_VALUE);
        distTo[idIndex.get(source)] = 0.0;

        // initialise all entries in edgeTo to -1
        Arrays.fill(edgeTo, -1);

        // add first stop to pq
        pq.add(new Connection(source, 0.0));

        while(settled.size() != noStops)
        {
            // terminates once priority queue is empty - returns relevant cost
            if(pq.isEmpty())
                return distTo[idIndex.get(destination)];

            // removes the stop with the lowest cost and stores its index
            int uIndex = idIndex.get(pq.remove().getStop());

            if(!settled.contains(uIndex))
            {
                // once stop is removed from pq it becomes settled
                settled.add(uIndex);

                Stop origin = stops.get(uIndex);
                if(origin != null)
                {
                    // process all neighbouring stops to current stop u
                    for(int i = 0; i < origin.getNoDestinations(); i++)
                    {
                        Connection v = origin.getConnection(i);
                        int vIndex = idIndex.get(v.getStop());

                        // checks if stop already settled
                        if(!settled.contains(v.getStop()))
                        {
                            // gets distance, and if lower than current distance, replaces it
                            double cost = distTo[uIndex] + v.getCost();
                            if(cost < distTo[vIndex])
                            {
                                distTo[vIndex] = cost;
                                edgeTo[vIndex] = uIndex;
                            }
                        }
                        // add each of the stops that neighbour u to the pq
                        pq.add(new Connection(v.getStop(), distTo[vIndex]));
                    }
                }
            }
        }
        return distTo[idIndex.get(destination)];    // returns only the relevant cost
    }

    public int[] getPath(int origin, int destination, int[] edgeTo)
    {
        Stack<Integer> pathStack = new Stack<>();   // stack used to store and reverse order

        // get id of stop in that is the adjacent to the destination on the shortest path
        int stop = stops.get(edgeTo[idIndex.get(destination)]).getId();
        while(stop != origin)
        {
            pathStack.push(stop);       // add stop to stack
            stop = stops.get(edgeTo[idIndex.get(stop)]).getId();    // get next stop on shortest path
        }

        // by popping from stack, effectively reverses order, so stop now go from origin to destination
        int[] path = new int[pathStack.size()];
        for(int i = 0; i < path.length; i++)
        {
            path[i] = pathStack.pop();
        }
        return path;
    }

    // returns -1 if id not present
    public int getIndex(int id)
    {
        Integer index = idIndex.get(id);
        if(index != null)
            return index;
        return -1;
    }

    public Stop[] getMatchingStops(String searchTerm)
    {
        List<String> keys = new ArrayList<>();
        // for every key with a prefix that matches, add to arraylist
        names.keysWithPrefix(searchTerm).forEach(keys::add);
        // convert to array before returning
        Stop[] stops = new Stop[keys.size()];
        for(int i = 0; i < stops.length; i++)
        {
            stops[i] = names.get(keys.get(i));
        }
        return stops;
    }

    // returns stops that arrive at passed time, sorted by id
    // returns null if no matching stops
    public Stop[] getStopsByTime(int time)
    {
        TreeSet<Stop> set = times.get(time);
        if(set == null)
            return null;

        //convert tree set to array before returning
        Stop[] array = new Stop[set.size()];
        for(int i = 0; i < array.length; i++)
        {
            array[i] = set.pollFirst();
        }
        return array;
    }

    public void addToTimes(int time, Stop stop)
    {
        // tree set used as it maintains sorted order and implicitly handles duplicates
        TreeSet<Stop> set = times.get(time);
        // if no times already in tree tree, new tree set made and added
        if(set == null)
        {
            TreeSet<Stop> newSet = new TreeSet<>();
            newSet.add(stop);
            times.put(time, newSet);
        }
        else    // if time already in tree, add to the tree set
        {
            set.add(stop);
        }
    }

    // converts time string to int, returns negative number based on failure
    public int convertTime(String str)
    {
        // checks string in correct format
        if(!str.matches("\\d{1,2}:\\d{2}:\\d{2}"))
            return -1;

        int[] timeArray = new int[3];
        String[] timeStrArray = str.trim().split(":");      // split hours, minutes, seconds into string array
        for(int i = 0; i < timeStrArray.length; i++)
        {
            timeArray[i] = Integer.parseInt(timeStrArray[i]);
        }

        // check numbers in correct range
        if(timeArray[0] < 0 || timeArray[0] > 23 || timeArray[1] < 0 || timeArray[1] > 59 || timeArray[2] < 0 || timeArray[2] > 59)
            return -2;

        // convert to single int and return
        return (timeArray[0] * 100 + timeArray[1]) * 100 + timeArray[2];
    }

    public int getNoStops()
    {
        return noStops;
    }
}


