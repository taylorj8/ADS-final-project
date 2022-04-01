// author Jamie Taylor

package com.company;

public class Connection implements Comparable<Connection> {

    private final int stop;
    private final double cost;

    public Connection(int destination, double cost)
    {
        this.stop = destination;
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