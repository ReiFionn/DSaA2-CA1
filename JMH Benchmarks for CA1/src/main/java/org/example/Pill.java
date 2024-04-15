package org.example;

import java.util.ArrayList;

public class Pill {
    private String name;
    private int root;
    private ArrayList<Integer> indexes = new ArrayList<Integer>();
    private boolean twoColours;

    public Pill(String name, int root) {
        this.name = name;
        this.root = root;
        this.twoColours = false;
    }

    public boolean isTwoColours() {
        return twoColours;
    }
    public void setTwoColours(boolean twoColours) {
        this.twoColours = twoColours;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getRoot() {
        return root;
    }
    public void setRoot(int root) {
        this.root = root;
    }

    public ArrayList<Integer> getIndexes() {
        return indexes;
    }
    public void setIndexes(ArrayList<Integer> indexes) {
        this.indexes = indexes;
    }

    @Override
    public String toString() {
        return "Pill{" +
                "name='" + name + '\'' +
                ", root=" + root +
                ", indexes=" + indexes +
                ", isTwoColours=" + twoColours +
                '}';
    }
}