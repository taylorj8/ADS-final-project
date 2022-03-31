// @author Jamie Taylor

package com.company;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class Network {

    private int noStops, noConnections;
    private ArrayList<Stop> stops;

    public Network(String stopsFile, String transfersFile, String timesFile)
    {
        try
        {
            stops = new ArrayList<>();
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

                line = br.readLine();
                noStops++;
            }
            Collections.sort(stops);

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

                int cost = 2;
                if(type == 2)
                    cost = Integer.parseInt(contents[3]) / 100;

                int index = binarySearch(stops, origin);
                if(index != -1)     // if stop is not on list, skip
                {
                    stops.get(index).addConnection(destination, cost);
                    noConnections++;
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

                    int index = binarySearch(stops, origin);
                    if(index != -1)     // if stop is not on list, skip
                    {
                        // add connection only if not already added
                        if(!stops.get(index).containsDestination(destination))
                        {
                            stops.get(index).addConnection(destination, 1);
                            noConnections++;
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

    public void computeDijkstra(int source, double[] distTo)
    {
        // set of settled stops
        Set<Integer> settled = new HashSet<>();
        PriorityQueue<Connection> pq = new PriorityQueue<>(noStops);

        // initialise distTo array to max values except source which is 0
        Arrays.fill(distTo, Double.MAX_VALUE);
        distTo[source] = 0.0;

        // add first stop to pq
        pq.add(new Connection(source, 0.0));


        while(settled.size() != noStops)
        {
            // terminates once priority queue is empty
            if(pq.isEmpty())
                return;

            // removes and stores the stop with the lowest distance
            int u = pq.remove().getStop();

            if(!settled.contains(u))
            {
                // once stop is removed from pq it becomes settled
                settled.add(u);

                Stop origin = getStop(u);
                if(origin != null)
                {
                    // process all neighbouring stops to current stop u
                    for(int i = 0; i < origin.getNoDestinations(); i++)
                    {
                        Connection v = origin.getConnection(i);

                        // checks if stop already settled
                        if(!settled.contains(v.getStop()))
                        {
                            // gets distance, and if lower than current distance, replaces it
                            double distance = distTo[u] + v.getCost();
                            if(distance < distTo[v.getStop()])
                            {
                                distTo[v.getStop()] = distance;
                            }
                        }
                        // add each of the stops that neighbour u to the pq
                        pq.add(new Connection(v.getStop(), distTo[v.getStop()]));
                    }
                }
            }
        }
    }

    public Stop getStop(int i)
    {
        return stops.get(binarySearch(stops, i));
    }

    public int binarySearch(ArrayList<Stop> stops, int x)
    {
        return binarySearch(stops, x, 0, stops.size());
    }

    private int binarySearch(ArrayList<Stop> stops, int x, int lo, int hi)
    {
        if (hi >= lo)
        {
            int mid = lo + (hi - lo) / 2;

            if (stops.get(mid).getId() == x)
                return mid;

            if (stops.get(mid).getId() > x)
                return binarySearch(stops, x, lo, mid - 1);

            return binarySearch(stops, x, mid + 1, hi);
        }

        // if element is not present, return -1
        return -1;
    }
}


