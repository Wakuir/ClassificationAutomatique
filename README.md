# SAE 1.012 - Classification automatique de dépêches

## Objectif

L'objectif de ce projet est de réaliser une classification automatique de dépêches en utilisant des méthodes de machine learning. 
Pour cela, nous disposons d'un jeu de données de dépêches étiquetées, 
et nous allons entraîner un modèle de classification sur ces données (Classe "Classification.java et sa procédure principale).

## Données

Le jeu de données est composé de 500 dépêches étiquetées selon 5 catégories possibles :
- Culture
- Économie
- Politique
- Sciences
- Sports

Les données sont stockées dans le fichier `depeches.txt`. Pour chaque dépêche, on dispose de la catégorie et du texte de la dépêche selon le modèle suivant :  
`.N 002`  
`.D 09/09/2024`  
`.C SCIENCES`  
`.T Cancer de la prostate : des pratiques de dépistage hétérogènes en Europe. L'incidence de cette tumeur est en  
augmentation depuis 1980, mais la mortalité est plutôt en diminution, selon une étude menée dans 26 pays.`

- La première ligne correspond au numéro de la dépêche (qui permet de l'identifier).
- La seconde correspond à la date de la dépêche
- La troisième correspond à la catégorie de la dépêche
- La quatrième ligne correspond au texte de la dépêche 

## Étape 1 : Élaboration d'un lexique à la main

Pour la première étape, nous avons élaboré un lexique à la main, en sélectionnant des mots-clés pour chaque catégorie.
Chaque mot de lexique s'est vu associé un poids, censé représenter l'importance de ce mot pour la catégorie.

### Lexiques établis :

|                    Culture                    |                   SCIENCES                   |                   SPORTS                    |       POLITIQUE       |                   ÉCONOMIE                    |
|:---------------------------------------------:|:--------------------------------------------:|:-------------------------------------------:|:---------------------:|:---------------------------------------------:|
|               `Culture` -> `3`                |               `Science` -> `3`               |               `Sport` -> `3`                |  `Politique` -> `3`   | `Économie` (plus variante sans accent) -> `3` |
|                `Cinéma` -> `3`                |              `dépistage` -> `2`              |              `Football` -> `3`              |   `Élection` -> `3`   |              `Industrie` -> `3`               |
|                 `Film` -> `2`                 |              `Génétique` -> `3`              |              `Marathon` -> `3`              |   `Election` -> `3`   |                `Budget` -> `2`                |
|             `Littérature` -> `3`              |              `Écologie` -> `2`               |              `Cyclisme` -> `3`              |    `Député` -> `3`    |              `Récession` -> `3`               |
|             `Documentaire` -> `2`             |              `Physique` -> `2`               |          `Jeux olympiques` -> `2`           | `Gouvernement` -> `2` |              `Croissance` -> `3`              |
|                `Groupe` -> `2`                |           `Nanoparticules` -> `3`            |             `Paris 2024` -> `1`             |   `Réforme` -> `3`    |               `Chômage` -> `3`                |
|               `Musique` -> `3`                |               `Énergie` -> `2`               |              `Athlètes` -> `3`              |  `Démocratie` -> `3`  |            `Investissement` -> `2`            |
|               `Théâtre` -> `3`                | `energie` (plus variante sans accent) -> `2` |            `Basket-ball` -> `3`             |    `Parti` -> `1`     |              `Inflation` -> `3`               |
|                `Opéra` -> `3`                 |              `Télescope` -> `3`              |               `équipe` -> `2`               |  `Assemblée` -> `2`   |               `Dépense` -> `2`                |
|              `Collection` -> `2`              |              `Biologie` -> `3`               | `equipe` (plus variante sans accent) -> `2` |    `Sénat` -> `2`     |                `Crédit` -> `3`                |
|              `Patrimoine` -> `2`              |               `Cancer` -> `3`                |              `Gymnaste` -> `3`              |  `République` -> `3`  |               `Monnaie` -> `3`                |
|               `Festival` -> `2`               |               `Climat` -> `2`                |               `Course` -> `2`               |    `Mandat` -> `2`    |               `Déficit` -> `3`                |
|                `Dessin` -> `3`                |             `Innovation` -> `3`              |               `Aviron` -> `3`               |  `Opposition` -> `2`  |              `Entreprise` -> `2`              |
|            `Bande dessinée` -> `3`            |      `Intelligence artificielle` -> `3`      |             `Ping-pong` -> `3`              |     `Vote` -> `3`     |                `Banque` -> `2`                |
|                `Roman` -> `3`                 |              `Fossiles` -> `3`               |               `Défis` -> `1`                |  `Président` -> `1`   |                `Marché` -> `1`                |
|               `Concert` -> `3`                |             `Archéologie` -> `3`             |               `Rugby` -> `3`                |   `Ministre` -> `3`   |             `Exportation` -> `2`              |
|              `Exposition` -> `2`              |              `Recherche` -> `2`              |              `Plongée` -> `3`               |  `Parlement` -> `3`   |             `Importation` -> `2`              |
| `Ecrivain` (plus variante sans accent) -> `3` |               `Chimie` -> `3`                |            `Compétition` -> `2`             |   `Commune` -> `2`    |                `Fiscal` -> `3`                |
|               `Galerie` -> `2`                |                `Océan` -> `2`                |               `Volley` -> `3`               |    `Maire` -> `2`     |                 `Prix` -> `2`                 |
|               `Artiste` -> `3`                |            `Environnement` -> `2`            |              `épreuves` -> `1`              |    `Préfet` -> `3`    |                                               |
|               `Fiction` -> `2`                |              `pandémie` -> `3`               |              `epreuves` -> `1`              |  `Préfecture` -> `3`  |                                               |
|                                               |                 `ADN` -> `3`                 |               `effort` -> `2`               |                       |                                               |

#### Résultats :

`CULTURE : 99.0%`  
`ECONOMIE : 0.0%`  
`POLITIQUE : 0.0%`  
`SCIENCES : 1.0%`  
`SPORT : 14.0%`  
`Moyenne : 22.8%`

Ces résultats s'expliquent par le fait que la catégorie culture est choisie si tous les scores sont nuls.
Le lexique étant faiblement peuplés, la catégorie culture est souvent choisie.

### Première amélioration des lexiques :

|        Culture         |       SCIENCES       |        SPORTS         |        POLITIQUE        |       ÉCONOMIE       |
|:----------------------:|:--------------------:|:---------------------:|:-----------------------:|:--------------------:|
|   `sculpter` -> `3`    |    `TDAH` -> `3`     |  `Prévention` -> `1`  |     `Dictat` -> `3`     |  `fiscalité` -> `3`  |
|    `concert` -> `3`    |  `Maternité` -> `3`  |   `Sportifs` -> `3`   |  `Constitution` -> `2`  |   `capital` -> `3`   |
|     `série` -> `3`     | `Traitement` -> `3`  |  `Médailles` -> `2`   |    `Diplomat` -> `3`    |   `salaire` -> `2`   |
|     `livre` -> `2`     | `Laboratoire` -> `3` |   `Activité` -> `1`   |     `budget` -> `2`     | `fabrication` -> `3` |
|     `danse` -> `2`     |    `Étude` -> `2`    | `Récupération` -> `2` |    `société` -> `2`     | `Production` -> `3`  |
| `rétrospective` -> `2` |                      |   `Champion` -> `2`   |      `loi` -> `3`       | `Financement` -> `3` |
|   `création` -> `2`    |                      | `Championnats` -> `2` |      `état` -> `3`      |   `Secteur` -> `1`   |
|                        |                      |                       | `administration` -> `2` | `Rentabilité` -> `3` |
|                        |                      |                       |    `décision` -> `3`    |  `Commerce` -> `2`   |
|                        |                      |                       |     `régime` -> `2`     |                      |
|                        |                      |                       |    `Campagne` ->`2`     |                      |

#### Résultats :

`CULTURE : 96.0%`  
`ECONOMIE : 2.0%`  
`POLITIQUE : 43.0%`  
`SCIENCES : 1.0%`  
`SPORT : 13.0%`  
`Moyenne : 31.0%`

Malgré une légère amélioration globale, la catégorie sport a perdu un point. Cela reste une amélioration significative pour les catégories économie et politique. La catégorie culture a quant à elle perdu 3 points.

### Deuxième amélioration des lexiques :

|         SPORT          |       SCIENCES       |
|:----------------------:|:--------------------:|
|     `jeux` -> `2`      | `Découverte` -> `3`  |
|  `Olympiques` -> `2`   |   `Avancée` -> `2`   |
|    `Records` -> `2`    | `Technologie` -> `3` |
| `Participation` -> `2` | `Expérience` -> `2`  |
|    `Exploit` -> `1`    |   `Donnée` -> `2`    |
|   `Exploits` -> `1`    |   `Vaccin` -> `2`    |
|   `Sportive` -> `3`    |  `Médecine` -> `3`   |
|   `Sportives` -> `3`   |   `Planète` -> `2`   |
|                        | `Observation` -> `2` |
|                        |   `Progrès` -> `2`   |
|                        |   `Analyse` -> `2`   |

#### Résultats :

`CULTURE : 96.0%`  
`ECONOMIE : 2.0%`  
`POLITIQUE : 43.0%`  
`SCIENCES : 1.0%`  
`SPORT : 22.0%`  
`Moyenne : 32.8%`

Cette amélioration a eu effet de renforcer la catégorie sport, qui a gagné 9 points. Les autres catégories n'ont pas été impactées.

## Étape 2 : Classification automatique

### Création d'un dictionnaire de lexique 

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

Cette méthode a pour but de retourner un vecteur contenant tous les mots présents dans au moins une dépêche de la catégorie donnée. La chaîne de caractère `:` est ignorée pour ne pas poser de problème lors de la lecture du fichier. 
Cette méthode est également outillé de telle sorte qu'elle renvoie le nombre de comparaisons effectuées.
Ce dictionnaire étant initialisé à 0, il sera ensuite mis à jour pour chaque dépêche de la catégorie. Une précondition a été ajoutée : le vecteur de dépêches doit être trié par catégorie.

### Calcul des scores des mots

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

Cette méthode a pour but de mettre à jour le dictionnaire de lexique en fonction des dépêches de la catégorie donnée. Pour chaque mot de chaque dépêche, on incrémente le score du mot si la dépêche est de la catégorie donnée, et on le décrémente sinon. 

### Attribution d'un poids en fonction du score calculé

    public static int poidsPourScore(int score) {
        if (score < 0) {
            return 1;
        } else if (score == 0) {
            return 2;
        } else {
            return 3;
        }
    }

Cette méthode a pour but de retourner un poids en fonction du score du mot. Si le score est négatif, le poids est de 1. Si le score est nul, le poids est de 2. Si le score est positif, le poids est de 3.

### Génération du fichier de lexique pour une catégorie donnée

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

Cette méthode a pour but de générer un fichier de lexique pour une catégorie donnée. Elle initialise un dictionnaire de lexique, met à jour les scores des mots, et écrit le fichier de lexique.

### Nouveaux résultats obtenus grâce à la classification automatique

`CULTURE : 76.0%`  
`ECONOMIE : 60.0%`  
`POLITIQUE : 82.0%`  
`SCIENCES : 62.0%`  
`SPORT : 85.0%`  
`Moyenne : 73.0%`   

Ces résultats sont bien meilleurs que ceux obtenus avec les lexiques établis à la main. 
La catégorie sport a gagné 63 points, la catégorie économie 58 points, la catégorie politique 39 points, la catégorie culture 17 points et la catégorie sciences 61 points.

### Utilisation du flux RSS

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

Cette méthode permet la lecture d'un fichier XML puis l'écriture sous forme de dépêches dans un fichier texte `depechesRSS.txt`, récupère l'adresse du String et une liste de catégorie. Parcours du contenu des Nodes (nœuds) ayant le TAG "item", pour afficher chaque catégorie sous le format des dépêches

### Contenu du fichier après éxecution de la méthode

    .N 1
    .D Thu, 23 Jan 2025 06:00:00 +0100
    .C POLITIQUE
    .T Le tournant historique de Donald Trump à la Maison Blanche : « révolution du bon sens » ou réaction conservatrice ?
    
    .N 2
    .D Wed, 22 Jan 2025 22:47:54 +0100
    .C ECONOMIE
    .T Un nouvel incendie se propage rapidement au nord de Los Angeles, attisé par les « vents », le « faible taux d’humidité » et les « broussailles » asséchées
    
    .N 3
    .D Thu, 23 Jan 2025 05:30:13 +0100
    .C POLITIQUE
    .T Le PS exige de nouvelles concessions de François Bayrou, en pleine négociation sur le budget
    
    .N 4
    .D Thu, 23 Jan 2025 07:49:50 +0100
    .C POLITIQUE
    .T La porte-parole du ministère de l’intérieur démissionne avant la parution de son livre, dans lequel elle critique le RN et le syndicat Alliance
    
    .N 5
    .D Thu, 23 Jan 2025 06:59:35 +0100
    .C POLITIQUE
    .T En direct, guerre en Ukraine : un bombardement russe sur la ville de Zaporijia ravage des immeubles résidentiels
    
    .N 6
    .D Thu, 23 Jan 2025 06:30:04 +0100
    .C CULTURE
    .T Les multiples débouchés des masters en math : « Je ne pensais pas que cela ouvrait autant de portes »
    
    .N 7
    .D Thu, 23 Jan 2025 07:32:52 +0100
    .C POLITIQUE
    .T En direct, cessez-le-feu à Gaza : Donald Trump classe les rebelles houthistes du Yémen comme « organisation terroriste étrangère »
    
    .N 8
    .D Thu, 23 Jan 2025 10:00:15 +0100
    .C SPORT
    .T « Gérer mes biens immobiliers », un fiasco à 1,3 milliard d’euros pour Bercy
    
    .N 9
    .D Thu, 23 Jan 2025 09:56:48 +0100
    .C POLITIQUE
    .T Budget 2025 : Marine Tondelier dénonce les coupes sur l’écologie et promet de voter la censure
    
    .N 10
    .D Thu, 23 Jan 2025 08:26:14 +0100
    .C SCIENCES
    .T L’énergie solaire dépasse le charbon pour la première fois dans la production d’électricité de l’UE en 2024
    
    .N 11
    .D Thu, 23 Jan 2025 09:30:10 +0100
    .C SPORT
    .T Moins de curistes traditionnels, plus de courts séjours et de spas : les stations thermales en quête de renouveau
    
    .N 12
    .D Thu, 23 Jan 2025 01:29:49 +0100
    .C POLITIQUE
    .T Cinq commerces « mitraillés » à la kalachnikov en une semaine dans le Doubs, une stratégie d’« intimidation » liée au narcotrafic
    
    .N 13
    .D Thu, 23 Jan 2025 00:50:42 +0100
    .C SCIENCES
    .T Une skieuse de 10 ans tuée après avoir heurté une paroi rocheuse sur le domaine de la station de Villard-de-Lans
    
    .N 14
    .D Thu, 23 Jan 2025 05:29:15 +0100
    .C SPORT
    .T Ligue des champions : le PSG renverse Manchester City après un match « dingue », un succès pour Luis Enrique
    
    .N 15
    .D Wed, 22 Jan 2025 15:02:00 +0100
    .C SCIENCES
    .T Allemagne : deux personnes, dont un enfant, tués par un homme armé d’un couteau
    
    .N 16
    .D Thu, 23 Jan 2025 06:23:20 +0100
    .C POLITIQUE
    .T En Corée du Sud, les enquêteurs demandent l’inculpation du président Yoon pour rébellion et abus de pouvoir
    
    .N 17
    .D Sat, 01 Apr 2023 06:00:21 +0200
    .C POLITIQUE
    .T Quelles quantités d’eau sont prélevées et consommées par la population, les usines et l’agriculture en France ?
    
    .N 18
    .D Thu, 23 Jan 2025 06:30:09 +0100
    .C CULTURE
    .T Des pierres de Soleil sacrifiées il y a 5 000 ans



## Comparaison d'approches algorithmiques

### Approche 1 : Lexiques non triés et recherche linéaire

`Temps d'exécution moyen sur 10 exécutions : 613ms`  
`Nombre de comparaisons : 1.07431344E8` (Environ `100M`)

### Approche 2 : Lexiques triés et recherche dichotomique

`Temps d'exécution moyen sur 10 exécutions : `~~`366ms`~~`20ms`  
`Nombre de comparaisons : 6.5671816E7` (Environ ~~`100M`~~ `65.6M`)

## Généralisation de la classification automatique par lexique

### Détection des catégories dans le jeu de données

    public static ArrayList<Categorie> detecterCategories(ArrayList<Depeche> depeches) {
        ArrayList<String> categories = new ArrayList<>();

            for (Depeche depeche : depeches) {
                String categorie = depeche.getCategorie();
                if (!categories.contains(categorie)) { // Vérifie si la catégorie est déjà dans la liste
                    categories.add(categorie); // Ajoute uniquement si elle n'est pas déjà présente
                }
            }
            ArrayList<Categorie> arrayList = new ArrayList<>();
            for (int i = 0; i < categories.size(); i++) {
                arrayList.add(new Categorie(categories.get(i)));
            }
            return arrayList;
        }

Cette méthode permet de détecter les catégories présentes dans le jeu de données initial.
Avec `depeches2.txt`, on obtient les catégories suivantes : `[CINEMA, LIVRES, GEEK, TV-RADIO, MUSIQUES]`

## Nouvelle approche de classification : KNN (K-plus proches voisins)

### Principe

La méthode KNN se base sur la distance entre les dépêches.  
Elle est calculée en fonction du nombre de termes en commun entre deux dépêches : chaque fois qu'un mot d'une dépêche est aussi présent dans l'autre (les mots vides tels que `le`, `la`, `les`, `de`, etc. sont ignorés),
la distance entre les deux dépêches est réduite de 1. On obtient donc des distances négatives, ce qui n'est pas très intuitif, mais cela représente bien l'inversion proportionnelle entre la distance et le nombre de mots.

### Implémentation

Une nouvelle classe (KNN.java) a été créée pour implémenter la méthode KNN au travers de sa procédure principale.

#### Calcul de distance

    public static int distance(Depeche d1, Depeche d2) {
        // Vecteur de mots vides
        ArrayList<String> motsNonSignificatifs = new ArrayList<>(Arrays.asList(
                "le", "la", "les", "de", "des", "du", "un", "une", "et", "ou", "donc", "ni", "car", "ce", "cet", "cette"
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

Cette méthode a pour but de calculer la distance entre deux dépêches. Elle parcourt les mots de la première dépêche, et si un mot n'est pas un mot vide et qu'il est présent dans la deuxième dépêche, la distance est décrémentée.

#### Estimation de la catégorie 

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

Cette méthode retourne la catégorie la plus présente parmi les k plus proches voisins. 
Elle calcule la distance entre la dépêche donnée et chaque dépêche de la base d'apprentissage, trie les distances par ordre croissant, récupère les k plus proches voisins, et calcule les fréquences des catégories parmi les k plus proches voisins.
La catégorie la plus présente est ensuite retournée.

### Résultats obtenus avec la méthode KNN

`CULTURE : 37.0%`  
`ECONOMIE : 40.0%`  
`POLITIQUE : 68.0%`  
`SCIENCES : 67.0%`  
`SPORT : 52.0%`  
`Moyenne : 52.8%`

Ces résultats sont moins bons que ceux obtenus avec la méthode de classification automatique par lexique.
Cela s'explique par le fait que le calcul de la distance ne prend en compte que le nombre de mots en commun.