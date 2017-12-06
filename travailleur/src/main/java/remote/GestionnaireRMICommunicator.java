/**
 * Interface permettant aux différents gestionnaires de communiquer entre eux avec des requêtes
 */
package remote;

import java.rmi.RemoteException;

public interface GestionnaireRMICommunicator extends java.rmi.Remote {

    public void receiveRequest(int id, long time) throws RemoteException;

    public void receiveRelease(int id, long time, int value) throws RemoteException;

    public void receiveResponse(int id, long time) throws RemoteException;

}