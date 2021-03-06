/**
 */
package remote;

import java.rmi.RemoteException;


/**
 * Interface permettant aux différents gestionnaires de communiquer (avec RMI) entre eux avec des requêtes
 *
 * @author Edward Ransome
 * @author Michael Spierer
 */
public interface GestionnaireRMICommunicator extends java.rmi.Remote {

    void receiveRequest(int id, long time) throws RemoteException;

    void receiveRelease(int id, long time, int value) throws RemoteException;

    void receiveResponse(int id, long time) throws RemoteException;

}