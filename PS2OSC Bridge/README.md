# DataViz

## Fonctionnement

Lancé depuis un IDE (IntelliJ IDEA Community Edition 15.0) : simplement run le main


Lancé via processing : copier les `.jar` dans le dossier `code` de `app` et copier le `config.json` à côté de l'exécutable
processing. Ouvrir le pde dans processing et play.

## Code

Permet la connexion aux differents topics du projet via zeromq. Les topics sont configurés dans le config.json et récupérés 
au démarrage de l'app. Chaque topic est surveillé dans un thread different (cf code java).

