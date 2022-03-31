// @author Jamie Taylor

package com.company;

import java.util.ArrayList;

// basically a key-value pair
public class Stop implements Comparable<Stop> {

    private final int id, code;
    private final String name, desc, zoneId;
    private final double lat, lon;
    private ArrayList<Connection> connections;

    public Stop(int id, int code, String name, String desc, double lat, double lon, String zoneId)
    {
        this.id = id;
        this.code = code;
        this.name = name;
        this.desc = desc;
        this.lat = lat;
        this.lon = lon;
        this.zoneId = zoneId;
        connections = new ArrayList<>();
    }

    // for binary search
    Stop(int id)
    {
        this.id = id;
        this.code = -1;
        this.name = null;
        this.desc = null;
        this.lon = -1;
        this.lat = -1;
        this.zoneId = null;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void addConnection(int destination, double cost)
    {
        connections.add(new Connection(destination, cost));
    }

    public ArrayList<Connection> getConnections()
    {
        return connections;
    }

    public boolean containsDestination(int destination)
    {
        for(Connection connection : connections)
        {
            if(connection.getStop() == destination)
                return true;
        }
        return false;
    }

    public int getNoDestinations()
    {
        return connections.size();
    }

    public Connection getConnection(int i)
    {
        return connections.get(i);
    }

    @Override
    public int compareTo(Stop other)
    {
        return Double.compare(this.getId(), other.getId());
    }

    @Override
    public String toString()
    {
        return id + ", " + name;
    }
}