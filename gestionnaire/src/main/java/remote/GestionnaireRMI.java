package remote;

import java.rmi.RemoteException;

public interface GestionnaireRMI extends java.rmi.Remote {

    public int consult() throws RemoteException;
    public void set(int i) throws RemoteException;

}
