//@ author Jamie Taylor

package com.company;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Network network = new Network("stops.txt", "transfers.txt", "stop_times.txt");

        Scanner s = new Scanner(System.in);
        boolean running = true;
        while(running)
        {
            System.out.println("What would you like to do? Enter the appropriate number or quit to exit:\n" +
                    "1. Find shortest path between two stops\n" +
                    "2. Search for stops by name\n" +
                    "3. Search for stops by arrival time\n");
            String userInput = s.next();

            switch(userInput.toLowerCase())
            {
                case "1":
                    int origin = -1, destination = -1;
                    boolean validInput = false;
                    while(!validInput)
                    {
                        System.out.print("Enter the ID of the origin stop: ");
                        try
                        {
                            origin = s.nextInt();
                        }
                        catch(Exception e)
                        {
                            System.out.println("Invalid input");
                            continue;
                        }

                        if(network.getIndex(origin) == -1)
                        {
                            System.out.println("Origin ID is invalid");
                        }
                        else
                        {
                            validInput = true;
                        }
                    }

                    validInput = false;
                    while(!validInput)
                    {
                        System.out.print("Enter the ID of the destination stop: ");
                        try
                        {
                            destination = s.nextInt();
                        }
                        catch(Exception e)
                        {
                            System.out.println("Invalid input: enter an integer");
                            continue;
                        }

                        if(network.getIndex(destination) == -1)
                        {
                            System.out.println("Destination ID is invalid");
                        }
                        else
                        {
                            validInput = true;
                            int[] edgeTo = new int[network.getNoStops()];
                            double cost = network.dijkstraSingleDest(origin, destination, edgeTo);
                            if(cost != Double.MAX_VALUE)
                            {
                                int[] path = network.getPath(origin, destination, edgeTo);

                                System.out.printf("The cost from stop %d to stop %d is %.01f, taking the path %d, ",
                                        origin, destination, cost, origin);
                                for(int stop : path)
                                {
                                    System.out.printf("%d, ", stop);
                                }
                                System.out.printf("%d.\n\n", destination);
                            }
                            else
                            {
                                System.out.printf("No path exists between stop %d and stop %d.\n\n", origin, destination);
                            }
                        }
                    }
                    break;
                case "2":
                    System.out.print("Enter the search term: ");
                    String searchTerm = s.next().toLowerCase();
                    Stop[] matchingStops = network.getMatchingStops(searchTerm);

                    if(matchingStops.length != 0)
                    {
                        System.out.print("These are the stops that match:\n\n");
                        for(Stop stop : matchingStops)
                        {
                            System.out.print(stop.toString() + "\n\n");
                        }
                    }
                    else
                    {
                        System.out.print("There are no stops that match your search.\n\n");
                    }
                    break;
                case "3":
                    boolean inputValid = false;
                    String arrivalTime = "";
                    int time = -1;
                    while(!inputValid)
                    {
                        System.out.print("Enter the time of arrival (formatted as hh:mm:ss): ");
                        arrivalTime = s.next();
                        time = network.convertTime(arrivalTime);

                        if(time == -1)
                        {
                            System.out.println("Invalid input");
                        }
                        else if(time == -2)
                        {
                            System.out.println("Numbers out of range");
                        }
                        else
                        {
                            inputValid = true;
                        }
                    }
                    Stop[] timedStops = network.getStopsByTime(time);

                    if(timedStops != null)
                    {
                        System.out.printf("These are the the last stop in each of the trips that arrive at %s:\n\n",
                                arrivalTime);
                        for(Stop stop : timedStops)
                        {
                            System.out.print(stop.toString() + "\n\n");
                        }
                    }
                    else
                    {
                        System.out.print("There are no stops that match your search.\n\n");
                    }
                    break;
                case "quit":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid input. ");
            }
        }
        System.out.print("End of program");
    }
}

