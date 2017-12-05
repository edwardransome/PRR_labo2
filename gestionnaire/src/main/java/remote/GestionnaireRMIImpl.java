package remote;

import java.rmi.RMISecurityManager;

public class GestionnaireRMIImpl implements  GestionnaireRMI {

    private int globalVariable;

    public int consult() {
        return 0;
    }

    public void set() {

    }

    public static void main(String[] args) {
        System.setSecurityManager(new RMISecurityManager());
    }
}
