/**
 * PRR Laboratoire 2: RMI
 *
 * Edward Ransome & Michael Spierer
 *
 * Installation et utilisation:
 * Pour utiliser le projet, il faut compiler les fichiers source avec la
 * commande javac ou directement depuis un IDE. Depuis la racine des fichiers
 * compilés du gestionnaire, il faut lancer le registre RMI. Il sera ensuite
 * possible de lancer des gestionnaires et des travailleurs. L'utilisation de
 * la commande rmic n'est pas nécessaire.
 *
 * Les gestionnaires prennent deux paramètres: l'identifiant de leur site et le
 * nombre total de sites.
 * Les travailleurs prennent un seul paramètre: l'identifiant de leur site.
 *
 * Nous faisons l'hypothèse que tous les gestionnaires ont été lancés avec de
 * lancer les travailleurs. L'adresse
 * utilisée pour la communication RMI est rmi://localhost/{Nom du gestionnaire}.
 *
 * Conception:
 * Le projet utilise l'algorithme de Lamport pour gérer une section critique.
 * Dû à l'utilisation de RMI, les messages de l'algorithme sont remplacés par
 * des appels de fonction. Pour envoyer un message de libération à un site
 * par exemple, il faut appeler sa méthode receiveRelease(int id, long time). Le
 * système d'horloge logique, lui, reste inchangé.
 *
 * Chaque gestionnaire a donc trois méthodes, une par message, permettant la
 * communication entre les sites. Les méthodes accessibles par les travailleurs
 * permettent d'attendre une section critique, obtenir la valeur actuelle de la
 * variable globale (ne nécessite pas l'obtention de la section critique) et de
 * relacher la section critique. L'attente de la section critique se fait par
 * l'utilisation de wait()/notify(). Quand un gestionnaire en attente peut
 * entrer en section critique, un notify() va réveiller un thread de ce
 * gestionnaire qui attendait sur la section critique.
 */

import remote.GestionnaireRMIImpl;

import java.rmi.Naming;

/**
 * Classe main permettant de lancer un gestionnaire
 * Prend en paramètre : son id et le nombre de site total
 *
 * @author Edward Ransome
 * @author Michael Spierer
 */
public class Main {
    public static void main(String[] args) throws Exception {
        //Lecture des arguments et initialisation
        if (args.length != 2) {
            throw new IllegalArgumentException("Veuillez lancer le gestionnaire avec deux paramètres: son id et le nombre de sites");
        }
        int id = Integer.parseInt(args[0]);
        int numberOfSites = Integer.parseInt(args[1]);

        GestionnaireRMIImpl gest;
        try {
            String serverName = "Gestionnaire" + id;
            gest = new GestionnaireRMIImpl(id, numberOfSites);
            Naming.rebind("//localhost/" + serverName, gest);
            System.out.println("Gestionnaire " + serverName + " est pret!");
        } catch (Exception e) {
            System.out.println("Exception a l'enregistrement: " + e);
        }
    }

}
