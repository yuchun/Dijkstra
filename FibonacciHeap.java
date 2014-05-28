import java.util.*;
import java.util.Map.Entry;

public class FibonacciHeap
{
    /*
     * The implementation of Dijikstra Algorithm using FibonacciHeap
     * @param graph
     * @param index of source node
     * */
	public static int[] DijikstraFibonacci(Graph g, int source){
		/*
		 * heap is used to find the shortest path every time using removeMin operation
		 * */
		FibonacciHeap heap = new FibonacciHeap();
		
		/*
		 * map is used for mapping the FibonacciNodes in the FibonacciHeap with a vertex index,
		 * and we can find an element in the heap and do heap operations for given vertex
		 * */
		FibonacciNode[] map = new FibonacciNode[g.getVertexNum()];
		
		/*
		 * searched is used to store the found vertex
		 * */
		int[] searched = new int[g.getVertexNum()];
		for(int i=0; i<g.getVertexNum(); i++){
			FibonacciNode node = new FibonacciNode(i, Integer.MAX_VALUE);
			map[i] = node;
			heap.insert(node);
			searched[i] = -1;
		}

		heap.decreaseKey(map[source], 0);
		
		while(heap.empty() == false){
			FibonacciNode node = heap.removeMin();

			searched[node.getVertexNum()] = node.getDistance();

			/*
			 * check the cost of all the edges start from the newly deleted node, and decreaseKey if necessarily
			 * */
			LinkedList<Edge> edges = g.getVertex(node.getVertexNum());
			
			for(Edge e: edges){
				/*
				 * already searched, forget about it
				 * */
				if(searched[e.to] != -1)
					continue;

				int newCost = node.getDistance() + e.cost;
				FibonacciNode childNode = map[e.to];

				if(childNode.getDistance() > newCost){
					heap.decreaseKey(childNode, newCost);
				}
			}
		}
		return searched;
	}
	
	
	/*
	 * Examine whether the heap is empty or not
	 * @return true for empty, false for not empty
	 * */
    public boolean empty()
    {
        return minNode == null;
    }

    /*
     * If theNode is not a root and new key < parent's key, remove subtree rooted at theNode from 
     * its doubly linked sibling list.
     * Insert into top-level list.
     * @param node:the node to be decreaseKey
     * @param distance: the new key value of the node
     * */
    public void decreaseKey(FibonacciNode node, int distance)
    {
        node.setDistance(distance);
        FibonacciNode parent = node.parent;

        if ((parent != null) && (node.getDistance() < parent.getDistance())) {
        	node.remove();
        	minNode.addSibling(node);
            cascadingCut(parent);
        }

        if (node.getDistance() < minNode.getDistance()) {
            minNode = node;
        }
    }

    /*
     * Insert a node to the FibonacciHeap
     * @param node: the node to be inserted into the heap
     * */
    public void insert(FibonacciNode node)
    {

    	if(minNode == null)
    		minNode = node;
    	else{
            minNode.addSibling(node);

            if (node.getDistance() < minNode.getDistance()) {
                minNode = node;
            }
        }

        count++;
    }

    /*
     * Poll the min value of the heap
     * @return the minNode
     * */
    public final FibonacciNode poll()
    {
        return minNode;
    }

    /*
     * Remove the min node from the heap, will do pairwise combine after removal
     * @return the minNode
     * */
    public FibonacciNode removeMin()
    {
    	if(minNode == null)
    		return null;
    	
        FibonacciNode rNode = minNode;

        int numOfChildren = rNode.degree;
        FibonacciNode node = rNode.child;
        FibonacciNode right = null;

        // for each child of z do...
        while (numOfChildren > 0) {
            right = node.right;

            node.remove();
            minNode.addSibling(node);

            node = right;
            numOfChildren--;
        }

        rNode.left.right = rNode.right;
        rNode.right.left = rNode.left;

        if (rNode == rNode.right) {
            minNode = null;
        } else {
            minNode = rNode.right;
            pairwiseCombine();
        }

        count--;
        return rNode;
    }

    /*
     * Get the number of nodes in the heap
     * */
    public int size()
    {
        return count;
    }

    /*
     * do cascadingCut after decreaseKey operation
     * @param node: the parent of the node that decreased key
     * */
    private void cascadingCut(FibonacciNode node)
    {
        FibonacciNode parent = node.parent;

        if (parent != null) {	
            if (node.childCut == false) {
                node.childCut = true;
            } else {
                node.remove();
                minNode.addSibling(node);
                cascadingCut(parent);
            }
        }
    }

	/*
	 * combine each tree with same degree
	 * @return the min node(should not change)
	 * */
    private void pairwiseCombine()
    {
    	/*The total number of FibonacciTree in the heap is Ceiling(log2(count))*/
        int arraySize = 1;
        int cnt = 1;
        while(cnt < count){
        	arraySize++;
        	cnt*=2;
        }
        arraySize++;
    	
        int numRoots = 0;
        FibonacciNode node = minNode;

        if (node != null) {
            numRoots++;
            node = node.right;

            while (node != minNode) {
                numRoots++;
                node = node.right;
            }
        }

        /*
         * Ironically, the speed of ArrayList is faster than HashMap, or else it is easy and elegant to
         * implement this piece of code using HashMap
         * */
        ArrayList<FibonacciNode> array = new ArrayList<FibonacciNode>();

        for (int i = 0; i < arraySize; i++) {
            array.add(null);
        }

        // For each node in root list do...
        while (numRoots > 0) {
            // Access this node's degree..
            int degree = node.degree;
            FibonacciNode next = node.right;

            // ..and see if there's another of the same degree.
            while (true) {
                FibonacciNode another = array.get(degree);
                if (another == null) {
                    break;
                }

                if (node.distance > another.distance){
                	another.addChild(node);
                	node = another;
                }else
                	node.addChild(another);

                array.set(degree, null);
                degree++;
            }
            
            array.set(degree, node);
            
            node = next;
            numRoots--;
        }

        minNode = null;

        for (int i = 0; i < arraySize; i++) {
            node = array.get(i);
            if (node == null) {
                continue;
            }
            
            if(minNode == null)
            	minNode = node;
            else{
                node.remove();
                minNode.addSibling(node);
                if (node.distance < minNode.distance) {
                    minNode = node;
                }
            }
        }
    }

	private static class FibonacciNode
	{

	    public FibonacciNode(int vertexNum, int distance)
	    {
	        right = this;
	        left = this;
	        child = null;
	        parent = null;
	        degree = 0;
	        childCut = false;
	        this.vertexNum = vertexNum;
	        this.distance = distance;
	    }
	
		/*
		 * Add a sibling into the tree
		 * */
		public void addSibling(FibonacciNode node){
            node.left = this;
            node.right = this.right;
            this.right = node;
            node.right.left = node;
			
			if(parent != null){
				parent.degree++;
				node.parent = this.parent;
			}
		}
		
		/*remove its right sibling from the tree*/
	    public void removeSibling() {
	        left.right = right;
	        right.left = left;
	        right = this;
	        left = this;
			if(parent != null)
				parent.degree--;
			parent = null;
	    }
	    
	    /*
	     * Add a child for this node
	     * */
	    public void addChild(FibonacciNode child) {
	    	child.remove();
	        if (this.child != null){
	            //this.child.addSibling(child);
	            child.left = this.child;
	            child.right = this.child.right;
	            this.child.right = child;
	            child.right.left = child;
	        }
	        else{
	            this.child = child;
	            child.right = child;
	            child.left = child;
	        }
	        child.parent = this;
	        child.childCut = false;
	        this.degree++;

	    }
	    
	    /*
	     * Remove this node from the tree
	     * */
	    public void remove() {
	        if (parent != null) {
	            if (this == this.right)
	                parent.child = null;
	            else
	                parent.child = right;
	            this.parent.degree--;
	        }

	        if (this != right) {
		        left.right = right;
		        right.left = left;
		        right = this;
		        left = this;
	        }
	        parent = null;
	        this.childCut = false;
	    }
	
	    /*
	     * Obtain the distance of this node to source node
	     * @return distance
	     */
	    public final int getDistance(){
	        return distance;
	    }
	
	    /*
	     * Set the distance value of this node
	     * */
	    public void setDistance(int distance){
	    	this.distance = distance;
	    }
	    
	    /*
	     * Obtain the vertex number for this node.
	     * @return vertex number
	     */
	    public final int getVertexNum()
	    {
	        return vertexNum;
	    }	  
	    
	    /*
	     * The sequence number of vertex
	     */
	    int vertexNum;
		
	    /*
	     * True if node has lost a child since it became a child of its current parent. 
	     * Set to false by remove min, which is the only operation that makes one node a child of another. 
	     * Undefined for a root node.
	     */
	    boolean childCut;
	
	    /*
	     * key value, the distance from source to this node 
	     */
	    int distance;
	
	    /*
	     * number of children
	     */
	    int degree;	
	
	    /*
	     * pointer to left sibling
	     */
	    FibonacciNode left;
	    
	    /*
	     * pointer to right sibling
	     */
	    FibonacciNode right;
	
	    /*
	     * pointer to parent node
	     */
	    FibonacciNode parent;
	
	    /*
	     * Pointer to one of the node's children.
	     * Null iff node has no child.
	     */
	    FibonacciNode child;

	}

    /*
     * Points to the minimum node in the heap.
     */
    private FibonacciNode minNode;

    /**
     * Number of nodes in the heap.
     */
    private int count;
    
}
