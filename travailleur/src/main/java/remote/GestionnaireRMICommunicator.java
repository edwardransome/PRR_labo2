package remote;

import java.rmi.RemoteException;

/**
 * Interface permettant aux différents gestionnaires de communiquer (avec RMI) entre eux avec des requêtes
 *
 * @author Edward Ransome
 * @author Michael Spierer
 */
public interface GestionnaireRMICommunicator extends java.rmi.Remote {

    public void receiveRequest(int id, long time);

    public void receiveRelease(int id, long time, int value);

    public void receiveResponse(int id, long time);

}