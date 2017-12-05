package remote;

import javafx.util.Pair;

import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Comparator;
import java.util.PriorityQueue;

public class GestionnaireRMIImpl extends UnicastRemoteObject implements GestionnaireRMI {

    private int globalVariable;

    private int id;
    private int numberOfSites;
    private long clock;

    //Queue de priorité qui va contenir les requêtes ordonnées par estampille
    private PriorityQueue<Pair<Integer, Long>> sites;

    public GestionnaireRMIImpl(int id, int numberOfSites) throws RemoteException {
        super();
        this.id = id;
        this.numberOfSites = numberOfSites;
        this.clock = 0;
        sites = new PriorityQueue<Pair<Integer, Long>>(numberOfSites, new Comparator<Pair<Integer, Long>>() {
            public int compare(Pair<Integer, Long> o1, Pair<Integer, Long> o2) {
                return Long.valueOf(o1.getValue()).compareTo(Long.valueOf(o2.getValue()));
            }
        });
    }

    /**
     * Permet de consulter la variable globale. Ne nécessite pas la possession de la section
     * critique.
     * @return  la valeur de la variable globale
     */
    public int consult() {
        return globalVariable;
    }

    /**
     * Permet au Travailleur de modifier la variable globale. Pourra être effectué uniquement
     * lorsque la section critique est accordée, c'est-à-dire la requête de ce site est la
     * plus ancienne de la queue.
     * @param i
     */
    public void set(int i) {
        //request critical section
        //wait for response
        //pop queue
        //do stuff

    }

    /**
     * Recoit une requete de section critique d'un site et l'ajoute a la queue
     * @param id  id du site effectuant la requete
     * @param time
     */
    public void receiveRequest(int id, long time){

    }

    /**
     * Recoit un message de relachement de Lamport. Retire de la queue la requête du site
     * correspondant
     * @param id  le site qui relache la section critique
     */
    public void receiveRelease(int id){

    }

    /**
     * Envoit une requete a tous les autres sites et ajoute sa propre requete a la queue
     */
    private void requestCriticalSection(){

    }

    /**
     * Envoit une quittance a un site confirmant la reception de la requete
     * @param id
     */
    private void sendResponse(int id){

    }

    /**
     * Permet de relacher la section critique. Retire la requete de ce site de la queue
     * et informe tous les autres sites de faire de même.
     */
    private void releaseCriticalSection(){

    }

}
