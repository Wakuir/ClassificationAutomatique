public class PaireResultatCompteur<T> {

    private final T resultat;
    private final int compteur;

    public PaireResultatCompteur(T resultat, int compteur) {
        this.resultat = resultat;
        this.compteur = compteur;
    }

    public T getResultat() {
        return resultat;
    }

    public int getCompteur() {
        return compteur;
    }
}
