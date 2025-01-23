import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Classification {
    private static final int NUM_TEST = 3;

    /**
     * Crée un vecteur de dépêches à partir d'un fichier texte
     *
     * @param nomFichier le nom du fichier texte contenant les dépêches
     * @return un vecteur de dépêches
     */
    public static ArrayList<Depeche> lectureDepeches(String nomFichier) {
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
    public static int classementDepeches(ArrayList<Depeche> depeches, ArrayList<Categorie> categories, String nomFichier) {
        ArrayList<PaireChaineEntier> nbReussites = new ArrayList<>();
        //Incrémentation du nom et d'un nombre de réussites (valeur initiale 0) pour chaque catégorie
        for (Categorie categorie : categories) {
            nbReussites.add(new PaireChaineEntier(categorie.getNom(), 0));
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
                    PaireResultatCompteur<Integer> score = categorie.score(depeches.get(i));
                    scores.add(new PaireChaineEntier(categorie.getNom(), score.getResultat()));
                    nbComparaisons += score.getCompteur();
                }

                file.write(depeches.get(i).getId() + " : " + UtilitairePaireChaineEntier.chaineMax(scores) + "\n");
                //Si notre score le plus haut est égal à la catégorie de la dépêche, on ajoute +1 à la réussite de la catégorie.
                if (UtilitairePaireChaineEntier.chaineMax(scores).compareTo(depeches.get(i).getCategorie()) == 0) {
                    int indice = UtilitairePaireChaineEntier.indicePourChaine(nbReussites, depeches.get(i).getCategorie()).getResultat();
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

            System.out.println("Résultats écrits dans le fichier " + nomFichier);
            return nbComparaisons;
        } catch (IOException e) {
            // Gère l'erreur lorsque le fichier n'existe pas
            System.out.println(e.getMessage());
            return -1;
        }
    }

    public static int classementDepechesLexiqueTrie(ArrayList<Depeche> depeches, ArrayList<Categorie> categories, String nomFichier) {
        ArrayList<PaireChaineEntier> nbReussites = new ArrayList<>();
        //Incrémentation du nom et d'un nombre de réussites (valeur initiale 0) pour chaque catégorie
        for (Categorie categorie : categories) {
            nbReussites.add(new PaireChaineEntier(categorie.getNom(), 0));
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
                if (UtilitairePaireChaineEntier.chaineMax(scores).compareTo(depeches.get(i).getCategorie()) == 0) {
                    int indice = UtilitairePaireChaineEntier.indicePourChaine(nbReussites, depeches.get(i).getCategorie()).getResultat();
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

            System.out.println("Résultats écrits dans le fichier " + nomFichier);
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
        //construction d'un vecteur motsCategorie
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

    /**
     * Permet la lecture d'un fichier XML puis l'écriture sous forme d'une dépêche dans un fichier texte "depechesRSS"
     * @param rssAdresse Url du fichier XML sur internet
     * @param categories Vecteur Categorie parmi lesquelles les dépêches seront classées
     */
    public static void readRSS(String rssAdresse, ArrayList<Categorie> categories) {
        try {
            //Initialisation des variables permettant la création et la connection au fichier XML
            DocumentBuilderFactory instance = DocumentBuilderFactory.newInstance();
            DocumentBuilder document = instance.newDocumentBuilder();
            Document doc = document.parse(rssAdresse);
            doc.getDocumentElement().normalize();
            //Création du fichier "depechesRSS.txt" en local
            FileWriter file_W = new FileWriter("depechesRSS.txt");
            NodeList nList = doc.getElementsByTagName("item");
            //Parcours de chaque élèment NodeListe qui à le TAG "item"
            for (int i = 0; i < nList.getLength(); i++) {
                //Création d'une Node courante
                Node node_cour = nList.item(i);
                //Tant que la node courante est un élément de node afficher le nom, la date, la catégorie et le titre
                if (node_cour.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node_cour;
                    file_W.write(".N " + (i + 1) + "\n");
                    file_W.write(".D " + element.getElementsByTagName("pubDate").item(0).getTextContent() + "\n");
                    ArrayList<PaireChaineEntier> scores = new ArrayList<>();
                    //Désigne quelle catégorie est choisie pour le Texte
                    for (Categorie categorie : categories) {
                        scores.add(new PaireChaineEntier(categorie.getNom(), categorie.score(new Depeche("", "", "", element.getElementsByTagName("title").item(0).getTextContent())).getResultat()));
                    }
                    file_W.write(".C " + UtilitairePaireChaineEntier.chaineMax(scores) + "\n");
                    file_W.write((".T " + element.getElementsByTagName("title").item(0).getTextContent() + "\n\n").replace("\u00A0", " "));
                }
            }
            file_W.close();
            System.out.println("votre saisie a été écrite avec succès dans depeches2.txt");

        } catch (ParserConfigurationException | IOException | SAXException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        //Chargement des dépêches en mémoire
        System.out.println("chargement des dépêches");
        ArrayList<Depeche> depeches = lectureDepeches("./depeches.txt");
        ArrayList<Depeche> test = lectureDepeches("./test.txt");

        // Affichage des dépêches
        for (Depeche depeche : depeches) {
            depeche.afficher();
        }

        // Initialisation des catégories
        ArrayList<Categorie> categories = new ArrayList<>(Arrays.asList(
                new Categorie("CULTURE"),
                new Categorie("ECONOMIE"),
                new Categorie("POLITIQUE"),
                new Categorie("SCIENCES"),
                new Categorie("SPORT")));

        // Initialisation du lexique pour chaque catégorie
        for (Categorie categorie : categories) {
            categorie.initLexique("./Etape1/Test" + NUM_TEST + "/" + categorie.getNom() + ".txt");
        }

        // Test 5.2 c, affichage du lexique de la catégorie CULTURE
        Categorie culture = categories.get(0);
        ArrayList<PaireChaineEntier> lexique = culture.getLexique();

        for (int i = 0; i < culture.getLexique().size(); i++) {
            PaireChaineEntier motLexique = lexique.get(i);
            System.out.println("Mot: " + motLexique.getChaine() + ", Poids : " + motLexique.getEntier());
        }

        // Test 5.2 d, poids associé à un mot donné
        //Initialisation des variables
        String mot;
        Scanner sc = new Scanner(System.in);
        //demande de saisie a l'utilisateur
        System.out.println("---------------------");
        System.out.println("Saisie d'un mot : ");
        mot = sc.nextLine();
        int poids = UtilitairePaireChaineEntier.entierPourChaine(lexique, mot).getResultat();
        // teste pour savoir si le mot est trouvé ou non, si trouvé affichage du poids.
        if (poids != 0) {
            //affiche du poids pour mot
            System.out.println("Le poids du mot choisi est : " + poids);
        } else {
            System.out.println("Mot non trouve");
        }

        // Test 5.3 b, score d'une dépêche pour la catégorie SPORT
        for (int i = 150; i < 200; i++) {
            Depeche dep = depeches.get(i);
            System.out.println("DEPECHE " + i);
            for (Categorie categorie : categories) {
                System.out.println("Score de la dépêche pour la catégorie " + categorie.getNom() + " : " + categorie.score(dep));
            }
        }

        // 5.4 b, Calcul des scores des différentes catégories
        ArrayList<PaireChaineEntier> scores = new ArrayList<>();

        // Initialisation des scores à 0
        for (Categorie categorie : categories) {
            scores.add(new PaireChaineEntier(categorie.getNom(), 0));
        }
        //Parcours complet du vecteur des dépêches
        for (int i = 0; i < depeches.size(); i++) {
            Depeche depeche = depeches.get(i);
            // Parcours complet du vecteur des catégories
            for (int j = 0; j < scores.size(); j++) {
                Categorie categorie = categories.get(j);
                // Calcul du score de la dépêche pour la catégorie
                scores.get(j).setEntier(categorie.score(depeche).getResultat());
            }

            System.out.println("Catégorie ayant le plus grand score pour la dépêche " + i + " : " + UtilitairePaireChaineEntier.chaineMax(scores));
        }

        // 5.6, Classement des dépêches
        classementDepeches(test, categories, "./resultats.txt");

        // Génération du lexique pour chaque catégorie
        for (Categorie categorie : categories) {
            String nomFichierLexique = "Etape2/" + categorie.getNom() + ".txt";
            generationLexique(depeches, categorie.getNom(), nomFichierLexique);
            categorie.initLexique(nomFichierLexique);
        }

        // Calcul du temps d'exécution moyen sur un faible échantillon
        int nbEssais = 10;
        long tempsTotal = 0;
        float nbComparaisons = 0;
        for (int i = 0; i < nbEssais; i++) {
            long tempsDebut = System.currentTimeMillis();

            // Classement des dépêches
            nbComparaisons += classementDepeches(test, categories, "resultatsEtape2.txt");

            long tempsFin = System.currentTimeMillis();

            tempsTotal += tempsFin - tempsDebut;
        }

        System.out.println("Temps d'exécution moyen (non trié) : " + tempsTotal / nbEssais + " ms");
        System.out.println("Nombre de comparaisons total par itération : " + nbComparaisons / nbEssais);

        // Utilisation de méthodes triées
        for (Categorie categorie : categories) {
            // Tri du lexique de la catégorie
            categorie.getLexique().sort(Comparator.comparing(PaireChaineEntier::getChaine));
        }

        // Calcul du temps d'exécution moyen sur un faible échantillon
        tempsTotal = 0;
        nbComparaisons = 0;
        for (int i = 0; i < nbEssais; i++) {
            long tempsDebut = System.currentTimeMillis();

            // Classement des dépêches
            nbComparaisons += classementDepechesLexiqueTrie(test, categories, "resultatsEtape2Trie.txt");

            long tempsFin = System.currentTimeMillis();

            tempsTotal += tempsFin - tempsDebut;
        }

        System.out.println("Temps d'exécution moyen (trié) : " + tempsTotal / nbEssais + " ms");
        System.out.println("Nombre de comparaisons total par itération : " + nbComparaisons / nbEssais);
        readRSS("https://www.lemonde.fr/rss/une.xml", categories);
    }
}