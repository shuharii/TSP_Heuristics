package heuristics;

// this class for define a link for A and B city.
public class Link {
    int LinkID;
    City A;
    City B;
    double dist;
    Link(int LinkID, City A, City B){
        this.LinkID = LinkID;
        this.dist = A.dist(B);
        this.A = A;
        this.B = B;
    }
}
