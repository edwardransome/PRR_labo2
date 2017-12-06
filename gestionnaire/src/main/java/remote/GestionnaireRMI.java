package remote;

import java.rmi.RemoteException;

/**
 * Interface implémenté par le gestionnaire afin de communiquer avec RMI pour les appels depuis un travailleur
 *
 * @author Edward Ransome
 * @author Michael Spierer
 */
public interface GestionnaireRMI extends java.rmi.Remote {

    int consult() throws RemoteException;
    void set(int i) throws RemoteException;

}
