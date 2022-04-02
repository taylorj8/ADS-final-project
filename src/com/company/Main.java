//@ author Jamie Taylor

package com.company;

import java.util.Locale;
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
                case "1":       //todo add list of edges
                    boolean validInput = false;
                    int origin = -1, destination = -1;
                    while(!validInput)
                    {
                        System.out.print("Enter the ID of the origin stop: ");
                        try
                        {
                            origin = s.nextInt();
                        }
                        catch(Exception e)
                        {
                            System.out.print("Invalid input\n");
                            continue;
                        }

                        if(network.getIndex(origin) == -1)
                        {
                            System.out.print("Origin ID is invalid\n");
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
                            System.out.print("Invalid input\n");
                            continue;
                        }

                        if(network.getIndex(destination) == -1)
                        {
                            System.out.print("Destination ID is invalid\n");
                        }
                        else
                        {
                            validInput = true;
                            int[] edgeTo = new int[network.getNoStops()];
                            double cost = network.dijkstraSingleDest(origin, destination, edgeTo);
                            int[] path = network.getPath(origin, destination, edgeTo);

                            System.out.printf("The cost from stop %d to stop %d is %.01f, taking the path ",
                                    origin, destination, cost);
                            for(int i = 0; i < path.length-1; i++)
                            {
                                System.out.printf("%d, ", path[i]);
                            }
                            System.out.printf("%d.\n\n", path[path.length-1]);
                        }
                    }
                    break;
                case "2":
                    System.out.print("Enter the search term:");
                    break;
                case "3":
                    System.out.print("Enter the time of arrival: ");
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

