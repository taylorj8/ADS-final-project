package com.company;

public class Trip implements Comparable<Trip> {


    private final int tripId, id, stopSequence;
    private final double distTravelled;
    private final String arrivalTime, departureTime;

    public Trip(int tripId, int id, int stopSequence, double distTravelled, String arrivalTime, String departureTime)
    {
        this.tripId = tripId;
        this.id = id;
        this.stopSequence = stopSequence;
        this.distTravelled = distTravelled;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
    }

    public int getTripId()
    {
        return tripId;
    }

    @Override
    public int compareTo(Trip other)
    {
        return Integer.compare(tripId, other.getTripId());
    }

    @Override
    public String toString()
    {
        // prints all info about trip
        return String.format("Trip ID: %d\nStop ID: %d\nStop sequence: %d\nDistance travelled: %.4f\nArrival time: %s\nDeparture time: %s",
                tripId, id, stopSequence, distTravelled, arrivalTime, departureTime);
    }
}
