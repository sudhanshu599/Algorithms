/**
 * WIP
 **/
package com.williamfiset.algorithms.graphtheory;

import java.util.*;

public class Boruvkas {

  static final int INF = 987654321;
  static final Edge INF_EDGE = new Edge(INF, INF, INF);

  static class Edge implements Comparable<Edge> {
    final int u, v, cost;
    public Edge(int u, int v, int cost) {
      this.u = u;
      this.v = v;
      this.cost = cost;
    }
    public String toString() {
      return String.format("%d %d, cost: %d", u, v, cost);
    }
    @Override public int compareTo(Edge other) {
      int cmp = cost - other.cost;
      // Break ties by picking lexicographically smallest edge pair.
      if (cmp == 0) {
        cmp = u - other.u;
        if (cmp == 0) return v - other.v;
        return cmp;
      }
      return cmp;
    }
  }

  // TODO: Remove the need to hash edges.
  private static long hashEdge(Edge e) {
    return e.u < e.v ? 
           ((long)e.u << 32) | e.v : 
           ((long)e.v << 32) | e.u;
  }

  int n;
  boolean solved;
  List<List<Edge>> graph;

  public Boruvkas(List<List<Edge>> graph) {
    if (graph == null) throw new IllegalArgumentException();
    this.graph = graph;
    this.n = graph.size();
  }

  // Given a graph represented as an edge list this method finds
  // the Minimum Spanning Tree (MST) cost if there exists 
  // a MST, otherwise it returns null.
  public Long solve() {

    long sum = 0L;
    UnionFind uf = new UnionFind(n);
    Set<Long> edgeSet = new HashSet<>();

    for(int components = n;;components = uf.components) { // do while?

      // Gotta track cheapest edge in a "component". Components are id[i]
      Map<Integer, Edge> cheapest = new HashMap<>(); // use array?
      for(int i = 0; i < n; i++) cheapest.put(uf.id[i], INF_EDGE);
      
      for (int i = 0; i < n; i++) {
        List<Edge> edges = g.get(i);
        for (Edge e : edges) {
          
          int uc = uf.id[e.u], vc = uf.id[e.v];
          if (uc == vc) continue;

          // Q: do we need both?
          if (e.compareTo(cheapest.get(vc)) < 0) {
            cheapest.put(vc, e);
          }
          if (e.compareTo(cheapest.get(uc)) < 0) {
            cheapest.put(uc, e);
          }
        }
      }

      for (Edge e : cheapest.values()) {
        if (e.cost != INF && !edgeSet.contains(hashEdge(e))) {
          
          // System.out.println(e);

          sum += e.cost;
          uf.union(e.u, e.v);
          edgeSet.add(hashEdge(e));

          // LinkedList<Edge> edges = g.get(e.u);
          // edges.remove(e);
          // g.set(e.u, edges);
          // edgeSet.add(e);
        }
      }

      // Was not able to reduce num components.
      if (uf.components == components) break;

    }

    // Make sure we have a MST that includes all the nodes
    if (uf.size(0) != n) return null;
    return sum;
  }

  static List<LinkedList<Edge>> createEmptyGraph(int n) {
    List<LinkedList<Edge>> g = new ArrayList<>();
    for(int i = 0; i < n; i++) g.add(new LinkedList<>());
    return g;
  }

  static void addDirectedEdge(List<LinkedList<Edge>> g, int from, int to, int cost) {
    g.get(from).add(new Edge(from, to, cost));
  }

  static void addUndirectedEdge(List<LinkedList<Edge>> g, int from, int to, int cost) {
    addDirectedEdge(g, from, to, cost);
    addDirectedEdge(g, to, from, cost);
  }

  public static void main(String[] args) {
    
    int n = 10;
    List<LinkedList<Edge>> g = createEmptyGraph(n);

    // Edges are treated as undirected
    addDirectedEdge(g, 0, 1, 5);
    addDirectedEdge(g, 1, 2, 4);
    addDirectedEdge(g, 2, 9, 2);
    addDirectedEdge(g, 0, 4, 1);
    addDirectedEdge(g, 0, 3, 4);
    addDirectedEdge(g, 1, 3, 2);
    addDirectedEdge(g, 2, 7, 4);
    addDirectedEdge(g, 2, 8, 1);
    addDirectedEdge(g, 9, 8, 0);
    addDirectedEdge(g, 4, 5, 1);
    addDirectedEdge(g, 5, 6, 7);
    addDirectedEdge(g, 6, 8, 4);
    addDirectedEdge(g, 4, 3, 2);
    addDirectedEdge(g, 5, 3, 5);
    addDirectedEdge(g, 3, 6, 11);
    addDirectedEdge(g, 6, 7, 1);
    addDirectedEdge(g, 3, 7, 2);
    addDirectedEdge(g, 7, 8, 6);

    Boruvkas solver = new Boruvkas();
    System.out.println(solver.solve(g, n));

  }

  // Union find data structure 
  private static class UnionFind {
    int components;
    int[] id, sz;

    public UnionFind(int n) {
      components = n;
      id = new int[n];
      sz = new int[n];
      for (int i = 0; i < n; i++) {
        id[i] = i;
        sz[i] = 1;
      }
    }

    public int find(int p) {
      int root = p;
      while (root != id[root])
        root = id[root];
      while (p != root) { // Do path compression
        int next = id[p];
        id[p] = root;
        p = next;
      }
      return root;
    }

    public boolean connected(int p, int q) {
      return find(p) == find(q);
    }

    public int size(int p) {
      return sz[find(p)];
    }

    public void union(int p, int q) {
      int root1 = find(p), root2 = find(q);
      if (root1 == root2) return;
      if (sz[root1] < sz[root2]) {
        sz[root2] += sz[root1];
        id[root1] = root2;
      } else {
        sz[root1] += sz[root2];
        id[root2] = root1;
      }
      components--;
    }
  }

}


