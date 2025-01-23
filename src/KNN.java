import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class KNN {

    /**
     * Calcule la distance entre deux dépêches (le nombre de mots significatifs en commun)
     * @return la distance entre les deux dépêches
     */
    public static int distance(Depeche d1, Depeche d2) {
        // Vecteur de mots vides
        ArrayList<String> motsNonSignificatifs = new ArrayList<>(Arrays.asList(
                "le", "la", "les", "de", "des", "du", "un", "une", "et", "ou", "donc", "ni", "car", "ce", "cet", "cette", "a", "à", "il", "elle", "on"
        ));

        int distance = 0;
        // Parcours des mots de la première dépêche
        for (String mot : d1.getMots()) {
            // Si le mot n'est pas vide et qu'il est présent dans la deuxième dépêche
            if (!motsNonSignificatifs.contains(mot) && d2.getMots().contains(mot)) {
                // La distance est décrémentée.
                distance--;
            }
        }
        return distance;
    }

    /**
     * Retourne la catégorie la plus fréquente parmi les k plus proches voisins
     * @param baseApprentissage la base d'apprentissage
     * @param categories les possibles catégories
     * @param depeche la dépêche à classer
     * @param k le nombre de voisins minimum à considérer
     */
    public static String categorieEstimee(ArrayList<Depeche> baseApprentissage, ArrayList<Categorie> categories, Depeche depeche, int k) {
        ArrayList<PaireChaineEntier> distances = new ArrayList<>();
        // Calcul de la distance pour chaque dépêche
        for (Depeche d : baseApprentissage) {
            distances.add(new PaireChaineEntier(d.getCategorie(), distance(depeche, d)));
        }

        // Tri des distances par ordre croissant
        distances.sort(Comparator.comparingInt(PaireChaineEntier::getEntier));

        // Récupération des k plus proches voisins
        ArrayList<PaireChaineEntier> plusProchesVoisins = new ArrayList<>(distances.subList(0, k));

        // Calcul des fréquences des catégories parmi les k plus proches voisins
        ArrayList<PaireChaineEntier> frequences = new ArrayList<>();

        for (Categorie c : categories) {
            // Compte le nombre d'occurences de la catégorie c parmi les k plus proches voisins
            frequences.add(new PaireChaineEntier(c.getNom(), plusProchesVoisins.stream().filter(p -> p.getChaine().equals(c.getNom())).toList().size()));
        }

        return UtilitairePaireChaineEntier.chaineMax(frequences);
    }

    public static void testKNN(ArrayList<Depeche> baseApprentissage, ArrayList<Depeche> depeches, ArrayList<Categorie> categories, String nomFichier) {
        ArrayList<PaireChaineEntier> nbReussites = new ArrayList<>();
        //Incrémentation du nom et d'un nombre de réussites (valeur initiale 0) pour chaque catégorie
        for (Categorie categorie : categories) {
            nbReussites.add(new PaireChaineEntier(categorie.getNom(), 0));
        }
        try {
            FileWriter file = new FileWriter(nomFichier);
            //Parcours des dépêches
            int i = 0;
            while (i < depeches.size()) {
                String categorieEstimee = categorieEstimee(baseApprentissage, categories, depeches.get(i), 1);
                file.write(baseApprentissage.get(i).getId() + " : " + categorieEstimee + "\n");
                //Si notre score le plus haut est égal à la catégorie de la dépêche, on ajoute +1 à la réussite de la catégorie.
                if (categorieEstimee.compareTo(baseApprentissage.get(i).getCategorie()) == 0) {
                    int indice = UtilitairePaireChaineEntier.indicePourChaine(nbReussites, baseApprentissage.get(i).getCategorie()).getResultat();
                    nbReussites.get(indice).setEntier(nbReussites.get(indice).getEntier() + 1);
                }
                i++;
            }
            //Ecrit le taux de réussite de chaque catégorie
            for (Categorie categorie : categories) {
                int indice = UtilitairePaireChaineEntier.indicePourChaine(nbReussites, categorie.getNom()).getResultat();
                file.write(nbReussites.get(indice).getChaine() + " : " + ((float) nbReussites.get(indice).getEntier()) + "%\n");
            }
            file.write("Moyenne : " + UtilitairePaireChaineEntier.moyenne(nbReussites) + "%\n");
            file.close();
        } catch (IOException e) {
            // Gère l'erreur lorsque le fichier n'existe pas
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        ArrayList<Depeche> baseApprentissage = Classification.lectureDepeches("./depeches.txt");
        ArrayList<Depeche> test = Classification.lectureDepeches("./test.txt");
        ArrayList<Categorie> categories = new ArrayList<>(Arrays.asList(
                new Categorie("CULTURE"),
                new Categorie("ECONOMIE"),
                new Categorie("POLITIQUE"),
                new Categorie("SCIENCES"),
                new Categorie("SPORT")));

        testKNN(baseApprentissage, test, categories,"resultatsKNN.txt");
    }
}
