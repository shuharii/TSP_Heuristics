package heuristics;

// City class.
public class City {
    private final double Locx;
    private final double Locy;
    private final int CityID;

public City(int CityID, double Locx, double Locy){
    super();
    this.CityID = CityID;
    this.Locx = Locx;
    this.Locy = Locy;
}

public Integer getCityID(){
    return CityID;
}

public double getLocx(){
    return Locx;
}

public double getLocy(){
    return Locy;
}

public double dist(City otherCity){
    //System.out.println("dist : "+Math.sqrt( Math.pow(this.Locx - otherCity.Locx,2) + Math.pow( this.Locy - otherCity.Locy,2)));
    return Math.sqrt( Math.pow(this.Locx - otherCity.Locx,2) + Math.pow( this.Locy - otherCity.Locy,2));
}
}
