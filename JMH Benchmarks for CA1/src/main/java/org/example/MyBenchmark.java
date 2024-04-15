package org.example;

import javafx.scene.paint.Color;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.RunnerException;
import sun.tools.jar.Main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Measurement(iterations = 10)
@Warmup(iterations = 5)
@Fork(value = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)

public class MyBenchmark {
    DisjointSet disjointSet;
    Pill[] pills;

    @Setup
    public void setUp() {
        disjointSet = new DisjointSet(500*200);

        pills = new Pill[10];
        for (int i = 0; i < 10; i++) {
            pills[i] = new Pill("Pill" + i, i, new Color(i,i,i,i));
        }
    }

    @Benchmark
    public void testFind() {
        Random random = new Random();
        int index = random.nextInt(disjointSet.size);

        disjointSet.find(index);
    }

    @Benchmark
    public void testUnion() {
        Random random = new Random();
        int indexOne = random.nextInt(disjointSet.size);
        int indexTwo = random.nextInt(disjointSet.size);

        disjointSet.union(indexOne,indexTwo);
    }

    @Benchmark
    public void testPillSetters() {
        for (Pill pill : pills) {
            pill.setName("UpdatedName");
            pill.setRoot(123);
            pill.setIndexes(new ArrayList<>());
            pill.setTwoColours(true);
        }
    }

    @Benchmark
    public void testPillGetters() {
        for (Pill pill : pills) {
            pill.getName();
            pill.getRoot();
            pill.getIndexes();
            pill.isTwoColours();
        }
    }

    public static void main(String[] args) throws RunnerException, IOException {
        Main.main(args);
    }
}
