package remote;

import java.rmi.RemoteException;

public interface TravailleurRMI extends java.rmi.Remote {
    String test() throws RemoteException;
}
