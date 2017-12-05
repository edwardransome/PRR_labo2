import remote.GestionnaireRMIImpl;

import java.rmi.RMISecurityManager;

public class Main {
    public static void main(String[] args) {
        //Lecture des arguments et initialisation
        if(args.length != 2){
            throw new IllegalArgumentException("Veuillez lancer le gestionnaire avec deux paramètres: son id et le nombre de sites");
        }
        int id = Integer.parseInt(args[0]);
        int numberOfSites = Integer.parseInt(args[1]);

        GestionnaireRMIImpl gest = new GestionnaireRMIImpl(id, numberOfSites);
        System.setSecurityManager(new RMISecurityManager());
        try{
            String serverName = "Gestionnaire"+id;

        } catch(Exception e){
            System.out.println("Exception a l'enregistrement: " + e);
        }
    }

}
