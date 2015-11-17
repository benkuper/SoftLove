# Tracking
Détection de position, placement en zones, détection de gestes

## Informations de déploiement
Utilisation de Visual Studio 2015.

### Gestion des dépendances : Nugets.
Installation des packages : "Install Packages"

### SDK Microsoft Kinect : Télécharger et Installer le SDK 1.8

## Publisher

Le programme de tracking envoi plusieurs informations : la position (x,z), la zone actuelle (A,B,present,absent,...)
Pour y souscrire :
- Port 5555, topic : "geste" --> Reçu : "A","B",... ou "present","absent"
- Port 5555, topic : "position" --> Reçu : "X:Z"
- Port 5555, topic : "geste" --> TODO

## Architecture
|_ src
	|_ SoftLove.sln : Fichier Projet Visual Studio
	|_ SoftLove/
			|_ amd64 : DLLs 64b
			|_ i386 : DLLs x86
			|_ Communication
					|_ Publisher.cs : Classe du Publisher, utilisé pour l'envoi des données
			|_ Exceptions : Classes d'Exceptions...
			|_ Zoning
					|_ Enums.cs : Divers Enums
					|_ Localisation.cs : Classe utilisée pour le placement d'un individue dans une zones
					|_ Zone.cs : Définition d'une Zone
					|_ ZoneRegister : Classe utilisée pour la configuration de zones
			|_ GestureBehaviour
					|_ GBTreatment.cs : TODO
			