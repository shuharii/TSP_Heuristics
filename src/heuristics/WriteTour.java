package heuristics;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

//this class write a tour list to file for graph plot.
public class WriteTour {
    WriteTour(String filepath, ArrayList<Integer> tour, ArrayList<City> cities) throws IOException {
        FileWriter myWriter = new FileWriter(filepath);
        for (int i = 0; i < tour.size(); i++){
            myWriter.write(   cities.get(TSPData.whereIsCityID(cities,tour.get(i))).getLocx() + "," + cities.get(TSPData.whereIsCityID(cities,tour.get(i))).getLocy() );
            if(i != tour.size()-1){
                myWriter.write(",");
            }
        }
        myWriter.close();
    }
}
