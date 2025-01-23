import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Categorie {

    private final String nom; // Le nom de la catégorie. Ex : sport, politique, etc...
    private ArrayList<PaireChaineEntier> lexique; // Le lexique de la catégorie

    // constructeur
    public Categorie(String nom) {
        this.nom = nom;
    }


    public String getNom() {
        return nom;
    }


    public ArrayList<PaireChaineEntier> getLexique() {
        return lexique;
    }


    // Initialisation du lexique de la catégorie à partir du contenu d'un fichier texte
    public void initLexique(String nomFichier) {
        try {
            // lecture du fichier d'entrée
            FileInputStream file = new FileInputStream(nomFichier);
            Scanner scanner = new Scanner(file);
            lexique = new ArrayList<>();
            int numLigne = 0;
            while (scanner.hasNextLine()) {
                String ligne = scanner.nextLine();
                String[] mots = ligne.split(":");
                if (mots.length == 2) {
                    PaireChaineEntier paire = new PaireChaineEntier(mots[0], Integer.parseInt(mots[1]));
                    lexique.add(paire);
                } else {
                    System.out.println("Erreur lors de la lecture du fichier '" + nomFichier + "', ligne" + numLigne + ".");
                }
                numLigne++;
            }
            scanner.close();
        } catch (IOException e) {
            System.out.println("Erreur lors de la lecture du fichier '" + nomFichier + ".");
        }
    }

    //calcul du score d'une dépêche pour cette catégorie
    public PaireResultatCompteur<Integer> score(Depeche d) {
        int score = 0;
        int compteur = 0;
        // Parcours des mots de la dépêche
        for (String mot : d.getMots()) {
            // Parcours des mots du lexique
            PaireResultatCompteur<Integer> paire = UtilitairePaireChaineEntier.entierPourChaine(lexique, mot);
            score += paire.getResultat();
            compteur += paire.getCompteur();
        }

        return new PaireResultatCompteur<>(score, compteur);
    }

    public PaireResultatCompteur<Integer> scoreLexiqueTrie(Depeche d) {
        int score = 0;
        int compteur = 0;
        // Parcours des mots de la dépêche
        for (String mot : d.getMots()) {
            // Parcours des mots du lexique
            PaireResultatCompteur<Integer> paire = UtilitairePaireChaineEntier.entierPourChaineDicho(lexique, mot);
            score += paire.getResultat();
            compteur += paire.getCompteur();
        }

        return new PaireResultatCompteur<>(score, compteur);
    }
}
