import remote.GestionnaireRMI;

import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        if(args.length != 1){
            throw new IllegalArgumentException("Veuillez lancer le gestionnaire avec un paramètre: son id");
        }
        int id = Integer.parseInt(args[0]);
        boolean done = false;
        Scanner scanner = new Scanner(System.in);

        try {
            //System.setSecurityManager(new RMISecurityManager());
            GestionnaireRMI gest = (GestionnaireRMI) Naming.lookup("rmi://localhost/Gestionnaire" + id);
            while(!done){
                System.out.println("Entrez 1 pour afficher la variable globale");
                System.out.println("Entrez 2 pour modifier la variable globale");
                System.out.println("Entrez 3 pour quitter");
                int input = scanner.nextInt();

                switch (input){
                    case 1:{
                        int value = gest.consult();
                        System.out.println("La valeur actuelle de la variable globale est de " + value);
                        break;
                    }

                    case 2:{
                        System.out.println("Entrez une nouvelle valeur pour la variable globale: ");
                        int newValue = scanner.nextInt();
                        gest.set(newValue);
                        break;
                    }

                    case 3:{
                        done = true;
                        break;
                    }

                    default:{
                        System.out.println("Saisie incorrecte. Veuillez reessayer\n");
                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Au revoir!");
        scanner.close();
    }
}
