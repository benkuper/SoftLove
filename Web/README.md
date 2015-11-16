# Web

I/ Le code et architecture

L'architecture est la suivante :

.
├── pom.xml
├── README.md
├── src
│   ├── main
│   │   ├── java
│   │   │   └── src
│   │   │       ├── ImageManager.java
│   │   │       ├── KeywordCounter.java
│   │   │       ├── MainThread.java
│   │   │       ├── MainWeb.java
│   │   │       ├── ProfilThread.java
│   │   │       ├── TFIDFCalculator.java
│   │   │       ├── WebContentManager.java
│   │   │       └── ZMQConnector.java
│   │   └── resources
│   └── test
│       ├── java
│       └── resources
└── target
    ├── classes
    │   └── src
    │       ├── ImageManager.class
    │       ├── KeywordCounter.class
    │       ├── MainThread.class
    │       ├── MainWeb.class
    │       ├── ProfilThread.class
    │       ├── TFIDFCalculator.class
    │       ├── WebContentManager.class
    │       └── ZMQConnector.class
    └── test-classes


Le code est répartit en différentes classes se trouvant dans le répertoire /src/main/java/src/

- ImageManager : classe permettant le traitement des images (récupération du lien et envoie sur le topic "images").
- KeywordCounter : classe réalisant toutes les méthodes en rapport avec les mots clés comme la récupération des synonymes, l'extraction des mots clés, la récupération des réponses à renvoyer par la suite sur le topic "keywords".
- MainThread : classe Thread représentant le traitement d'une requête (effectue les différents appelle sur les classes).
- MainWeb : la classe principale représentant le lancement du Thread pour la récupération du profil (ProfilThread) et une boucle infinie pour lancer les Threads de requête (MainThread).
- ProfilThread : classe Thread permettant de mettre à jour les données du profil utilisateur.
- TFIDFCalculator : classe regroupant les méthodes pour calculer le TF-IDF de la requête.
- WebContentManager : classe regroupant les méthodes d'accès aux pages internet notamment la récupération d'une page ou l'appel à google/amazon.
- ZMQConnector : classe permettant de réaliser la connection ZeroMQ Publish/Subscribe.

II/ Les dépendances

Notre code possède des dépendances externes :

- jsoup en version 1.8.3
- jeromq en version 0.3.5
- junit en version 3.8.1 (aucun test junit réalisé à ce jour)

Nous utilisons également le site http://www.synonymo.fr/ qui s'il est en maintenant ou non accessible me permettra pas au code de renvoyer les mots clés enrichis de synonymes. Cependant cela n'entrave pas le bon déroulement d'une requête.

III/ Paramétrage et lancement

Pour l'instant le fichier de paramétrage n'est pas implémenté et le lancement se réalise au moyen d'un IDE en lançant la classe MainWeb.
