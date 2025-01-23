import java.util.ArrayList;

public class UtilitairePaireChaineEntier {

    /**
     * Recherche de la chaine dans le vecteur de PaireChaineEntier
     * @param listePaires vecteur de PaireChaineEntier dans lequel il faut chercher l'élément dont la chaine est passée en paramètre
     * @param chaine chaine à rechercher
     * @return l'indice de l'élément ayant pour attribut 'chaine' chaine dans le vecteur si elle est trouvée, -1 sinon
     */
    public static PaireResultatCompteur<Integer> indicePourChaine(ArrayList<PaireChaineEntier> listePaires, String chaine) {
        int i = 0;
        //parcours complet du vecteur jusqu'à ce que i == size() ou que chaine trouvée
        while (i < listePaires.size() && !listePaires.get(i).getChaine().equalsIgnoreCase(chaine)) {
            i++;
        }

        //Si i == size, alors chaine non trouvée, on retourne -1.
        if (i == listePaires.size()) {
            return new PaireResultatCompteur<>(-1, i + 1);
        } else {
            // String trouvé on retourne l'indice
            return new PaireResultatCompteur<>(i, i + 1);
        }
    }

    //Recherche dichotomique de la chaine dans le vecteur de PaireChaineEntier
    public static PaireResultatCompteur<Integer> indicePourChaineDicho(ArrayList<PaireChaineEntier> listePaires, String chaine) {
        int debut = 0;
        int fin = listePaires.size() - 1;
        int milieu;
        int compteur = 0;
        // Tant que le début est inférieur à la fin
        while (debut <= fin) {
            //calcul du milieu
            milieu = (debut + fin) / 2;
            if (listePaires.get(milieu).getChaine().compareTo(chaine) > 0) {
                fin = milieu - 1;
            } else {
                debut = milieu + 1;
            }
            compteur++;
        }

        if (fin < 0 || listePaires.get(fin).getChaine().compareTo(chaine) != 0) {
            // Si chaine non trouvée
            return new PaireResultatCompteur<>(-1, compteur);
        } else {
            // Si chaine trouvée
            return new PaireResultatCompteur<>(fin, compteur);
        }
    }

    /**
     * Retourne l'entier associé à la chaine passée en paramètre dans le vecteur de PaireChaineEntier
     * @param listePaires le vecteur de PaireChaineEntier dans lequel il faut chercher l'entier associé à la chaine
     * @param chaine la chaine dont on veut connaitre l'entier associé
     * @return l'entier associé à la chaine si elle est trouvée, 0 sinon
     */
    public static PaireResultatCompteur<Integer> entierPourChaine(ArrayList<PaireChaineEntier> listePaires, String chaine) {
        int i = 0;
        //parcours complet du vecteur jusqu'à ce que i == size() ou que chaine trouvée
        while (i < listePaires.size() && !listePaires.get(i).getChaine().equalsIgnoreCase(chaine)) {
            i++;
        }
        if (i == listePaires.size() || !listePaires.get(i).getChaine().equals(chaine)) {
            // Si chaine non trouvée
            return new PaireResultatCompteur<>(0, i + 1);
        } else {
            // Si chaine trouvée
            return new PaireResultatCompteur<>(listePaires.get(i).getEntier(), i + 1);
            // retourne le poids à l'indice ou s'est arrêtée la boucle
        }
    }

    // Recherche dichotomique de l'entier dans le vecteur de PaireChaineEntier
    public static PaireResultatCompteur<Integer> entierPourChaineDicho(ArrayList<PaireChaineEntier> listePaires, String chaine) {
        int indice = indicePourChaineDicho(listePaires, chaine).getResultat();
        return new PaireResultatCompteur<>(indice == -1 ? 0 : listePaires.get(indice).getEntier(), indice);
    }

    /**
     * Retourne la chaine ayant l'entier le plus grand dans le vecteur de PaireChaineEntier
     * @param listePaires le vecteur de PaireChaineEntier dans lequel il faut chercher la chaine ayant l'entier le plus grand
     * @return la chaine associée l'entier le plus grand dans le vecteur (Première occurrence du max si égalité)
     */
    public static String chaineMax(ArrayList<PaireChaineEntier> listePaires) {
        // Valeur de la première paire du vecteur
        PaireChaineEntier paireChaineEntierMax = listePaires.get(0);

        // Parcours de listePaire[1..size()-1]
        for (int i = 1; i < listePaires.size(); i++) {
            PaireChaineEntier paireCourante = listePaires.get(i);
            if (paireCourante.getEntier() > paireChaineEntierMax.getEntier()) {
                // L'entier de la paire courante est plus grand que l'entier max
                paireChaineEntierMax = paireCourante;
            }
        }

        return paireChaineEntierMax.getChaine();
    }

    /**
     * Retourne la moyenne des entiers du vecteur de PaireChaineEntier
     * @param listePaires le vecteur de PaireChaineEntier dont on veut calculer la moyenne des entiers
     * @return la moyenne des entiers du vecteur
     */
    public static float moyenne(ArrayList<PaireChaineEntier> listePaires) {
        float som = 0;
        //parcours du vecteur et création de la somme
        for (PaireChaineEntier listePaire : listePaires) {
            som = som + listePaire.getEntier();
        }
        //renvoie de la moyenne
        return som/listePaires.size();
    }

}
