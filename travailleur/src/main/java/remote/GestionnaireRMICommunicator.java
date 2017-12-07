package remote;

/**
 * Interface permettant aux différents gestionnaires de communiquer (avec RMI) entre eux avec des requêtes
 *
 * @author Edward Ransome
 * @author Michael Spierer
 */
public interface GestionnaireRMICommunicator extends java.rmi.Remote {

    void receiveRequest(int id, long time);

    void receiveRelease(int id, long time, int value);

    void receiveResponse(int id, long time);

}