import java.io.*;
import java.util.Scanner;

class GraphLists {
    class Node {
        public int vert;
        public int wgt;
        public Node next;
    }

    private int V, E;       // V = number of vertices, E = number of edges
    private Node[] adj;     // adj[] is the adjacency lists array
    private Node m;
    private int[] mst;      // mst[] Holds values of parent[] from the Prim's algorithm
    private int[] visited;  // Used for traversing graph
    private int id;

    // Constructor
    public GraphLists(String graphFile)  throws IOException
    {
        int u, v;
        int e, wgt;
        Node t;

        FileReader fr = new FileReader(graphFile);
        BufferedReader reader = new BufferedReader(fr);

        String splits = " +"; 
        String line = reader.readLine();        
        String[] parts = line.split(splits);
        System.out.println("Parts[] = " + parts[0] + " " + parts[1]);
        
        V = Integer.parseInt(parts[0]);
        E = Integer.parseInt(parts[1]);
        
        // Create sentinel node
        m = new Node(); 
        m.next = m;
        
        // Create adjacency lists, initialised to sentinel node z       
        adj = new Node[V+1];        
        for(v = 1; v <= V; ++v)
            adj[v] = m;               
        
       // Read the edges
        System.out.println("Reading edges from text file");
        for(e = 1; e <= E; ++e)
        {
            line = reader.readLine();
            parts = line.split(splits);
            u = Integer.parseInt(parts[0]);
            v = Integer.parseInt(parts[1]); 
            wgt = Integer.parseInt(parts[2]);
            
            System.out.println("Edge " + toChar(u) + "--(" + wgt + ")--" + toChar(v));    
            
            // Code to put edge into adjacency matrix 
            t = new Node(); t.vert = v; t.wgt = wgt; t.next = adj[u]; adj[u] = t;
            t = new Node(); t.vert = u; t.wgt = wgt; t.next = adj[v]; adj[v] = t;    
        }      
        reader.close();
    }

    // Converts vertex into char 
    private char toChar(int u)
    {  
        return (char)(u + 64);
    }

    // Method to display the graph representation
    public void display() {
        int v;
        Node n;

        for(v=1; v<=V; ++v){
            System.out.print("\nadj[" + toChar(v) + "] ->" );
            for(n = adj[v]; n != m; n = n.next) 
                System.out.print(" |" + toChar(n.vert) + " | " + n.wgt + "| ->");    
        }
        System.out.println("");
    }
   
    // Initialises Depth First Traversal of Graph
    public void DF(int s) {
        visited = new int[V + 1];
        for(int v = 1; v <= V; v++) { 
            visited[v] = 0;
        }

        id = 0; 

        System.out.print("\nDepth First Graph Traversal\n");
        System.out.println("Starting with Vertex " + toChar(s));
        if(visited[s] == 0){
            // Start visiting graph vertices using DF from starting vertex s.
            dfVisit( 0, s); 
        }
        System.out.print("\n\n");
    }

    // Recursive Depth First Traversal for adjacency list
    private void dfVisit(int prev, int v) {
        Node t;
        int u;
        visited[v] = ++id; 
        System.out.print("\n DF just visited vertex " + toChar(v) + " along " + toChar(prev) + "--" + toChar(v) );

        for(t = adj[v]; t != m; t = t.next){   
            // Pull data from node
            u = t.vert;
            if( visited[u] == 0){
                dfVisit(v, u);
            }
        }
    }

    // Breadth first traversal using Queue
    public void BF(int s) {
        visited = new int[V + 1];
        for(int v = 1; v <= V; v++) { 
            visited[v] = 0;
        }
        id = 0;
        int u, v;
        Node t;
        visited[s] = ++id;  
        System.out.print("\n DF just visited vertex " + toChar(s));

        Queue Q = new Queue();
        Q.enQueue(s);

        while(!Q.isEmpty()){
            v = Q.deQueue();
            for(t = adj[v]; t != m; t = t.next){
                u = t.vert;
                if( visited[u] == 0){
                    visited[u] = ++id;  
                    System.out.print("\n DF just visited vertex " + toChar(u));
                    Q.enQueue(u);
                }
            }
        }
    }
    
    // Heap implementation for Prim's Algorithm
    public void MST_Prim(int s)
    {
        int v, u;
        int wgt, wgt_sum = 0;
        int[] dist, parent, hPos;
        Node t;

        // Initialising arrays
        dist = new int[V + 1];      // The distance from starting vertex
        parent = new int[V + 1];    // Array to hold parent of curent vertex
        hPos = new int[V +1];       // Heap Position !!!important

        for(v = 0; v <= V; ++v){   
            dist[v] = Integer.MAX_VALUE;
            parent[v] = 0;
            hPos[v] = 0;
        }

        dist[s] = 0; 
        Heap h =  new Heap(V, dist, hPos); // Heap initially empty
        h.insert(s);           // s will be the root of the MST

        while (! h.isEmpty())  // Should repeat |V| -1 times
        {
            v = h.remove();    // Add v to the MST
            wgt_sum += dist[v]; // Add the wgt of v to sum
            dist[v] = -dist[v]; // Mark v as now in the MST

            // Node t
            for(t = adj[v]; t != m; t = t.next){    // For each u e adj(v)
                // Pull data from node
                u = t.vert;
                wgt = t.wgt;

                if(wgt < dist[u]){ // Weight < current value

                    dist[u] = wgt;
                    parent[u] = v;
                    if(hPos[u] == 0) {      // If not in heap then insert
                        h.insert(u);
                    } else {
                        h.siftUp(hPos[u]); // If already in heap siftup the modified heap node
                    }
                }
            }
        }
        System.out.print("\n\nWeight of MST = " + wgt_sum + "\n");
        mst = parent;                           
    }

    // Prints MST
    public void showMST()
    {
        System.out.print("\n\nMinimum Spanning tree parent array is:\n");
        for(int v = 1; v <= V; ++v)
            System.out.println(toChar(v) + " -> " + toChar(mst[v]));
        System.out.println("");
    }
}

// Heap Code to implement Prim's Algorithm
class Heap
{
    private int[] a;       // heap array
    private int[] hPos;    // hPos[h[k]] == k
    private int[] dist;    // dist[v] = priority of v
    private int N;         // heap size

    public Heap(int maxSize, int[] _dist, int[] _hPos) 
    {
        N = 0;
        a = new int[maxSize + 1];
        dist = _dist;
        hPos = _hPos;
    }

    public boolean isEmpty() 
    {
        return N == 0;
    }

    public void siftUp(int k) 
    {
        int v = a[k];
        a[0] = 0;
        dist[0] = Integer.MIN_VALUE;

        // While distance of last vertex in heap smaller than parent's
        while(dist[v] < dist[a[k/2]]){
            a[k] = a[k/2];  // Parent moves up the tree to the position of the child
            hPos[a[k]] = k; // Heap position hPos[] modified
            k = k/2;        // Index changed to parent's
        }
        a[k] = v;           // Resting index added to heap
        hPos[v] = k;        // Resting index added to hPos[]
    }

    // Removes vertex at the top of the heap and replaces it with the smallest value in heap resizing it
    public void siftDown(int k) 
    {
        int v, j;
        v = a[k];  
        while(k <= N/2){
            j = 2 * k;
            // If the left side of the tree is bigger, increment j
            if(j < N && dist[a[j]] > dist[a[j + 1]]) ++j;

            // If size of parent vertex is less than its child, stop.
            if(dist[v] <= dist[a[j]]) break;

            a[k] = a[j];    // If parent > child, assign parent's position
            hPos[a[k]] = k; // Update new pos of last vertex on tree
            k = j;          // Update position
        }
        a[k] = v;           // Resting index added to heap
        hPos[v] = k;        // Resting index added to hPos[]
    }

    public void insert(int x) 
    {
        a[++N] = x;         // Attaches new vertex to the end of the heap
        siftUp(N);          // Passes same index for siftup
    }

    public int remove() 
    {   
        int v = a[1];   
        hPos[v] = 0;    // v is no longer in heap
        a[1] = a[N--];  // Last node of heap moved to top
        siftDown(1); // Pass index at top to siftdown
        a[N+1] = 0;     // Put null node into empty spot
        return v;       // Return vertex at top of heap
    } 
}

// Queue Code for implementation of BF
class Queue{
    class Node {
        public int vert;
        public Node next;
    }
    public Queue() {
        z = new Node(); // Yhe sentinel
        z.next = z; // Points to itself
        head = z; // Head points to sentinel
        tail = null;
    }

    Node head, tail, z;

    // Checks if queue is empty - head reaching the sentinel
    public boolean isEmpty() {
        return head == head.next;
    }

    public void enQueue(int num) {
        Node newN = new Node();
        newN.vert = num;
        newN.next = z; // New node is initialised to point at sentinel

        if (head == z) // Case of empty list
        {
            head = newN;
        } else // Case if list not empty
        {
            tail.next = newN;
        }
        tail = newN; // New node is now at the tail
    }

    // Head points to the next node, removing it
    public int deQueue() {
        int output = head.vert;
        head = head.next;
        return output;
    }
}

// Driver Code
public class PrimLists {
    public static void main(String[] args) throws IOException
    {
        Scanner sc = new Scanner(System.in);
        String fname;
        int s;
        int mode = 0;   // Switch case variable

        System.out.println("\n\nGraph Traversal and Prim's Algorithm Program\n");
        System.out.print("\nEnter .txt filename:  ");
        fname = sc.nextLine();

        System.out.print("\nEnter starting vertex:  ");
        s = sc.nextInt();
        
        System.out.print("\nChoose method:\n1 - Depth First Graph Traversal\n2 - Breadth First Graph Traversal\n3 - Prim's Algorithm\n\nEnter choice:  ");
        mode = sc.nextInt();
        sc.close(); 
        
        GraphLists g = new GraphLists(fname);
        g.display();
        switch (mode){
            case 1:{
                g.DF(s);
                break;
            }
            case 2:{
                g.BF(s);
                break;
            }
            case 3:{
                g.MST_Prim(s);
                g.showMST();
                break;
            }
            default: System.out.println("\nInvalid option ");
        }
    }
}