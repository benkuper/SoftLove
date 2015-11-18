# Premiere et deuxième séance
Texte (retranscris de ce qui est dit)

  * API Sentiment : positif/negatif + %
  * API Free Natural Language Processing Service : sentiments aussi : 
  * API google trad 
  * MUST-HAVE : [url](http://demo.soulhackerslabs.com/emotion/analyze/)

Pricing pour moodpatrol :

 * Calcul du nombre de caractères pour le bouquin 55 x26 x150 = 215 000 char
Traduire 5 fois le livre : 20€ 

Autres capteurs à voir 
 
 * Traiter les labels de la kinect 
 * Devant caméra peut être à certains moments ??  
 * Capteurs autres (cardiogramme, sudation...)


Discusion avec le client 

 * Suite des recherche 
 * Présentation de la détection dans le texte aux artistes


# Troisième séance

Bilan : 
 * 3 Pocs de tests d'API ont été effectué avec succès
 * Adoption par l'ensemble du groupe de ZeroMQ comme service de communication
 * Définition des endpoints 
Découverte de la roue des émotions de Genève qui contient un diagramme comportant beaucoup d'état possible avec les bons paramètres

Doc de mood patrol
 * Joy 
 * Trust
 * Fear
 * Surprise
 * Sadness
 * Disgust
 * Anger
 * Anticipation

Test de ZeroMQ : Choix de conception : 
 + Un seul publisher partagé entre les objets. (un seul .bind(url:port))
 + plusieurs services qui prennent le publisher en paramètre (injection de dépendances) 
 + Compatible Android !

MS Band SDK
    + À première vue, plutôt simple à intégrer sur une app android. Une petite modification du manifest. Le codes détaillé dans la documentation.

Mappage modèle :

| Mood Patrol API | Modèle       |
| --------------- | ------------ |
| Joy             | Joyfulness   |
| Trust           | Satisfaction |
| Fear            | Awaiting     |
| Sadness         | Sadness      |
| Anger           | Jealousy     |
| Disgust         | Disgust      |
| Surprise        | Curiosity    |
