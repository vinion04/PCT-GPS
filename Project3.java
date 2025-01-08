

/*
 * there's probably a better way to read the file using the # of items it provides :/
 */

import java.util.LinkedList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Stack;

public class Project3 {

    //make node list to keep track
    LinkedList<Node> nodes;

    //display toggles
    private static final boolean DEBUG = false;
    private static final boolean DISPLAY = true;

    //constructor
    public Project3(){
        nodes = new LinkedList<>();
    }

    //adds nodes to node list
    public void addNode(String[] values){
        //take read values and assign
        String name = values[0];
        double lon = Double.parseDouble(values[1]);
        double lat = Double.parseDouble(values[2]);
        double weight = Double.parseDouble(values[3]);
        String desc = values[4];

        //create node based off line values
        Node newNode = new Node(name);
        newNode.lat = lat;
        newNode.lon = lon;
        newNode.weight = weight;
        newNode.desc = desc;
        nodes.add(newNode);
        if(DEBUG) {
            System.out.println("New node added: " + nodes);
            System.out.println("");
        }
            
    }

    //add edges to edge list
    private void addEdge(String[] values){
        //take read values and assign
        String node1 = values[0];
        String node2 = values[1];
        String direction = values[2];
        //used to break loop
        boolean found = false;

        /*
         * traverse nodes to find "from" line value that matches
         * traverse nodes again to find "to" line value that matches
         * calculate the weight using haversine
         * add edge from, to
         * if bidirectional, assign weight back to from node
         */
        for(Node from : nodes){
            if(found){
                break;
            }
            if(from.name.equals(node1)){
                for(Node to : nodes){
                    if(to.name.equals(node2)){
                        double weight = haversine(from.lat, from.lon, to.lat, to.lon);
                        from.edges.add(new Edge(from, to, weight));
                        if(DEBUG)System.out.println("FROM " + from.name + " TO " + to.name + " WEIGHT " + weight );
                        found = true;

                        if(direction.equals("1")){
                            continue;
                        }
                        else{
                            to.edges.add(new Edge(to, from, weight));
                            if(DEBUG)System.out.println("FROM " + to.name + " TO " + from.name + " WEIGHT " + weight);
                            
                        }
                    }
                } 
            }
        }
        
    }

    //used to caluclate root between start and end
    private void calculateRoot(String start, String end){
        
        //find start
        for(Node nStart : nodes){
            if(nStart.name.equals(start)){
                //find end
                for(Node nEnd : nodes){
                    if(nEnd.name.equals(end)){
                        //break
                        if(DEBUG) {
                            System.out.println("Calculating root between: " + nStart.name + " and " + nEnd.name);
                        }
                        //run dijkstra's
                        dijkstra(nStart, nEnd);
                        //break out of loop
                        break;
                    }
                } 
            }
        }
    }

    private void readValues(){
        //make scanner to read file
        Scanner keyboard = new Scanner(System.in);

        System.out.println("Enter to read and assign Nodes");
        keyboard.nextLine();

        //reading the map.dat file
        try{
            File data = new File("C:\\Users\\haile\\OneDrive\\Documents\\CIT360\\Graph\\map.dat");  //had to use the whole pathname bc otherwise it wouldn't find the file !
            Scanner scanner = new Scanner(data);
            //to store line input
            String line;
            //to keep track of lines
            int lineNumber = 1;
            //store start and end
            String start;
            String end;
            
            //while scanner has not reached end
            while(scanner.hasNextLine()){
                line = scanner.nextLine();
                if(DEBUG) System.out.println("Line Number: " + lineNumber);
                
                //read lines with Node values
                if(lineNumber <= 26 && lineNumber >= 2){
                    String[] nodeValues = line.split(",");
                    if(DEBUG){
                        for(int i = 0; i < nodeValues.length; i++){
                        System.out.println(nodeValues[i]);
                        }
                    }
                    //add node
                    addNode(nodeValues);
                }
                
                //read lines with Edge values
                if(lineNumber <= 60 && lineNumber >= 28){
                    String[] edgeValues = line.split(" ");
                    if(DEBUG){
                        for(int i = 0; i < edgeValues.length; i++){
                            System.out.println(edgeValues[i]);
                            }
                    }
                    //add edge
                    addEdge(edgeValues);
                }

                //read case lines
                if(lineNumber >= 62){
                    //if line is last in the file (doesnt have next line)
                    if(!scanner.hasNextLine()){
                        String[] routeValues = line.split(" ");
                        start = routeValues[0];
                        end = routeValues[1];
                        calculateRoot(start, end);
                        break;
                    }
                    //calculate root on lines that aren't last (yet)
                    String[] routeValues = line.split(" ");
                    start = routeValues[0];
                    end = routeValues[1];
                    calculateRoot(start, end);
                }
                lineNumber++;   //increase line number
            }
            scanner.close();
            keyboard.close();
        }catch (FileNotFoundException e){   //catch error if file not found
            System.out.println("An error occurred.");
            e.printStackTrace();   
        }
    }

    //Geeksforgeeks copied code for haversine function
    private static double haversine(double lat1, double lon1,
                            double lat2, double lon2)
    {
        // distance between latitudes and longitudes
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
 
        // convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
 
        // apply formulae
        double a = Math.pow(Math.sin(dLat / 2), 2) + 
                   Math.pow(Math.sin(dLon / 2), 2) * 
                   Math.cos(lat1) * 
                   Math.cos(lat2);
        double rad = 6371;
        double c = 2 * Math.asin(Math.sqrt(a));
        return rad * c; //gives distance in kilometers
    }

    //i chose to use dijkstra's algortihm
    public void dijkstra(Node start, Node end){
        if (DISPLAY) {
            System.out.println("");
            System.out.println("START: " + start + " (" + start.desc + ") " + " END: " + end + " (" + end.desc + ")");
        }
        //reset all visited data
        if(DISPLAY) System.out.println("Resetting visited data and distance data");
        resetVisitedData();
        resetDistanceData();
        
        Node curr = start;  //start at start node
        curr.weight = 0;  //doesn't have a distance to itself

        Scanner keyboard = new Scanner(System.in);
        while(curr != null ){
            if (DEBUG) System.out.println("Current node is " + curr + "(" + curr.weight + ")");
            //for if the end was found
            if(curr == end){
                if(DEBUG){
                    System.out.println("Found the end, leaving loop.");
                }
                break;
            }

            //pick current edges one by one
            for(int i = 0; i < curr.edges.size(); i++){
                Edge edge = curr.edges.get(i);
                if(DEBUG) System.out.println("\tEdge " + (i+1) + " " + edge + ". visited? " + edge.end.visited);
                

                if(edge.end.visited){
                    continue;   //skip to next iteration
                }

                double newDistance = curr.weight + edge.weight;
                if(DEBUG){
                     System.out.println("\t\tnewDistance =" + newDistance);
                }
                
                if(newDistance < edge.end.weight){
                    //found new shorter path, update the node on
                    //other side of the edge
                    edge.end.weight = newDistance;
                    edge.end.prev = curr;
                    if(DEBUG) System.out.println("\t\tShorter path found! Updated distance and prev on " +edge.end);
                }
            }

            //clean up
            curr.visited = true;

            //get the next node to process
            curr = getSmallestNodeDistance();
            if(DEBUG) {
                System.out.println("Next node is " +curr);
                keyboard.nextLine();
            }
        }

        Stack<Node> path = new Stack<>();
        curr = end;
        while(curr != null){
            if(curr == start){
                path.push(curr);
                break;
            }
            path.push(curr);
            curr = curr.prev;
        }
        int pathCount = 0;
        Edge currEdge;
        double currDistance = 0;
        while(!path.isEmpty()){
            String s = String.format("%.2f", currDistance);
            if(DISPLAY || DEBUG) {
                Node x = path.pop();
                for(Node n : nodes){
                    if(n == x){
                        System.out.println("Step " + pathCount + ": " + x + ". " + "Distance traveled: " + s + " km");
                        pathCount ++;
                        currEdge = n.edges.get(0);
                        currDistance += currEdge.weight;
                    }
                }
            }
        }  
        keyboard.close();
    }

    private void resetVisitedData(){
        for(Node n : nodes){
            n.visited = false;
        }
    }

    private void resetDistanceData(){
        for(Node n : nodes){
            n.weight = Integer.MAX_VALUE - 1;
        }

    }


    private Node getSmallestNodeDistance(){
        double min = Integer.MAX_VALUE;
        Node candidate = null;
        for(Node n : nodes){
            if(n.weight < min && !n.visited){
                min = n.weight;
                candidate = n;
            }
        }
        return candidate;
    }

    public static void main(String[] args){
        Project3 graph = new Project3();
        graph.readValues();
    }
}
