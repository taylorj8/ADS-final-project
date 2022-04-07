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
            System.out.print("What would you like to do?\n" +
                    "1. Find shortest path between two stops\n" +
                    "2. Search for stops by name\n" +
                    "3. Search for trips by arrival time\n" +
                    "Enter the appropriate number or quit to exit: ");
            String userInput = s.nextLine();

            switch(userInput.toLowerCase())
            {
                case "1":
                    int origin = -1, destination = -1;
                    boolean validInput = false;
                    while(!validInput)
                    {
                        System.out.print("Enter the ID of the origin stop: ");
                        if(s.hasNextInt())
                        {
                            origin = s.nextInt();
                            if(network.getIndex(origin) == -1)
                            {
                                System.out.printf("Stop %d is not in the network\n", origin);
                            }
                            else
                            {
                                validInput = true;
                            }
                        }
                        else
                        {
                            System.out.println("Invalid input: enter an integer\n");
                            s.nextLine();   // clears input
                        }
                    }

                    validInput = false;
                    while(!validInput)
                    {
                        System.out.print("Enter the ID of the destination stop: ");
                        if(s.hasNextInt())
                        {
                            destination = s.nextInt();
                            if(network.getIndex(destination) == -1)
                            {
                                System.out.printf("Stop %d is not in the network\n", destination);
                            }
                            else
                            {
                                validInput = true;
                                if(origin == destination)
                                {
                                    System.out.println("The cost from a stop to itself is 0.0.\n");
                                }
                                else
                                {
                                    int[] edgeTo = new int[network.getNoStops()];
                                    double cost = network.dijkstraSingleDest(origin, destination, edgeTo);

                                    //if the cost == max_value, it means no path exists
                                    if(cost != Double.MAX_VALUE)
                                    {
                                        int[] path = network.getPath(origin, destination, edgeTo);

                                        System.out.printf("The lowest cost from stop %d to stop %d is %.01f, taking the path %d, ",
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
                        }
                        else
                        {
                            System.out.println("Invalid input: enter an integer\n");
                            s.nextLine(); //clears input
                        }
                    }
                    s.nextLine();   //clears input
                    break;
                case "2":
                    System.out.print("Enter the search term: ");
                    String searchTerm = s.nextLine().toLowerCase();     //converts to lowercase so matching ignores the case
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
                        arrivalTime = s.nextLine();

                        // returns -1 if format invalid, -2 if numbers out of range
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
                    Trip[] timedStops = network.getTripsByTime(time);

                    if(timedStops != null)
                    {
                        System.out.printf("Here are the trips that arrive at %s:\n\n",
                                arrivalTime);
                        for(Trip stop : timedStops)
                        {
                            System.out.print(stop.toString() + "\n\n");
                        }
                    }
                    else
                    {
                        System.out.println("There are no trips that match your search.\n");
                    }
                    break;
                case "quit":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid input.\n");
            }
        }
        System.out.print("End of program");
    }
}

