

public class Edge {
    public double weight;
    public Node start;
    public Node end;

    public Edge(Node start, Node end, double w){
        this.start = start;
        this.end = end;
        this.weight = w;
    }

    public String toString(){
        return this.start + " - " + this.weight + " -> " + this.end;
    }
}
