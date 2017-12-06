package remote;

import javafx.util.Pair;

import javax.sql.rowset.Predicate;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class GestionnaireRMIImpl extends UnicastRemoteObject implements GestionnaireRMI, GestionnaireRMICommunicator {

    private int globalVariable;
    private int futureValue;

    private int id;
    private int numberOfSites;
    private long clock;
    private int numberOfResponses;

    //Queue de priorité qui va contenir les requêtes ordonnées par estampille
    private PriorityQueue<Pair<Integer, Long>> sites;

    public GestionnaireRMIImpl(int id, int numberOfSites) throws RemoteException {
        super();
        this.id = id;
        this.numberOfSites = numberOfSites;
        this.clock = 0;
        this.numberOfResponses = 0;
        sites = new PriorityQueue<Pair<Integer, Long>>(numberOfSites, new Comparator<Pair<Integer, Long>>() {
            public int compare(Pair<Integer, Long> o1, Pair<Integer, Long> o2) {
                int comp = Long.valueOf(o1.getValue()).compareTo(Long.valueOf(o2.getValue()));
                return comp == 0 ? Integer.valueOf(o1.getKey()).compareTo(Integer.valueOf(o2.getKey())) : comp;
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
        futureValue = i;
        requestCriticalSection();
    }

    /**
     * Recoit une requete de section critique d'un site et l'ajoute a la queue
     * @param id  id du site effectuant la requete
     * @param time estampille
     */
    public void receiveRequest(int id, long time){

    }

    /**
     * Recoit un message de relachement de Lamport. Retire de la queue la requête du site
     * correspondant et modifie la variable globale
     * @param id  le site qui relache la section critique
     */
    public void receiveRelease(int id, long time, int value){
        sites.removeIf( p -> p.getKey() == id);

        if(!sites.isEmpty()){
            if(sites.peek().getKey() == this.id){ //Notre requete est la plus récente
                enterCriticalSection();
            }
        }
    }

    /**
     * Reçoit la réponse (quittance) d'un autre site.
     * @param id  le site qui envoit la réponse
     * @param time  estampille
     */
    public void receiveResponse(int id, long time){
        numberOfResponses++;
        if(numberOfResponses == numberOfSites - 1){
            if(sites.peek().getKey() == id){
                //on peut entrer en section critique
                enterCriticalSection();
            }else{
                //on ne peut pas
            }
        }

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
        //appeler receiveRelease sur tous les sites
        for(int i = 0; i < numberOfSites; ++i){
            try {
                GestionnaireRMICommunicator gest =
                        (GestionnaireRMICommunicator) Naming.lookup("rmi://localhost/Gestionnaire" + i);
                gest.receiveRelease(id, clock, globalVariable);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Permet d'entrer en section critique. Met à jour la valeur de la variable globale puis sors de la section
     */
    private void enterCriticalSection() {
        globalVariable = futureValue;
        numberOfResponses = 0;
        releaseCriticalSection();
    }


}
