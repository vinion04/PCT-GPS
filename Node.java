

import java.util.LinkedList;

public class Node {
    //BASICS
    public String name;
    public LinkedList<Edge> edges;

    //Project3 attributes
    public double lat;
    public double lon;
    public double weight;
    String desc;
    public boolean visited;
    public Node prev;


    //constructor
    public Node(String name){
        this.name = name;
        edges = new LinkedList<>();
        
        this.visited = false;
    }

    public String toString(){
        return this.name;
    }
}
