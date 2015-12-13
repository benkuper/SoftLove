# DataViz

## Fonctionnement

Lancé depuis un IDE (IntelliJ IDEA Community Edition 15.0) : simplement run le main


Lancé via processing : copier les `.jar` dans le dossier `code` de `app` et copier le `config.json` à côté de l'exécutable
processing. Ouvrir le pde dans processing et play.

## Code

Utilise le même fichier de config que le pont PS2OSC.

Tous les messages OSC atteignant ce programme doivent respecter ce formalisme :

n @ @ i D D D

Où : 

* n correspond au nombre de destinataires
* @ correspond au nom d'une des configurations de "listConfig" dans config.json
(Web-images,Web-keywords,Tracking-geste,Tracking-zone,Speech2Text,Facebook,ChatBot,Emotion)
* i correspond au nombre de messages à transmettre
* D correspond aux données des messages

