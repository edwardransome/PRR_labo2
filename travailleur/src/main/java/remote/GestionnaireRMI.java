package remote;

public interface GestionnaireRMI extends java.rmi.Remote {

    int consult();
    void set(int i);
    void waitForCriticalSection();
    void releaseCriticalSection();

}