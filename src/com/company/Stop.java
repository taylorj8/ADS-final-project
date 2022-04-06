// @author Jamie Taylor

package com.company;

import java.util.ArrayList;

// basically a key-value pair
public class Stop implements Comparable<Stop> {

    private final int id, code;
    private final String name, desc, zoneId;
    private final double lat, lon;
    private final ArrayList<Connection> connections;

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

    public boolean containsDest(int destination)
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
        // code not always present in file, if not found, code = n/a
        String codeString = "n/a";
        if(code != -1)
            codeString = String.valueOf(code);

        // prints all info about stop
        StringBuilder str = new StringBuilder(String.format("Name: %s\nDescription: %s\nID: %d\nCode: %s\nZone ID: %s\nLongitude: %f\nLatitude: %f\nConnected stop(s) by id: ",
                name, desc, id, codeString, zoneId, lon, lat));

        // if stop has connections, adds all those connections to the string
        if(connections.size() != 0)
        {
            for(Connection connection : connections)
            {
                str.append(connection.getStop()).append(", ");
            }
        }
        else
        {
            str.append("n/a");
            return str.toString();
        }
        // trims off end of string to get rid of trailing ", "
        return str.substring(0, str.length()-2);
    }
}