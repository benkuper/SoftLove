Ce projet offre une surcouche du projet ELIZA afin qu'il soit intégrable dans le projet #SoftLove
et de le faire communiquer avec les différents modules de ce dernier via une architecture Publish/Subscribe

Ce projet est développé pour être interfacé avec le module de Reconnaissance vocale de softlove et
Pour compiler le projet, l'inclure dans un projet java IntelliJ.

Ce projet nécessite Maven pour importer les dépendances de ZeroMQ, de base il est inclut dans Intellij mais il est
possible d'installer Maven directement sur l'OS et de lancer la commande : mvn install depuis la racine du projet
pour importer les dépendances.

Il est également possible de lancer l'application par le fichier .jar présent dans le dossier out/
