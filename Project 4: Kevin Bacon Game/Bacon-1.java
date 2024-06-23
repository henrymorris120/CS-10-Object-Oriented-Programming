import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.Scanner;

/**
 * Kevin Bacon Game
 * PS4
 * @author Henry Morris and Izzy Axinn, Dartmouth CS 10, Spring 2022
 */
public class Bacon {
    Map<String, String> actorIDToActors;
    Map<String, String> movieIDToMovies;
    Map<String, ArrayList<String>> movieIDToActorsID;
    Graph<String, String> actorMovieGraph;
    String centerOfUniverse;
    Graph<String, String> baconGraph;
    Boolean play = true;
    Scanner in = new Scanner(System.in);
    int numActors;
    int actorsConnected;
    int min = 0;
    int max = 0;


    /**
     * reads three files set each return to map initialized above
     * @throws Exception
     */
    public void readFiles() throws Exception {
        actorIDToActors = readFile1("bacon/actors.txt");
        movieIDToMovies = readFile1("bacon/movies.txt");
        movieIDToActorsID = readFile2("bacon/movie-actors.txt");
    }

    /**
     * return map with key string and value string after using BufferedReader to read a file
     * @param filename              name of file being read
     * @return                      map based on files
     * @throws Exception
     */
    public Map<String, String> readFile1(String filename) throws Exception{
        Map<String, String> map = new HashMap<String, String>();
        BufferedReader in = new BufferedReader(new FileReader(filename));
        String currentLine = in.readLine();
        while (currentLine != null) {
            String[] data = currentLine.split("\\|");
            map.put(data[0], data[1]);
            currentLine = in.readLine();
        }
        in.close();
        return map;
    }

    /**
     * return map with key string and value array list after using BufferedReader to read a file
     * @param filename              name of file being read
     * @return                      map based on files
     * @throws Exception
     */
    public HashMap<String, ArrayList<String>> readFile2(String filename) throws Exception {
        HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
        BufferedReader in = new BufferedReader(new FileReader(filename));
        String currentLine = in.readLine();
        String previousMovie = null;
        while (currentLine != null) {
            String[] data = currentLine.split("\\|");
            String currentMovie = data[0];
            if (!currentMovie.equals(previousMovie)) {
                map.put(currentMovie, new ArrayList<String>());
                map.get(currentMovie).add(data[1]);
            }
            else {
                map.get(currentMovie).add(data[1]);
            }
            previousMovie = currentMovie;
            currentLine = in.readLine();
        }
        in.close();
        return map;
    }

    /**
     * create graph connecting every actor through movies as edges
     * @throws Exception
     */
    public void createGraph() throws Exception{
        readFiles();
        Graph<String, String> graph = new AdjacencyMapGraph<String, String>();
        for (String actor: actorIDToActors.values()) {
            graph.insertVertex(actor);
        }
        // Iterate through every movie
        for (String movieID: movieIDToActorsID.keySet()) {
            // create array list for every actor in movieID
            ArrayList<String> actorsInThisMovie = new ArrayList<String>();
            for (String actor : movieIDToActorsID.get(movieID)) {
                actorsInThisMovie.add(actor);
            }
            // draw undirected edged between each actor in the list just made and every other actor
            for (int i=0; i< actorsInThisMovie.size(); i++) {
                for (int j= i + 1; j < actorsInThisMovie.size(); j++) {
                    graph.insertUndirected(actorIDToActors.get(actorsInThisMovie.get(i)),actorIDToActors.get(actorsInThisMovie.get(j)),movieIDToMovies.get(movieID));

                }
            }
        }
        numActors = graph.numVertices();
        actorMovieGraph = graph;
    }

    /**
     * print Instructions for game and the opening line saying Kevin Bacon is the center of universe
     */
    public void printCommandsAndOpeningLine() {
        // print commands
        System.out.println("Commands:");
        System.out.println("c,<#>: list top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation");
        System.out.println("d,<low> <high>: list actors sorted by degree, with degree between low and high");
        System.out.println("i: list actors with infinite separation from the current center");
        System.out.println("p,<name>: find path from <name> to current center of the universe");
        System.out.println("s,<low> <high>: list actors sorted by non-infinite separation from the current center, with separation between low and high");
        System.out.println("u,<name>: make <name> the center of the universe");
        System.out.println("q: quit game" + "\n");
        newCenter("Kevin Bacon");
    }

    /**
     * print there is new universe plus connected actors and average separation of actor from new center
     */
    public void newUniverseCenterPrint() {

        actorsConnected = baconGraph.numVertices() - 1;
        double avgSep = PS4GraphLib.averageSeparation(baconGraph, centerOfUniverse);
        System.out.println(centerOfUniverse + " is now the center of the acting universe, connected to " + actorsConnected + " actors with average separation " + avgSep + "\n");
    }

    /**
     * if center is different from current center of universe, then recalculate baconGraph, centerOfUniverse
     * and call method to print the news
     * @param center           new center of universe
     */
    public void newCenter(String center) {
        if (center != centerOfUniverse) {
            baconGraph = PS4GraphLib.bfs(actorMovieGraph,center);
            centerOfUniverse = center;
            newUniverseCenterPrint();
        }
    }

    /**
     * use priority queue to create a sorted list of each vertex's distance the the root in baconGraph
     * @return          list sorted by actors closest to the center of universe to farthest
     */
    public ArrayList<String> actorSepFromCenterLowToHigh () {
        // create priority queue and add actors to it
        PriorityQueue<String> pq = new PriorityQueue<String>((String a1, String a2) -> PS4GraphLib.getPath(baconGraph, a1).size() - PS4GraphLib.getPath(baconGraph, a2).size());
        for (String actor: baconGraph.vertices()) {
            int pathSize = PS4GraphLib.getPath(baconGraph, actor).size() - 1;
            if(pathSize >= min && pathSize <= max) {
                pq.add(actor);
            }
        }
        // create array list and remove each actor from pq and add to it
        ArrayList<String> sorted = new ArrayList<String>();
        while (!pq.isEmpty()) {
            sorted.add(pq.poll());
        }
        return sorted;
    }

    /**
     * using a priority queue, return a list of all actors sorted by degree (in or out (instruction were unclear on this point so
     * this is what I decided to do)
     * @return          list sorted by degrees from least to greatest
     */
    public ArrayList<String> actorsSortedDegrees () {
        // create priority queue and add actors to it
        PriorityQueue<String> pq = new PriorityQueue<String>((String a1, String a2) -> actorMovieGraph.inDegree(a1) - actorMovieGraph.inDegree(a2));
        for (String actor: actorMovieGraph.vertices()) {
            if(actorMovieGraph.inDegree(actor) >= min && actorMovieGraph.inDegree(actor) <= max) {
                pq.add(actor);
            }
        }
        // create array list and remove each actor from pq and add to it
        ArrayList<String> sorted = new ArrayList<String>();
        while (!pq.isEmpty()) {
            sorted.add(pq.poll());
        }
        return sorted;
    }

    /**
     * find the average separation for when the root is the center of the universe and return it as a double
     * @param root
     * @return average separation for when the root is the center of the universe
     */
    public double getAvgSep(String root){
        Graph<String, String> tempPathTree = PS4GraphLib.bfs(actorMovieGraph, root);
        return PS4GraphLib.averageSeparation(tempPathTree, root);
    }

    /**
     * create and return using a priority queue a list of actors sorted by how good to how bad of center of universe they would be
     * @return sorted list from lowest average separations to greatest
     */
    public ArrayList<String> actorsSortedAvgSep () {
        // create priority queue and add actors to it
        PriorityQueue<String> pq = new PriorityQueue<String>((String a1, String a2) -> (int)(Math.signum(getAvgSep(a1) - getAvgSep(a2))));
        for (String actor : baconGraph.vertices()) {
                pq.add(actor);
        }
        // create array list and remove each actor from pq and add to it
        ArrayList<String> sorted = new ArrayList<String>();
        while (!pq.isEmpty()) {
            sorted.add(pq.poll());
        }
        return sorted;
    }

    /**
     * return list of actors not in current baconGraph
     */
    public void infiniteDegree() {
        System.out.println(PS4GraphLib.missingVertices(actorMovieGraph, baconGraph));
    }


    /**
     * prints out the path describing how to go from the root actor to the parameter actor
     * @param actor             where the path to the root of baconGraph starts
     */
    public void findPath(String actor) {
        // check if actor is a vertex in baconGraph
        if (baconGraph.hasVertex(actor)) {
            System.out.println(centerOfUniverse + " game >");
            System.out.println("p " + actor);
            int baconNumber = PS4GraphLib.getPath(baconGraph, actor).size() - 1;
            System.out.println(actor + "'s number is " + baconNumber);
            String current = actor;
            // continue until current is the root of baconGraph
            while (baconGraph.outDegree(current) != 0) {
                for (String next: baconGraph.outNeighbors(current)) {
                    String movie = baconGraph.getLabel(current, next);
                    System.out.println(current + " appeared in [" + movie + "] with " + next);
                    current = next;
                }
            }
        }
        else {
            System.out.println("No path found");
        }

    }

    /**
     * intializes game and continues until play equal false
     */
    public void start() {
        while (play) {
            play = readScanner();
            min = 0;
            max = Integer.MAX_VALUE;
            if(play){System.out.println("Processed finished. Enter a new command:" );}
        }
    }

    /**
     * reads Scanner and uses what input writes to play Bacon Game, returns a boolean that if false ends the game
     * @return      Boolean
     */
    public Boolean readScanner() {
        String action = in.nextLine();
        String[] splitAction = action.split(",");
        // quits game by returning false
        if (action.equals("q")) {
            return false;
        }
        // prints out actors missing from baconGraph
        // returns true
        else if (action.equals("i")) {
            infiniteDegree();
            return true;
        }
        // sets new center of universe, returns invalid input not if inputted in correctly
        // returns true
        else if (splitAction[0].equals("u") && splitAction.length == 2) {
            if (actorMovieGraph.hasVertex(splitAction[1])) {
                newCenter(splitAction[1]);
                return true;
            }
            System.out.println("invalid input: " + action);
            return true;
        }
        // prints out path based on input, returns invalid input if not inputted in correctly
        // returns true
        else if (splitAction[0].equals("p") && splitAction.length == 2) {
            if (actorMovieGraph.hasVertex(splitAction[1])) {
                findPath(splitAction[1]);
                return true;
            }
            System.out.println("invalid input: " + action);
            return true;
        }
        // prints out range of actors in sorted average separation list based on input
        // prints invalid input if not inputted in correctly
        // returns true
        else if (splitAction[0].equals("c") && splitAction.length == 2) {
            try {
                int num = Integer.parseInt(splitAction[1]);
                if (Math.abs(num) <= (actorsConnected + 1) && num != 0) {
                    String list = "";
                    ArrayList<String> sortedCenters = actorsSortedAvgSep();
                    if (num > 0) {
                        for (int i = 0; i < num; i++) {
                            if (i == num - 1) {
                                list += sortedCenters.get(i);
                            }
                            else {
                                list += sortedCenters.get(i) + ", ";
                            }
                        }
                    }
                    else {
                        for (int i = 0; i > num; i--) {
                            if (i ==  num + 1) {
                                list += sortedCenters.get(actorsConnected + i);
                            }
                            else {
                                list += sortedCenters.get(actorsConnected + i) + ", ";
                            }
                        }

                    }
                    System.out.println(list);
                    return true;
                }
            }
            catch (Exception e) {
                System.out.println("invalid input: " + action);
                return true;
            }
        }
        // prints out range of actors from list, sorted by in-degrees for each actor vertex, based on input
        // prints invalid input if not inputted in correctly
        // returns true
        else if (splitAction[0].equals("d") && splitAction.length == 2) {
            try {
                String temp = splitAction[1];
                String [] tempSplit = temp.split(" ");
                min = Integer.parseInt(tempSplit[0]);
                max = Integer.parseInt(tempSplit[1]);
                ArrayList<String> actors = actorsSortedDegrees();
                String str = "";
                if(max > min) {
                    for (String actor : actors) {
                        str += actor;
                        str += ", ";
                    }
                    str = str.substring(0,str.length()-2);
                    System.out.println(str);
                    return true;
                }
                else {
                    System.out.println("invalid input: " + action);
                    return true;
                }
            }
            catch (Exception e) {
                System.out.println("invalid input: " + action);
                return true;
            }

        }
        // prints out range of actors, in a list sorted by separation from center of universe, based on input
        // prints invalid input if not inputted in correctly
        // returns true
        else if (splitAction[0].equals("s") && splitAction.length == 2) {
            try {
                String temp = splitAction[1];
                String [] tempSplit = temp.split(" ");
                min = Integer.parseInt(tempSplit[0]);
                max = Integer.parseInt(tempSplit[1]);
                String str = "";
                ArrayList<String> actors = actorSepFromCenterLowToHigh();
                if(max > min) {
                    for (String actor : actors) {
                        str += actor;
                        str += ", ";
                    }
                    str = str.substring(0,str.length()-2);
                    System.out.println(str);
                    return true;
                }
                else {
                    System.out.println("invalid input: " + action);
                    return true;
                }
            }
            catch (Exception e) {
                System.out.println("invalid input: " + action);
                return true;
            }
        }
        System.out.println("invalid input: " + action);
        return true;
    }

    public static void main(String[] args) throws Exception{
        Bacon b = new Bacon();
        b.createGraph();
        b.printCommandsAndOpeningLine();
        b.start();
    }
}
