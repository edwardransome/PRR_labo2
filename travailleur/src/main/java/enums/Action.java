package enums;

/**
 * Enum déclarant les différents types d'actions que l'on peut faire dans le Main
 *
 * @author Edward Ransome
 * @author Michael Spierer
 */

public enum Action {
    CONSULT, SET, QUIT;

    public int getIndex(){
        return ordinal()+1;
    }
}
