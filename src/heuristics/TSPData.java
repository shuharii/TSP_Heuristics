package heuristics;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class TSPData {

    ArrayList<City> cities;

    public TSPData() throws FileNotFoundException {
        //private final double[][] cities;
        String filepath = "ca4663.tsp";
        this.cities = getCitiesFromFile(filepath);
    }
    //return all cities
    public ArrayList<City> getCities(){
        return this.cities;
    }

    //read file and return a recorded City List.
    public ArrayList<City> getCitiesFromFile(String filepath) throws FileNotFoundException {
        // create scanner
        Scanner sc = new Scanner(new File(filepath));
        sc.useLocale(Locale.US);
        // read number of cities
        int dim = 0;
        while (sc.hasNextLine() && dim == 0) {
            String line = sc.nextLine();
            String[] words = line.split(" ");
            for (int i = 0; i < words.length; i++) {
                if (words[i].equals("DIMENSION")) {
                    dim = Integer.parseInt(words[i + 2]);
                }
            }
        }
        ArrayList<City> cities = new ArrayList<>(dim);
        while (!sc.hasNextDouble()) {
            sc.nextLine();
        }
        double locx;
        double locy;

        for (int i = 0; i < dim; i++) {
            sc.nextDouble();
            locx = sc.nextDouble();
            locy = sc.nextDouble();
            cities.add( new City(i,locx,locy) );
        }// create and return TSP
        return cities;
    }
    //divide_tsp algorithm
    public static ArrayList<Integer> divide_tsp(ArrayList<City> cities, int n){
        if(cities.size() <= n){
            ArrayList<Integer> greedy;
            ArrayList<Integer> nn;
            //try to calculate best tour piece.
            nn = nn_tsp(cities,0);
            greedy = greedy_tsp(cities);
            if(tourLenn(greedy,cities) < tourLenn(nn,cities)){
                return greedy;
            }
            else{
                return nn;
            }
        }
        else {
            ArrayList<City> left = new ArrayList();
            ArrayList<City> right = new ArrayList();
            //split cities to left and right pieces.
            split_cities(left, right, cities);
            //combine the calculated tours with lazy version.
            return speed_join(divide_tsp(left,n), divide_tsp(right,n), cities);
            //combine the calculated tours with brute force version.
            //return join_tours(divide_tsp(left,n), divide_tsp(right,n), cities);
        }
    }
    //divide and conquer algorithm with improvement.
    public static ArrayList<Integer> improve_divide_tsp(ArrayList<City> cities, int n){
        ArrayList<Integer> divide = divide_tsp(cities,n);
        return longest_link_improve(divide,cities);
    }
    //greedy algorithm with improvement.
    public static ArrayList<Integer> improve_greedy_tsp(ArrayList<City> cities){
        ArrayList<Integer> greedy = greedy_tsp(cities);
        return longest_link_improve(greedy,cities);
    }
    //nearest neighbor algorithm improved with repeat function.
    public static ArrayList<Integer> rep_nn_tsp(ArrayList<City> cities, int repeat){
        ArrayList<Integer> repeatTour;
        double repeatTourLen;
        ArrayList<Integer> smallestTour = new ArrayList<>();
        double smallestTourLen = Double.MAX_VALUE;
        int randomVal = (int) (Math.random() * (cities.size()));
        for(int i= 0; i<repeat; i++){
            repeatTour = nn_tsp(cities,randomVal);
            repeatTourLen = tourLenn(repeatTour,cities);
            if(smallestTourLen > repeatTourLen){
                smallestTour = repeatTour;
                smallestTourLen = repeatTourLen;
            }
        }
        return smallestTour;
    }
    //repeated nearest neighbor algorithm with improvement
    public static ArrayList<Integer> improve_rep_nn_tsp(ArrayList<City> cities, int repeat){
        ArrayList<Integer> nn = rep_nn_tsp(cities,repeat);
        return longest_link_improve(nn,cities);
    }
    //if there is a improvement, segments will reverse.
    public static boolean reverse_segment_if_improvement(ArrayList<Integer> tour, Integer i, Integer j, ArrayList<City> cities){
        Integer A;
        Integer B = tour.get( i );
        Integer C;
        Integer D = tour.get(j % tour.size());
        try{
            A = tour.get(i - 1);
        }
        catch (IndexOutOfBoundsException e){
            A = tour.get(tour.size() -1);
        }
        try{
            C = tour.get(j - 1);
        }
        catch (IndexOutOfBoundsException e){
            C = tour.get(tour.size() -1);
        }
        double ab = cities.get(whereIsCityID(cities,A)).dist(cities.get(whereIsCityID(cities,B)));
        double cd = cities.get(whereIsCityID(cities,C)).dist(cities.get(whereIsCityID(cities,D)));
        double ac = cities.get(whereIsCityID(cities,A)).dist(cities.get(whereIsCityID(cities,C)));
        double bd = cities.get(whereIsCityID(cities,B)).dist(cities.get(whereIsCityID(cities,D)));
        if(ab + cd > ac + bd){
            Collections.reverse(tour.subList(i,j));
            return true;
        }
        return false;
    }
    //brute force improvement algorithm (this algorithm works with small amount of cities).
    public static ArrayList<Integer> improve_tour(ArrayList<Integer> tour, ArrayList<City> cities){
        while(true){
            ArrayList<Boolean> improvements = new ArrayList<>();
            for(int i = 0; i < ((tour.size() * (tour.size()-1) /2) -1  ); i++){
                ArrayList<Integer>[] ls = subsegments(tour.size());
                improvements.add(reverse_segment_if_improvement(tour,ls[i].get(0),ls[i].get(1),cities));
            }
            if(improvementsCtrl(improvements)){
                return tour;
            }
        }
    }
    //if there is no need to improvement for links, function will return false for all links.
    public static boolean improvementsCtrl(ArrayList<Boolean> improvements) {
        for (Boolean imp : improvements)
            if (imp){
                return false;
            }
        return true;
    }
    //algorithm creates a subsegment for brute force.
    public static ArrayList<Integer>[] subsegments(int N){
        ArrayList<Integer>[] retVal = new ArrayList[(N * (N-1) / 2) - 1];
        int counter = 0;
        for(int length = N - 1; length>2-1; length--) {
            for (int i = N - length ; i > -1; i--) {// i = 20-19 + 1
                retVal[counter] = new ArrayList<>();
                retVal[counter].add(i);
                retVal[counter].add(i + length);
                counter++;
            }
        }
        return retVal;
    }
    //lazy tour join for divide and conquer algorithm.
    public static ArrayList<Integer> speed_join(ArrayList<Integer> leftTour, ArrayList<Integer> rightTour, ArrayList<City> cities){
        ArrayList<Integer> returnVal = new ArrayList<>();
        returnVal.addAll(leftTour);
        returnVal.addAll(rightTour);
        return returnVal;
    }
    //greedy tsp algorithm.
    public static ArrayList<Integer> greedy_tsp(ArrayList<City> cities){
        ArrayList<City> new_segment;
        ArrayList<Integer> rtVal = new ArrayList<>();
        HashMap<Integer, ArrayList<City>> endpoints = new HashMap<>();
        fillEndpoints(endpoints,cities);
        for(ArrayList<City> link : shortest_links_first(cities)){
            City A = link.get(0);
            City B = link.get(1);
            // if A is endpoint and B is endpoint and A is not B, this means A and B should join.
            if (mapControl(endpoints, A) && mapControl(endpoints, B) && !listEquality(endpoints.get(A.getCityID()),endpoints.get(B.getCityID()))) {
                new_segment = join_endpoints(endpoints, A, B);
                if(new_segment.size() == cities.size()){
                    //City ArrayList to Integer ArrayList
                    //if the segment include all cities, this segment is out tour.
                    for (City city : new_segment) {
                        rtVal.add(city.getCityID());
                    }
                    return rtVal;
                }
            }
        }
        return rtVal;
    }
    //this control algorithm return a true value if A and B are equal.
    public static boolean listEquality(ArrayList<City> A, ArrayList<City> B){
        return A == B;
    }
    //this algorithm merge lists if endpoint are equal for greedy.
    public static ArrayList<City>join_endpoints(HashMap<Integer, ArrayList<City>> endpoints, City A, City B){
        ArrayList<City> Aseg = new ArrayList<>(endpoints.get(A.getCityID()));
        ArrayList<City> Bseg = new ArrayList<>(endpoints.get(B.getCityID()));
        if(Aseg.get(Aseg.size()-1) != A){
            Collections.reverse(Aseg);
        }
        if(Bseg.get(0) != B){
            Collections.reverse(Bseg);
        }
        Aseg.addAll(Bseg);

        endpoints.remove(A.getCityID());
        endpoints.remove(B.getCityID());

        endpoints.put(Aseg.get(0).getCityID(), Aseg);
        endpoints.put(Aseg.get(Aseg.size()-1).getCityID(), Aseg);
        return Aseg;
    }
    //this algorithm fill the HashMap for greedy algorithm
    public static void fillEndpoints(HashMap<Integer, ArrayList<City>> endpoints, ArrayList<City> cities){
        for (City c : cities){
            ArrayList<City> temp = new ArrayList<>();
            temp.add(c);
            endpoints.put(c.getCityID(),temp);
        }
    }
    //this algorithm return a tour index of given cityID.
    public static Integer tourIndex(ArrayList<Integer> tour, Integer CityID){
        for(int i = 0; i<tour.size(); i++){
            if(Objects.equals(tour.get(i), CityID)){
                return i;
            }
        }
        return 5000;
    }
    //improvement algorithm, it improves the worst 350 cities position.
    public static ArrayList<Integer> longest_link_improve(ArrayList<Integer> tour, ArrayList<City> cities){
        ArrayList<City>[] comb = createLinks(tour,cities);
        Link[] arr = new Link[comb.length];
        for (int i = 0; i<comb.length; i++){
            Link lk = new Link(i,comb[i].get(0),comb[i].get(1));
            arr[i] = lk;
        }
        Links ls = new Links();
        ls.sort(arr,0, arr.length-1);
        ArrayList<Integer> longestCityIDs = new ArrayList<>();
        int ct = 0;
        double dist = Double.MAX_VALUE;
        //record worst 350 cities
        while(longestCityIDs.size() != 350){
            dist = arr[arr.length-1-ct].dist;
            if(isItInList(longestCityIDs, arr[arr.length - 1 - ct].A.getCityID())){
                longestCityIDs.add(arr[arr.length-1-ct].A.getCityID());
            }
            if(isItInList(longestCityIDs, arr[arr.length - 1 - ct].B.getCityID())){
                longestCityIDs.add(arr[arr.length-1-ct].B.getCityID());
            }
            ct++;
        }
        //improve them using bruteforce.
        for (Integer longestCityID : longestCityIDs) {
            Integer tourIndexOfA = tourIndex(tour, longestCityID);
            for (int m = 0; m < tour.size(); m++) {
                if (m != tourIndexOfA && m < tourIndexOfA) {
                    reverse_segment_if_improvement(tour, m, tourIndexOfA, cities);
                } else if (m != tourIndexOfA && m > tourIndexOfA) {
                    reverse_segment_if_improvement(tour, tourIndexOfA, m, cities);
                }
            }
        }
        return tour;
    }
    //this function return false value when given integer value in list.
    public static boolean isItInList(ArrayList<Integer> list, Integer i){
        for(Integer inte : list){
            if(Objects.equals(inte, i)){
                return false;
            }
        }
        return true;
    }
    //this algorithm create link list for tour.
    public static ArrayList<City>[] createLinks(ArrayList<Integer> tour, ArrayList<City> cities){
        ArrayList<City>[] arr = new ArrayList[cities.size()];
        for (int i = 0; i<cities.size()-1; i++){
            ArrayList<City> ls = new ArrayList<>();
            arr[i] = new ArrayList<>();
            ls.add(cities.get(tour.get(i)));
            ls.add(cities.get(tour.get(i + 1)));
            arr[i].addAll(ls);
            ls.clear();
        }
        ArrayList<City> ls = new ArrayList<>();
        arr[arr.length-1] = new ArrayList<>();
        ls.add(cities.get(tour.get(tour.size()-1)));
        ls.add(cities.get(tour.get(0)));
        arr[arr.length-1].addAll(ls);
        return arr;
    }
    // this function sort the shortest link to longest link.
    public static ArrayList<City>[] shortest_links_first(ArrayList<City> cities){
        ArrayList<City>[] comb = double_combination(cities).clone();
        Link[] arr = new Link[comb.length];
        for (int i = 0; i<comb.length; i++){
            Link lk = new Link(i,comb[i].get(0),comb[i].get(1));
            arr[i] = lk;
        }
        Links ls = new Links();

        ls.sort(arr,0, arr.length-1);

        ArrayList<City>[] rtVal = new ArrayList[comb.length];
        for(int i = 0; i<rtVal.length; i++){
            rtVal[i] = new ArrayList<>();
            rtVal[i].add(arr[i].A);
            rtVal[i].add(arr[i].B);
        }
        return rtVal;
    }
    public static boolean mapControl(HashMap<Integer, ArrayList<City>> endpoints, City c){
        return endpoints.containsKey(c.getCityID());
    }
    public static ArrayList<Integer> nn_tsp(ArrayList<City> cities, int start){
        ArrayList<Integer> tour = new ArrayList<>();
        City C = cities.get(start);
        tour.add(C.getCityID());
        ArrayList<City> unvisited = new ArrayList<>(cities.subList(1, cities.size() ));
        while(unvisited.size() != 0){
            C = nearest_neighbor(C,unvisited);
            tour.add(C.getCityID());
            unvisited.remove(C);
        }
        return tour;
    }
    //completed.
    public static City nearest_neighbor(City C, ArrayList<City> cities){
        double nearest = Double.MAX_VALUE;
        City rCity = cities.get(0);
        for(City ct : cities){
            if(C.dist(ct) < nearest && !Objects.equals(C.getCityID(), ct.getCityID())){
                nearest = C.dist(ct);
                rCity = ct;
            }
        }
        return rCity;
    }
    //this algorithm merge the left and right tour using rotations and brute force.
    public static ArrayList<Integer> join_tours(ArrayList<Integer> leftTour, ArrayList<Integer> rightTour, ArrayList<City> cities){
        ArrayList<Integer>[] segments1 = rotations(leftTour);
        ArrayList<Integer>[] segments2 = rotations(rightTour);
        ArrayList<Integer>[] stemp = new ArrayList[2];
        ArrayList<Integer>[] returnVal = new ArrayList[leftTour.size()* rightTour.size()*2];
        int counter = 0;
        for (ArrayList<Integer> s1 : segments1){
            for(ArrayList<Integer> s : segments2){
                stemp[0] = new ArrayList<>(s);
                stemp[1] = new ArrayList<>(s);
                Collections.reverse(stemp[1]);
                for(ArrayList<Integer> s2 : stemp){
                    returnVal[counter] = join_tours_helper(s1,s2);
                    counter++;
                }
            }
        }
        return returnVal[(shortest_tour(returnVal, cities))];//ArrayList<City>;
    }
    //function return the given city index of city list.
    public static Integer whereIsCityID(ArrayList<City> cities, Integer i){
        Integer index = 0;
        for (City ct : cities){
            if (Objects.equals(ct.getCityID(), i)){
                return index;
            }
            index++;
        }
        return index;
    }
    //this function return a shortest tour index of given tour arraylist.
    public static Integer shortest_tour(ArrayList<Integer>[] tours, ArrayList<City> cities){
        System.out.println("shortest head");
        int indexOfList = 0;
        double len = Double.MAX_VALUE;
        for (int i = 0; i<tours.length; i++){
            if(tours[i] != null){
                double temp = tourLenn(tours[i],cities);
                if(len > temp){
                    len = temp;
                    indexOfList = i;
                }
            }
        }
        System.out.println("shortest tail");
        return indexOfList;
    }
    //this function calculate the tour length for given tour.
    public static double tourLenn(ArrayList<Integer> tour, ArrayList<City> cities){
        double totalDistance = 0;
        for(int j = 0; j< tour.size()-1; j++){
            totalDistance = totalDistance + cities.get(whereIsCityID(cities,tour.get(j))).dist(cities.get(whereIsCityID(cities,tour.get(j+1))));
        }
        if(tour.size() != 0){
            totalDistance = totalDistance + cities.get(whereIsCityID(cities,tour.get(tour.size()-1))).dist(cities.get(whereIsCityID(cities,tour.get(0))));
        }
        return totalDistance;
    }
    //this function calculate the double combination of given cities.
    public static ArrayList<City>[] double_combination(ArrayList<City> cities){
        ArrayList<City>[] combin = new ArrayList[cities.size()*(cities.size()-1)/2];
        int counter = 0;
        for (int i = 0; i< cities.size()-1; i++)
            for (int j = 1; j< cities.size(); j++){
                if( i+j <= cities.size()-1) {
                    combin[counter] = new ArrayList<>();
                    combin[counter].add(cities.get(i));
                    combin[counter].add(cities.get(j + i));
                    counter++;
                }
            }
        return combin;
    }
    //This algorithm split the cities of two part.
    public static void split_cities(ArrayList<City> A, ArrayList<City> B, ArrayList<City> data) {
        ArrayList<City>[] twopart = new ArrayList[2];
        double xmax=maxvalue(data,0);
        double xmin=minvalue(data,0);
        double ymax=maxvalue(data,1);
        double ymin=minvalue(data,1);
        //if the cities are vertical it split them vertical, if horizontal it split horizontal.
        if((xmax-xmin)>=(ymax-ymin)) {sortX(data,0,data.size()-1);}
        else {sortY(data,0,data.size()-1);}
        for(int i = 0; i< data.size()/2; i++)
            A.add(data.get(i));
        for(int i=data.size()/2 ;i< data.size(); i++)
            B.add(data.get(i));
    }
    //this helps join tour algorithm to join given integer lists.
    public static ArrayList<Integer> join_tours_helper(ArrayList<Integer> A, ArrayList<Integer> B){
        ArrayList<Integer> returnVal = new ArrayList<>(A.size() + B.size());
        returnVal.addAll(A);
        returnVal.addAll(B);
        return returnVal;
    }
    //this function calculate and return the rotations of given tour.
    public static ArrayList<Integer>[] rotations(ArrayList<Integer> tour){
        ArrayList<Integer>[] ls = new ArrayList[tour.size()];
        for(int i = 0; i<tour.size(); i++){
            ls[i] = new ArrayList<>();
            ls[i].addAll(tour.subList(i, tour.size()));
            ls[i].addAll(tour.subList(0,i));
        }
        return ls;
    }
    //this function calculate the minimum city location for given dimension parameter
    public static double minvalue(ArrayList<City> cities,int type) {
        City city=null;
        double min=Double.MAX_VALUE;
        double temp=0;

        for (City value : cities) {
            city = value;
            if (type == 0) {
                temp = city.getLocx();
            } else {
                temp = city.getLocy();
            }
            if (temp < min) {
                min = temp;
            }
        }
        return min;
    }
    //this function calculate the maximum city location for given dimension parameter
    public static double maxvalue(ArrayList<City> cities,int type) {
        City city=null;
        double max=Double.MIN_VALUE;
        double temp=0;

        for (City value : cities) {
            city = value;
            if (type == 0) {
                temp = city.getLocx();
            } else {
                temp = city.getLocy();
            }
            if (temp > max) {
                max = temp;
            }
        }
        return max;
    }
    //mergesort for X axis.
    public static void sortX(ArrayList<City> cities,int left, int right) {
        if (left < right) {
            int m = (left+right)/2;
            sortX(cities, left, m);
            sortX(cities , m+1, right);
            mergeX(cities, left, m, right);
        }
    }
    //mergesort for Y axis.
    public static void sortY(ArrayList<City> cities,int left, int right) {
        if (left < right) {
            int m = (left+right)/2;
            sortY(cities, left, m);
            sortY(cities , m+1, right);
            mergeY(cities, left, m, right);
        }
    }
    //merge for X axis.
    public static void mergeX(ArrayList<City> cities,int left, int m, int right) {
        int a = m - left + 1;
        int b = right - m;

        City[] Left = new City [a];
        City[] Right = new City [b];

        for (int i=0; i<a; ++i)
            Left[i] = cities.get(left + i);

        for (int j=0; j<b; ++j)
            Right[j] = cities.get(m + 1+ j);

        int i = 0, j = 0;
        int k = left;
        while (i < a && j < b) {
            if (Left[i].getLocx()<= Right[j].getLocx()) {
                cities.set(k,Left[i]);
                i++;
            }
            else {
                cities.set(k,Right[j]);
                j++;
            }
            k++;
        }
        while (i < a) {
            cities.set(k,Left[i]);
            i++;
            k++;
        }
        while (j < b) {
            cities.set(k,Right[j]);
            j++;
            k++;
        }
    }
    // merge for Y axis.
    public static void mergeY(ArrayList<City> cities,int left, int m, int right) {
        int a = m - left + 1;
        int b = right - m;
        City[] Left = new City [a];
        City[] Right = new City [b];

        for (int i=0; i<a; ++i)
            Left[i] = cities.get(left + i);

        for (int j=0; j<b; ++j)
            Right[j] = cities.get(m + 1+ j);

        int i = 0, j = 0;
        int k = left;
        while (i < a && j < b) {
            if (Left[i].getLocy() <= Right[j].getLocy()) {
                cities.set(k,Left[i]);
                i++;
            }
            else {
                cities.set(k,Right[j]);
                j++;
            }
            k++;
        }
        while (i < a) {
            cities.set(k,Left[i]);
            i++;
            k++;
        }
        while (j < b) {
            cities.set(k,Right[j]);
            j++;
            k++;
        }
    }
}