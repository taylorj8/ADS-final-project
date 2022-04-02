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

    public Network(String stopsFile, String transfersFile, String timesFile)
    {
        stops = new ArrayList<>();
        idIndex = new HashMap<>();
        try
        {
            File file = new File("src\\com\\company\\input-files\\" + stopsFile);
            BufferedReader br = new BufferedReader(new FileReader(file));

            noStops = 0;
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

                // index allows me to map a stop's id to its index in the arraylist, allowing for constant time
                // lookup on average
                idIndex.put(Integer.parseInt(contents[0]), noStops);

                line = br.readLine();
                noStops++;
            }

            file = new File("src\\com\\company\\input-files\\" + transfersFile);
            br = new BufferedReader(new FileReader(file));

            line = br.readLine();
            line = br.readLine();
            while(line != null)
            {
                String[] contents = line.split(",");
                int origin = Integer.parseInt(contents[0]);
                int destination = Integer.parseInt(contents[1]);
                int type = Integer.parseInt(contents[2]);

                double cost = 2.0;
                if(type == 2)
                    cost = Double.parseDouble(contents[3]) / 100.0;

                int index = idIndex.get(origin);
                if(index != -1)     // if stop is not on list, skip
                {
                    stops.get(index).addConnection(destination, cost);
                }
                line = br.readLine();
            }


            file = new File("src\\com\\company\\input-files\\" + timesFile);
            br = new BufferedReader(new FileReader(file));

            line = br.readLine();
            line = br.readLine();
            while(line != null)
            {
                String[] first = line.split(",");
                int currentTripId = Integer.parseInt(first[0]);
                String[] second = br.readLine().split(",");

                int origin = Integer.parseInt(first[3]);
                while(Integer.parseInt(second[0]) == currentTripId && line != null)
                {
                    int destination = Integer.parseInt(second[3]);

                    int index = idIndex.get(origin);
                    if(index != -1)     // if stop is not on list, skip
                    {
                        // add connection only if not already added
                        if(!stops.get(index).containsDestination(destination))
                        {
                            stops.get(index).addConnection(destination, 1);
                        }
                    }

                    origin = destination;
                    line = br.readLine();
                    if(line != null)
                        second = line.split(",");
                }
            }
            br.close();
        }
        catch(Exception e)
        {
            System.err.print(Arrays.toString(e.getStackTrace()));
        }
    }

    //returns cost of shortest path between source and destination
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
            // terminates once priority queue is empty
            if(pq.isEmpty())
                return distTo[destination];

            // removes the stop with the lowest cost and stores its index
            int u = idIndex.get(pq.remove().getStop());

            if(!settled.contains(u))
            {
                // once stop is removed from pq it becomes settled
                settled.add(u);

                Stop origin = stops.get(u);
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
                            double cost = distTo[u] + v.getCost();
                            if(cost < distTo[vIndex])
                            {
                                distTo[vIndex] = cost;
                                edgeTo[vIndex] = u;
                            }
                        }
                        // add each of the stops that neighbour u to the pq
                        pq.add(new Connection(v.getStop(), distTo[vIndex]));
                    }
                }
            }
        }
        return distTo[destination];
    }

    public int[] getPath(int origin, int destination, int[] edgeTo)
    {
        LinkedList<Integer> pathStack = new LinkedList<>();

        int stop = stops.get(edgeTo[idIndex.get(destination)]).getId();
        while(stop != origin)
        {
            pathStack.push(stop);
            stop = stops.get(edgeTo[idIndex.get(stop)]).getId();
        }

        int[] path = new int[pathStack.size()+2];
        path[0] = origin;
        for(int i = 1; !pathStack.isEmpty(); i++)
        {
            path[i] = pathStack.pop();
        }
        path[path.length-1] = destination;
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

    public int getNoStops()
    {
        return noStops;
    }

}


