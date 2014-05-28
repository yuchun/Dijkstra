import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;


public class dijikstra {
	public static void main(String[] args){
		dijikstra dij = new dijikstra();
		if(args.length <= 0){
			dij.printHelp();
			return;			
		}
			
		if(args[0].equals("-s") || args[0].equals("-f")){
			boolean usingSimple;
			if(args[0].equals("-s"))
				usingSimple = true;
			else
				usingSimple = false;
			if(args.length != 2){
				dij.printHelp();
				return;				
			}
		    BufferedReader reader;
		    try {
				reader = new BufferedReader(new FileReader(args[1]));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		    if(dij.initializeGraphByFile(reader) == false)
		    	return;
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			System.out.println(dij.graph.toString());
			if(usingSimple == true){
				int[] dists = dij.DijikstraSimple();
				for(int i=0; i<dists.length; i++)
					System.out.println(dists[i]);
			}else{
				int[] dists = dij.DijikstraFibonacci();
				for(int i=0; i<dists.length; i++)
					System.out.println(dists[i]);
			}
		}else if(args[0].equals("-r")){
			if(args.length != 4){
				dij.printHelp();
				return;				
			}
		
			System.out.println(args[1] + " " + args[2]);
			int nofv = Integer.parseInt(args[1]);
			int source = Integer.parseInt(args[3]);
			if(source >= nofv){
				dij.printHelp();
				return;
			}
			double density = Double.parseDouble(args[2]);
			if(density < 0 || density > 100){
				dij.printHelp();
				return;
			}
			if(density == 100)
				dij.genCompleteGraph(nofv, source);
			else{
				int nofe = (int) (density/100 * nofv * (nofv-1)/2);
				if(nofe < nofv - 1){
					System.out.println("Too few edges, cannot be connected graph");
					return;
				}
				System.out.println(nofv + " " + nofe);
				dij.genGraphRandom(nofv, nofe, source);
			}
			//System.out.println(dij.graph.toString());
			
			int[] distF;
			int[] distS;
			
			long startTime = System.currentTimeMillis();
			distS = dij.DijikstraSimple();
			System.out.println("Simple test: " + (System.currentTimeMillis() - startTime));
			
			startTime = System.currentTimeMillis();
			distF = dij.DijikstraFibonacci();

			System.out.println("Fibonacci test: " + (System.currentTimeMillis() - startTime));	
/*
			for(int i=0; i<distF.length; i++){
				if(distF[i] != distS[i])
					System.out.println("Error at " + i + " " + distF[i] + " vs " + distS[i]);
			}
*/			return;
		}else{
			dij.printHelp();
			return;
		}
	}
	
	public dijikstra(){
		
	}
	
	public int[] DijikstraSimple(){
		return SimpleScheme.DijikstraSimple(graph, source);
	}
	
	public int[] DijikstraFibonacci(){
		return FibonacciHeap.DijikstraFibonacci(graph, source);
	}
	
	private void genCompleteGraph(int nofv,int source){
		Random random = new Random();

		numOfVertex = nofv;
		numOfEdge = nofv*(nofv-1)/2;
		this.source = source;
		graph = new Graph(numOfVertex, numOfEdge);
		
		for(int i=0; i<nofv-1; i++){
			for(int j=i+1; j<nofv; j++){
				int cost = random.nextInt(maxCost) + 1;
				graph.addEdge(i, j, cost);
				graph.addEdge(j, i, cost);
			}
		}
	}

	private void genGraphRandom(int nofv, int nofe, int source){

		if(nofe < nofv*(nofv-1)/2/2){

			Random random = new Random();

			numOfVertex = nofv;
			numOfEdge = nofe;
			this.source = source;

			while(true){
				ArrayList<ArrayList<Integer>> pool = new ArrayList<ArrayList<Integer>>();
				for(int i=0; i<nofv; i++){
					ArrayList<Integer> list = new ArrayList<Integer>();
					for(int j=i+1; j<nofv; j++)
						list.add(j);
					pool.add(list);
				}

				graph = new Graph(numOfVertex, numOfEdge);

				/*Each vertex has an edge, add the possibility of connectivity*/
				for(int i=0; i<nofv-1; i++){
					for(;;){
						int v2 = random.nextInt(pool.get(i).size());
						int cost = random.nextInt(maxCost)+1;
						if(generateOneEdge(i, pool.get(i).remove(v2), cost) == true)
							break;
					}
				}
				for(int i=0; i<nofe - nofv; i++){
					for(;;){
						int v1 = random.nextInt(nofv);
						if(pool.get(v1).size() == 0)
							continue;
						int v2 = random.nextInt(pool.get(v1).size());
						int cost = random.nextInt(maxCost)+1;
						if(generateOneEdge(v1, pool.get(v1).remove(v2), cost) == true)
							break;
					}

				}
				System.out.print("check connectivity...");
				if(graph.isConnected()){
					System.out.println("Passed");
					break;
				}
				System.out.println("Failed");
			}
		}else{
			while(true){
				Random random = new Random();
				genCompleteGraph(nofv, source);
				int remaining = nofv*(nofv-1)/2;

				while(remaining > nofe){
					int delete = random.nextInt(remaining);
					int v1=0, v2=0;
					int j=0;
					for(int i=0; i<graph.getVertexNum(); i++){
						j += graph.getVertex(i).size();
						if(j > delete){
							j -= graph.getVertex(i).size();
							v1 = i;
							v2 = delete - j;
							if(v2 > graph.getVertex(i).size()-1)
								v2 = graph.getVertex(i).size()-1;
							graph.getVertex(v1).remove(v2);
							break;
						}
					}
					remaining--;
				}
				System.out.print("check connectivity...");
				if(graph.isConnected()){
					System.out.println("Passed");
					break;
				}
				System.out.println("Failed");
			}
		}
	}

	private boolean generateOneEdge(int v1, int v2, int cost){
		if(v1 == v2)
			return false;

		LinkedList<Edge> edges = graph.getVertex(v1);
		for(Edge e: edges){
			if(e.to == v2){
				return false;
			}
		}

		graph.addEdge(v1, v2, cost);
		graph.addEdge(v2, v1, cost);

		return true;
	}

		
	private boolean initializeGraphByFile(BufferedReader reader){
		String str;
		try {
			str = reader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
		int source = Integer.parseInt(str);
		this.source = source;
		
		try {
			str = reader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
		String[] tokens = str.split("\\s+");
		numOfVertex = Integer.parseInt(tokens[0]);
		numOfEdge = Integer.parseInt(tokens[1]);
		
		if(source >= numOfVertex)
			return false;
		graph = new Graph(numOfVertex, numOfEdge);
		int cntEdge = 0;
		try {
			while((str = reader.readLine()) != null) {
				
				tokens = str.split("\\s+");
				int v1 = Integer.parseInt(tokens[0]);
				int v2 = Integer.parseInt(tokens[1]);
				int cost = Integer.parseInt(tokens[2]);
			    
				if(v1 >= numOfVertex || v2 >= numOfVertex)
					return false;
				cntEdge++;
				if(cntEdge > numOfEdge)
					return false;
				graph.addEdge(v1, v2, cost);
				graph.addEdge(v2, v1, cost);

			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}

		if(graph.isConnected() == false)
			return false;
		return true;
	}
	
	private void printHelp(){
		System.out.println("Invalid format of command:");
		System.out.println("\tdijikstra -r n d x");
		System.out.println("or");
		System.out.println("\tdijikstra -s file-name");
		System.out.println("or");
		System.out.println("\tdijikstra -f file-name");	
	} 
	
	private Graph graph = null;
	private int source;
	private int numOfVertex;
	private int numOfEdge;
	private static int maxCost = 1000;
}
