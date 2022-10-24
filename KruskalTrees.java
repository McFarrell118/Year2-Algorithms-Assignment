import java.io.*;
import java.util.Scanner;
 
class Edge {
    public int u, v, wgt;   // Starting vertex, ending vertex, weight of edge

    public Edge() {
        u = 0;
        v = 0;
        wgt = 0;
    }

    // Inserts value into edges
    public Edge( int x, int y, int w) {
        this.u = x;
        this.v = y;
        this.wgt = w;
    }
    
    // Prints out edge
    public void show() {
        System.out.print("Edge " + toChar(u) + "--" + wgt + "--" + toChar(v) + "\n") ;
    }
    
    private char toChar(int u)
    {  
        return (char)(u + 64);
    }
}

// Heap code for sorting edges by weight
class Heap
{
	private int[] a;    // Heap array
    int N, Nmax;        // Heap size
    Edge[] edge;        // Edge[v].wgt = priority of v

    // Bottom up heap constructor
    public Heap(int _N, Edge[] _edge) {
        int i;
        Nmax = N = _N;
        a = new int[N+1];
        edge = _edge;
       
        // initially fills heap array with indices of edge []
        for (i=0; i <= N; ++i) 
            a[i] = i;
           
        // Convert h[] into a heap from the bottom up
        for(i = N/2; i > 0; --i)
            siftDown(a[i]);
    }

    // Removes vertex at the top of the heap and replaces it with the smallest value in heap
    private void siftDown( int k) {
        int e, j;
        e = a[k];
        while( k <= N/2) {
            j = 2 * k;
            if (j < N && edge[a[j]].wgt > edge[a[j+1]].wgt) j++;    // If the left side of the tree is >, incerent j
            if (edge[e].wgt <= edge[a[j]].wgt) break;   // If weight of parent vertex < its child, stop.

            a[k] = a[j];    // If parent > than child, assign parent's position
            k = j;          // Update position
        }
        a[k] = e;           // Resting index added to heap
    }

    public int remove() {
        a[0] = a[1];        // Top of heap moved to position 0
        a[1] = a[N--];      // Last node of heap moved to top
        siftDown(1);     // Pass index at top to siftdown
        return a[0];        // Returns edge at top of heap
    }
}

class UnionFindSets
{
    private int[] treeParent;
    private int[] rank;
    private int N;
    
    public UnionFindSets(int V)
    { 
        N = V;                      // Number of vertices
        treeParent = new int[V+1];  // Location of the parent vertex
        rank = new int[V+1];        // Rank of the vertex

        for(int i = 0; i < V; i++) { 
            treeParent[i] = i;  // Vertexes are in seperate sets
            rank[i] = 0;        // Array of rank values initialised to 0
        }
    }

    // If the parent of the vertex is not the root, find root and make into parent
    public int findSet( int vertex)
    {   
        if(treeParent[vertex] != vertex){
            treeParent[vertex] = findSet(treeParent[vertex]);
        }
        return treeParent[vertex];
    }
    
    // Links two edges, Parent of tree will be the source vertex of the edge
    public void union( int set1, int set2)
    {
        for(int i = 0; i < N; i++){
            if(treeParent[i] == set2){
                treeParent[i] = set1;
            }
        }
    }

    // Ranks vertices and sets the parent array accordingly
    public void unionByRank(int set1, int set2){
        int u = findSet(set1);  // Parent of the source
        int v = findSet(set2);  // Parent of the destination

        if (rank[u] < rank[v]){         // If rank is smaller, attach to higher rank
            treeParent[u] = v;
        } else if (rank[u] > rank[v]){  // Else make one of them as the root and increment their rank
            treeParent[v] = u;
        } else {
            treeParent[v] = u;
            rank[u]++;
        }
    }
    
    // Shows trees
    public void showTrees()
    {
        int i;
        for(i=1; i<=N; ++i)
            System.out.print(toChar(i) + "->" + toChar(treeParent[i]) + "  " );
        System.out.print("\n");
    }
    
    // Shows sets
    public void showSets()
    {
        int u, root;
        int[] shown = new int[N+1];
        for (u=1; u<=N; ++u)
        {   
            root = findSet(u);  // find the root
            if(shown[root] != 1) {
                showSet(root);
                shown[root] = 1;
            }            
        }   
        System.out.print("\n");
    }

    private void showSet(int root)
    {
        int v;
        System.out.print("Set{");
        for(v=1; v<=N; ++v)
            if(findSet(v) == root)
                System.out.print(toChar(v) + " ");
        System.out.print("}  ");
    }
    
    private char toChar(int u)
    {  
        return (char)(u + 64);
    }
}

class Graph 
{ 
    private int V, E;       // V = Number of vertices
    private Edge[] edge;    // E = Number of edges
    private Edge[] mst;     // mst = Minimum Spanning Tree        

    // Constructor
    public Graph(String graphFile) throws IOException
    {
        int u, v;
        int w, e;

        FileReader fr = new FileReader(graphFile);
		BufferedReader reader = new BufferedReader(fr);
	           
        String splits = " +"; 
		String line = reader.readLine();        
        String[] parts = line.split(splits);
        System.out.println("Parts[] = " + parts[0] + " " + parts[1]);
        
        V = Integer.parseInt(parts[0]);
        E = Integer.parseInt(parts[1]);
        
        // Create edge array
        edge = new Edge[E+1];   
        
        System.out.println("Reading edges from text file");
        for(e = 1; e <= E; ++e)
        {
            line = reader.readLine();
            parts = line.split(splits);
            u = Integer.parseInt(parts[0]);
            v = Integer.parseInt(parts[1]); 
            w = Integer.parseInt(parts[2]);
            System.out.println("Edge " + toChar(u) + "--(" + w + ")--" + toChar(v));                         
            
            edge[e] = new Edge(u, v, w); // New Edge object  
        }
        reader.close();
    }
 
    // Krustkal MST
    public Edge[] MST_Kruskal() 
    {
        int i = 0;
        Edge e;
        int uSet, vSet; // Set 1 and Set 2
        UnionFindSets partition;
        
        // Create edge array to store MST
        mst = new Edge[V-1]; 

        // Heap for sorting indices of array of edges
        Heap h = new Heap(E, edge);

        // Create partition of singleton sets for the vertices
        System.out.println("\nSets before Kruskal's:");
        partition = new UnionFindSets(V);
        partition.showSets();

        while(i < V-1){

            // Inserts the edge from the top of the heap, removes that edge from the heap, then sorts the edge heap array by the edge's weight
            e = h.edge[h.remove()];
            
            uSet =  partition.findSet(e.u);
            vSet = partition.findSet(e.v);
            if(uSet != vSet){
                partition.unionByRank(uSet, vSet);
                System.out.print("Inserting egde to MST: ");
                e.show();
                mst[i++] = e;
            }  
        }
        System.out.println("\nTree of vertices:");
        partition.showTrees();
        System.out.println("\nSets after Kruskal's:");
        partition.showSets();
        return mst;
    }

    private char toChar(int u)
    {  
        return (char)(u + 64);
    }

    public void showMST()
    {
        System.out.print("\nMinimum spanning tree build from following edges:\n");
        for(int e = 0; e < V-1; ++e) {
            mst[e].show(); 
        }
        System.out.println();
    }
} 
    
// Driver code
class KruskalTrees {
    public static void main(String[] args) throws IOException
    {
        Scanner sc = new Scanner(System.in);
        String fname = "wGraph1.txt";

        System.out.print("\nEnter graph file: ");
        fname = sc.nextLine();
        sc.close();

        Graph g = new Graph(fname);
        g.MST_Kruskal();
        g.showMST();  
    }
}    