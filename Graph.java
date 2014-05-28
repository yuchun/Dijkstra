import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;


public class Graph {
	/*
	 * each element of vertex is one vertex in the graph, and it is a LinkedList comprised with the Edges 
	 * start from this vertex, the index of it in the ArrayList is its index in the graph. 
	 * */
	private ArrayList<LinkedList<Edge>> vertex;
	int vertexNum;
	int edgeNum;
	
	public Graph(int vertexNum, int edgeNum){
		vertex = new ArrayList<LinkedList<Edge>>();
		this.vertexNum = vertexNum;
		this.edgeNum = edgeNum;
		for(int i=0; i<vertexNum; i++)
			vertex.add(new LinkedList<Edge>());
	}
	
	public String toString(){
		return vertex.toString();
	}
	
	public void addEdge(int v, int to, int cost){
		vertex.get(v).add(new Edge(to, cost));
	}
	
	public LinkedList<Edge> getVertex(int v){
		return vertex.get(v);
	}
	
	public int getVertexNum(){
		return vertexNum;
	}
	
	public int getEdgeNum(){
		return edgeNum;
	}
	
	public int dfs(boolean verbose){
		int num = 0;
		HashSet<Integer> hashSet = new HashSet<Integer>();
		Stack<Integer> stack = new Stack<Integer>();
		stack.add(0);
		hashSet.add(0);
		while(stack.empty() == false){
			Integer cur = stack.pop();
			if(verbose)
				System.out.print(cur + " ");
			num++;
			LinkedList<Edge> edges = vertex.get(cur);
			for(Edge e: edges){
				if(hashSet.contains(e.to))
					continue;
				hashSet.add(e.to);
				stack.add(e.to);
			}
		}
		if(verbose)
			System.out.println();
		return num;
	}
	
	public boolean isConnected(){
		if(dfs(false) < vertexNum)
			return false;
		return true;
	}
}
