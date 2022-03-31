// @author Jamie Taylor

package com.company;

import java.util.ArrayList;

// basically a key-value pair
public class Stop implements Comparable<Stop>{

    private final int id, code;
    private final String name, desc, zoneId;
    private final double lat, lon;
    private ArrayList<Stop> connections;

    Stop(int id, int code, String name, String desc, double lat, double lon, String zoneId)
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

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void addStop(Stop stop)
    {
        connections.add(stop);
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