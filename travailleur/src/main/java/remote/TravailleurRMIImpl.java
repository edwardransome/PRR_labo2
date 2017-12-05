package remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class TravailleurRMIImpl extends UnicastRemoteObject implements TravailleurRMI{

    public TravailleurRMIImpl() throws RemoteException {
    }

    public String test() throws RemoteException {
        return "";
    }
}
