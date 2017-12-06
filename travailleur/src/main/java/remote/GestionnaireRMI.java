package remote;

public interface GestionnaireRMI extends java.rmi.Remote {

    public int consult();
    public void set(int i);

}
