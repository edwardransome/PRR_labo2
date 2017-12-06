/**
 * Interface permettant aux différents gestionnaires de communiquer entre eux avec des requêtes
 */
package remote;

public interface GestionnaireRMICommunicator extends java.rmi.Remote {

    public void receiveRequest(int id, long time);

    public void receiveRelease(int id, long time, int value);

    public void receiveResponse(int id, long time);

}