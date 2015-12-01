# Update module Emotion


## Fonctionnement 
Ce module a pour but de récupérer les mots fournis par le module speech2text en *pub/sub*.  Ces mots sont envoyés à plusieurs API en ligne qui nous renvoi des choses tout à fait intéressantes.

Le texte passe d'abord par l'API Bing qui traduit le texte en anglais pour
utiliser les APIs décrites ensuite. (*REST*)

L'api la plus importante est mood patrol qui renvoi 8 sentiments (qui correspondent à notre modèle de roue des émotions de Plutchik). Les autres sont simplement des API qui nous permette de savoir si le groupe de mot est plutôt positif ou négatif. L'api que nous appelons PosNeg est actuellement cassée, l'API de secours (que nous appelons sentiment) marche bien et nous permis de correctement faire la fusion. 
(Tout ce qui va en ligne, c'est du *REST*. En interne c'est du *pub/sub*)

Les résultats sont lissés assez brutalement avec les 5 dernières requêtes.

Il y a un paramétrage nécéssaire à faire dans le fichier `FinalClient.java` (au
niveau de IP)

On a aussi 'théorisé' le publisher du heartbeat du msband dans un mock (appelé `BandHeartbeatMock.java`). Ça marche plutôt bien.

Notre sortie la plus exploitable est donc celle fournie dans la classe FusionMaster.java qui publish sur le topic qui lui est passé en paramètre. (La classe FinalClient.java montre bien l'utilisation.)

## Code
On utilise zeroMQ pour ls *pub/sub* et spécifique Unirest pour les requêtes HTTP. Toutes les dépendances sont gérées dans le maven.

Selon l'utilisation (localhost, ou non) voir 2 endroits en particulier : 

La classe BingTranslator et la classe `FinalClient.java` pour la configuration en mode avec les mocks ou sans.

Le gros du code se trouve dans `FusionMaster.java` et `FinalClient.java`

### Protocole d'utilisation
TCP

### Protocole de communication
Pub/sub

### La sortie 
```json
{
    "intensity": 1.0,
        "moodWheel" : 
        {
            "joy":1.0,
            "trust":1.0,
            "fear":1.0,
            "sadness":1.0,
            "anger":1.0,
            "disgust":1.0,
            "surprise":1.0,
            "anticipationo":1.0
        }
}
```

# LINKÉ à ?
speech2text et msband

