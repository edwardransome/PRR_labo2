package remote;

import enums.TypeMessage;
import javafx.util.Pair;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 * Classe implémentant l'interface de communication RMI inter-gestionnaires ainsi que l'interface de communication
 * avec les travailleurs.
 *
 * @author Edward Ransome
 * @author Michael Spierer
 */
public class GestionnaireRMIImpl extends UnicastRemoteObject implements GestionnaireRMI, GestionnaireRMICommunicator {

    // Variable globale
    private int globalVariable;
    // Id du site
    private final int id;
    // Nombre de site total
    private final int numberOfSites;
    // Horloge logique local
    private long clock;
    // Vaut true si la section critique est accordé à ce site
    private Boolean scAccordee;
    // Une Arraylist de Pair pour l'implémentation de l'algorithme de Lamport où chaque index correspond à un site
    // et chaque Pair correspond à un type de messages associé à une estampille.
    private ArrayList<Pair<TypeMessage, Long>> sites;

    public GestionnaireRMIImpl(int id, int numberOfSites) throws RemoteException {
        super();
        this.id = id;
        this.numberOfSites = numberOfSites;
        this.clock = 0;
        scAccordee = false;
        sites = new ArrayList<>(numberOfSites);
        for (int i = 0; i < numberOfSites; ++i) {
            sites.add(new Pair<>(TypeMessage.LIBERE, 0L));
        }
    }


    /**
     * Permet de consulter la variable globale. Ne nécessite pas la possession de la section
     * critique.
     *
     * @return la valeur de la variable globale
     */
    public int consult() {
        return globalVariable;
    }

    /**
     * Permet au Travailleur de modifier la variable globale. Pourra être effectué uniquement
     * lorsque la section critique est accordée, c'est-à-dire la requête de ce site est la
     * plus ancienne de la queue.
     *
     * @param i la variable globale que l'on souhaite setter dans la section critique
     */
    public void set(int i) {
        if (scAccordee) {
            globalVariable = i;
        }
    }

    public synchronized void waitForCriticalSection() {
        requestCriticalSection();
        while (!scAccordee) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Recoit une requete de section critique d'un site et l'ajoute a la queue
     *
     * @param site l'id du site effectuant la requete
     * @param time estampille
     */
    public void receiveRequest(int site, long time) {
        clock = Math.max(clock, time) + 1;

        sites.set(site, new Pair<>(TypeMessage.REQUETE, clock));
        sendResponse(site);

        if (sites.get(id).getKey() == TypeMessage.REQUETE && permission(id)) {
            enterCriticalSection();
        }

    }

    /**
     * methode privé permettant de savoir si l'on peut avoir acces a la section critique
     *
     * @param site l'id du site qui veut savoir s'il a la permission d'entrée en section critique
     * @return true si on peut rentrer en section critique
     */
    private boolean permission(int site) {
        boolean canAccess = true;
        for (int i = 0; i < numberOfSites; i++) {
            if (i != id) {
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
     *
     * @param site l'id du site qui relache la section critique
     */
    public void receiveRelease(int site, long time, int value) {
        clock = Math.max(clock, time) + 1;

        globalVariable = value;
        sites.set(site, new Pair<>(TypeMessage.LIBERE, clock));

        if (sites.get(id).getKey() == TypeMessage.REQUETE && permission(id)) {
            enterCriticalSection();
        }
    }

    /**
     * Reçoit la réponse (quittance) d'un autre site.
     *
     * @param site l'id du site qui envoit la réponse
     * @param time estampille
     */
    public void receiveResponse(int site, long time) {
        clock = Math.max(clock, time) + 1;

        Pair<TypeMessage, Long> dernierMessage = sites.get(site);
        if (dernierMessage.getKey() != TypeMessage.REQUETE) {
            sites.set(site, new Pair<>(TypeMessage.QUITTANCE, time));
        }

        if (sites.get(id).getKey() == TypeMessage.REQUETE && permission(id)) {
            enterCriticalSection();
        }
    }


    /**
     * Envoit une requete a tous les autres sites et ajoute sa propre requete a la queue
     */
    private void requestCriticalSection() {
        clock++;
        sites.set(id, new Pair<>(TypeMessage.REQUETE, clock));
        for (int i = 0; i < numberOfSites; ++i) {
            if (i != id) {
                try {
                    final int currentSite = i;
                    //Pour éviter de devoir attendre la fin de l'execution de toutes les méthodes receiveRequest, on
                    //lance des threads, ce qui permet d'envoyer tous les messages sans attendre.
                    Thread T = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                GestionnaireRMICommunicator gest =
                                        (GestionnaireRMICommunicator) Naming.lookup("rmi://localhost/Gestionnaire" + currentSite);
                                gest.receiveRequest(id, clock);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    T.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Envoit une quittance a un site confirmant la reception de la requete
     *
     * @param site le site a qui envoyer la quittance
     */
    private void sendResponse(int site) {
        try {
            GestionnaireRMICommunicator gest =
                    (GestionnaireRMICommunicator) Naming.lookup("rmi://localhost/Gestionnaire" + site);
            gest.receiveResponse(id, clock);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Permet de relacher la section critique. Retire la requete de ce site de la file
     * et informe tous les autres sites de faire de même.
     */
    public void releaseCriticalSection() {
        //appeler receiveRelease sur tous les sites
        scAccordee = false;
        for (int i = 0; i < numberOfSites; ++i) {
            try {
                GestionnaireRMICommunicator gest =
                        (GestionnaireRMICommunicator) Naming.lookup("rmi://localhost/Gestionnaire" + i);
                gest.receiveRelease(id, clock, globalVariable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Permet d'entrer en section critique. Notifie les threads en attente que la section est disponible.
     */
    private synchronized void enterCriticalSection() {
        scAccordee = true;
        notify();
    }
}