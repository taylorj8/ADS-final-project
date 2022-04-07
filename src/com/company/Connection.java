// @author Jamie Taylor

package com.company;

public class Connection implements Comparable<Connection> {

    private final int stop;
    private final double cost;

    public Connection(int stop, double cost)
    {
        this.stop = stop;
        this.cost = cost;
    }

    public int getStop() {
        return stop;
    }

    public double getCost() {
        return cost;
    }

    @Override
    public int compareTo(Connection other)
    {
        return Double.compare(this.getCost(), other.getCost());
    }
}