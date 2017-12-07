import enums.Action;
import remote.GestionnaireRMI;

import java.rmi.Naming;
import java.util.Scanner;

/**
 * Classe main permettant de lancer un travailleur
 * Prend en paramètre : son id
 *
 * @author Edward Ransome
 * @author Michael Spierer
 */
public class Main {
    public static void main(String[] args) {
        if(args.length != 1){
            throw new IllegalArgumentException("Veuillez lancer le travailleur" +
                    " avec un paramètre: son id");
        }
        int id = Integer.parseInt(args[0]);
        boolean done = false;
        Scanner scanner = new Scanner(System.in);

        try {
            GestionnaireRMI gest = (GestionnaireRMI) Naming.lookup("rmi://localhost/Gestionnaire" + id);
            while(!done){
                System.out.println("Entrez "+ Action.CONSULT.getIndex() +" pour afficher la variable globale");
                System.out.println("Entrez "+ Action.SET.getIndex() +" pour modifier la variable globale");
                System.out.println("Entrez "+ Action.QUIT.getIndex() +" pour quitter");
                int input = scanner.nextInt();
                try {
                    Action a = Action.values()[input-1];

                    switch (a) {
                        case CONSULT: //On désire consulter la valeur de la variable
                            int value = gest.consult();
                            System.out.println("La valeur actuelle de la variable globale est de " + value);
                            break;

                        case SET: //On désire obtenir la section critique, modifier la variable, puis relacher la SC
                            System.out.println("Tentative d'obtenir la section critique...");
                            gest.waitForCriticalSection();
                            System.out.println("Section critique obtenue!");
                            int currentValue = gest.consult();
                            System.out.println("La valeur actuelle de la variable globale est de " + currentValue);
                            System.out.println("Entrez une nouvelle valeur pour la variable globale: ");
                            int newValue = scanner.nextInt();
                            gest.set(newValue);
                            gest.releaseCriticalSection();
                            break;

                        case QUIT: //Quitter le travailleur
                            done = true;
                            break;

                    }
                }catch (IndexOutOfBoundsException e ){
                    System.out.println("Saisie incorrecte. Veuillez reessayer\n");
                    continue;
                }

            }
        } catch (Exception e) {
            System.out.println("Erreur RMI detectee: " + e);
        }

        System.out.println("Au revoir!");
        scanner.close();
    }
}
