package heuristics;

import java.util.ArrayList;

public class dividaconq {

    public void permutation(ArrayList<City> small, ArrayList<City> cities, ArrayList<City>[] perm) {
        int count = 0;
        if(cities.size()==0) {
            perm[count]=small;
            count++;
            return;
        }
        ArrayList<City> small1=new ArrayList<City>();
        ArrayList<City> cities1=new ArrayList<City>();
        City a=null;
        for(int i=0;i<cities.size();i++) {
            cities1=(ArrayList<City>) cities.clone();
            if(small==null) {
                small1=null;
            }
            else {
                small1=(ArrayList<City>)small.clone();
            }
            a=cities1.get(i);
            assert small1 != null;
            small1.add(a);
            cities1.remove(i);
            permutation(small1,cities1,perm);
        }
    }
}
