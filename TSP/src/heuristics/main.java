package heuristics;

import java.io.IOException;
import java.util.*;

public class main {

    public static void main(String[] args) {
        func();
    }
    public static void func() {
        TSPData data;
        try {
            data = new TSPData();
            //                                                  TEST HEURISTICS
            long start = System.currentTimeMillis();
            ArrayList<Integer> tour = new ArrayList<>((ArrayList<Integer>) TSPData.divide_tsp(data.getCities(),600).clone());
            System.out.println("divide_tsp with 600 : " + TSPData.tourLenn(tour, data.getCities()));
            WriteTour wtour = new WriteTour("divide_tsp_600.txt", tour, data.getCities());
            long end = System.currentTimeMillis();
            long elapsedTime = end - start;
            System.out.println("\n" + (elapsedTime / 1000) + " second and " + elapsedTime % 1000 + " millisecond.");
            tour.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
