package remote;

import enums.TypeMessage;
import javafx.util.Pair;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class GestionnaireRMIImpl extends UnicastRemoteObject implements GestionnaireRMI, GestionnaireRMICommunicator {

    private int globalVariable;
    private int futureValue;

    private final int id;
    private final int numberOfSites;
    private long clock;

    private ArrayList<Pair<TypeMessage, Long>> sites;

    public GestionnaireRMIImpl(int id, int numberOfSites) throws RemoteException {
        super();
        this.id = id;
        this.numberOfSites = numberOfSites;
        this.clock = 0;
        sites = new ArrayList<>(numberOfSites);
        for(int i = 0; i < numberOfSites; ++i){
            sites.add(new Pair<>(TypeMessage.LIBERE, 0L));
        }
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
     * @param i la variable globale que l'on souhaite setter dans la section critique
     */
    public void set(int i) {
        futureValue = i;
        requestCriticalSection();
    }

    /**
     * Recoit une requete de section critique d'un site et l'ajoute a la queue
     * @param site l'id du site effectuant la requete
     * @param time estampille
     */
    public void receiveRequest(int site, long time){
        clock = Math.max(clock, time) + 1;

        sites.set(id, new Pair<>(TypeMessage.REQUETE, clock));
        sendResponse(id);

        if(sites.get(this.id).getKey() == TypeMessage.REQUETE && permission(this.id)){
            enterCriticalSection();
        }

    }

    /**
     * methode privé permettant de savoir si l'on peut avoir acces a la section critique
     * @param site l'id du site qui veut savoir s'il a la permission d'entrée en section critique
     * @return true si on peut rentrer en section critique
     */
    private boolean permission(int site){
        boolean canAccess = true;
        for(int i = 0; i< numberOfSites ; i++){
            if(i != id){
                canAccess &= sites.get(site).getValue() < sites.get(i).getValue()
                        || (sites.get(site).getValue() == sites.get(i).getValue()
                        && site < i);
            }
        }
        return canAccess;
    }

    /**
     * Recoit un message de relachement de Lamport. Retire de la queue la requête du site
     * correspondant et modifie la variable globale
     * @param site l'id du site qui relache la section critique
     */
    public void receiveRelease(int site, long time, int value){
        clock = Math.max(clock, time) + 1;

        globalVariable = value;
        sites.set(id, new Pair<>(TypeMessage.LIBERE, clock));

        if(sites.get(this.id).getKey() == TypeMessage.REQUETE && permission(this.id)){
            enterCriticalSection();
        }
    }

    /**
     * Reçoit la réponse (quittance) d'un autre site.
     * @param site l'id du site qui envoit la réponse
     * @param time  estampille
     */
    public void receiveResponse(int site, long time){
        clock = Math.max(clock, time) + 1;

        Pair<TypeMessage, Long> dernierMessage = sites.get(site);
        if(dernierMessage.getKey() != TypeMessage.REQUETE){
            sites.set(site, new Pair<>(TypeMessage.QUITTANCE, time));
        }

        if(sites.get(this.id).getKey() == TypeMessage.REQUETE && permission(this.id)){
            enterCriticalSection();
        }
    }


    /**
     * Envoit une requete a tous les autres sites et ajoute sa propre requete a la queue
     */
    private void requestCriticalSection(){

        clock++;
        sites.set(this.id,new Pair<>(TypeMessage.REQUETE,clock));
        for(int i = 0; i < numberOfSites; ++i){
            if(i != this.id){
                try {
                    final int currentSite = i;
                    Thread T = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                GestionnaireRMICommunicator gest =
                                        (GestionnaireRMICommunicator) Naming.lookup("rmi://localhost/Gestionnaire" + currentSite);
                                gest.receiveRequest(id, clock);
                            } catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                    });
                    T.start();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Envoit une quittance a un site confirmant la reception de la requete
     * @param site le site a qui envoyer la quittance
     */
    private void sendResponse(int site){
        try {
            GestionnaireRMICommunicator gest =
                    (GestionnaireRMICommunicator) Naming.lookup("rmi://localhost/Gestionnaire" + id);
            gest.receiveResponse(this.id, clock);
        } catch (Exception e){
            e.printStackTrace();
        }
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
     * Permet d'entrer en section critique. Met à jour la valeur de la variable globale puis sort de la section
     */
    private void enterCriticalSection() {
        globalVariable = futureValue;
        releaseCriticalSection();
    }
}