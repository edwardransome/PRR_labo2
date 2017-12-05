import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        boolean done = false;
        Scanner scanner = new Scanner(System.in);

        while(!done){
            System.out.println("Entrez 1 pour afficher la variable globale");
            System.out.println("Entrez 2 pour modifier la variable globale");
            System.out.println("Entrez 3 pour quitter");
            int input = scanner.nextInt();

            switch (input){
                case 1:{

                    break;
                }

                case 2:{
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

        System.out.println("Au revoir!");
        scanner.close();
    }
}
