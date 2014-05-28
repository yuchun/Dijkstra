import java.util.ArrayList;
import java.util.LinkedList;


public class SimpleScheme {
	public static int[] DijikstraSimple(Graph g, int source){
		int nofv = g.getVertexNum();
		int nofe = g.getEdgeNum();
		boolean[] searched = new boolean[nofv]; 
		int[] dist = new int[nofv];
		for(int i=0; i<nofv; i++){
			if(i == source){
				dist[i] = 0;
			}else{
				dist[i] = Integer.MAX_VALUE;

			}
			searched[i] = false;
		}
		
		for(int i=0; i<nofv-1; i++){
			int min = Integer.MAX_VALUE;
			int minIndex = -1;
			for(int j=0; j<nofv; j++){
				if(searched[j] == false && dist[j] < min){
					min = dist[j];
					minIndex = j;
				}
			}
			if(minIndex == -1){
				System.out.println("Impossible");
				break;
			}
			searched[minIndex] = true;
			LinkedList<Edge> edges = g.getVertex(minIndex);
			for(Edge e:edges){
				int newDist = Integer.MAX_VALUE;
				if(dist[minIndex] < Integer.MAX_VALUE){
					newDist = dist[minIndex] + e.cost;
				}
				if(dist[e.to] > newDist)
					dist[e.to] = newDist;
			}
		}

		return dist;

	}
}
