# Emotion

Hello, World!

## Fonctionnement 
Ce module a pour but de récupérer les mots fournis par le module speech2text.  Ces mots sont envoyés à plusieurs API en ligne qui nous renvoi des choses tout à fait intéressantes.

L'api la plus importante est mood patrol qui renvoi 7 sentiments (qui correspondent à notre modèle de roue des émotions). Les autres sont simplement des API qui nous permette de savoir si le groupe de mot est plutôt positif ou négatif.

Les résultats sont lissés assez brutalement avec les 5 dernières requêtes.

Pour le lancer ça ne marche pas pour l'instant.. enfin si mais sur eclipse. La compilation maven fonctionne mais l'éxecution plante parce qu'il y a un problème de chemin pour accéder aux fichier :
 * keys.properties, qui contient les clés pour les API que nous avons obtenus sur le site [mashape.com](mashape.com)
 * ip.propertes, qui contient les ips, port, topic du module speech2text

## Code
On utilise zeroMQ pour ls pub/sub et spécifique Unirest pour les requêtes HTTP. Toutes les dépendances sont gérées dans le maven.

Wrapper Java pour l'utilisation de l'API Bing Translator. Avec la dépendance Maven suivante :
```xml
  <dependency>
    <groupId>com.memetix</groupId>
    <artifactId>microsoft-translator-java-api</artifactId>
    <version>0.6.2</version>
    <type>jar</type>
  </dependency>
```
Selon l'utilisation (localhost, ou non) voir ces deux lignes : 

https://github.com/benkuper/SoftLove/blob/master/Emotion/workspace/src/main/java/fr/insa/ihme/emotion/modules/BingTranslator.java#L31

Les **clés des API** sont à renseigner pour le moment dans le fichier source FinalClient.java (les premières lignes du main)
Les API de [mashape](mashape.com)
 * [moodpatrol](https://market.mashape.com/soulhackerslabs/moodpatrol-emotion-detection-from-text)
 * [sentiment](https://market.mashape.com/vivekn/sentiment-3)
 * [loudelement](https://market.mashape.com/loudelement/free-natural-language-processing-service)

L'api BING :
 * [Getting started with the Bing Translator API](http://www.restlessprogrammer.com/2013/03/setting-up-free-bing-translator-api.html)
