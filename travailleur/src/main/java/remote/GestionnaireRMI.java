package remote;

/**
 * Interface implémentée par le gestionnaire afin qu'un travailleur puisse utiliser RMI sur ses méthodes
 *
 * @author Edward Ransome
 * @author Michael Spierer
 */
public interface GestionnaireRMI extends java.rmi.Remote {

    int consult();

    void set(int i);

    void waitForCriticalSection();

    void releaseCriticalSection();

}