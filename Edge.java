
public class Edge {
	public final int to;
	public final int cost;
	public Edge(int to, int cost){
		this.to = to;
		this.cost = cost;
	}
	public String toString(){
		return to + " " + cost;
	}
}
