// @author Jamie Taylor

package com.company;

// basically a key-value pair
public class Stop implements Comparable<Stop>{

    private final int id;
    private final String name;

    Stop(int id, String name)
    {
       this.id = id;
       this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
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