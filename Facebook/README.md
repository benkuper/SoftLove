#Explication du fonctionnement logiciel
##Fonctionnalités
- Connexion à l'API facebook
- Recherche des mots clés des posts likés
- TFIDF avec un dictionnaire des stopwords pour avoir uniquement les mots les plus pertinents
- Envoi de ses mots clés
- Recherche des images de l'utilisateur
- Envoi de l'url de ces images

##Paramétrage
Les adresses des sender et treceiver sont pour l'instant dans le code du fichier Application.java, elles seront bientôt déportées dans un ficheir externe pour une plus grande configuration

##Lancement
Lancer le script howToCompile.sh
L'invite de commande indique l'url qu'il faut copier/coller dans le navigateur. Après avoir accepté, copier/coller après la partie code= de la nouvelle adresse.
Une interface graphique sera développée pour ne pas avoir à faire cette étape

#Explication du code
- fichier Application.java: contient le main de l'application, appelle les méthodes permettant la communication à facebook et la communication entre modules
- dossier fb: contient les classes utilisées pour la connexion à facebook
- dossier tfidf: contient les classes utilisées pour connaître les mots clés des posts de l'utilisateur
- dossier zmq: contient les classes utilisées pour la communication entre les modules

#Librairies externes
-scribe pour la connexion à facebook
-ZeroMQ pour la communication entre les modules

