package heuristics;

public class Links {
    Links(){ }
    // merge sort for links
    void merge(Link[] arr, int l, int m, int r) {
        int n1 = m - l + 1;
        int n2 = r - m;
        Link[] L = new Link[n1];
        Link[] R = new Link[n2];
        System.arraycopy(arr, l, L, 0, n1);

        for (int j = 0; j < n2; ++j)
            R[j] = arr[m + 1 + j];
        int i = 0, j = 0;
        int k = l;
        while (i < n1 && j < n2) {
            if (L[i].dist <= R[j].dist) {
                arr[k] = L[i];
                i++;
            }
            else {
                arr[k] = R[j];
                j++;
            }
            k++;
        }
        while (i < n1) {
            arr[k] = L[i];
            i++;
            k++;
        }
        while (j < n2) {
            arr[k] = R[j];
            j++;
            k++;
        }
    }
    void sort(Link[] arr, int l, int r) {
        if (l < r) {
            int m =l+ (r-l)/2;
            sort(arr, l, m);
            sort(arr, m + 1, r);
            merge(arr, l, m, r);
        }
    }
}
