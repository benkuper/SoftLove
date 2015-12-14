# Web

I/ Le code et architecture

L'architecture est la suivante :
.
├── config.json
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
- ZMQConnector : classe permettant de réaliser la connection ZeroMQ Publish/Subscribe (lecture du fichier config.json pour les adresses, ports et topics publiés et souscrits).

II/ Les dépendances

Notre code possède des dépendances externes :

- jsoup en version 1.8.3
- jeromq en version 0.3.5
- junit en version 3.8.1 (aucun test junit réalisé à ce jour)
- com.googlecode.json-simple en version 1.1
- com.google.apis en version v1-rev51-1.20.0

Nous utilisons également le site http://www.synonymo.fr/ qui s'il est en maintenant ou non accessible me permettra pas au code de renvoyer les mots clés enrichis de synonymes. Cependant cela n'entrave pas le bon déroulement d'une requête.

III/ Paramétrage et lancement

Le paramétrage se fait à l'aide du fichier config.json. Les variables à renseigner sont dans l'ordre : l'IP et port du publisher de mots clés, le nom de son topic (keywords), l'IP  et port du publisher d'images, le nom de son topic (images), l'IP  et port du suscriber de mots clés, le nom de son topic (Parole), l'IP et port du subscriber pour les réseaux sociaux et le nom de son topic (fb/links).
 
Le lancement se réalise au moyen d'un IDE en lançant la classe MainWeb ou en lançant le fichier execWeb.jar en ligne de commande (java -jar execWeb.jar).
