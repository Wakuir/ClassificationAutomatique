import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.smartcardio.CardTerminal;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

public class Classification2 {
        private static final int NUM_TEST = 3;

        /**
         * Crée un vecteur de dépêches à partir d'un fichier texte
         *
         * @param nomFichier le nom du fichier texte contenant les dépêches
         * @return un vecteur de dépêches
         */
        private static ArrayList<Depeche> lectureDepeches(String nomFichier) {
            //creation d'un tableau de dépêches
            ArrayList<Depeche> depeches = new ArrayList<>();
            try {
                // lecture du fichier d'entrée
                FileInputStream file = new FileInputStream(nomFichier);
                Scanner scanner = new Scanner(file);

                while (scanner.hasNextLine()) {
                    String ligne = scanner.nextLine();
                    String id = ligne.substring(3);
                    ligne = scanner.nextLine();
                    String date = ligne.substring(3);
                    ligne = scanner.nextLine();
                    String categorie = ligne.substring(3);
                    ligne = scanner.nextLine();
                    StringBuilder lignes = new StringBuilder(ligne.substring(3));
                    while (scanner.hasNextLine() && !ligne.isEmpty()) {
                        ligne = scanner.nextLine();
                        if (!ligne.isEmpty()) {
                            lignes.append('\n').append(ligne);
                        }
                    }
                    Depeche uneDepeche = new Depeche(id, date, categorie, lignes.toString());
                    depeches.add(uneDepeche);
                }
                scanner.close();
            } catch (IOException e) {
                System.out.println("Erreur lors de la lecture du fichier '" + nomFichier + "'.");
            }
            return depeches;
        }

        /**
         * Écrit dans un fichier texte la catégorie calculée pour chaque dépêche puis calcul la moyenne de chaque catégorie trouvé puis la moyenne totale de réussite
         *
         * @param depeches   Vecteur de dépêche dont on cherche le score
         * @param categories Vecteur de Catégorie
         * @param nomFichier Nom du fichier dans lequel va être écrit les résultats
         * @return le nombre de comparaisons effectuées par l'algorithme
         */
        public static int classementDepechesLexiqueTrie(ArrayList<Depeche> depeches, ArrayList<Categorie> categories, String nomFichier) {
            ArrayList<PaireChaineEntier> nbReussites = new ArrayList<>();
            ArrayList<PaireChaineEntier> nbDepeches = new ArrayList<>();
            //Incrémentation du nom et d'un nombre de réussites (valeur initiale 0) pour chaque catégorie
            for (Categorie categorie : categories) {
                nbReussites.add(new PaireChaineEntier(categorie.getNom(), 0));
                nbDepeches.add(new PaireChaineEntier(categorie.getNom(), 0));
            }
            try {
                FileWriter file = new FileWriter(nomFichier);
                //Parcours des dépêches
                int i = 0;
                int nbComparaisons = 0;
                while (i < depeches.size()) {
                    ArrayList<PaireChaineEntier> scores = new ArrayList<>();
                    //Ajout du score a chaque catégorie
                    for (Categorie categorie : categories) {
                        PaireResultatCompteur<Integer> scoreLexiqueTrie = categorie.scoreLexiqueTrie(depeches.get(i));
                        scores.add(new PaireChaineEntier(categorie.getNom(), scoreLexiqueTrie.getResultat()));
                        nbComparaisons += scoreLexiqueTrie.getCompteur();
                    }

                    file.write(depeches.get(i).getId() + " : " + UtilitairePaireChaineEntier.chaineMax(scores) + "\n");
                    //Si notre score le plus haut est égal à la catégorie de la dépêche, on ajoute +1 à la réussite de la catégorie.
                    int indice = UtilitairePaireChaineEntier.indicePourChaine(nbReussites, depeches.get(i).getCategorie()).getResultat();
                    if (UtilitairePaireChaineEntier.chaineMax(scores).compareTo(depeches.get(i).getCategorie()) == 0) {
                        nbReussites.get(indice).setEntier(nbReussites.get(indice).getEntier() + 1);
                    }

                    nbDepeches.get(indice).setEntier(nbDepeches.get(indice).getEntier() + 1);

                    i++;
                }
                //Ecrit le taux de réussite de chaque catégorie
                for (Categorie categorie : categories) {
                    int indice = UtilitairePaireChaineEntier.indicePourChaine(nbReussites, categorie.getNom()).getResultat();
                    nbReussites.get(indice).setEntier((int) ((float) nbReussites.get(indice).getEntier() / nbDepeches.get(indice).getEntier() * 100));
                    file.write(nbReussites.get(indice).getChaine() + " : " + ((float) nbReussites.get(indice).getEntier()) + "%\n");
                }

                file.write("Moyenne : " + UtilitairePaireChaineEntier.moyenne(nbReussites) + "%\n");
                file.close();
                return nbComparaisons;
            } catch (IOException e) {
                // Gère l'erreur lorsque le fichier n'existe pas
                System.out.println(e.getMessage());
                return -1;
            }
        }

        /**
         * Precondition : Depeches est trié par catégorie
         *
         * @param depeches  les dépêches dont il faut examiner le contenu
         * @param categorie la catégorie dont il faut extraire les mots
         * @return un vecteur contenant tous les mots présents dans au moins une dépêche de la catégorie donnée et le nombre de comparaisons effectuées par l'algorithme
         */
        public static ArrayList<PaireChaineEntier> initDico(ArrayList<Depeche> depeches, String categorie) {
            // Initialisation du dictionnaire
            ArrayList<PaireChaineEntier> resultat = new ArrayList<>();
            boolean categorieTrouvee = false;
            int i = 0;
            // Parcours des dépêches tant que la catégorie n'est pas trouvée ou que la fin du vecteur n'est pas atteinte
            while (i < depeches.size() && (!categorieTrouvee || depeches.get(i).getCategorie().equals(categorie))) {
                if (!categorieTrouvee && depeches.get(i).getCategorie().equals(categorie)) {
                    // On vient de trouver la catégorie
                    categorieTrouvee = true;
                }
                if (categorieTrouvee) {
                    // Parcours des mots de la dépêche
                    for (String mot : depeches.get(i).getMots()) {
                        PaireResultatCompteur<Integer> indice = UtilitairePaireChaineEntier.indicePourChaine(resultat, mot);
                        if (indice.getResultat() == -1 && !mot.equals(":")) {
                            // Ajout du mot au dictionnaire s'il n'est pas déjà présent
                            resultat.add(new PaireChaineEntier(mot, 0));
                        }
                    }
                }

                i++;
            }
            return resultat;
        }

        /**
         * Met à jour les scores des mots présents dans 'dictionnaire'. Lorsqu'un mot présent dans
         * 'dictionnaire' apparaît dans une dépêche de depeches, son score est : décrémenté si la dépêche
         * n'est pas dans la catégorie categorie et incrémenté si la dépêche est dans la catégorie categorie.
         *
         * @param depeches     le vecteur de dépêches
         * @param categorie    la catégorie pour laquelle on veut calculer les scores
         * @param dictionnaire le vecteur de mots pour lesquels on veut calculer les scores
         */
        public static void calculScores(ArrayList<Depeche> depeches, String categorie, ArrayList<PaireChaineEntier> dictionnaire) {
            // Parcours des dépêches
            for (Depeche depeche : depeches) {
                // Parcours des mots de la dépêche
                for (String mot : depeche.getMots()) {
                    // Recherche de l'indice du mot dans le dictionnaire
                    int indice = UtilitairePaireChaineEntier.indicePourChaine(dictionnaire, mot).getResultat();
                    //Le mot est présent dans le dictionnaire.
                    if (indice != -1) {
                        // Si la dépêche est de la catégorie, on incrémente le score du mot, sinon on le décrémente
                        if (depeche.getCategorie().equals(categorie)) {
                            dictionnaire.get(indice).setEntier(dictionnaire.get(indice).getEntier() + 1);
                        } else {
                            dictionnaire.get(indice).setEntier(dictionnaire.get(indice).getEntier() - 1);
                        }
                    }
                }
            }
        }

        /**
         * Détermine le poids associé à un score (1, 2 ou 3)
         * Le poids le plus faible est associé à un score négatif, un poids moyen est associé lorsque le score est nul et le poids le plus élevé est associé à un score positif
         *
         * @param score le score pour lequel on veut déterminer le poids
         * @return le poids associé au score
         */
        public static int poidsPourScore(int score) {
            if (score < 0) {
                return 1;
            } else if (score == 0) {
                return 2;
            } else {
                return 3;
            }
        }

        /**
         * Génère le lexique pour une catégorie donnée et l'écrit dans un fichier texte
         *
         * @param depeches   Liste de dépêche sur lequel le lexique va se baser
         * @param categorie  Nom de la catégorie sur lequel le lexique sera appliqué
         * @param nomFichier Nom du fichier cible
         */
        public static void generationLexique(ArrayList<Depeche> depeches, String categorie, String nomFichier) {
            //construction d'une ArrayList motsCategorie
            ArrayList<PaireChaineEntier> motsCategorie = initDico(depeches, categorie);
            //mise à jour des scores
            calculScores(depeches, categorie, motsCategorie);

            try {
                //Ecriture du lexique dans le fichier avec leurs poids
                FileWriter file = new FileWriter(nomFichier);
                for (PaireChaineEntier paireChaineEntier : motsCategorie) {
                    file.write(paireChaineEntier.getChaine() + ":" + poidsPourScore(paireChaineEntier.getEntier()) + "\n");
                }
                file.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        public static ArrayList<Categorie> detecterCategories(ArrayList<Depeche> depeches) {
            ArrayList<String> categories = new ArrayList<>();

            for (Depeche depeche : depeches) {
                String categorie = depeche.getCategorie();
                if (!categories.contains(categorie)) { // Vérifie si la catégorie est déjà dans la liste
                    categories.add(categorie); // Ajoute uniquement si elle n'est pas déjà présente
                }
            }

            ArrayList<Categorie> arrayList = new ArrayList<>();
            for (String categorie : categories) {
                arrayList.add(new Categorie(categorie));
            }

            System.out.println("Catégories détectées : " + Arrays.toString(categories.toArray()));
            return arrayList;
        }

        public static void main(String[] args) {
            //Chargement des dépêches en mémoire
            ArrayList<Depeche> depeches = lectureDepeches("./depeches2.txt");

            // Initialisation des catégories
            ArrayList<Categorie> categories = detecterCategories(depeches);
            for (Categorie categorie : categories){
                System.out.println(categorie.getNom());
            }

            // Initialisation du lexique pour chaque catégorie
            for (Categorie categorie : categories) {
                categorie.initLexique("./Etape1/Test" + NUM_TEST + "/" + categorie.getNom() + ".txt");
            }

            for (Categorie categorie : categories) {
                String nomFichierLexique = "Etape2/" + categorie.getNom() + ".txt";
                generationLexique(depeches, categorie.getNom(), nomFichierLexique);
                categorie.initLexique(nomFichierLexique);
            }
            // Utilisation de méthodes triées
            for (Categorie categorie : categories) {
                // Tri du lexique de la catégorie
                categorie.getLexique().sort(Comparator.comparing(PaireChaineEntier::getChaine));
            }
            classementDepechesLexiqueTrie(depeches, categories, "./resultatsEtape2.txt");

        }
    }
