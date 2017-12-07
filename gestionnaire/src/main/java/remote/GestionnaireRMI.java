package remote;

import java.rmi.RemoteException;

/**
 * Interface implémentée par le gestionnaire afin qu'un travailleur puisse utiliser RMI sur ses méthodes
 *
 * @author Edward Ransome
 * @author Michael Spierer
 */
public interface GestionnaireRMI extends java.rmi.Remote {

    int consult() throws RemoteException;

    void set(int i) throws RemoteException;

    void waitForCriticalSection() throws RemoteException;

    void releaseCriticalSection() throws RemoteException;

}
