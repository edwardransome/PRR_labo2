package remote;

import javafx.util.Pair;

import java.rmi.RMISecurityManager;
import java.util.Comparator;
import java.util.PriorityQueue;

public class GestionnaireRMIImpl implements  GestionnaireRMI {

    private int globalVariable;

    private int id;
    private int numberOfSites;

    //Queue de priorité qui va contenir les requêtes ordonnées par estampille
    private PriorityQueue<Pair<Integer, Long>> sites;

    public GestionnaireRMIImpl(int id, int numberOfSites) {
        super();
        this.id = id;
        this.numberOfSites = numberOfSites;
        sites = new PriorityQueue<Pair<Integer, Long>>(numberOfSites, new Comparator<Pair<Integer, Long>>() {
            public int compare(Pair<Integer, Long> o1, Pair<Integer, Long> o2) {
                return Long.valueOf(o1.getValue()).compareTo(Long.valueOf(o2.getValue()));
            }
        });
    }

    public int consult() {
        return globalVariable;
    }

    public void set() {
        //request critical section
        //wait for response
        //pop queue
        //do stuff

    }

}
