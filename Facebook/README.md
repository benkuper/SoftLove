#Explication du fonctionnement logiciel
##Fonctionnalités
- Connexion à l'API facebook
- Recherche des mots clés des posts likés
- TFIDF avec un dictionnaire des stopwords pour avoir uniquement les mots les plus pertinents
- Envoi de ses mots clés
- Recherche des images de l'utilisateur
- Envoi de l'url de ces images

##Paramétrage
Les adresses des sender et treceiver sont pour l'instant dans le code du fichier Application.java, elles seront bientôt déportées dans un ficheir externe pour une plus grande configuration.
TreeTagger utilise les fichiers dans le dossier models pour définir les méthodes d'analyse syntaxique.

##Lancement
Lancer le script howToCompile.sh
L'invite de commande indique l'url qu'il faut copier/coller dans le navigateur. Après avoir accepté, copier/coller après la partie code= de la nouvelle adresse.
Une interface graphique sera développée pour ne pas avoir à faire cette étape

#Explication du code
- fichier Application.java: contient le main de l'application, appelle les méthodes permettant la communication à facebook et la communication entre modules
- dossier fb: contient les classes utilisées pour la connexion à facebook
- dossier tfidf: contient les classes utilisées pour connaître les mots clés des posts de l'utilisateur
- dossier zmq: contient les classes utilisées pour la communication entre les modules

##ZMQ : topics
Le module émet sur les ports 5564 et 5565, et reçoit sur le port 5563. Les topics utilisés sont fb/likes, fb/picture, fb/keywords et fb/data.

### Réception
Recevoir un message même vide sur fb/likes, fb/pictures ou fb/keywords provoquera une réaction et l'émission de nouvelles données (voir partie émission).

####Topics écoutés
 * fb/likes
 * fb/picture
 * fb/keywords
 * fb/data : celui-ci est particulier, il permet de faire une recherche en utilisant directement l'api facebook. Les données envoyées sur ce topic seront utilisées comme URL. Exemple: fb/data "me" => appel sur "https://graph.facebook.com/me" => renvoi brut des données reçues sur le topic fb/me port 5563. LES DONNEES RECUES DEPUIS CETTE URL SERONT UNIQUEMENT ENVOYEES SUR LE TOPIC fb/(données d'entrée).


### Émission
Un ZMQEmitter émet régulièrement (toutes les secondes) des données sur le port 5565 au topic fb/data. Les données émises sont au format JSON. Les données sont de la forme suivante:
```{
    "fb/likes" : (json), // json contenant tous les posts likés
    "fb/picture" : (string), // liens vers des images de l'utilisateur
    "fb/keywords" : (string) // un top 50 des mots les plus récurrents dans les posts likés 
}```

Un ZMQSender envoie des données pour chaque topic séparément. Les envois se font sur le port 5563. Un nouvel envoi est fait à chaque appel reçu sur le ZMQReceiver correspondant:
####fb/likes
En recevant un message sur fb/likes, le sender renvoie l'ensemble des posts likés sur le topic fb/likes, au format JSON.

####fb/picture
En recevant un message sur fb/picture, le sender renvoie un lien vers la photo de profil de l'utilisateur sur le topic fb/picture.

####fb/keywords
En recevant un message sur fb/keywords, le sender renvoie une liste ordonnées du top 50 des mots les plus utilisés dans les posts likés de l'utilisateur.

#Librairies externes
-scribe pour la connexion à facebook
-ZeroMQ pour la communication entre les modules
-TreeTagger pour la lemmatisation et l'analyse syntaxique

