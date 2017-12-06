package remote;

import java.rmi.RemoteException;

public interface GestionnaireRMI extends java.rmi.Remote {

    int consult() throws RemoteException;
    void set(int i) throws RemoteException;
    void waitForCriticalSection() throws RemoteException;
    void releaseCriticalSection() throws RemoteException;

}
