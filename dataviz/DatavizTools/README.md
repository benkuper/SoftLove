Outils pour la visualisation des données
=================================
* SpiderChart : graph araignée pour les 8 émotions : "Joy", Trust"; "Fear"; "Sadness"; "Anger"; "Distrust"; "Surprise"; "Anticipation";

Utilisation : `SpiderChart.spider()` avec en paramètre, le nom du fichier, les 8 émotions en paramètre et une intensité 0, 1 , 2 ou 3 qui influe sur la couleur du graphe.


* WebPage : Un outil permettant de prendre une screenshot ou d'ouvrir un navigateur sur une page  `webpage.screenshot(url,filename)`

Dépendances : Nodejs & NPM : [https://nodejs.org/] et PhantomJS : [http://phantomjs.org/] (npm install -g phantomjs ou téléchargement sur le site).

`webpage.openInBrowser(url)`, devrait fonctionner sur Linux (testé), Windows et Mac (non testés). 
Dépendances : aucune
