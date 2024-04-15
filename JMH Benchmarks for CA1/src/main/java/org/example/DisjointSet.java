package org.example;

public class DisjointSet {
    public int[] roots;
    public int size; //all pixels
    public int numPills; //number of pills
    public int[] sizes; //size of each pill

    public DisjointSet(int hw) {
        if (hw < 0) {
            throw new IllegalArgumentException();
        }

        roots = new int[hw];
        sizes = new int[hw];
        this.size = numPills = hw;

        for (int i = 0; i < hw; i++) {
            roots[i] = i;
            sizes[i] = 1;
        }
    }

    public int find(int p) {
        int root = p;
        while(root != roots[root])
            root = roots[root];

        while(p!=root) {
            int next = roots[p];
            roots[p] = root;
            p = next;
        }
        return root;
    }

    public void union(int p, int q) { //by size
        int root1 = find(p);
        int root2 = find(q);

        if(root1 == root2) return;

        if(sizes[root1] < sizes[root2]) {
            sizes[root2] += sizes[root1];
            roots[root1] = root2;
        } else {
            sizes[root1] += sizes[root2];
            roots[root2] = root1;
        }
        numPills--;
    }
}